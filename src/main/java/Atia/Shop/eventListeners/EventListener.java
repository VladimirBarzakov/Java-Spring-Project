/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.eventListeners;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.service.API.WinStrategy.WinStrategyExecutor;
import Atia.Shop.service.IMPL.WinStrategyImpl.OpenBiddingHighestBid;
import Atia.Shop.service.IMPL.WinStrategyImpl.SecretBiddingFirstBid;
import Atia.Shop.service.IMPL.WinStrategyImpl.SecretBiddingSecondBid;
import Atia.Shop.utils.emailService.EmailServiceImpl;
import Atia.Shop.utils.emailService.MailTemplates;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Component
public class EventListener {

    private final AuctionService auctionService;
    private final ItemService itemService;
    private final UserService userService;
    private final BidService bidService;
    private final EmailServiceImpl emailService;

    @Autowired
    public EventListener(AuctionService auctionService,
            ItemService itemService,
            UserService userService,
            BidService bidService,
            EmailServiceImpl emailService) {
        this.auctionService = auctionService;
        this.userService = userService;
        this.itemService = itemService;
        this.bidService = bidService;
        this.emailService = emailService;
    }

    private List<AuctionServiceModel> getAllActiveAuctions() {
        return this.auctionService.getAllAuctions()
                .stream()
                .filter(x -> x.getStatus().compareTo(AuctionStatus.CREATED) >= 0
                && x.getStatus().compareTo(AuctionStatus.SELLED) < 0)
                .collect(Collectors.toList());
    }

    private void updateAllFromPendingToLive(List<AuctionServiceModel> pendingAuctions) {
        List<AuctionServiceModel> emptyAuctions = new ArrayList();
        List<AuctionServiceModel> readyToGoLiveAuctions = new ArrayList();
        for (AuctionServiceModel auction : pendingAuctions) {
            if (this.auctionService.isAuctionEmpty(auction.getId())) {
                emptyAuctions.add(auction);
                continue;
            }
            auction.setStatus(AuctionStatus.LIVE);
            readyToGoLiveAuctions.add(auction);
        }
        this.deleteEmptyAuctions(emptyAuctions);
        this.auctionService.saveAll(readyToGoLiveAuctions);
    }

    private void updateAllFromLive(List<AuctionServiceModel> liveAuctions) {
        List<AuctionServiceModel> winndeAuctions = new ArrayList();
        List<AuctionServiceModel> expiredAuctions = new ArrayList();
        for (AuctionServiceModel auction : liveAuctions) {
            if (auction.getBestBid() == null) {
                expiredAuctions.add(auction);
            } else {
                winndeAuctions.add(auction);
            }
        }
        this.updateAllFromLiveToWinned(winndeAuctions);
        this.updateAllFromLiveToExpired(expiredAuctions);
    }

    private void deleteEmptyAuctions(List<AuctionServiceModel> emptyAuctions) {
        this.auctionService.deleteAllAuctions(emptyAuctions);
    }

    private void updateAllFromLiveToWinned(List<AuctionServiceModel> winnedAuctions) {
        List<BidServiceModel> allAuctionBids;
        WinStrategyExecutor winExecutor;
        AuctionServiceModel auction;
        int listSize = winnedAuctions.size();
        for (int i = 0; i < listSize; i++) {
            allAuctionBids = null;
            winExecutor = null;
            auction=winnedAuctions.get(i);
            switch (auction.getAuctionStrategy()) {
                case OPEN_BIDDING_HIGHEST_BID:
                    winExecutor = new OpenBiddingHighestBid();
                    break;
                case SECRET_BIDDING_FIRST_BID:
                    winExecutor = new SecretBiddingFirstBid();
                    break;
                case SECRET_BIDDING_SECOND_BID:
                    winExecutor = new SecretBiddingSecondBid();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            allAuctionBids = this.bidService.getAllByAuctionId(auction.getId());
            winExecutor.electBestBidAndWinnerAndWinPrice(allAuctionBids);
            BidServiceModel bestBid = winExecutor.getBestBid();
            UserServiceModel winner = winExecutor.getWinner();
            BigDecimal winPrice = winExecutor.getWinPrice();
            
            if (bestBid == null) {
                this.updateAllFromLiveToExpired(Arrays.asList(auction));
                winnedAuctions.remove(i);
                i--;
                listSize = winnedAuctions.size();
                continue;
            }
            auction.setStatus(AuctionStatus.SELLED);
            auction.setAuctionWinner(winner);
            auction.setWinPrice(winPrice);
        }
        for(AuctionServiceModel winnedAuction : winnedAuctions){
            this.sendEmailToAuctionSeller(winnedAuction);
            this.sendEmailToAuctionWinner(winnedAuction);
            LOGGER.info(String.format(">>>EVENT-LISTENER<<< Auction with id %d have been winned by %s", 
                    winnedAuction.getId(), winnedAuction.getAuctionWinner().getDomainEmail()));
        }
        
        this.auctionService.saveAll(winnedAuctions);
    }

    private void updateAllFromLiveToExpired(List<AuctionServiceModel> expiredAuctions) {
        for (AuctionServiceModel auction : expiredAuctions) {
            this.itemService.removeAllFromAuction(this.itemService.findAllAucItemsByAuctionId(auction.getId()), false);
            auction.setStatus(AuctionStatus.EXPIRED);
        }
        this.auctionService.saveAll(expiredAuctions);
    }

    @Scheduled(cron = "0 * * * * *")
    protected void updateAuctionsAfterTime() {
        LocalDateTime now = LocalDateTime.now();
        List<AuctionServiceModel> activeAuctions = this.getAllActiveAuctions();
        this.updateAllFromPendingToLive(
                activeAuctions
                        .stream()
                        .filter(x -> x.getStatus().compareTo(AuctionStatus.LIVE) < 0
                        && x.getDateStarted().compareTo(java.sql.Timestamp.valueOf(now)) <= 0)
                        .collect(Collectors.toList()));
        this.updateAllFromLive(activeAuctions
                .stream()
                .filter(x -> x.getStatus().compareTo(AuctionStatus.LIVE) >= 0
                && x.getDateExpired().compareTo(java.sql.Timestamp.valueOf(now)) <= 0)
                .collect(Collectors.toList()));
    }
    
    private void sendEmailToAuctionSeller(AuctionServiceModel auction){
        Map<String, String> map =new HashMap();
        map.put("${auctionName}", auction.getTitle());
        map.put("${winnerName}", auction.getAuctionWinner().getName());
        map.put("${winnerMail}", auction.getAuctionWinner().getDomainEmail());
        map.put("${auctionprice}", auction.getWinPrice().toPlainString());
        this.emailService.sendSimpleMessage(auction.getSeller().getDomainEmail(), "Sold Auction", MailTemplates.SELLED_AUCTION_SELLER, map);
    }
    
    private void sendEmailToAuctionWinner(AuctionServiceModel auction){
        Map<String, String> map =new HashMap();
        map.put("${auctionName}", auction.getTitle());
        map.put("${sellerName}", auction.getSeller().getName());
        map.put("${sellerMail}", auction.getSeller().getDomainEmail());
        map.put("${auctionprice}", auction.getWinPrice().toPlainString());
        this.emailService.sendSimpleMessage(auction.getAuctionWinner().getDomainEmail(), "Winned Auction", MailTemplates.SELLED_AUCTION_WINNER, map);
    }

}

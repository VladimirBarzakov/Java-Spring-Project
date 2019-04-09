/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.SHOP;

import Atia.Shop.config.errorMesseges.ReportToUserExceptionMessage;
import Atia.Shop.config.errorMesseges.SystemExceptionMessage;
import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.Bid;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.domain.repositories.BidRepository;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.service.API.WinStrategy.WinStrategyExecutor;
import Atia.Shop.service.IMPL.WinStrategyImpl.OpenBiddingHighestBid;
import Atia.Shop.service.IMPL.WinStrategyImpl.SecretBiddingFirstBid;
import Atia.Shop.service.IMPL.WinStrategyImpl.SecretBiddingSecondBid;
import Atia.Shop.utils.valdiation.InputValidator;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Service
@Transactional
public class BidServiceImpl implements BidService {

    private final ModelMapper modelMapper;
    private final AuctionService auctionService;
    private final UserService userService;
    private final BidRepository bidRepository;
    private final InputValidator inputValidator;

    @Autowired
    public BidServiceImpl(
            ModelMapper modelMapper,
            AuctionService auctionService,
            UserService userService,
            BidRepository bidRepository,
            InputValidator inputValidator) {
        this.modelMapper = modelMapper;
        this.auctionService = auctionService;
        this.userService = userService;
        this.bidRepository = bidRepository;
        this.inputValidator = inputValidator;
    }

    @Override
    public BidServiceModel placeBid(BidServiceModel bibServiceModel) throws ReportToUserException {
        this.inputValidator.validateObject(bibServiceModel, bibServiceModel.getAmount(), bibServiceModel.getAuction());
        if (bibServiceModel.getBidder().getId()
                .equals(bibServiceModel.getAuction().getSeller().getId())) {
            throw new ReportToUserException(ReportToUserExceptionMessage.BID_CANNNOT_BE_PLACED_ON_OWN_AUCTION);
        }
        if (!this.auctionService.isAuctionBiddable(bibServiceModel.getAuction())) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.BID_INVALID_AUCTION_STATUS);
        }
        Bid bid = this.modelMapper.map(bibServiceModel, Bid.class);
        bid.setBidDate(new Date());
        bid = this.bidRepository.save(bid);

        AuctionServiceModel auction = bibServiceModel.getAuction();
        WinStrategyExecutor winExecutor = null;
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

        BidServiceModel bestBid = winExecutor.shouldUpdateBestBid(auction, this.modelMapper.map(bid, BidServiceModel.class));

        if (bestBid != null && bestBid.getId() == bid.getId()) {
            auction.setBestBid(this.modelMapper.map(bid, BidServiceModel.class));
            this.auctionService.saveAll(Arrays.asList(auction));
        }
        return this.modelMapper.map(bid, BidServiceModel.class);
    }

    @Override
    public List<BidServiceModel> getAllByAuctionId(Long auctionId) {
        this.inputValidator.validateObject(auctionId);
        Auction auction = this.modelMapper.map(this.auctionService.getAuctionById(auctionId), Auction.class);
        List<BidServiceModel> bids = this.bidRepository.getAllByAuction(auction)
                .stream()
                .map(x -> this.modelMapper.map(x, BidServiceModel.class))
                .collect(Collectors.toList());
        return bids;
    }

    @Override
    public List<BidServiceModel> getALLByBidderMail(String bidderEmail) {
        this.inputValidator.validateString(bidderEmail);
        User bidder = this.modelMapper.map(this.userService.getUserByDomainEmail(bidderEmail), User.class);
        return this.bidRepository.getAllByBidder(bidder)
                .stream()
                .map(x -> this.modelMapper.map(x, BidServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BidServiceModel> adminGetALL() {
        return this.bidRepository.findAll()
                .stream()
                .map(x -> this.modelMapper.map(x, BidServiceModel.class))
                .collect(Collectors.toList());
    }

}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.WinStrategyImpl;

import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class OpenBiddingHighestBid extends BaseStrategyExecutor{
    
    @Override
    public void electBestBidAndWinnerAndWinPrice(List<BidServiceModel> allAuctionBids) {
        allAuctionBids.sort((a, b) -> {
            int comparator = b.getAmount().compareTo(a.getAmount());
            if (comparator != 0) {
                return comparator;
            }
            comparator = a.getBidDate().compareTo(b.getBidDate());
            return comparator;
        });
        if (allAuctionBids.isEmpty()) {
            return;
        } else {
            this.bestBid = allAuctionBids.get(0);
            this.winner = bestBid.getBidder();
            this.winPrice = bestBid.getAmount();
        }
    }
 
}

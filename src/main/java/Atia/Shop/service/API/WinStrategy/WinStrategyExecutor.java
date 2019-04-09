/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.WinStrategy;

import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface WinStrategyExecutor {
    
    public void electBestBidAndWinnerAndWinPrice(List<BidServiceModel> allAuctionBids);
    
    public BidServiceModel shouldUpdateBestBid(AuctionServiceModel auction, BidServiceModel currentBid);
    
    public BidServiceModel getBestBid();

    public UserServiceModel getWinner();

    public BigDecimal getWinPrice();
}

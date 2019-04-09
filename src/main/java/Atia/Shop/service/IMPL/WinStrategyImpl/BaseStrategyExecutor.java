/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.WinStrategyImpl;

import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.service.API.WinStrategy.WinStrategyExecutor;
import java.math.BigDecimal;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public abstract class BaseStrategyExecutor implements WinStrategyExecutor{
    
    protected BidServiceModel bestBid;
    protected UserServiceModel winner;
    protected BigDecimal winPrice;

    public BaseStrategyExecutor() {
        this.bestBid=null;
        this.winner=null;
        this.winPrice=null;
    }
    
    
  
    @Override
    public BidServiceModel shouldUpdateBestBid(AuctionServiceModel auction, BidServiceModel currentBid) {
        if(auction.getBestBid()==null || auction.getBestBid().getAmount().compareTo(currentBid.getAmount())<0){
            return currentBid;
        }
        return null;  
    }

    public BidServiceModel getBestBid() {
        return this.bestBid;
    }

    public UserServiceModel getWinner() {
        return this.winner;
    }

    public BigDecimal getWinPrice() {
        return this.winPrice;
    }
    
    
}

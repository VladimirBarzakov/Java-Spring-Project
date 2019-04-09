/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities.enums;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public enum AuctionStrategy {
    OPEN_BIDDING_HIGHEST_BID("Open bidding, highest bid wins", "Open Bidding, Highest Bid Wins", true),
    SECRET_BIDDING_FIRST_BID("Secret bidding, highest bid wins","Secret Bidding, Highest Bid Wins",false),
    SECRET_BIDDING_SECOND_BID("Secret bidding, second highest bid wins","Secret Bidding, Highest Bid Wins and pays second highest price", false);
    
    private String description;
    private String strategy;
    private boolean isOpenBidding;
    
    AuctionStrategy(String strategy, String description, boolean isOpenBidding){
        this.strategy=strategy;
        this.description=description;
        this.isOpenBidding=isOpenBidding;
    }

    public String getDescription() {
        return description;
    }

    public String getStrategy() {
        return strategy;
    }

    public boolean isIsOpenBidding() {
        return isOpenBidding;
    }
    
    
  
}

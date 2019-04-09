/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.buyer.bids.all;

import Atia.Shop.domain.models.DTO.BasePresenter;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AllBidlViewModel extends BasePresenter{
    
    private Long id;
    
    private String bidderName;
    
    private String auctionTitle;
    
    private Long auctionId;
    
    private Date bidDate; 
    
    private BigDecimal amount;
    
    private String auctionStatus;
    
    private UserServiceModel auctionWinner;

    public AllBidlViewModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public String getAuctionTitle() {
        return auctionTitle;
    }

    public void setAuctionTitle(String auctionTitle) {
        this.auctionTitle = this.trimToBigField(auctionTitle);
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }
    
    public Date getBidDate() {
        return bidDate;
    }

    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public UserServiceModel getAuctionWinner() {
        return auctionWinner;
    }

    public void setAuctionWinner(UserServiceModel auctionWinner) {
        this.auctionWinner = auctionWinner;
    }
}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels;

import Atia.Shop.config.validation.ValidProperties;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class BidServiceModel {
    
    private Long id;
    
    private UserServiceModel bidder;
    
    private AuctionServiceModel auction;
    
    private Date bidDate;    
    
    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Digits(integer=ValidProperties.DECIMAL_DIGITS, fraction=ValidProperties.DECIMAL_SCALE)
    private BigDecimal amount;

    public BidServiceModel() {
    }

    public UserServiceModel getBidder() {
        return bidder;
    }

    public void setBidder(UserServiceModel bidder) {
        this.bidder = bidder;
    }

    public AuctionServiceModel getAuction() {
        return auction;
    }

    public void setAuction(AuctionServiceModel auction) {
        this.auction = auction;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
}

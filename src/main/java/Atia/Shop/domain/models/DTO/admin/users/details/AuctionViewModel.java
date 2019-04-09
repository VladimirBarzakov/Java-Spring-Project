/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.users.details;

import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.models.DTO.BasePresenter;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AuctionViewModel extends BasePresenter{

    private Long id;

    private String title;

    private String description;

    private BigDecimal initialPrice;
    
    private BigDecimal BestBidAmount;
    
    private AuctionStrategy auctionStrategy;
    
    private BigDecimal winPrice;
    
    private Date dateStarted;

    private Date dateExpired;

    private String Status;

    public AuctionViewModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = this.trimToSmallField(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = this.trimToSmallField(description);
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public BigDecimal getBestBidAmount() {
        return BestBidAmount;
    }

    public void setBestBidAmount(BigDecimal BestBidAmount) {
        this.BestBidAmount = BestBidAmount;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
    }

    public AuctionStrategy getAuctionStrategy() {
        return auctionStrategy;
    }

    public void setAuctionStrategy(AuctionStrategy auctionStrategy) {
        this.auctionStrategy = auctionStrategy;
    }

    public BigDecimal getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }
    
}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.archive;

import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.models.DTO.BasePresenter;
import Atia.Shop.domain.models.serviceModels.pictures.AuctionPictureServiceModel;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AllAuctionViewModel extends BasePresenter{

    private Long id;

    private String title;

    private String description;
    
    private String sellerName;

    private String auctionWinnerName;

    private Date dateStarted;

    private Date dateExpired;
    
    private AuctionStrategy auctionStrategy;

    private BigDecimal bestBidAmount;

    private BigDecimal initialPrice;
    
    private BigDecimal winPrice;

    private String status;
    
    private AuctionPictureServiceModel thumbnail;
    

    public AllAuctionViewModel() {
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
        this.title = this.trimToBigField(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = this.trimToBigField(description);
    }

    public String getAuctionWinnerName() {
        return auctionWinnerName;
    }

    public void setAuctionWinnerName(String auctionWinnerName) {
        this.auctionWinnerName = auctionWinnerName;
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

    public BigDecimal getBestBidAmount() {
        return bestBidAmount;
    }

    public void setBestBidAmount(BigDecimal bestBidAmount) {
        this.bestBidAmount = bestBidAmount;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public AuctionPictureServiceModel getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(AuctionPictureServiceModel thumbnail) {
        this.thumbnail = thumbnail;
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

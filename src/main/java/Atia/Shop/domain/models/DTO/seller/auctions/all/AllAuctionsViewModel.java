/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.auctions.all;

import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.models.DTO.BasePresenter;
import Atia.Shop.domain.models.serviceModels.pictures.AuctionPictureServiceModel;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AllAuctionsViewModel extends BasePresenter {

    private Long id;

    private String title;

    private String description;

    private String auctionWinnerName;

    private Date dateStarted;

    private Date dateExpired;

    private BigDecimal initialPrice;
    
    private BigDecimal BestBidAmount;

    private String status;
    
    private boolean isAuctionEditable;
    
    private boolean isAuctionDeletable;
    
    private boolean isArchivable;
    
    private AuctionStrategy auctionStrategy;
    
    private AuctionPictureServiceModel thumbnail;
    
    
    public AllAuctionsViewModel() {
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

    public boolean isIsAuctionEditable() {
        return isAuctionEditable;
    }

    public void setIsAuctionEditable(boolean isAuctionEditable) {
        this.isAuctionEditable = isAuctionEditable;
    }

    public boolean isIsAuctionDeletable() {
        return isAuctionDeletable;
    }

    public void setIsAuctionDeletable(boolean isAuctionDeletable) {
        this.isAuctionDeletable = isAuctionDeletable;
    }

    public BigDecimal getBestBidAmount() {
        return BestBidAmount;
    }

    public void setBestBidAmount(BigDecimal BestBidAmount) {
        this.BestBidAmount = BestBidAmount;
    }

    public boolean isIsArchivable() {
        return isArchivable;
    }

    public void setIsArchivable(boolean isArchivable) {
        this.isArchivable = isArchivable;
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

}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels;

import Atia.Shop.domain.models.serviceModels.pictures.AuctionPictureServiceModel;
import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.entities.enums.AuctionStrategy;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AuctionServiceModel {
    
    private Long id;
    
    @Length(min=ValidProperties.AUCTION_TITLE_MIN_LENGHT,max =ValidProperties.AUCTION_TITLE_MAX_LENGHT)
    private String title;
    
    @Length(min=ValidProperties.AUCTION_DESCRIPTION_MIN_LENGHT,max =ValidProperties.AUCTION_DESCRIPTION_MAX_LENGHT)
    private String description;
    
    private UserServiceModel seller;
    
    private UserServiceModel auctionWinner;
    
    private Date dateStarted;
    
    private Date dateExpired;
    
    private BidServiceModel bestBid;
    
    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Digits(integer=ValidProperties.DECIMAL_DIGITS, fraction=ValidProperties.DECIMAL_SCALE)
    private BigDecimal initialPrice;
    
    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Digits(integer=ValidProperties.DECIMAL_DIGITS, fraction=ValidProperties.DECIMAL_SCALE)
    private BigDecimal winPrice;
    
    private AuctionStatus status;
    
    private AuctionStrategy auctionStrategy;
    
    private AuctionPictureServiceModel thumbnail;

    public AuctionServiceModel() {
        
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
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserServiceModel getSeller() {
        return seller;
    }

    public void setSeller(UserServiceModel seller) {
        this.seller = seller;
    }

    public UserServiceModel getAuctionWinner() {
        return auctionWinner;
    }

    public void setAuctionWinner(UserServiceModel auctionWinner) {
        this.auctionWinner = auctionWinner;
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

    public BidServiceModel getBestBid(){
        return this.bestBid;
    }
    
    public void setBestBid(BidServiceModel bestBid){
        this.bestBid = bestBid;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
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

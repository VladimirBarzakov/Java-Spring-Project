/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities;

import Atia.Shop.domain.entities.pictures.AuctionPicture;
import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.domain.entities.base.BaseEntityLongID;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.entities.enums.AuctionStrategy;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "auctions")
public class Auction extends BaseEntityLongID implements Serializable {

    @NotEmpty
    @Length(min=ValidProperties.AUCTION_TITLE_MIN_LENGHT,max =ValidProperties.AUCTION_TITLE_MAX_LENGHT)
    @Column(name = "title", nullable = false, updatable = true, length = ValidProperties.AUCTION_TITLE_MAX_LENGHT)
    private String title;

    @NotEmpty
    @Length(min=ValidProperties.AUCTION_DESCRIPTION_MIN_LENGHT,max =ValidProperties.AUCTION_DESCRIPTION_MAX_LENGHT)
    @Column(name = "description", nullable = false, updatable = true, length = ValidProperties.AUCTION_DESCRIPTION_MAX_LENGHT)
    private String description;

    @ManyToOne()
    @JoinColumn(name = "seller", nullable = false, updatable = false)
    private User seller;

    @ManyToOne()
    @JoinColumn(name = "auction_winner", nullable = true, updatable = true)
    private User auctionWinner;

    @Column(name = "date_sratded", nullable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStarted;

    @Column(name = "date_expired", nullable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateExpired;

    @OneToOne
    @JoinColumn(name = "best_bid", nullable = true, updatable = true)
    private Bid bestBid;

    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Digits(integer=ValidProperties.DECIMAL_DIGITS, fraction=ValidProperties.DECIMAL_SCALE)
    @Column(name = "ititial_price", nullable = true, updatable = true, precision = ValidProperties.DECIMAL_PRECISION, scale = ValidProperties.DECIMAL_SCALE)
    private BigDecimal initialPrice;
    
    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Digits(integer=ValidProperties.DECIMAL_DIGITS, fraction=ValidProperties.DECIMAL_SCALE)
    @Column(name = "win_price", nullable = true, updatable = true, precision = ValidProperties.DECIMAL_PRECISION, scale = ValidProperties.DECIMAL_SCALE)
    private BigDecimal winPrice;

    @Column(name = "status", nullable = false, updatable = true)
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;
    
    @Column(name = "strategy", nullable = false, updatable = true)
    @Enumerated(EnumType.STRING)
    private AuctionStrategy auctionStrategy;
    
    @OneToOne
    @JoinColumn(name="auction_thumbnail", nullable=true, updatable = true )
    private AuctionPicture thumbnail;


    public Auction() {
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

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
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

    public User getAuctionWinner() {
        return auctionWinner;
    }

    public void setAuctionWinner(User auctionWinner) {
        this.auctionWinner = auctionWinner;
    }

    public Bid getBestBid() {
        return bestBid;
    }

    public void setBestBid(Bid bestBid) {
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

    public AuctionPicture getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(AuctionPicture thumbnail) {
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

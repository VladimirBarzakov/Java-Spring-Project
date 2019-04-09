/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.domain.entities.base.BaseEntityLongID;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "bids")
public class Bid extends BaseEntityLongID implements Serializable {

    @ManyToOne()
    @JoinColumn(name = "bidder", nullable = false, updatable = false)
    private User bidder;

    @ManyToOne()
    @JoinColumn(name = "auction_id", nullable = false, updatable = false)
    private Auction auction;

    @Column(name = "bid_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date bidDate;

    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Column(name = "bid_amount", nullable = false, updatable = false, precision = ValidProperties.DECIMAL_PRECISION, scale = ValidProperties.DECIMAL_SCALE)
    private BigDecimal amount;

    public Bid() {
    }

    public User getBidder() {
        return bidder;
    }

    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
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

}

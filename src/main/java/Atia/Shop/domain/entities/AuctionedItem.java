/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.domain.entities.base.BaseEntityLongID;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "auctioned_items")
public class AuctionedItem extends BaseEntityLongID implements Serializable {

    @ManyToOne()
    @JoinColumn(nullable = false, updatable = false)
    private Auction auction;

    @ManyToOne()
    @JoinColumn(nullable = true, updatable = true)
    private Item parent;

    @NotEmpty
    @Column(name = "auc_item_name", nullable = false, updatable = true, length = ValidProperties.ITEM_NAME_MAX_LENGHT)
    private String itemName;

    @Column(name = "auc_item_description", nullable = true, updatable = true, length = ValidProperties.ITEM_DESCRIPTION_MAX_LENGHT)
    private String itemDescription;
    
    @Column(name = "auc_item_location", nullable = true, updatable = true, length = ValidProperties.ITEM_LOCATION_MAX_LENGHT)
    private String itemLocation;

    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Column(name = "auc_item_price", nullable = true, updatable = true, precision = ValidProperties.DECIMAL_PRECISION, scale = ValidProperties.DECIMAL_SCALE)
    private BigDecimal itemPrice;

    @Column(name = "auc_quantity", nullable = false)
    private Integer quantity;

    public AuctionedItem() {
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Item getParent() {
        return parent;
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }
    
    

}

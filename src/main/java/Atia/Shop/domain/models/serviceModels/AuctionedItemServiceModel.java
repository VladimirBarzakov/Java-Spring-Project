/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels;

import Atia.Shop.config.validation.ValidProperties;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AuctionedItemServiceModel {
    
    private Long id;
    
    private AuctionServiceModel auction;
    
    private ItemServiceModel parent;
    
    @Length(min=ValidProperties.ITEM_NAME_MIN_LENGHT, max =ValidProperties.ITEM_NAME_MAX_LENGHT)
    private String itemName;
    
    @Length(min=ValidProperties.ITEM_DESCRIPTION_MIN_LENGHT, max =ValidProperties.ITEM_DESCRIPTION_MAX_LENGHT)
    private String itemDescription;
    
    @Length(min=ValidProperties.ITEM_LOCATION_MIN_LENGHT, max =ValidProperties.ITEM_LOCATION_MAX_LENGHT)
    private String itemLocation;
    
    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Digits(integer=ValidProperties.DECIMAL_DIGITS, fraction=ValidProperties.DECIMAL_SCALE)
    private BigDecimal itemPrice;
    
    @Min(ValidProperties.INT_MIN)
    private Integer quantity;

    public AuctionedItemServiceModel() {
    }

    public AuctionServiceModel getAuction() {
        return auction;
    }

    public void setAuction(AuctionServiceModel auction) {
        this.auction = auction;
    }

    public ItemServiceModel getParent() {
        return parent;
    }

    public void setParent(ItemServiceModel parent) {
        this.parent = parent;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

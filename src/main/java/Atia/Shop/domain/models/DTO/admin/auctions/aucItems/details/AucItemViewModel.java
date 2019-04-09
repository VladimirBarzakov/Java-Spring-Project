/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.auctions.aucItems.details;

import Atia.Shop.domain.entities.enums.AuctionStatus;
import java.math.BigDecimal;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AucItemViewModel {
    
    private Long auctionId;
    
    private String itemName;
    
    private String itemDescription;
    
    private String itemLocation;
    
    private BigDecimal itemPrice;
    
    private Integer quantity;
    
    private AuctionStatus auctionStatus;

    public AucItemViewModel() {
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public AuctionStatus getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(AuctionStatus auctionStatus) {
        this.auctionStatus = auctionStatus;
    }
    
    

}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.archive.details;


import Atia.Shop.domain.models.DTO.BasePresenter;
import java.math.BigDecimal;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DetailsAucItemViewModel extends BasePresenter{
    
    private Long id;

    private String itemName;

    private Integer quantity;

    private BigDecimal itemPrice;
    
    private String itemLocation;
    
    private Long parentId;


    public DetailsAucItemViewModel() {
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
        this.itemName = this.trimToSmallField(itemName);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = this.trimToSmallField(itemLocation);
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
}

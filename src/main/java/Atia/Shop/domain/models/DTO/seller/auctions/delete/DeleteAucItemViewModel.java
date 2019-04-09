/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.auctions.delete;

import java.math.BigDecimal;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DeleteAucItemViewModel {

    private String itemName;

    private Integer quantity;

    private BigDecimal itemPrice;


    public DeleteAucItemViewModel() {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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


}

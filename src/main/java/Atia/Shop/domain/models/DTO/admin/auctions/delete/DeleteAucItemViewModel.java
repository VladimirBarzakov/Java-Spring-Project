/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.auctions.delete;

import Atia.Shop.domain.models.DTO.BasePresenter;
import java.math.BigDecimal;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DeleteAucItemViewModel extends BasePresenter{

    private String itemName;

    private Integer quantity;

    private BigDecimal itemPrice;


    public DeleteAucItemViewModel() {
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


}

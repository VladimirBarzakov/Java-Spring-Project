/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.auctions.edit;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class EditItemBindingModel{

    private Long id;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Size(min = ValidProperties.ITEM_NAME_MIN_LENGHT, max = ValidProperties.ITEM_NAME_MAX_LENGHT, message = ValidationMesseges.ITEM_NAME)
    private String name;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Min(value = ValidProperties.INT_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    private Integer quantity;

    @DecimalMin(value = ValidProperties.DECIMAL_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    @Digits(integer = ValidProperties.DECIMAL_DIGITS, fraction = ValidProperties.DECIMAL_SCALE, message = ValidationMesseges.MONEY_FORMAT)
    private BigDecimal initialPrice;

    private boolean markedForAdd;

    public EditItemBindingModel() {
        this.markedForAdd = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }

    public boolean getMarkedForAdd() {
        return markedForAdd;
    }

    public void setMarkedForAdd(boolean markedForAdd) {
        this.markedForAdd = markedForAdd;
    }

}

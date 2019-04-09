/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.items.create;

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
public class CreateItemBindingModel {

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Size(min = ValidProperties.ITEM_NAME_MIN_LENGHT, max = ValidProperties.ITEM_NAME_MAX_LENGHT, message = ValidationMesseges.ITEM_NAME)
    private String name;

    @Size(min = ValidProperties.ITEM_DESCRIPTION_MIN_LENGHT, max = ValidProperties.ITEM_DESCRIPTION_MAX_LENGHT, message = ValidationMesseges.ITEM_DESCRIPTION)
    private String description;
    
    @Size(min = ValidProperties.ITEM_LOCATION_MIN_LENGHT, max = ValidProperties.ITEM_LOCATION_MAX_LENGHT, message = ValidationMesseges.ITEM_LOCATION)
    private String location;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Min(value = ValidProperties.INT_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    private Integer quantity;

    @DecimalMin(value = ValidProperties.DECIMAL_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    @Digits(integer = ValidProperties.DECIMAL_DIGITS, fraction = ValidProperties.DECIMAL_SCALE, message = ValidationMesseges.MONEY_FORMAT)
    private BigDecimal initialPrice;

    public CreateItemBindingModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}

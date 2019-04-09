/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.buyer.auctions.details;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class RegisterBidBindingModel {
    
    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @DecimalMin(value = ValidProperties.DECIMAL_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    @Digits(integer = ValidProperties.DECIMAL_DIGITS, fraction = ValidProperties.DECIMAL_SCALE, message = ValidationMesseges.MONEY_FORMAT)
    private BigDecimal amount;

    public RegisterBidBindingModel() {
    }
    
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    
    
    
}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels;

import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.config.validation.ValidProperties;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class ItemServiceModel {

    private Long id;

    @Length(min = ValidProperties.ITEM_NAME_MIN_LENGHT, max = ValidProperties.ITEM_NAME_MAX_LENGHT)
    private String name;

    @Length(min = ValidProperties.ITEM_DESCRIPTION_MIN_LENGHT, max = ValidProperties.ITEM_DESCRIPTION_MAX_LENGHT)
    private String description;

    @Min(ValidProperties.INT_MIN)
    private Integer quantity;

    @Length(min = ValidProperties.ITEM_LOCATION_MIN_LENGHT, max = ValidProperties.ITEM_NAME_MAX_LENGHT)
    private String location;

    @DecimalMin(ValidProperties.DECIMAL_MIN)
    @Digits(integer = ValidProperties.DECIMAL_DIGITS, fraction = ValidProperties.DECIMAL_SCALE)
    private BigDecimal initialPrice;

    private Date dateAdded;

    private UserServiceModel seller;
    
    private ItemPictureServiceModel thumbnail;

    public ItemServiceModel() {
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserServiceModel getSeller() {
        return seller;
    }

    public void setSeller(UserServiceModel seller) {
        this.seller = seller;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ItemPictureServiceModel getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ItemPictureServiceModel thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    

}

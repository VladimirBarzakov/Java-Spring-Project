/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities;

import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.domain.entities.pictures.ItemPicture;
import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.domain.entities.base.BaseEntityLongID;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Vladimir
 */
@Entity
@Table(name = "items")
public class Item extends BaseEntityLongID implements Serializable {

    @NotEmpty
    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Size(min = ValidProperties.ITEM_NAME_MIN_LENGHT, max = ValidProperties.ITEM_NAME_MAX_LENGHT, message = ValidationMesseges.ITEM_NAME)
    @Column(name = "name", nullable = false, updatable = true, length = ValidProperties.ITEM_NAME_MAX_LENGHT)
    private String name;

    @Size(min = ValidProperties.ITEM_DESCRIPTION_MIN_LENGHT, max = ValidProperties.ITEM_DESCRIPTION_MAX_LENGHT, message = ValidationMesseges.ITEM_DESCRIPTION)
    @Column(name = "description", nullable = true, updatable = true, length = ValidProperties.ITEM_DESCRIPTION_MAX_LENGHT)
    private String description;
    
    @Size(min = ValidProperties.ITEM_LOCATION_MIN_LENGHT, max = ValidProperties.ITEM_LOCATION_MAX_LENGHT, message = ValidationMesseges.ITEM_LOCATION)
    @Column(name = "location", nullable = true, updatable = true, length = ValidProperties.ITEM_LOCATION_MAX_LENGHT)
    private String location;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Min(value = ValidProperties.INT_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    @Column(name = "quantity", nullable = false, updatable = true)
    private Integer quantity;

    @DecimalMin(value = ValidProperties.DECIMAL_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    @Digits(integer = ValidProperties.DECIMAL_DIGITS, fraction = ValidProperties.DECIMAL_SCALE, message = ValidationMesseges.MONEY_FORMAT)
    @Column(name = "initial_price", nullable = true, updatable = true, precision = ValidProperties.DECIMAL_PRECISION, scale = ValidProperties.DECIMAL_SCALE)
    private BigDecimal initialPrice;

    @Column(name = "date_added", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;
    
    @OneToOne
    @JoinColumn(name="item_thumbnail", nullable=true, updatable = true )
    private ItemPicture thumbnail;


    @ManyToOne()
    private User seller;

    public Item() {
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
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

    public ItemPicture getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ItemPicture thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    

}

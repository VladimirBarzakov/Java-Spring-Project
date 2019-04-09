/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.items.all;

import Atia.Shop.domain.models.DTO.BasePresenter;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AllItemViewModel extends BasePresenter{

    private Long id;

    private String name;
    
    private String location;

    private Integer quantity;

    private BigDecimal initialPrice;

    private Date dateAdded;
    
    private boolean isEditable;
    
    private boolean isDeletable;
    
    private ItemPictureServiceModel thumbnail;

    public AllItemViewModel() {
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
        this.name = this.trimToBigField(name);
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

    public boolean isIsEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isIsDeletable() {
        return isDeletable;
    }

    public void setIsDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = this.trimToBigField(location);
    }

    public ItemPictureServiceModel getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ItemPictureServiceModel thumbnail) {
        this.thumbnail = thumbnail;
    }
}

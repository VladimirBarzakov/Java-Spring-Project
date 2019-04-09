/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.items.details;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DetailsItemViewModel {
    
    private Long id;

    private String name;

    private String description;
    
    private String location;

    private Integer quantity;

    private BigDecimal initialPrice;
    
    private boolean isItemEditable;

    private List<DetailsAucItemViewModel> aucItem;
    
    private List<DetailsItemPictureViewModel> itemPictures;

    public DetailsItemViewModel() {
        this.aucItem = new ArrayList();
        this.isItemEditable=false;
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

    public List<DetailsAucItemViewModel> getAucItem() {
        return aucItem;
    }

    public void setAucItem(List<DetailsAucItemViewModel> aucItem) {
        this.aucItem = aucItem;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DetailsItemPictureViewModel> getItemPictures() {
        return itemPictures;
    }

    public void setItemPictures(List<DetailsItemPictureViewModel> itemPictures) {
        this.itemPictures = itemPictures;
    }

    public boolean isIsItemEditable() {
        return isItemEditable;
    }

    public void setIsItemEditable(boolean isItemEditable) {
        this.isItemEditable = isItemEditable;
    }
    
    
}

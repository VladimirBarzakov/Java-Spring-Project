/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels.pictures;

import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class ItemPictureServiceModel {
    
    private Long id;
    
    private ItemServiceModel item;
    
    private String pictureId;
    
    private Date dateAdded;
    
    private String description;
    
    private String originalFileName;

    public ItemPictureServiceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemServiceModel getItem() {
        return item;
    }

    public void setItem(ItemServiceModel item) {
        this.item = item;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    
    public String getPicturePath(){
        return WebConstrants.ITEM_PIC_WEB_ROUTE+this.pictureId;
    }  
}

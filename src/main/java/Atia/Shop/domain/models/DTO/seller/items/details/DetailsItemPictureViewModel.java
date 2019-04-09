/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.items.details;

import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.domain.models.DTO.BasePresenter;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DetailsItemPictureViewModel extends BasePresenter{
    
    private Long id;
    
    private String pictureId;
     
    private Date dateAdded;
    
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.description = this.trimToSmallField(description);
    }
    
    public String getPicturePath(){
        return WebConstrants.ITEM_PIC_WEB_ROUTE+this.pictureId;
    }
    
    
    
}

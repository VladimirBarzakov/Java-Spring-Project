/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities.pictures;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.base.BaseEntityLongID;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "items_images")
public class ItemPicture extends BaseEntityLongID implements Serializable {
    
    @NotNull
    @ManyToOne()
    @JoinColumn(name = "item", nullable = false, updatable = false)
    private Item item;
    
    @Column(name = "picture_id", nullable = false, updatable = false, unique=true)
    private String pictureId;
    
    @Column(name = "date_added", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;
    
    @Column(name = "original_file_name", nullable = false, updatable = false)
    private String originalFileName;
    
    @Column(name = "item_picture_description", nullable = true, updatable = true, length = ValidProperties.PICTURE_DESCRIPTION_MAX_LENGHT)
    private String description;

    public ItemPicture() {
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
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
    
    
}

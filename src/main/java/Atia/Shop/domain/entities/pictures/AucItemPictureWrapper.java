/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities.pictures;

import Atia.Shop.config.validation.ValidProperties;
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
import javax.validation.constraints.NotEmpty;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "auct_item_images")
public class AucItemPictureWrapper extends BaseEntityLongID implements Serializable{
    
    
    @Column(name = "auctioned_itemId", nullable = false, updatable = false)
    private Long auctionedItemId;
    
    @Column(name = "auction_id", nullable = false, updatable = false)
    private Long auctionId;
    
    @NotEmpty
    @Column(name = "auctioned_item_name", nullable = false, updatable = true, length=ValidProperties.ITEM_NAME_MAX_LENGHT)
    private String auctionedItemName; 
    
    @ManyToOne()
    @JoinColumn(name = "auction_picture", nullable = false, updatable = false)
    private AuctionPicture auctionPicture;  
    
    @Column(name = "auction_picture_description", nullable = true, updatable = true, length = ValidProperties.PICTURE_DESCRIPTION_MAX_LENGHT)
    private String description;
    
    @Column(name = "date_added", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAdded;
    
    @Column(name = "original_file_name", nullable = false, updatable = false)
    private String originalFileName;

    public AucItemPictureWrapper() {
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }
    
    public Long getAuctionedItemId() {
        return auctionedItemId;
    }

    public void setAuctionedItemId(Long auctionedItemId) {
        this.auctionedItemId = auctionedItemId;
    }

    public AuctionPicture getAuctionPicture() {
        return auctionPicture;
    }

    public String getAuctionedItemName() {
        return auctionedItemName;
    }

    public void setAuctionedItemName(String auctionedItemName) {
        this.auctionedItemName = auctionedItemName;
    }
    
    public void setAuctionPicture(AuctionPicture auctionPicture) {
        this.auctionPicture = auctionPicture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    
    
    
}

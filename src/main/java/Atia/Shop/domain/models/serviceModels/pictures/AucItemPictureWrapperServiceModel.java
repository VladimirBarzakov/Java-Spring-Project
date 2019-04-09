/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels.pictures;

import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AucItemPictureWrapperServiceModel {
    
    private Long id;
    
    private Long auctionedItemId;
    
    private String auctionedItemName;
    
    private Long auctionId;
    
    private AuctionPictureServiceModel auctionPicture;  
    
    private String description;
     
    private Date dateAdded;
    
    private String originalFileName;

    public AucItemPictureWrapperServiceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuctionedItemName() {
        return auctionedItemName;
    }

    public void setAuctionedItemName(String auctionedItemName) {
        this.auctionedItemName = auctionedItemName;
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

    public AuctionPictureServiceModel getAuctionPicture() {
        return auctionPicture;
    }

    public void setAuctionPicture(AuctionPictureServiceModel auctionPicture) {
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

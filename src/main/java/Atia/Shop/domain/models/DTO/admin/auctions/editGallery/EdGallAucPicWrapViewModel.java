/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.auctions.editGallery;

import Atia.Shop.config.constants.WebConstrants;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class EdGallAucPicWrapViewModel {
    
    private Long id;
    
    private String auctionPicturePictureFileID;
     
    private Date dateAdded;
    
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuctionPicturePictureFileID() {
        return auctionPicturePictureFileID;
    }

    public void setAuctionPicturePictureFileID(String auctionPicturePictureFileID) {
        this.auctionPicturePictureFileID = auctionPicturePictureFileID;
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
    
    public String getPicturePath(){
        return WebConstrants.AUCTION_PIC_WEB_ROUTE+this.auctionPicturePictureFileID;
    }  
}

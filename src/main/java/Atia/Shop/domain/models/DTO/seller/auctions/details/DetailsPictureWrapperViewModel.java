/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.auctions.details;

import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.domain.models.DTO.BasePresenter;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DetailsPictureWrapperViewModel extends BasePresenter{
    
    private Long id;
    
    private Long auctionedItemId;
    
    private String auctionPicturePictureFileID;
    
    private String auctionedItemName;
     
    private Date dateAdded;
    
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuctionedItemId() {
        return auctionedItemId;
    }

    public void setAuctionedItemId(Long auctionedItemId) {
        this.auctionedItemId = auctionedItemId;
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
        this.description = this.trimToSmallField(description);
    }
    
    public String getPicturePath(){
        return WebConstrants.AUCTION_PIC_WEB_ROUTE+this.auctionPicturePictureFileID;
    }

    public String getAuctionedItemName() {
        return auctionedItemName;
    }

    public void setAuctionedItemName(String auctionedItemName) {
        this.auctionedItemName = this.trimToSmallField(auctionedItemName);
    }
    
    
    
    
    
}

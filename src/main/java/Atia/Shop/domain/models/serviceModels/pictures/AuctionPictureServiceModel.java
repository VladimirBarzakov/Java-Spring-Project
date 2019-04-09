/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels.pictures;

import Atia.Shop.config.constants.WebConstrants;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AuctionPictureServiceModel {
    
    private Long id;
    
    private String pictureFileID;
    
   
    private int usageCounter;

    public AuctionPictureServiceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPictureFileID() {
        return pictureFileID;
    }

    public void setPictureFileID(String pictureFileID) {
        this.pictureFileID = pictureFileID;
    }

    public int getUsageCounter() {
        return usageCounter;
    }

    public void setUsageCounter(int usageCounter) {
        this.usageCounter = usageCounter;
    }
    
    public String getPicturePath(){
        return WebConstrants.AUCTION_PIC_WEB_ROUTE+this.pictureFileID;
    }  
}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities.pictures;

import Atia.Shop.domain.entities.base.BaseEntityLongID;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "auction_pictures")
public class AuctionPicture extends BaseEntityLongID implements Serializable{
    
    @Column(name = "picture_file_id", nullable = false, updatable = false, unique = true)
    private String pictureFileID;
    
    @Column(name="usage_counter", nullable = false, updatable=true)
    private int usageCounter;

    public AuctionPicture() {
        this.usageCounter=0;
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
    
    

}

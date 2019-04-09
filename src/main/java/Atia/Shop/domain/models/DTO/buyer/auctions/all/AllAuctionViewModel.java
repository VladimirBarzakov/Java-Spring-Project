/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.buyer.auctions.all;

import Atia.Shop.domain.models.DTO.BasePresenter;
import Atia.Shop.domain.models.serviceModels.pictures.AuctionPictureServiceModel;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class AllAuctionViewModel extends BasePresenter{
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private String sellerName;
    
    private Date dateStarted;  
    
    private Date dateExpired;
       
    private BigDecimal initialPrice;
    
    private AuctionPictureServiceModel thumbnail;

    public AllAuctionViewModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = this.trimToBigField(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = this.trimToBigField(description);
    }
    
    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }

    public AuctionPictureServiceModel getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(AuctionPictureServiceModel thumbnail) {
        this.thumbnail = thumbnail;
    }
    

}

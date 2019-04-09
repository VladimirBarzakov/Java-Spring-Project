/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.buyer.auctions.details;

import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.models.DTO.BasePresenter;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DetailsAuctionViewModel extends BasePresenter{
    private Long id;
    
    private String title;
    
    private String description;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy-HH:mm")
    private Date dateStarted;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy-HH:mm")
    private Date dateExpired;
    
    private BigDecimal initialPrice;
    
    private BigDecimal allItemsPrice;
    
    private BigDecimal bestBidAmount;
    
    private AuctionStrategy auctionStrategy;
    
    private List<DetailsAucItemViewModel> auctionedItems;
    
    private List<DetailsPictureWrapperViewModel> aucItemPictures;
    
    private boolean isOpenBidding;
    
    private String status;
    
    private UserServiceModel seller;
    
    
    public DetailsAuctionViewModel() {
        this.auctionedItems=new ArrayList();
        this.aucItemPictures=new ArrayList();
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
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


    public List<DetailsAucItemViewModel> getAuctionedItems() {
        return auctionedItems;
    }

    public void setAuctionedItems(List<DetailsAucItemViewModel> auctionedItems) {
        this.auctionedItems = auctionedItems;
        this.setAllItemsPrice(this.aggregatePrice(
                auctionedItems.stream().map(x->x.getItemPrice()).collect(Collectors.toList()), 
                auctionedItems.stream().map(x->x.getQuantity()).collect(Collectors.toList())));
    }

    public BigDecimal getAllItemsPrice() {
        return allItemsPrice;
    }

    public void setAllItemsPrice(BigDecimal allItemsPrice) {
        this.allItemsPrice = allItemsPrice;
    }

    public List<DetailsPictureWrapperViewModel> getAucItemPictures() {
        return aucItemPictures;
    }

    public void setAucItemPictures(List<DetailsPictureWrapperViewModel> aucItemPictures) {
        this.aucItemPictures = aucItemPictures;
    }

    public BigDecimal getBestBidAmount() {
        return bestBidAmount;
    }

    public void setBestBidAmount(BigDecimal bestBidAmount) {
        this.bestBidAmount = bestBidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AuctionStrategy getAuctionStrategy() {
        return auctionStrategy;
    }

    public void setAuctionStrategy(AuctionStrategy auctionStrategy) {
        this.auctionStrategy = auctionStrategy;
    }

    public boolean isIsOpenBidding() {
        return isOpenBidding;
    }

    public void setIsOpenBidding(boolean isOpenBidding) {
        this.isOpenBidding = isOpenBidding;
    }

    public UserServiceModel getSeller() {
        return seller;
    }

    public void setSeller(UserServiceModel seller) {
        this.seller = seller;
    }
}

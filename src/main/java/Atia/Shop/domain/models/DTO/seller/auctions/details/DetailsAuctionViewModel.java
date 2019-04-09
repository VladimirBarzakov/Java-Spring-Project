/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.auctions.details;

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

    private BigDecimal BestBidAmount;

    private List<DetailsAucItemViewModel> auctionedItems;
    
    private boolean isAuctionEditable;
    
    List<DetailsPictureWrapperViewModel> aucItemPictures;
    
    private UserServiceModel auctionWinner;
    
    private AuctionStrategy auctionStrategy;
    
    private BigDecimal winPrice;

    public DetailsAuctionViewModel() {
        this.auctionedItems = new ArrayList();
        this.aucItemPictures = new ArrayList();
        this.isAuctionEditable=false;
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

    public BigDecimal getBestBidAmount() {
        return BestBidAmount;
    }

    public void setBestBidAmount(BigDecimal BestBidAmount) {
        this.BestBidAmount = BestBidAmount;
    }

    public boolean isIsAuctionEditable() {
        return isAuctionEditable;
    }

    public void setIsAuctionEditable(boolean isAuctionEditable) {
        this.isAuctionEditable = isAuctionEditable;
    }

    public List<DetailsPictureWrapperViewModel> getAucItemPictures() {
        return aucItemPictures;
    }

    public void setAucItemPictures(List<DetailsPictureWrapperViewModel> aucItemPictures) {
        this.aucItemPictures = aucItemPictures;
    }

    public UserServiceModel getAuctionWinner() {
        return auctionWinner;
    }

    public void setAuctionWinner(UserServiceModel auctionWinner) {
        this.auctionWinner = auctionWinner;
    }

    public BigDecimal getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    public AuctionStrategy getAuctionStrategy() {
        return auctionStrategy;
    }

    public void setAuctionStrategy(AuctionStrategy auctionStrategy) {
        this.auctionStrategy = auctionStrategy;
    }
    
    
    
}

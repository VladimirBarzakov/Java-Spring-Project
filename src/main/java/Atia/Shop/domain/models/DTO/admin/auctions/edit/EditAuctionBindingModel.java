/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.auctions.edit;

import Atia.Shop.config.validation.ValidProperties;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.models.DTO.BasePresenter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class EditAuctionBindingModel extends BasePresenter{

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    private Long id;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Length(min = ValidProperties.AUCTION_TITLE_MIN_LENGHT, max = ValidProperties.AUCTION_TITLE_MAX_LENGHT, message = ValidationMesseges.AUCTION_TITLE)
    private String title;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @Length(min = ValidProperties.AUCTION_DESCRIPTION_MIN_LENGHT, max = ValidProperties.AUCTION_DESCRIPTION_MAX_LENGHT, message = ValidationMesseges.AUCTION_DESCRIPTION)
    private String description;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date dateStarted;

    @NotNull(message = ValidationMesseges.NULL_VALUE)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date dateExpired;

    @DecimalMin(value = ValidProperties.DECIMAL_MIN, message = ValidationMesseges.NEGATIVE_VALUE)
    @Digits(integer = ValidProperties.DECIMAL_DIGITS, fraction = ValidProperties.DECIMAL_SCALE, message = ValidationMesseges.MONEY_FORMAT)
    private BigDecimal initialPrice;

    private BigDecimal allItemsPrice;
    
    @NotNull(message=ValidationMesseges.NULL_VALUE)
    private AuctionStrategy auctionStrategy;

    private List<EditAucItemsBindingModel> auctionedItems;

    private List<EditItemBindingModel> availableItems;

    public EditAuctionBindingModel() {
        this.auctionedItems = new ArrayList();
        this.availableItems = new ArrayList();
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

    public List<EditAucItemsBindingModel> getAuctionedItems() {
        return auctionedItems;
    }

    public void setAuctionedItems(List<EditAucItemsBindingModel> auctionedItems) {
        this.auctionedItems = auctionedItems;
        this.setAllItemsPrice(this.aggregatePrice(
                auctionedItems.stream().map(x->x.getItemPrice()).collect(Collectors.toList()), 
                auctionedItems.stream().map(x->x.getQuantity()).collect(Collectors.toList())));
    }

    public List<EditItemBindingModel> getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(List<EditItemBindingModel> availableItems) {
        this.availableItems = availableItems;
    }

    public BigDecimal getAllItemsPrice() {
        return allItemsPrice;
    }

    public void setAllItemsPrice(BigDecimal allItemsPrice) {
        this.allItemsPrice = allItemsPrice;
    }

    public AuctionStrategy getAuctionStrategy() {
        return auctionStrategy;
    }

    public void setAuctionStrategy(AuctionStrategy auctionStrategy) {
        this.auctionStrategy = auctionStrategy;
    }
}

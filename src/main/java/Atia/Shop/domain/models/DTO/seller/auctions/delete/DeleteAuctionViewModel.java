/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.auctions.delete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DeleteAuctionViewModel {

    private Long id;

    private String title;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date dateStarted;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date dateExpired;

    private BigDecimal initialPrice;

    private BigDecimal allItemsPrice;

    private BigDecimal BestBidAmount;

    private List<DeleteAucItemViewModel> auctionedItems;

    public DeleteAuctionViewModel() {
        this.auctionedItems = new ArrayList();
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

    public List<DeleteAucItemViewModel> getAuctionedItems() {
        return auctionedItems;
    }

    public void setAuctionedItems(List<DeleteAucItemViewModel> auctionedItems) {
        this.auctionedItems = auctionedItems;
        this.calcPriceOfAllItems();
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

    private void calcPriceOfAllItems() {
        BigDecimal sum = BigDecimal.ZERO;
        for (DeleteAucItemViewModel aucItem : auctionedItems) {
            sum = sum.add(
                    aucItem.getItemPrice() == null ? BigDecimal.ZERO : aucItem.getItemPrice()
                    .multiply(new BigDecimal(aucItem.getQuantity())));
        }
        this.setAllItemsPrice(sum);
    }
}

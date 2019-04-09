/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.items.delete;

import Atia.Shop.domain.models.DTO.BasePresenter;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class DeleteAucItemViewModel extends BasePresenter{

    private Long id;

    private String auctionTitle;

    private Integer quantity;

    private BigDecimal auctionInitialPrice;
    
    private Date auctionDateStarted;
    
    private Date auctionDateExpired;

    private String auctionStatus;
    
    private Long auctionId;

    public DeleteAucItemViewModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuctionTitle() {
        return auctionTitle;
    }

    public void setAuctionTitle(String auctionTitle) {
        this.auctionTitle = this.trimToSmallField(auctionTitle);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAuctionInitialPrice() {
        return auctionInitialPrice;
    }

    public void setAuctionInitialPrice(BigDecimal auctionInitialPrice) {
        this.auctionInitialPrice = auctionInitialPrice;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public Date getAuctionDateStarted() {
        return auctionDateStarted;
    }

    public void setAuctionDateStarted(Date auctionDateStarted) {
        this.auctionDateStarted = auctionDateStarted;
    }

    public Date getAuctionDateExpired() {
        return auctionDateExpired;
    }

    public void setAuctionDateExpired(Date auctionDateExpired) {
        this.auctionDateExpired = auctionDateExpired;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }


}

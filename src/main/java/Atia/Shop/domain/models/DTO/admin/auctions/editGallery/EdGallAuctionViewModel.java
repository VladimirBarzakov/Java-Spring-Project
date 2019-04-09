/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.auctions.editGallery;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class EdGallAuctionViewModel {
    
    private Long id;
    
    private List<EdGallAucPicWrapViewModel> auctionPictures;
    
    private List<EdGallAucItemViewModel> aucItems;

    public EdGallAuctionViewModel() {
        this.auctionPictures = new ArrayList();
        this.aucItems=new ArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EdGallAucPicWrapViewModel> getAuctionPictures() {
        return auctionPictures;
    }

    public void setAuctionPictures(List<EdGallAucPicWrapViewModel> auctionPictures) {
        this.auctionPictures = auctionPictures;
    }

    public List<EdGallAucItemViewModel> getAucItems() {
        return aucItems;
    }

    public void setAucItems(List<EdGallAucItemViewModel> aucItems) {
        this.aucItems = aucItems;
    }

    

    

  
}

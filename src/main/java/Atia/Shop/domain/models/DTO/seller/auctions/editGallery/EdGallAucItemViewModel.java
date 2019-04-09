/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.seller.auctions.editGallery;



/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class EdGallAucItemViewModel {
    
    private Long id;
    
    private String itemName;
    
    private Long parentId;

    public EdGallAucItemViewModel() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    
    
}

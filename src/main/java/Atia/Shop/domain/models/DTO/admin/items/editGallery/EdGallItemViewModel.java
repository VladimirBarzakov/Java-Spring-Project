/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.items.editGallery;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class EdGallItemViewModel {
    
    private List<EdGallItemPictureViewModel> itemPictures;

    public EdGallItemViewModel() {
        this.itemPictures=new ArrayList();
    }
    
    public List<EdGallItemPictureViewModel> getItemPictures() {
        return itemPictures;
    }

    public void setItemPictures(List<EdGallItemPictureViewModel> itemPictures) {
        this.itemPictures = itemPictures;
    }
    
    public void addPicture(EdGallItemPictureViewModel itemPictureGalleryViewModel){
        this.itemPictures.add(itemPictureGalleryViewModel);
    }
    
    
    
}

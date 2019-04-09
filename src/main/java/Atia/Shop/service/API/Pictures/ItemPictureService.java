/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.Pictures;

import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.service.API.Pictures.Base.PictureCreatable;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface ItemPictureService extends PictureCreatable<ItemPictureServiceModel, ItemServiceModel>{
    
    boolean deleteAllPicturesByItem(ItemServiceModel item);
    
}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.Pictures;

import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.service.API.Pictures.Base.PictureConsumable;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface AuctionPictureService extends PictureConsumable<AucItemPictureWrapperServiceModel, AuctionServiceModel>{
    
    boolean deleteAllPicturesFromAuction(AuctionServiceModel auction);
    
    AucItemPictureWrapperServiceModel getPictureWrapperById(Long id);
    
}

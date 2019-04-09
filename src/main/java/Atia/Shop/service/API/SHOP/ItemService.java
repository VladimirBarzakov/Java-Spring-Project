/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.SHOP;

import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.exeptions.base.ReportToUserException;
import java.util.List;
import Atia.Shop.service.API.Pictures.Base.PictureCreatable;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface ItemService extends PictureCreatable<ItemPictureServiceModel, ItemServiceModel>{
    
    ItemServiceModel createItem(ItemServiceModel itemtoCreate, UserServiceModel seller);
    
    List<ItemServiceModel> getAllItemsByCreator(UserServiceModel seller, UserRolesEnum role);
    
    List<ItemServiceModel> getAllAvailableItems(UserServiceModel seller, UserRolesEnum role);
    
    List<ItemServiceModel> getAllItems(UserRolesEnum role);
    
    ItemServiceModel getItemById(Long itemId, String sellerEmail, UserRolesEnum role);
    
    public boolean updateItem(ItemServiceModel updatedItem, String sellerEmail, UserRolesEnum role);
    
    public boolean deleteItemById(Long itemId, String sellerEmail, UserRolesEnum role);
    
    public boolean isItemDeletable(Long itemId);
    
    public boolean isItemEditable(Long itemId);

    boolean addToAuction(List<ItemServiceModel> itemstoAdd, AuctionServiceModel auctionToAddTo)  throws ReportToUserException;
    
    AuctionedItemServiceModel getAucItemById(Long aucItemId);
    
    public boolean removeAllFromAuction(List<AuctionedItemServiceModel> aucItemsToremove, boolean deleteAucItemsFlag);
    
    List<AuctionServiceModel> getAllAuctionsByItemId(Long itemId);
    
    List<AuctionedItemServiceModel> findAllAucItemsByAuctionId(Long auctionId);
    
    public List<AuctionedItemServiceModel> getAllAucItemsByItemId(Long itemId);
}

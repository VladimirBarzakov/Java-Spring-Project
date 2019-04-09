/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.SHOP;

import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.Archive.Archivable;
import Atia.Shop.service.API.Pictures.Base.PictureConsumable;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface AuctionService extends PictureConsumable<AucItemPictureWrapperServiceModel, AuctionServiceModel>,Archivable<AuctionServiceModel, Long>{
    
    AuctionServiceModel createAuction(AuctionServiceModel auctionToCreate, String sellerEmail) throws ReportToUserException;
    
    List<AuctionServiceModel> getAllAuctionsBySeller(String sellerEmail, UserRolesEnum role);
    
    List<AuctionServiceModel> getAllAuctionsByWinner(String sellerEmail);
    
    AuctionServiceModel getAuctionById(Long id, String userEmail, UserRolesEnum role);
    
    AuctionServiceModel getAuctionById(Long id);
    
    boolean updateAuction(
            AuctionServiceModel updatedsAuction, 
            List<ItemServiceModel> itemtsToAdd, 
            List<AuctionedItemServiceModel> aucItemstoRemove) throws ReportToUserException;
    
    boolean updateAuction(
            AuctionServiceModel updatedAuction, 
            List<ItemServiceModel> itemtsToAdd, 
            List<AuctionedItemServiceModel> aucItemstoRemove, 
            String sellerEmail, 
            UserRolesEnum role) throws ReportToUserException;
    
    List<AuctionServiceModel> getAllAuctionsByStatus(AuctionStatus auctionStatus);
    
    boolean isAuctionEditable(Long id);
    
    boolean isAuctionEditable(AuctionServiceModel auction);
    
    boolean isAuctionDeletable(Long id);
    
    boolean isAuctionDeletable(AuctionServiceModel auction);
    
    boolean isAuctionBiddable(AuctionServiceModel auction);
    
    boolean isAuctionWinned(AuctionServiceModel auction);
    
    boolean verifyAuctionWinner(String userEmail, AuctionServiceModel auction);
    
    boolean deleteAuction(AuctionServiceModel auction, String sellerEmail, UserRolesEnum role);
    
    boolean deleteAllAuctions(List<AuctionServiceModel> auctions);
    
    boolean saveAll(List<AuctionServiceModel> auctions);
    
    boolean isAuctionEmpty(Long id);
    
    boolean expireAuction(AuctionServiceModel auction);
    
    List<AuctionServiceModel> getAllAuctions();
    
}

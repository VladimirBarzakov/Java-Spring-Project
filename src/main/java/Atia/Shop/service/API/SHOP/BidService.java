/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.SHOP;

import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.exeptions.base.ReportToUserException;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface BidService {
    
    BidServiceModel placeBid(BidServiceModel bibServiceModel) throws ReportToUserException;
    
    List<BidServiceModel> getAllByAuctionId(Long auctionId);
    
    List<BidServiceModel> getALLByBidderMail(String bidderMail);
    
    List<BidServiceModel> adminGetALL();
    
    boolean deleteSingleBid(Long bidId);
    
    boolean deleteAllBidsOfAuction(AuctionServiceModel auction);
    
    boolean deleteAllBidsOfArchiveAuction(AuctionServiceModel auction);
    
}

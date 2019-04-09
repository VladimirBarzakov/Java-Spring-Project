/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories;


import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.AuctionedItem;
import Atia.Shop.domain.entities.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Repository
public interface AuctionedItemsRepository extends JpaRepository<AuctionedItem, Long> {
    
    List<AuctionedItem> findAllByAuction(Auction auction);
    
    AuctionedItem findOneByParentAndAuction(Item parent, Auction auction);
    
    AuctionedItem findOneById(Long id);
    
    List<AuctionedItem> findAllByParentId(Long id);
    
    List<AuctionedItem> findAllByAuctionId(Long id);
    
    
    
    
}

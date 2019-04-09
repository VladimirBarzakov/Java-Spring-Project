/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories;

import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.Bid;
import Atia.Shop.domain.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    
    List<Bid> getAllByAuction(Auction auctionId);
    
    List<Bid> getAllByBidder(User bidder);
}

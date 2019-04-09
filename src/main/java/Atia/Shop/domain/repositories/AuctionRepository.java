/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories;

import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    

    List<Auction> findAllBySellerAndStatusNot(User seller, AuctionStatus status);
    
    List<Auction> findAllBySellerAndStatus(User seller, AuctionStatus status);
    
    List<Auction> findByStatusNot(AuctionStatus status);
    
    List<Auction> findAllByAuctionWinner(User auctionWinner);

    List<Auction> findAllByStatus(AuctionStatus status);

}

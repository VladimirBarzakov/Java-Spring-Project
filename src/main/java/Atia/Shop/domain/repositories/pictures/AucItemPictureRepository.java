/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories.pictures;


import Atia.Shop.domain.entities.pictures.AucItemPictureWrapper;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Repository
public interface AucItemPictureRepository extends JpaRepository<AucItemPictureWrapper, Long>{
    
    List<AucItemPictureWrapper> findAllByAuctionedItemId(Long auctionedItemId);
    
    List<AucItemPictureWrapper> findAllByAuctionId(Long auctionId);
    
}

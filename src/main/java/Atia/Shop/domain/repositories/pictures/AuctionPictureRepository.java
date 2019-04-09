/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories.pictures;

import Atia.Shop.domain.entities.pictures.AuctionPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Repository
public interface AuctionPictureRepository extends JpaRepository<AuctionPicture, Long>{

    public AuctionPicture findByPictureFileID(String pictureFileId);
    
    
}

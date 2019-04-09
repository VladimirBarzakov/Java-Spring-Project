/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories.pictures;

import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.pictures.ItemPicture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Repository
public interface ItemPictureRepository extends JpaRepository<ItemPicture, Long> {
    
    List<ItemPicture> findAllByItem(Item item);
}

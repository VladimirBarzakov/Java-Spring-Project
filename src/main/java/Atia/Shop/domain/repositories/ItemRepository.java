/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories;

import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    List<Item> findAllBySeller(User seller);
    
    List<Item> findAllBySellerAndQuantityGreaterThan(User seller, Integer quantity);
    
    Item getItemById(Long id);  
    
}

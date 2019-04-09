/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories;

import Atia.Shop.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
        User findByDomainEmail(String domainEmail);
}

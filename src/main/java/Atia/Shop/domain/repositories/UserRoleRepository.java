/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.repositories;

import Atia.Shop.domain.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Repository
public interface UserRoleRepository  extends JpaRepository<UserRole, String> {
    
    UserRole findOneByAuthority(String authority);
    
}

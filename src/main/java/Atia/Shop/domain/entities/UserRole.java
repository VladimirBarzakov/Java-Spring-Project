/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities;

import Atia.Shop.domain.entities.base.BaseEntityUUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "user_roles_table")
public class UserRole extends BaseEntityUUID implements GrantedAuthority {

    @Column(name = "authority", nullable = false, unique = true)
    private String authority;

    public UserRole() {
    }
    
    public UserRole(String authority) {
        this.setAuthority(authority);
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

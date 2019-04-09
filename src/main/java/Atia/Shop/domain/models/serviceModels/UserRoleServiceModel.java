/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class UserRoleServiceModel {
    
    private String id;
    
    private String authority;

    public UserRoleServiceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

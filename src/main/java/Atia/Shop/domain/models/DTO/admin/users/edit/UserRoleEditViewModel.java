/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.users.edit;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class UserRoleEditViewModel {
    private String id;
    
    private String authority;
    
    private boolean flag;

    public UserRoleEditViewModel() {
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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    
    
}

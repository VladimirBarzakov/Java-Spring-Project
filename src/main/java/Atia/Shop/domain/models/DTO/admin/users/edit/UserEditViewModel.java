/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.users.edit;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class UserEditViewModel {
    
    private String id;
    
    private String domainEmail;
    
    private String name;
    
    private List<UserRoleEditViewModel> allAuthorities;

    public UserEditViewModel() {
        this.allAuthorities=new ArrayList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomainEmail() {
        return domainEmail;
    }

    public void setDomainEmail(String domainEmail) {
        this.domainEmail = domainEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserRoleEditViewModel> getAllAuthorities() {
        return allAuthorities;
    }

    public void setAllAuthorities(List<UserRoleEditViewModel> allAuthorities) {
        this.allAuthorities = allAuthorities;
    }

}

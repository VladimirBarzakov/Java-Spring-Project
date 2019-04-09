/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.users.all;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class UserViewModel {
    
    private String id;
    
    private String domainEmail;
    
    private String name;
    
    private List<String> authorities;

    public UserViewModel() {
        this.authorities= new ArrayList<>();
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

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
    
    
    
    
}

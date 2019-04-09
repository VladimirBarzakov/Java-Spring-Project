/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.DTO.admin.users.details;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class UserDetailsViewModel {
    
    private String id;
    
    private String domainEmail;
    
    private String name;
    
    private List<String> authorities;
    
    private List<AuctionViewModel> createdAuctions;
    
    private List<AuctionViewModel> winnedAuctions;

    public UserDetailsViewModel() {
        this.authorities= new ArrayList<>();
        this.createdAuctions=new ArrayList<>();
        this.winnedAuctions = new ArrayList<>();
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

    public List<AuctionViewModel> getCreatedAuctions() {
        return createdAuctions;
    }

    public void setCreatedAuctions(List<AuctionViewModel> createdAuctions) {
        this.createdAuctions = createdAuctions;
    }

    public List<AuctionViewModel> getWinnedAuctions() {
        return winnedAuctions;
    }

    public void setWinnedAuctions(List<AuctionViewModel> winnedAuctions) {
        this.winnedAuctions = winnedAuctions;
    }
    
    
}

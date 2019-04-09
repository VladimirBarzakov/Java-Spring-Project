/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.models.serviceModels;

import Atia.Shop.config.validation.ValidProperties;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public class UserServiceModel {
    
    private String id;
    
    @Length(max=ValidProperties.USER_DOMAIN_MAIL_LENGHT)
    @Pattern(regexp=ValidProperties.USER_DOMAIN_MAIL_PATTERN)
    private String domainEmail;
    
    @Length(min=ValidProperties.USER_NAME_MIN_LENGHT, max=ValidProperties.USER_NAME_MAX_LENGHT)
    @Pattern(regexp=ValidProperties.USER_NAME_PATTERN)
    private String name;
    

    public UserServiceModel() {
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

}

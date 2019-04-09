/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.domain.entities;

import Atia.Shop.domain.entities.base.BaseEntityUUID;
import Atia.Shop.config.validation.ValidProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Entity
@Table(name = "users")
public class User extends BaseEntityUUID implements Serializable, UserDetails {

    @NotEmpty
    @Length(max=ValidProperties.USER_DOMAIN_MAIL_LENGHT)
    @Pattern(regexp = ValidProperties.USER_DOMAIN_MAIL_PATTERN)
    @Column(name = "domain_email", nullable = false, updatable = false, unique = true, length = ValidProperties.USER_DOMAIN_MAIL_LENGHT)
    private String domainEmail;

    @NotEmpty
    @Length(min=ValidProperties.USER_NAME_MIN_LENGHT, max=ValidProperties.USER_NAME_MAX_LENGHT)
    @Pattern(regexp = ValidProperties.USER_NAME_PATTERN)
    @Column(name = "name", nullable = false, updatable = true, length = ValidProperties.USER_NAME_MAX_LENGHT)
    private String name;

    @Column(name = "is_active", nullable = false, updatable = true)
    private Boolean isActive;

    @ManyToMany(cascade = CascadeType.ALL,
             targetEntity = UserRole.class,
             fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_to_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> authorities;

    @Column(name = "test_password", nullable = false, updatable = true)
    private String testPassword;

    public User() {
        this.authorities = new HashSet();
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getTestPassword() {
        return testPassword;
    }

    public void setTestPassword(String testPassword) {
        this.testPassword = testPassword;
    }

    @Override
    public Set<UserRole> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<UserRole> authorities) {
        this.authorities = authorities;
    }

    //Implementation of UserDetails
    @Override
    public String getPassword() {
        return this.testPassword;
    }

    @Override
    public String getUsername() {
        return this.domainEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isActive;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

}

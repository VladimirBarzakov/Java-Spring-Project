/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.config.userRoles;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public enum UserRolesEnum {
    ROOT("ROOT"),
    ADMIN("ADMIN"),
    USER("USER"),
    SELLER("SELLER"),
    BUYER("BUYER");
    
    
    UserRolesEnum(String role){
        this.role=role;
    }
    
    private final String role;

    public String getRole() {
        return role;
    }
    
    public boolean isRole(UserRolesEnum other){
        return this.role.equals(other.getRole());
    }

}

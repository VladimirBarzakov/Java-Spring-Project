/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.API.Users;


import Atia.Shop.domain.models.serviceModels.UserRoleServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.exeptions.base.ReportToUserException;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
public interface UserService extends UserDetailsService {
    
    UserServiceModel registerUser(UserServiceModel userBindingModel,String password)throws ReportToUserException;
    
    UserServiceModel getUserByDomainEmail(String domainEmail);
    
    void authenticateUser(String domainEmail);
    
    List<UserServiceModel> getAllUsers();
    
    UserServiceModel getUserById(String id);
    
    List<UserRoleServiceModel> getAllAvailableRoles();
    
    List<UserRoleServiceModel> getAllUserRolesById(String userId);
    
    boolean editUserRoles(String userId, String adminEmail, Set<UserRoleServiceModel> newRoles);
    
}


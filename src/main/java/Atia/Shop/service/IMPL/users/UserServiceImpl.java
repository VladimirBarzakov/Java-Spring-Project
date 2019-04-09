/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.users;

import Atia.Shop.config.errorMesseges.SystemExceptionMessage;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.UserRole;
import Atia.Shop.domain.models.serviceModels.UserRoleServiceModel;
import Atia.Shop.exeptions.MyUserServiceExeption;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.domain.repositories.UserRepository;
import Atia.Shop.domain.repositories.UserRoleRepository;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final InputValidator validationUtil;
    private static Boolean IS_DB_EMPTY;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
            ModelMapper modelMapper,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserRoleRepository userRoleRepository,
            InputValidator validationUtil) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.validationUtil=validationUtil;

        UserServiceImpl.IS_DB_EMPTY = this.isDBEmpty();
        this.populateAllRoles();
    }

    private void populateAllRoles() {
        for (UserRolesEnum enumRole : UserRolesEnum.values()) {
            UserRole role = this.userRoleRepository.findOneByAuthority(enumRole.getRole());
            if (role == null) {
                this.createUserRole(enumRole.getRole());
            }
        }
    }

    private Boolean isDBEmpty() {
        if (this.userRepository.count()==0) {
            return null;
        } else {
            return false;
        }
    }

    private void polulateUserRoles(User user) {
        Set<UserRole> authorities = user.getAuthorities();
        if (UserServiceImpl.IS_DB_EMPTY == null) {
            authorities.add(this.getUserRole(UserRolesEnum.ROOT.getRole()));
            authorities.add(this.getUserRole(UserRolesEnum.ADMIN.getRole()));
            UserServiceImpl.IS_DB_EMPTY = false;
        }
        authorities.add(this.getUserRole(UserRolesEnum.USER.getRole()));
        user.setAuthorities(authorities);
    }

    private void initBuisnessRights(User user) {
        Set<UserRole> authorities = user.getAuthorities();
        if (this.hasRole(user, UserRolesEnum.ADMIN.getRole())) {
            authorities.add(this.getUserRole(UserRolesEnum.SELLER.getRole()));
        }
        authorities.add(this.getUserRole(UserRolesEnum.BUYER.getRole()));
        user.setIsActive(true);
    }

    private UserRole getUserRole(String roleString) {
        return this.userRoleRepository.findOneByAuthority(roleString);
    }

    private UserRole createUserRole(String roleString) {
        UserRole role = new UserRole(roleString);
        role = this.userRoleRepository.save(role);
        return role;
    }

    private boolean hasRole(User user, String roleName) {
        return user.getAuthorities().stream().anyMatch((role) -> (role.getAuthority().equals(roleName)));
    }
    
    private User getByMail(String userMail){
        this.validationUtil.validateString(userMail);
        User user = this.userRepository.findByDomainEmail(userMail);
        if(user==null){
            throw new ReporPartlyToUserException(SystemExceptionMessage.NO_USER_WITH_SUCH_EMAIL);
        }
        return user;
    }

    private User getUserEntityById(String userId) {
        this.validationUtil.validateString(userId);
        User user = this.userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.NO_USER_WITH_SUCH_ID);
        }
        return user;
    }

    @Override
    public UserServiceModel registerUser(UserServiceModel userBindingModel, String password) throws ReportToUserException {
        this.validationUtil.validateObject(userBindingModel);
        this.validationUtil.validateString(password, userBindingModel.getDomainEmail());
        User user = this.userRepository.findByDomainEmail(userBindingModel.getDomainEmail());
        if (user != null) {
            throw new MyUserServiceExeption(ValidationMesseges.INVALID_USER_REG_UNIQUE_DOMAIN_EMAIL_MESSAGE);
        }
        user = this.modelMapper.map(userBindingModel, User.class);
        user.setTestPassword(this.bCryptPasswordEncoder.encode(password));
        this.polulateUserRoles(user);
        this.initBuisnessRights(user);
        user = this.userRepository.save(user);
        
        return this.modelMapper.map(user, UserServiceModel.class);
    }

    // This method is used by Spring Authentication service!
    @Override
    public UserDetails loadUserByUsername(String domainEmail) throws UsernameNotFoundException {
        this.validationUtil.validateString(domainEmail);
        User user = this.userRepository.findByDomainEmail(domainEmail);
        if (user == null) {
            throw new UsernameNotFoundException(SystemExceptionMessage.NO_USER_WITH_SUCH_ID);
        }
        return user;
    }

    @Override
    public UserServiceModel getUserByDomainEmail(String domainEmail) {
        this.validationUtil.validateString(domainEmail);
        User user = this.getByMail(domainEmail);
        return this.modelMapper.map(user, UserServiceModel.class);
    }

    @Override
    public void authenticateUser(String domainEmail) {
        UserDetails userDetails = this.loadUserByUsername(domainEmail);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public List<UserServiceModel> getAllUsers() {
        List<UserServiceModel> users = this.userRepository.findAll()
                .stream()
                .map(user -> this.modelMapper.map(user, UserServiceModel.class))
                .collect(Collectors.toList());
        return users;
    }

    @Override
    public UserServiceModel getUserById(String id) {
        return this.modelMapper.map(this.getUserEntityById(id), UserServiceModel.class);
    }

    @Override
    public List<UserRoleServiceModel> getAllAvailableRoles() {
        return this.userRoleRepository.findAll()
                .stream()
                .map(x -> this.modelMapper.map(x, UserRoleServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean editUserRoles(String userId, String adminEmail, Set<UserRoleServiceModel> newRoles) {
        this.validationUtil.validateString(userId,adminEmail);
        this.validationUtil.validateObject(newRoles);
        User admin = this.getByMail(adminEmail);
        if(!this.hasRole(admin,UserRolesEnum.ADMIN.getRole())){
            return false;
        }
        User user = this.getUserEntityById(userId);
        if(user.getDomainEmail().equals(adminEmail)){
            return false;
        }
        if(this.hasRole(user, UserRolesEnum.ROOT.getRole())){
            return false;
        }
        user.setAuthorities(newRoles.stream()
                        .map(x -> 
                                this.userRoleRepository.findById(x.getId()).orElseThrow(()->new ReporPartlyToUserException("No such Id!")))
                        .filter(x->!x.getAuthority().equals(UserRolesEnum.ROOT.getRole()))
                        .collect(Collectors.toSet())
        );
        this.userRepository.save(user);
        return true;
    }

    @Override
    public List<UserRoleServiceModel> getAllUserRolesById(String userId) {
        User user = this.getUserEntityById(userId);
        return user.getAuthorities()
                .stream()
                .map(x -> this.modelMapper.map(x, UserRoleServiceModel.class))
                .collect(Collectors.toList());
    }

}

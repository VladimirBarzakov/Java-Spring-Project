/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.users;

import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.models.serviceModels.UserRoleServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.repositories.UserRepository;
import Atia.Shop.domain.repositories.UserRoleRepository;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    private ModelMapper modelMapper;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private InputValidator validatuinUtil;

    private UserService userService;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.modelMapper = new ModelMapper();
        this.validatuinUtil=new InputValidator();
        this.userService = new UserServiceImpl(this.userRepository, this.modelMapper, this.bCryptPasswordEncoder, this.userRoleRepository, this.validatuinUtil);
    }

    @After
    public void tearDown() {
    }
    
    private UserServiceModel registerUser(String name, String email, String password) throws ReportToUserException{
        UserServiceModel usm = new UserServiceModel();
        usm.setName(name);
        usm.setDomainEmail(email);
        return this.userService.registerUser(usm, password);
    }

    /**
     * Test of registerUser method, of class UserServiceImpl.
     */
    @Test
    public void userService_registerUserOnEmpyDB_ShoudReturnCorrectROOT() throws Exception {
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";

        String password = "Password";

        this.registerUser(testName, emailEmail, password);

        User actual = this.userRepository.findByDomainEmail(emailEmail);

        Assert.assertEquals(testName, actual.getName());
        Assert.assertEquals(emailEmail, actual.getDomainEmail());

        Set<String> actualAuthorities = actual.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toSet());

        Assert.assertTrue(actualAuthorities.contains(UserRolesEnum.ROOT.getRole()));
        Assert.assertTrue(actualAuthorities.contains(UserRolesEnum.ADMIN.getRole()));
        Assert.assertTrue(actualAuthorities.contains(UserRolesEnum.USER.getRole()));
        Assert.assertTrue(actualAuthorities.contains(UserRolesEnum.SELLER.getRole()));
        Assert.assertTrue(actualAuthorities.contains(UserRolesEnum.BUYER.getRole()));
    }

    @Test
    public void userService_registerUserOnNonEmptyDB_ShoudReturnUserWithCorrectRights() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";;
        String password1 = "Password1";

        this.registerUser(testName1, emailEmail1, password1);

        String testName2 = "Plamen Barzakov";
        String emailEmail2 = "plamen.barzakov@atia.com";
        String password2 = "Password2";

        this.registerUser(testName2, emailEmail2, password2);

        User actual = this.userRepository.findByDomainEmail(emailEmail2);

        Assert.assertEquals(testName2, actual.getName());
        Assert.assertEquals(emailEmail2, actual.getDomainEmail());

        Set<String> actualAuthorities = actual.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toSet());

        Assert.assertTrue(!actualAuthorities.contains(UserRolesEnum.ROOT.getRole()));
        Assert.assertTrue(!actualAuthorities.contains(UserRolesEnum.ADMIN.getRole()));
        Assert.assertTrue(!actualAuthorities.contains(UserRolesEnum.SELLER.getRole()));
        Assert.assertTrue(actualAuthorities.contains(UserRolesEnum.USER.getRole()));
        Assert.assertTrue(actualAuthorities.contains(UserRolesEnum.BUYER.getRole()));
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void userService_registerUser_NullUser_ShoudThrowReporPartlyToUserException() throws Exception {
        UserServiceModel testUserSM = null;

        String password1 = "Password1";

        this.userService.registerUser(testUserSM, password1);
    }

    @Test(expected = ReportToUserException.class)
    public void userService_registerUser_DuplicateUserRegistration_ShoudThrowReportToUserException() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";
        String password1 = "Password1";
        UserServiceModel testUserSM1 = this.registerUser(testName1, emailEmail1, password1);
        
        this.userService.registerUser(testUserSM1, password1);

        this.userService.registerUser(testUserSM1, password1);
    }

    @Test
    public void userService_loadUserByUsername_WithCorrectInput_ShoudReturnSameUser() throws Exception {
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";
        String password = "Password1";

        UserServiceModel expected = this.registerUser(testName, emailEmail, password);

        UserDetails actual = this.userService.loadUserByUsername(emailEmail);

        Assert.assertEquals(expected.getDomainEmail(), actual.getUsername());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void userService_loadUserByUsername_WithNONCorrectInput_ShoudThrowUsernameNotFoundException() throws Exception {
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";
        String password = "Password1";
        this.registerUser(testName, emailEmail, password);

        this.userService.loadUserByUsername("NoSuchId");
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void userService_loadUserByUsername_WithNullInput_ShoudThrowReporPartlyToUserException() throws Exception {
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";
        String password = "Password1";
        this.registerUser(testName, emailEmail, password);
        
        this.userService.loadUserByUsername(null);
    }
    
    @Test
    public void userService_getUserByDomainEmail_WithCorrectInput_ShoudReturnSameUser() throws ReportToUserException{
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";
        String password = "Password1";
        UserServiceModel expexted = this.registerUser(testName, emailEmail, password);
        
        UserServiceModel actual = this.userService.getUserByDomainEmail(emailEmail);
        
        Assert.assertEquals(expexted.getId(), actual.getId());
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_getUserByDomainEmail_WithInvalidInput_ShoudThrowReporPartlyToUserException() throws ReportToUserException{
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";
        String password = "Password1";
        this.registerUser(testName, emailEmail, password);
        this.userService.getUserByDomainEmail("NoSuchEmail");
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_getUserByDomainEmail_WithNullInput_ShoudThrowReporPartlyToUserException() throws ReportToUserException{
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";
        String password = "Password1";
        this.registerUser(testName, emailEmail, password);
        this.userService.getUserByDomainEmail(null);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_authenticateUser_WithNullInput_ShoudThrowReporPartlyToUserException() throws ReportToUserException{
        String testName = "Vladimir Barzakov";
        String emailEmail = "vladimir.barzakov@atia.com";
        String password = "Password1";
        this.registerUser(testName, emailEmail, password);
        this.userService.authenticateUser(null);
    }
    
    @Test
    public void userService_getAllUsers_OnEmptyDB_ShoudReturnEmptyList() throws ReportToUserException{
        List<UserServiceModel> actual = this.userService.getAllUsers();
        Assert.assertEquals(0, actual.size());
    }
    
    @Test
    public void userService_getAllUsers_OnNonEmptyDB_ShoudReturnAllUsers() throws ReportToUserException{
        this.registerUser("username1", "a@atia.com", "password1");
        this.registerUser("username2", "b@atia.com", "password2");
        this.registerUser("username3", "c@atia.com", "password2");
        List<UserServiceModel> actual = this.userService.getAllUsers();
        for(UserServiceModel role:actual){
            Assert.assertNotNull(role);
        }
        Assert.assertEquals(3, actual.size());
    }
    
    @Test
    public void userService_getUserById_WithCorrectInput_ShoudReturnUser() throws ReportToUserException{
        this.registerUser("username1", "a@atia.com", "password1");
        UserServiceModel expected = this.registerUser("username2", "b@atia.com", "password2");
        this.registerUser("username3", "c@atia.com", "password2");
        
        UserServiceModel actual = this.userService.getUserById(expected.getId());
        Assert.assertEquals(expected.getId(), actual.getId());
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_getUserById_WithInvalidInput_ShoudReturnUser() throws ReportToUserException{
        this.registerUser("username1", "a@atia.com", "password1");
        this.registerUser("username2", "b@atia.com", "password2");
        this.registerUser("username3", "c@atia.com", "password2");
        
        this.userService.getUserById("NoSuchId");
    }
    
    @Test
    public void userService_getAllAvailableRoles_ShoudReturnAllRoles() throws ReportToUserException{
        List<UserRoleServiceModel> allRoles = this.userService.getAllAvailableRoles();
        for(UserRoleServiceModel role:allRoles){
            Assert.assertNotNull(role);
        }
        Assert.assertEquals(UserRolesEnum.values().length, 
                allRoles.stream().map(x->x.getAuthority()).collect(Collectors.toSet()).size());
    }
    
    @Test
    public void userService_getAllUserRolesById_ShoudReturnAllRootRolesAndAllUserRoles() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";;
        String password1 = "Password1";

        UserServiceModel root =this.registerUser(testName1, emailEmail1, password1);

        String testName2 = "Plamen Barzakov";
        String emailEmail2 = "plamen.barzakov@atia.com";
        String password2 = "Password2";

        UserServiceModel user = this.registerUser(testName2, emailEmail2, password2);

        List<UserRoleServiceModel> rootRoles = this.userService.getAllUserRolesById(root.getId());
        List<UserRoleServiceModel> userRoles = this.userService.getAllUserRolesById(user.getId());

        Set<String> rootRolesSet = rootRoles.stream().map(x -> x.getAuthority()).collect(Collectors.toSet());
        Set<String> userRolesSet = userRoles.stream().map(x -> x.getAuthority()).collect(Collectors.toSet());

        Assert.assertTrue(rootRolesSet.contains(UserRolesEnum.ROOT.getRole()));
        Assert.assertTrue(rootRolesSet.contains(UserRolesEnum.ADMIN.getRole()));
        Assert.assertTrue(rootRolesSet.contains(UserRolesEnum.SELLER.getRole()));
        Assert.assertTrue(rootRolesSet.contains(UserRolesEnum.USER.getRole()));
        Assert.assertTrue(rootRolesSet.contains(UserRolesEnum.BUYER.getRole()));
        
        Assert.assertTrue(!userRolesSet.contains(UserRolesEnum.ROOT.getRole()));
        Assert.assertTrue(!userRolesSet.contains(UserRolesEnum.ADMIN.getRole()));
        Assert.assertTrue(!userRolesSet.contains(UserRolesEnum.SELLER.getRole()));
        Assert.assertTrue(userRolesSet.contains(UserRolesEnum.USER.getRole()));
        Assert.assertTrue(userRolesSet.contains(UserRolesEnum.BUYER.getRole()));
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_getAllUserRolesById_WithNullValueShouldThrowReporPartlyToUserException() throws ReportToUserException{
        this.registerUser("username1", "a@atia.com", "password1");
        this.userService.getAllUserRolesById(null);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_getAllUserRolesById_WithIbvalidValueShouldThrowReporPartlyToUserException() throws ReportToUserException{
        this.registerUser("username1", "a@atia.com", "password1");
        this.userService.getAllUserRolesById("NoSuchId");
    }
    
    @Test
    public void userService_editUserRoles_AdminShouldChangeUserRoles() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";
        String password1 = "Password1";
        UserServiceModel root =this.registerUser(testName1, emailEmail1, password1);

        String testName2 = "Plamen Barzakov";
        String emailEmail2 = "plamen.barzakov@atia.com";
        String password2 = "Password2";
        UserServiceModel user = this.registerUser(testName2, emailEmail2, password2);

        List<UserRoleServiceModel> userRoles = this.userService.getAllUserRolesById(user.getId());
        Set<String> oldUserRolesSet = userRoles.stream().map(x -> x.getAuthority()).collect(Collectors.toSet());
        Assert.assertTrue(!oldUserRolesSet.contains(UserRolesEnum.ADMIN.getRole()));
        
        List<UserRoleServiceModel> allRoles = this.userService.getAllAvailableRoles();
        Set<UserRoleServiceModel> expected = allRoles.stream().filter(x->x.getAuthority().equals(UserRolesEnum.ADMIN.getRole())).collect(Collectors.toSet());
        this.userService.editUserRoles(user.getId(), root.getDomainEmail(), expected);
        
        List<UserRoleServiceModel> actual = this.userService.getAllUserRolesById(user.getId());
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(UserRolesEnum.ADMIN.getRole(), actual.get(0).getAuthority());
    }
    
    @Test
    public void userService_editUserRoles_AdminNotChangeHisOwnRoles() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";
        String password1 = "Password1";
        UserServiceModel root =this.registerUser(testName1, emailEmail1, password1);

        String testName2 = "Plamen Barzakov";
        String emailEmail2 = "plamen.barzakov@atia.com";
        String password2 = "Password2";
        UserServiceModel user = this.registerUser(testName2, emailEmail2, password2);
        
        List<UserRoleServiceModel> allRoles = this.userService.getAllAvailableRoles();
        Set<UserRoleServiceModel> roles = allRoles.stream().filter(x->x.getAuthority().equals(UserRolesEnum.ADMIN.getRole())).collect(Collectors.toSet());
        this.userService.editUserRoles(user.getId(), root.getDomainEmail(), roles);
        
        Set<UserRoleServiceModel> newRoles = allRoles.stream()
                .filter(x->x.getAuthority().equals(UserRolesEnum.BUYER.getRole())||x.getAuthority().equals(UserRolesEnum.SELLER.getRole()))
                .collect(Collectors.toSet());
        
        boolean expected = false;
        boolean altual = this.userService.editUserRoles(user.getId(), user.getDomainEmail(), newRoles);
        Assert.assertEquals(expected, altual);
        
        List<UserRoleServiceModel> newUserRoles = this.userService.getAllUserRolesById(user.getId());
        
        Assert.assertEquals(false, newUserRoles.stream().anyMatch(x->x.getAuthority().equals(UserRolesEnum.SELLER.getRole())));
    }
    
    @Test
    public void userService_editUserRoles_AdminNotChangeRootRoles() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";
        String password1 = "Password1";
        UserServiceModel root =this.registerUser(testName1, emailEmail1, password1);

        String testName2 = "Plamen Barzakov";
        String emailEmail2 = "plamen.barzakov@atia.com";
        String password2 = "Password2";
        UserServiceModel user = this.registerUser(testName2, emailEmail2, password2);
        
        List<UserRoleServiceModel> allRoles = this.userService.getAllAvailableRoles();
        Set<UserRoleServiceModel> roles = allRoles.stream().filter(x->x.getAuthority().equals(UserRolesEnum.ADMIN.getRole())).collect(Collectors.toSet());
        this.userService.editUserRoles(user.getId(), root.getDomainEmail(), roles);
        
        Set<UserRoleServiceModel> newRoles = new HashSet();
        
        boolean expected = false;
        boolean altual = this.userService.editUserRoles(root.getId(), user.getDomainEmail(), newRoles);
        Assert.assertEquals(expected, altual);
        
        List<UserRoleServiceModel> newUserRoles = this.userService.getAllUserRolesById(root.getId());
        
        Assert.assertEquals(true, newUserRoles.stream().anyMatch(x->x.getAuthority().equals(UserRolesEnum.ROOT.getRole())));
    }
    
    @Test
    public void userService_editUserRoles_NonAdminShouldNotChangeOtherUserRoles() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";
        String password1 = "Password1";
        UserServiceModel root =this.registerUser(testName1, emailEmail1, password1);

        String testName2 = "Plamen Barzakov";
        String emailEmail2 = "plamen.barzakov@atia.com";
        String password2 = "Password2";
        UserServiceModel user1 = this.registerUser(testName2, emailEmail2, password2);
        
        String testName3 = "ivan Barzakov";
        String emailEmail3 = "ivan.barzakov@atia.com";
        String password3 = "Password2";
        UserServiceModel user2 = this.registerUser(testName3, emailEmail3, password3);
        
        List<UserRoleServiceModel> allRoles = this.userService.getAllAvailableRoles();
        Set<UserRoleServiceModel> roles = allRoles.stream().filter(x->x.getAuthority().equals(UserRolesEnum.ADMIN.getRole())).collect(Collectors.toSet());
        
        boolean expected = false;
        boolean actual = this.userService.editUserRoles(user2.getId(), user1.getDomainEmail(), roles);

        Assert.assertEquals(expected, actual);
       
        List<UserRoleServiceModel> newUserRoles = this.userService.getAllUserRolesById(user2.getId());
        
        Assert.assertEquals(false, newUserRoles.stream().anyMatch(x->x.getAuthority().equals(UserRolesEnum.ADMIN.getRole())));
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_editUserRoles_WithNullRoleSet_ShouldthrowReporPartlyToUserException() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";
        String password1 = "Password1";
        UserServiceModel root =this.registerUser(testName1, emailEmail1, password1);
   
        this.userService.editUserRoles(root.getId(), root.getDomainEmail(), null);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void userService_editUserRoles_WithInvalidInputShouldthrowReporPartlyToUserException() throws Exception {
        String testName1 = "Vladimir Barzakov";
        String emailEmail1 = "vladimir.barzakov@atia.com";
        String password1 = "Password1";
        UserServiceModel root =this.registerUser(testName1, emailEmail1, password1);
   
        this.userService.editUserRoles(root.getId(), null, new HashSet());
    }
    

}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.admin;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.models.serviceModels.UserRoleServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.DTO.admin.users.all.UserViewModel;
import Atia.Shop.domain.models.DTO.admin.users.details.UserDetailsViewModel;
import Atia.Shop.domain.models.DTO.admin.users.details.AuctionViewModel;
import Atia.Shop.domain.models.DTO.admin.users.edit.UserEditViewModel;
import Atia.Shop.domain.models.DTO.admin.users.edit.UserRoleEditViewModel;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Controller
@RequestMapping("/admin/users")
public class AdminUsersController extends BaseController{
    
    private final static String PREFIX_URL = "admin/users";
    private final String LOG_FORMAT = ">>>ADMIN<<< Admin with domain mail \"%s\" %s user with ID %s";
     
    private final AuctionService auctionService;
    private final UserService userService;
    
    private final UserRolesEnum adminRole;

    @Autowired
    public AdminUsersController(
            MapperValidatorUtil mapperValidatorUtil, 
            AuctionService auctionService, 
            UserService userService) {
        super(mapperValidatorUtil);
        this.auctionService = auctionService;
        this.userService = userService;
        
        this.adminRole=UserRolesEnum.ADMIN;
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllUsers(ModelAndView model, Principal principal){
        List<UserViewModel> usersList = this.mapAllObjectsToObject(this.userService.getAllUsers(), UserViewModel.class); 
        usersList.forEach(user->user.setAuthorities(this.getUserRoleAuthorities(user.getId())));
        usersList.sort((a,b)->a.getName().compareTo(b.getName()));
        model.addObject("usersList", usersList);
        return this.view(PREFIX_URL + "/all.html", model); 
    }
    
    @GetMapping("/details")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getDetailsView(@RequestParam("id") String userId, ModelAndView model, Principal principal) {
        UserServiceModel userServiceModel = this.userService.getUserById(userId);
        UserDetailsViewModel userDetails = this.mapObjectToObject(userServiceModel, UserDetailsViewModel.class);
        userDetails.setAuthorities(this.getUserRoleAuthorities(userId));
        List<AuctionViewModel> ceratedAuctions = 
                this.mapAllObjectsToObject(this.auctionService.getAllAuctionsBySeller(userServiceModel.getDomainEmail(),
                        this.adminRole), AuctionViewModel.class);
        List<AuctionViewModel> winnedAuctions = 
                this.mapAllObjectsToObject(this.auctionService.getAllAuctionsByWinner(userServiceModel.getDomainEmail()), AuctionViewModel.class);

        userDetails.setCreatedAuctions(ceratedAuctions);
        userDetails.setWinnedAuctions(winnedAuctions);
        model.addObject("userDetails", userDetails);
        return this.view(PREFIX_URL + "/details.html", model);
    }
    @GetMapping("/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getEditForm(@RequestParam("id") String userId, ModelAndView model, Principal principal){
        UserEditViewModel user = this.mapObjectToObject(this.userService.getUserById(userId), UserEditViewModel.class);
        
        List<UserRoleEditViewModel> allAuthorities = 
                this.mapAllObjectsToObject(this.userService.getAllAvailableRoles(), UserRoleEditViewModel.class);
        List<String> userAuthoritiesIds = 
                this.userService.getAllUserRolesById(userId).stream().map(x->x.getId()).collect(Collectors.toList());
        allAuthorities.forEach(auth->{
            if(userAuthoritiesIds.contains(auth.getId())){
                auth.setFlag(true);
            }
        });
        user.setAllAuthorities(allAuthorities);
        model.addObject("userModel", user);
        return this.view(PREFIX_URL + "/edit.html", model);
    }
    
    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView postEditForm(@RequestParam("id") String userId, @ModelAttribute UserEditViewModel userEditModel, ModelAndView model, Principal principal){
        Set<UserRoleServiceModel> newRoles = userEditModel.getAllAuthorities()
                .stream()
                .filter(x->x.isFlag())
                .map(x->this.mapObjectToObject(x, UserRoleServiceModel.class))
                .collect(Collectors.toSet());
        this.userService.editUserRoles(userId, principal.getName(), newRoles);
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "edit user roles to", userId));
        return this.redirect("/"+PREFIX_URL+"/all");
    }
    
    private List<String> getUserRoleAuthorities(String userId){
        return this.userService.getAllUserRolesById(userId)
            .stream()
            .map(x->x.getAuthority())
            .collect(Collectors.toList());
    }   
}

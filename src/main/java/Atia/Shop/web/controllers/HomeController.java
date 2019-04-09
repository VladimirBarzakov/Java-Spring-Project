/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers;

import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */

@Controller
public class HomeController extends BaseController{

    private final UserService userService;
    
    @Autowired
    public HomeController(MapperValidatorUtil mapperValidatorUtil, UserService userService) {
        super(mapperValidatorUtil);
        this.userService=userService;
    }

    
    @GetMapping(value={"/","/home"})
    public ModelAndView home(Model model, Principal principal, HttpSession httpSession){
        if(principal!=null){
            UserServiceModel user = this.userService.getUserByDomainEmail(principal.getName());
            httpSession.setAttribute(WebConstrants.SESSION_USER_ATTR, user.getName());
        }
        return this.view("home.html");
    }
    
    
}

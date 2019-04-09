/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.constants.WebConstrants;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.domain.models.DTO.users.register.RegisterUserBindingModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */


@Controller
@RequestMapping("/users")
public class RegisterUserController extends BaseController{
    
    private final UserService userService;
    
    private final String LOG_FORMAT = ">>>REGISTER<<< Registered user with name \"%s\" with domain mail - \"%s\"";

    @Autowired
    public RegisterUserController(UserService userService, MapperValidatorUtil mapperValidatorUtil) {
        super(mapperValidatorUtil);
        this.userService = userService;
    }
    
    
    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerForm(ModelAndView model){
        model.addObject("registerUserBindingModel",new RegisterUserBindingModel());
        return this.view("users/register.html",model);
    }
    
    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerSubmit( @Valid @ModelAttribute RegisterUserBindingModel registerUserBindingModel,
            BindingResult bindingResult, ModelAndView model, HttpSession httpSession){
        if(bindingResult.hasErrors()){

            return this.view("users/register.html", model);
        }
        registerUserBindingModel.setTestPassword(registerUserBindingModel.getTestPassword().trim());
        registerUserBindingModel.setConfirmTestPassword(registerUserBindingModel.getConfirmTestPassword().trim());
        
        if(!registerUserBindingModel.getTestPassword().equals(registerUserBindingModel.getConfirmTestPassword())){
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE,
                    "Password and confirm password does not match");
            return this.view("users/register.html", model);
        }
        UserServiceModel  userServiceModel = this.mapObjectToObject(registerUserBindingModel, UserServiceModel.class);
        try {
            this.userService.registerUser(userServiceModel, registerUserBindingModel.getTestPassword());
        } catch (ReportToUserException ex) {
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE,ex.getMessage());
            return this.view("users/register.html", model);
        }
        this.userService.authenticateUser(registerUserBindingModel.getDomainEmail());
        httpSession.setAttribute(WebConstrants.SESSION_USER_ATTR, registerUserBindingModel.getName());
        LOGGER.info(String.format(LOG_FORMAT, registerUserBindingModel.getName(), registerUserBindingModel.getDomainEmail()));
        return this.redirect("/home");
    }  
    
}

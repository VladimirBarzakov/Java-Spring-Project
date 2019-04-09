/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers;


import Atia.Shop.service.API.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */

@Controller
public class AuthenticateController {
    
    private final UserService userService;

    @Autowired
    public AuthenticateController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public String loginForm(Model model){
        return "login";
    }
    
    @PostMapping("/login" )
    @PreAuthorize("isAnonymous()")
    public String loginSubmit( Model model){
        return "redirect:/home.html";
    }
    
    @GetMapping("/unauthorized")
    public String unauthorized(){
        return "unauthorized";
    }
}

/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.admin;

import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.models.DTO.admin.bids.all.AllBidlViewModel;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Controller
@RequestMapping("/admin/bids")
public class AdminBidsController extends BaseController{
    
    private final static String PREFIX_URL = "admin/bids";
     
    private final BidService bidService;
     
    private final UserRolesEnum adminRole;

    @Autowired
    public AdminBidsController(BidService bidservice, MapperValidatorUtil mapperValidatorUtil) {
        super(mapperValidatorUtil);
        this.bidService = bidservice;
        
        
        this.adminRole=UserRolesEnum.ADMIN;
    }
    
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllBidsAuctions(ModelAndView model){
        List<AllBidlViewModel> bidsList = 
                this.mapAllObjectsToObject(this.bidService.adminGetALL(), AllBidlViewModel.class);
        bidsList.sort((a, b) -> b.getBidDate().compareTo(a.getBidDate()));

        model.addObject("bidsList", bidsList);
        return this.view(PREFIX_URL+"/all.html", model);
    }
     
     
    
}

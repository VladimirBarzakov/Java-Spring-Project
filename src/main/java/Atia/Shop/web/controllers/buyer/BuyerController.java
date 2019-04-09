/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.buyer;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.DTO.buyer.auctions.all.AllAuctionViewModel;
import Atia.Shop.domain.models.DTO.buyer.auctions.aucItemDetails.AucItemViewModel;
import Atia.Shop.domain.models.DTO.buyer.auctions.details.DetailsAuctionViewModel;
import Atia.Shop.domain.models.DTO.buyer.auctions.details.DetailsAucItemViewModel;
import Atia.Shop.domain.models.DTO.buyer.auctions.details.DetailsPictureWrapperViewModel;
import Atia.Shop.domain.models.DTO.buyer.auctions.details.RegisterBidBindingModel;
import Atia.Shop.domain.models.DTO.buyer.bids.all.AllBidlViewModel;
import Atia.Shop.domain.models.DTO.buyer.winning.auctions.WinAuctionViewModel;
import Atia.Shop.domain.models.DTO.buyer.winning.details.WinDetAucItemViewModel;
import Atia.Shop.domain.models.DTO.buyer.winning.details.WinDetAuctionViewModel;
import Atia.Shop.domain.models.DTO.buyer.winning.details.WinDetPictureWrapperViewModel;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;

import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Controller
@RequestMapping("/buyer")
public class BuyerController extends BaseController{
    
     private final static String PREFIX_URL = "buyer";
     private final String LOG_FORMAT = ">>>BUYER<<< Buyer with domain mail \"%s\" %s auction with ID %d";
    
    private final AuctionService auctionService;
    private final UserService userService;
    private final ItemService itemService;
    private final BidService bidService;
    
    private final UserRolesEnum buyerRole;

    @Autowired
    public BuyerController(
            MapperValidatorUtil mapperValidatorUtil, 
            AuctionService auctionService,
            UserService userService, 
            BidService bidService,
            ItemService itemService) {
        super(mapperValidatorUtil);
        this.auctionService = auctionService;
        this.userService=userService;
        this.itemService=itemService;
        this.bidService = bidService;
        
        this.buyerRole=UserRolesEnum.BUYER;
    }
    
    @GetMapping("auctions/all")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView getAllActiveAuctions(ModelAndView model, Principal principal){
        List<AllAuctionViewModel> auctionsList = 
                this.mapAllObjectsToObject(this.auctionService.getAllAuctionsByStatus(AuctionStatus.LIVE), AllAuctionViewModel.class);

        auctionsList.sort((a, b) -> b.getDateStarted().compareTo(a.getDateStarted()));

        model.addObject("auctionsList", auctionsList);
        return this.view(PREFIX_URL+"/auctions/all.html", model);
    }
    
    @GetMapping("auctions/details")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView getDetailsView(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal){
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.buyerRole);
        
        DetailsAuctionViewModel auctionViewModel = this.mapObjectToObject(auction, DetailsAuctionViewModel.class);
        List<DetailsAucItemViewModel> auctionedItems = 
                this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), DetailsAucItemViewModel.class);
        auctionedItems.sort((a,b)->a.getItemName().compareTo(b.getItemName()));
        auctionViewModel.setAuctionedItems(auctionedItems);
        List<DetailsPictureWrapperViewModel>auctionPictures = 
                this.mapAllObjectsToObject(this.auctionService.getAllPicturesByEntity(auction), DetailsPictureWrapperViewModel.class);
        auctionPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        auctionViewModel.setAucItemPictures(auctionPictures);
        
        model.addObject("detailsAuctionViewModel", auctionViewModel);
        model.addObject("registerBidViewModel", new RegisterBidBindingModel());
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "see details to", auctionId));
        return this.view(PREFIX_URL+"/auctions/details.html", model);
    }
    
    
    @PostMapping("registerbid")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView registerBid( @RequestParam("id") Long auctionId, @Valid @ModelAttribute RegisterBidBindingModel registerBidViewModel,
            BindingResult bindingResult, ModelAndView model, Principal principal, final RedirectAttributes redirectAttrs){
        if (bindingResult.hasErrors()) {
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE,"Invalid bid amount");
            return this.getDetailsView(auctionId, model, principal);
        }
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId,principal.getName(), this.buyerRole);
        UserServiceModel bidder = this.userService.getUserByDomainEmail(principal.getName());
        BidServiceModel bid = this.mapObjectToObject( registerBidViewModel, BidServiceModel.class);
        
        bid.setBidder(bidder);
        bid.setAuction(auction);
         try {
             this.bidService.placeBid(bid);
        } catch (ReportToUserException ex) {
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE,ex.getMessage());
            return this.getDetailsView(auctionId, model, principal);
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "place bid on", auctionId));
        redirectAttrs.addFlashAttribute("isBidPlaced", true);
        return this.redirect("/"+this.PREFIX_URL+"/auctions/details?id="+auctionId);
    }
    
    @GetMapping("winning/auctions")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView getAllWinnedAuctions(ModelAndView model, Principal principal){
        List<WinAuctionViewModel> auctionsList = 
                this.mapAllObjectsToObject(this.auctionService.getAllAuctionsByWinner(principal.getName()), WinAuctionViewModel.class);
        auctionsList.sort((a, b) -> b.getDateExpired().compareTo(a.getDateExpired()));

        model.addObject("auctionsList", auctionsList);
        return this.view(PREFIX_URL+"/winning/auctions.html", model);
    }
    
    @GetMapping("winning/details")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView getWinDetailsView( @RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId,principal.getName(), this.buyerRole);
        if(!this.auctionService.verifyAuctionWinner(principal.getName(), auction)){
            throw new ReporPartlyToUserException(ValidationMesseges.AUCTION_BUYER_IS_NOT_AUCTION_WINNER);
        }
        WinDetAuctionViewModel auctiomViewModel = this.mapObjectToObject(auction, WinDetAuctionViewModel.class);
        
        List<WinDetAucItemViewModel> auctionedItems = 
                this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), WinDetAucItemViewModel.class);
        auctionedItems.sort((a,b)->a.getItemName().compareTo(b.getItemName()));
        auctiomViewModel.setAuctionedItems(auctionedItems);
        
        List<WinDetPictureWrapperViewModel>auctionPictures = 
                this.mapAllObjectsToObject(this.auctionService.getAllPicturesByEntity(auction), WinDetPictureWrapperViewModel.class);
        auctionPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        auctiomViewModel.setAucItemPictures(auctionPictures);
        
        model.addObject("detailsAuctionViewModel", auctiomViewModel);
        return this.view(PREFIX_URL + "/winning/details.html", model);
    }
    
    @GetMapping("bids/all")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView getAllBidsAuctions(ModelAndView model, Principal principal){
        List<AllBidlViewModel> bidsList = 
                this.mapAllObjectsToObject(this.bidService.getALLByBidderMail(principal.getName()), AllBidlViewModel.class);
        bidsList.sort((a, b) -> b.getBidDate().compareTo(a.getBidDate()));

        model.addObject("bidsList", bidsList);
        return this.view(PREFIX_URL+"/bids/all.html", model);
    }
    
    @GetMapping("/aucitem/details")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView getAuctionedItemDetails(@RequestParam("id") Long aucItemId, ModelAndView model, Principal principal){
        AuctionedItemServiceModel aucItem = this.itemService.getAucItemById(aucItemId);
        if(!this.auctionService.isAuctionBiddable(aucItem.getAuction()) && 
                !(this.auctionService.isAuctionWinned(aucItem.getAuction()) && 
                aucItem.getAuction().getAuctionWinner().getDomainEmail().equals(principal.getName())  )){
            throw new ReporPartlyToUserException(ValidationMesseges.ACCESS_DENIED);
        }
        AucItemViewModel aucItemView = this.mapObjectToObject(aucItem, AucItemViewModel.class);
        model.addObject("aucItem", aucItemView);
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "see details of item "+aucItemId, aucItem.getAuction().getId()));
        return this.view(PREFIX_URL+"/auctions/aucItemDetails/details.html", model);
    }
    
}

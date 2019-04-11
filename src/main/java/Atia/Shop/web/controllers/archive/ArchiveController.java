/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.archive;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.domain.entities.UserRole;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.DTO.archive.AllAuctionViewModel;
import Atia.Shop.domain.models.DTO.archive.details.DetailsAucItemViewModel;
import Atia.Shop.domain.models.DTO.archive.details.DetailsAuctionViewModel;
import Atia.Shop.domain.models.DTO.archive.details.DetailsPictureWrapperViewModel;

import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Controller
@RequestMapping("/archive")
public class ArchiveController extends BaseController {

    private final static String PREFIX_URL = "archive";
    private final String LOG_FORMAT = ">>>ARCHIVE<<< User with domain mail \"%s\" %s auction with ID %d";

    private final AuctionService auctionService;
    private final ItemService itemService;
    private final BidService bidService;

    private final UserRolesEnum sellerRole;
    private final UserRolesEnum adminRole;
    private final UserRolesEnum buyerRole;

    @Autowired
    public ArchiveController(
            MapperValidatorUtil mapperValidatorUtil,
            AuctionService auctionService,
            UserService userService,
            ItemService itemService,
            BidService bidService) {
        super(mapperValidatorUtil);
        this.auctionService = auctionService;
        this.itemService = itemService;
        this.bidService = bidService;

        this.sellerRole = UserRolesEnum.SELLER;
        this.adminRole = UserRolesEnum.ADMIN;
        this.buyerRole = UserRolesEnum.BUYER;
    }

    @GetMapping("/archive")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView archive(@RequestParam("id") Long auctionId, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), sellerRole);
        this.auctionService.archive(auction, principal.getName());
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "set to ARCHIVE", auctionId));
        return this.redirect("/seller/auctions/all");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getAllSeller(ModelAndView model, Principal principal) {
        List<AllAuctionViewModel> auctionsList = this.auctionService.getAllArchivesByCreator(principal.getName(), this.sellerRole)
                .stream()
                .map(asm -> this.mapObjectToObject(asm, AllAuctionViewModel.class))
                .sorted((a, b) -> b.getDateStarted().compareTo(a.getDateStarted()))
                .collect(Collectors.toList());

        model.addObject("auctionsList", auctionsList);
        return this.view(PREFIX_URL + "/all.html", model);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllAdmin(ModelAndView model, Principal principal) {
        List<AllAuctionViewModel> auctionsList = this.auctionService.getAllArchives(this.adminRole)
                .stream()
                .map(asm -> this.mapObjectToObject(asm, AllAuctionViewModel.class))
                .sorted((a, b) -> b.getDateStarted().compareTo(a.getDateStarted()))
                .collect(Collectors.toList());

        model.addObject("auctionsList", auctionsList);
        return this.view(PREFIX_URL + "/all.html", model);
    }

    @GetMapping("/details")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SELLER')")
    public ModelAndView getDetailsView(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> userRoles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        AuctionServiceModel auction = null;
        if (userRoles.contains("ADMIN")) {
            auction = this.auctionService.getArchiveById(auctionId, principal.getName(), this.adminRole);
        } else if (userRoles.contains("SELLER")) {
            auction = this.auctionService.getArchiveById(auctionId, principal.getName(), this.sellerRole);
        }
        DetailsAuctionViewModel auctionViewModel = this.mapObjectToObject(auction, DetailsAuctionViewModel.class);
        List<DetailsAucItemViewModel> auctionedItems
                = this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), DetailsAucItemViewModel.class);
        auctionedItems.sort((a, b) -> a.getItemName().compareTo(b.getItemName()));
        List<DetailsPictureWrapperViewModel> auctionPictures
                = this.mapAllObjectsToObject(this.auctionService.getAllPicturesByEntity(auction), DetailsPictureWrapperViewModel.class);
        auctionPictures.sort((a, b) -> a.getDateAdded().compareTo(b.getDateAdded()));
        auctionViewModel.setAuctionedItems(auctionedItems);
        auctionViewModel.setAucItemPictures(auctionPictures);

        model.addObject("detailsAuctionViewModel", auctionViewModel);
        return this.view(PREFIX_URL + "/details.html", model);
    }

    @GetMapping("/buyer/details")
    @PreAuthorize("hasAuthority('BUYER')")
    public ModelAndView getBuyerDetailsView(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getArchiveById(auctionId, principal.getName(), this.buyerRole);
        DetailsAuctionViewModel auctionViewModel = this.mapObjectToObject(auction, DetailsAuctionViewModel.class);
        List<DetailsAucItemViewModel> auctionedItems
                = this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), DetailsAucItemViewModel.class);
        auctionedItems.sort((a, b) -> a.getItemName().compareTo(b.getItemName()));
        List<DetailsPictureWrapperViewModel> auctionPictures
                = this.mapAllObjectsToObject(this.auctionService.getAllPicturesByEntity(auction), DetailsPictureWrapperViewModel.class);
        auctionPictures.sort((a, b) -> a.getDateAdded().compareTo(b.getDateAdded()));
        auctionViewModel.setAuctionedItems(auctionedItems);
        auctionViewModel.setAucItemPictures(auctionPictures);

        model.addObject("detailsAuctionViewModel", auctionViewModel);
        return this.view(PREFIX_URL + "/details.html", model);
    }

    @GetMapping("/admin/delete/{auctionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView deleteArchive(@PathVariable("auctionId") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getArchiveById(auctionId, principal.getName(), adminRole);

        if (!this.auctionService.deleteArchiveById(auctionId, adminRole)) {
            this.bidService.deleteAllBidsOfArchiveAuction(auction);
            this.auctionService.deleteArchiveById(auctionId, adminRole);
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete as Admin archive", auctionId));
        return this.redirect("/" + PREFIX_URL + "/admin/all");
    }

}

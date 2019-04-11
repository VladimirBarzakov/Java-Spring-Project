/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.admin;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.domain.models.DTO.admin.auctions.all.AllAuctionViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.delete.DeleteAucItemViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.delete.DeleteAuctionViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.details.DetailsAucItemViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.details.DetailsAuctionViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.details.DetailsPictureWrapperViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.edit.EditAucItemsBindingModel;
import Atia.Shop.domain.models.DTO.admin.auctions.edit.EditAuctionBindingModel;
import Atia.Shop.domain.models.DTO.admin.auctions.edit.EditItemBindingModel;
import Atia.Shop.domain.models.DTO.admin.auctions.editGallery.EdGallAucItemViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.editGallery.EdGallAucPicWrapViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.editGallery.EdGallAuctionViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.editGallery.get.EdGallAucPictureViewModel;
import Atia.Shop.domain.models.DTO.admin.auctions.aucItems.details.AucItemViewModel;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Controller
@RequestMapping("/admin/auctions")
public class AdminAuctionsController extends BaseController{
    
    private final static String PREFIX_URL = "admin/auctions";
    private final String LOG_FORMAT = ">>>ADMIN<<< Admin with domain mail \"%s\" %s auction with ID %d";
     
    private final AuctionService auctionService;
    private final ItemService itemService;
    private final BidService bidService;
    
    private final UserRolesEnum adminRole;

    @Autowired
    public AdminAuctionsController(
            AuctionService auctionService, 
            MapperValidatorUtil mapperValidatorUtil,
            ItemService itemService,
            BidService bidService) {
        super(mapperValidatorUtil);
        this.auctionService = auctionService;
        this.itemService = itemService;
        this.bidService=bidService;
        
        this.adminRole=UserRolesEnum.ADMIN;
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAllUsers(ModelAndView model, Principal principal){
        List<AllAuctionViewModel> auctionsList = 
                this.mapAllObjectsToObject(this.auctionService.getAllAuctions(), AllAuctionViewModel.class);
        model.addObject("auctionsList", auctionsList);
        return this.view(PREFIX_URL + "/all.html", model);
    }
    
    @GetMapping("/details")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getDetailsView(
            @RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.adminRole);
        DetailsAuctionViewModel auctionViewModel = this.mapObjectToObject(auction, DetailsAuctionViewModel.class);
        List<DetailsAucItemViewModel> auctionedItems = 
                this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), DetailsAucItemViewModel.class); 
        auctionedItems.sort((a,b)->a.getItemName().compareTo(b.getItemName()));
        List<DetailsPictureWrapperViewModel> auctionPictures = 
                this.mapAllObjectsToObject(this.auctionService.getAllPicturesByEntity(auction), DetailsPictureWrapperViewModel.class);
        auctionPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        auctionViewModel.setAuctionedItems(auctionedItems);
        auctionViewModel.setAucItemPictures(auctionPictures);
        
        model.addObject("detailsAuctionViewModel", auctionViewModel);
        return this.view(PREFIX_URL + "/details.html", model);
    }
    
    @GetMapping("/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getEditForm(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.adminRole);
        EditAuctionBindingModel auctionToedit = this.mapObjectToObject(auction,EditAuctionBindingModel.class);
        
        List<EditAucItemsBindingModel> auctionedItems = 
                this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), EditAucItemsBindingModel.class);
        auctionedItems.sort((a, b) -> a.getItemName().compareTo(b.getItemName()));
        List<EditItemBindingModel> availableItems =
                this.mapAllObjectsToObject(this.itemService.getAllAvailableItems(auction.getSeller(), this.adminRole), EditItemBindingModel.class);
        availableItems.sort((a, b) -> a.getName().compareTo(b.getName()));
        auctionToedit.setAuctionedItems(auctionedItems);
        auctionToedit.setAvailableItems(availableItems);

        model.addObject("editAuctionBindingModel", auctionToedit);
        return this.view(PREFIX_URL + "/edit.html", model);
    }
    
    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView postEditForm(@RequestParam("id") Long auctionId, @Valid @ModelAttribute EditAuctionBindingModel auctionToEdit,
            BindingResult bindingResult, ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return this.view(PREFIX_URL + "/edit.html", model);
        }
        AuctionServiceModel auction = this.mapObjectToObject(auctionToEdit, AuctionServiceModel.class);
        AuctionServiceModel auctionEntity = this.auctionService.getAuctionById(auctionId, principal.getName(), this.adminRole);
        auction.setStatus(auctionEntity.getStatus());
        auction.setSeller(auctionEntity.getSeller());
        List<ItemServiceModel> itemsToAdd = new ArrayList();

        List<AuctionedItemServiceModel> aucItemsToRemove = auctionToEdit.getAuctionedItems()
                .stream()
                .filter(x -> x.isMarkedForRemove())
                .map(auivm -> this.mapObjectToObject(auivm, AuctionedItemServiceModel.class))
                .collect(Collectors.toList());
        try {
            this.auctionService.updateAuction(auction, itemsToAdd, aucItemsToRemove,principal.getName() ,this.adminRole);
        } catch (ReportToUserException ex) {
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE, ex.getMessage());
            return this.getEditForm(auctionId, model, principal);
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "edit", auctionId));
        return this.redirect("/" + PREFIX_URL + "/edit?id=" + auctionId);
    }
    
    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getDeleteForm(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.adminRole);
        DeleteAuctionViewModel auctionViewModel = this.mapObjectToObject(auction, DeleteAuctionViewModel.class);
        List<DeleteAucItemViewModel> auctionedItems = this.itemService.findAllAucItemsByAuctionId(auctionId)
                .stream()
                .map(x -> this.mapObjectToObject(x, DeleteAucItemViewModel.class))
                .collect(Collectors.toList());
        auctionedItems.sort((a,b)->a.getItemName().compareTo(b.getItemName()));
        auctionViewModel.setAuctionedItems(auctionedItems);
        model.addObject("deleteAuctionViewModel", auctionViewModel);
        return this.view(PREFIX_URL + "/delete.html", model);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView postDeleteForm(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.adminRole);
        try {
            this.bidService.deleteAllBidsOfAuction(auction);
            this.auctionService.deleteAuction(auction, principal.getName(),this.adminRole);
        } catch (Exception ex) {
            throw new ReporPartlyToUserException(ex.getMessage(), ex.getCause());
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete", auctionId));
        return this.redirect("/" + PREFIX_URL + "/all");
    }
    
    

    @GetMapping("/aucitem/details")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAuctionedItemDetails(@RequestParam("id") Long aucItemId, ModelAndView model, Principal principal){
        AucItemViewModel aucItem = this.mapObjectToObject(this.itemService.getAucItemById(aucItemId), AucItemViewModel.class);
        model.addObject("aucItem", aucItem);
        return this.view(PREFIX_URL+"/aucItems/details.html", model);
    }
    
    @GetMapping("/gallery/edit/{auctionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getEditGallery(@PathVariable("auctionId") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.adminRole);
        List<EdGallAucPicWrapViewModel> auctionPictures
                = this.mapAllObjectsToObject(this.auctionService.getAllPicturesByEntity(auction), EdGallAucPicWrapViewModel.class);
        List<EdGallAucItemViewModel> aucItems
                = this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), EdGallAucItemViewModel.class);
        auctionPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        aucItems.sort((a,b)->a.getItemName().compareTo(b.getItemName()));
        EdGallAuctionViewModel auctionViewModel = new EdGallAuctionViewModel();
        auctionViewModel.setId(auctionId);
        auctionViewModel.setAucItems(aucItems);
        auctionViewModel.setAuctionPictures(auctionPictures);
        
        model.addObject("auctionViewModel", auctionViewModel);
        return this.view(PREFIX_URL + "/editGallery.html", model);
    }

    @GetMapping("/gallery/edit/{auctionId}/{itemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> restGetAucItemPics(@PathVariable("auctionId") Long auctionId, 
            @PathVariable("itemId") Long itemId, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.adminRole);
        
        List<EdGallAucPictureViewModel> aucItemPictures =
                this.mapAllObjectsToObject(this.itemService.getAllPicturesByEntity(item), EdGallAucPictureViewModel.class);
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.adminRole);
        Map<String,AucItemPictureWrapperServiceModel> auctionPictureWrappers = 
                this.auctionService.getAllPicturesByEntity(auction)
                .stream()
                .collect(Collectors.toMap(key -> key.getOriginalFileName(), auctionPicture -> auctionPicture));
        aucItemPictures.forEach(x->{
            if(auctionPictureWrappers.containsKey(x.getPictureId())){
                x.setAuctionItemPictureWrapperId(auctionPictureWrappers.get(x.getPictureId()).getId());
            }
        });
        return ResponseEntity.ok(aucItemPictures);
    }
    
    @DeleteMapping("/gallery/edit/{auctionId}/{aucItemId}/{picWrapperId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> restDeletePicFromAuction(@PathVariable("auctionId") Long auctionId,
            @PathVariable("aucItemId") Long aucItemId,
            @PathVariable("picWrapperId") Long picWrapperId, Principal principal) {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auctionId);
        pictureWrapper.setAuctionedItemId(aucItemId);
        pictureWrapper.setId(picWrapperId);
        
        try{
            this.auctionService.adminDeletePicture(pictureWrapper);
            LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete picture from", auctionId));
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(ReportToUserException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
    }
    
}

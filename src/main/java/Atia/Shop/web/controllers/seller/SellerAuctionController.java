/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.seller;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.domain.models.DTO.seller.auctions.edit.EditAuctionBindingModel;
import Atia.Shop.domain.models.DTO.seller.auctions.create.CreateAuctionBindingModel;
import Atia.Shop.domain.models.DTO.seller.auctions.all.AllAuctionsViewModel;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.domain.models.DTO.seller.auctions.delete.DeleteAucItemViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.delete.DeleteAuctionViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.details.DetailsPictureWrapperViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.details.DetailsAucItemViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.details.DetailsAuctionViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.edit.EditAucItemsBindingModel;
import Atia.Shop.domain.models.DTO.seller.auctions.edit.EditItemBindingModel;
import Atia.Shop.domain.models.DTO.seller.auctions.editGallery.EdGallAucItemViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.editGallery.EdGallAucPicWrapViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.editGallery.EdGallAuctionViewModel;
import Atia.Shop.domain.models.DTO.seller.auctions.editGallery.get.EdGallAucPictureViewModel;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
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
@RequestMapping("/seller/auctions")
public class SellerAuctionController extends BaseController {

    private final static String PREFIX_URL = "seller/auctions";
    private final String LOG_FORMAT = ">>>SELLER<<< Seller with domain mail \"%s\" %s auction with ID %d";

    private final AuctionService auctionService;
    private final ItemService itemService;
    private final UserService userService;
    
    private final UserRolesEnum sellerRole;

    @Autowired
    public SellerAuctionController(
            MapperValidatorUtil mapperValidatorUtil,
            AuctionService auctionService, 
            UserService userService,
            ItemService itemService) {
        super(mapperValidatorUtil);
        this.auctionService = auctionService;
        this.userService = userService;
        this.itemService = itemService;
        
        this.sellerRole=UserRolesEnum.SELLER;
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView createForm(ModelAndView model) {
        model.addObject("createAuctionBindingModel", new CreateAuctionBindingModel());
        return this.view(PREFIX_URL + "/create.html", model);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView postCreateForm(@Valid @ModelAttribute CreateAuctionBindingModel auctionToCreate, BindingResult bindingResult, 
            ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return this.view(PREFIX_URL + "/create.html", model);
        }
        AuctionServiceModel auction = this.mapObjectToObject(auctionToCreate, AuctionServiceModel.class);
        try {
            auction = this.auctionService.createAuction(auction, principal.getName());
        } catch (ReportToUserException ex) {
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE, ex.getMessage());
            return this.view(PREFIX_URL + "/create.html", model);
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "create", auction.getId()));
        return this.redirect("/" + PREFIX_URL + "/edit?id=" + auction.getId());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getAll(ModelAndView model, Principal principal) {
        List<AllAuctionsViewModel> auctionsList = this.auctionService.getAllAuctionsBySeller(principal.getName(), this.sellerRole)
                .stream().map(asm -> {
                    AllAuctionsViewModel avm = this.mapObjectToObject(asm, AllAuctionsViewModel.class);
                    avm.setIsAuctionDeletable(this.auctionService.isAuctionDeletable(asm));
                    avm.setIsAuctionEditable(this.auctionService.isAuctionEditable(asm));
                    avm.setIsArchivable(this.auctionService.isArchivable(asm));
                    return avm;
                })
                .sorted((a, b) -> b.getDateStarted().compareTo(a.getDateStarted()))
                .collect(Collectors.toList());
        
        model.addObject("auctionsList", auctionsList);
        return this.view(PREFIX_URL + "/all.html", model);
    }

    @GetMapping("/edit")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getEditForm(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        EditAuctionBindingModel auctionToedit = this.mapObjectToObject(
                this.auctionService.getAuctionById(auctionId, principal.getName(), this.sellerRole),
                EditAuctionBindingModel.class);
        if (!this.auctionService.isAuctionEditable(auctionId)) {
            throw new ReporPartlyToUserException(ValidationMesseges.AUCTION_IS_NOT_IN_CORRECT_STATUS);
        }
        List<EditAucItemsBindingModel> auctionedItems = 
                this.mapAllObjectsToObject(this.itemService.findAllAucItemsByAuctionId(auctionId), EditAucItemsBindingModel.class);
        auctionedItems.sort((a, b) -> a.getItemName().compareTo(b.getItemName()));
        UserServiceModel seller = this.userService.getUserByDomainEmail(principal.getName());
        List<EditItemBindingModel> availableItems =
                this.mapAllObjectsToObject(this.itemService.getAllAvailableItems(seller, this.sellerRole), EditItemBindingModel.class);
        availableItems.sort((a, b) -> a.getName().compareTo(b.getName()));
        auctionToedit.setAuctionedItems(auctionedItems);
        auctionToedit.setAvailableItems(availableItems);

        model.addObject("editAuctionBindingModel", auctionToedit);
        return this.view(PREFIX_URL + "/edit.html", model);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView postEditForm(@RequestParam("id") Long auctionId, @Valid @ModelAttribute EditAuctionBindingModel auctionToEdit,
            BindingResult bindingResult, ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return this.view(PREFIX_URL + "/edit.html", model);
        }
        AuctionServiceModel auction = this.mapObjectToObject(auctionToEdit, AuctionServiceModel.class);
        AuctionServiceModel auctionEntity = this.auctionService.getAuctionById(auctionId, principal.getName(), this.sellerRole);
        auction.setId(auctionEntity.getId());
        auction.setStatus(auctionEntity.getStatus());
        auction.setSeller(auctionEntity.getSeller());
        List<ItemServiceModel> itemsToAdd = auctionToEdit.getAvailableItems()
                .stream()
                .filter(x -> x.getMarkedForAdd())
                .map(aivm -> this.mapObjectToObject(aivm, ItemServiceModel.class))
                .collect(Collectors.toList());

        List<AuctionedItemServiceModel> aucItemsToRemove = auctionToEdit.getAuctionedItems()
                .stream()
                .filter(x -> x.isMarkedForRemove())
                .map(auivm -> this.mapObjectToObject(auivm, AuctionedItemServiceModel.class))
                .collect(Collectors.toList());
        try {
            this.auctionService.updateAuction(auction, itemsToAdd, aucItemsToRemove,principal.getName() ,this.sellerRole);
        } catch (ReportToUserException ex) {
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE, ex.getMessage());
            return this.getEditForm(auctionId, model, principal);
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "edit", auctionId));
        return this.redirect("/" + PREFIX_URL + "/edit?id=" + auctionId);
    }

    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getDeleteForm(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.sellerRole);
        if (!this.auctionService.isAuctionDeletable(auctionId)) {
            throw new ReporPartlyToUserException(ValidationMesseges.AUCTION_IS_NOT_IN_CORRECT_STATUS);
        }
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
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView postDeleteForm(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.sellerRole);
        try {
            this.auctionService.deleteAuction(auction, principal.getName(),this.sellerRole);
        } catch (Exception ex) {
            throw new ReporPartlyToUserException(ex.getMessage(), ex.getCause());
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete", auctionId));
        return this.redirect("/" + PREFIX_URL + "/all");
    }

    @GetMapping("/details")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getDetailsView(@RequestParam("id") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.sellerRole);
        DetailsAuctionViewModel auctionViewModel = this.mapObjectToObject(auction, DetailsAuctionViewModel.class);
        List<DetailsAucItemViewModel> auctionedItems = this.itemService.findAllAucItemsByAuctionId(auctionId)
                .stream()
                .map(x -> this.mapObjectToObject(x, DetailsAucItemViewModel.class))
                .collect(Collectors.toList());
        List<DetailsPictureWrapperViewModel> auctionPictures = 
                this.mapAllObjectsToObject(this.auctionService.getAllPicturesByEntity(auction), DetailsPictureWrapperViewModel.class);
        auctionPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        auctionViewModel.setAuctionedItems(auctionedItems);
        auctionViewModel.setAucItemPictures(auctionPictures);
        auctionViewModel.setIsAuctionEditable(this.auctionService.isAuctionEditable(auction));
        model.addObject("detailsAuctionViewModel", auctionViewModel);

        return this.view(PREFIX_URL + "/details.html", model);
    }

    @GetMapping("/gallery/edit/{auctionId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getEditGallery(@PathVariable("auctionId") Long auctionId, ModelAndView model, Principal principal) {
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.sellerRole);
        if (!this.auctionService.isAuctionEditable(auctionId)) {
            throw new ReporPartlyToUserException(ValidationMesseges.AUCTION_IS_NOT_IN_CORRECT_STATUS);
        }
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
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<?> restGetAucItemPics(@PathVariable("auctionId") Long auctionId, 
            @PathVariable("itemId") Long itemId, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.sellerRole);
        List<EdGallAucPictureViewModel> aucItemPictures =
                this.mapAllObjectsToObject(this.itemService.getAllPicturesByEntity(item), EdGallAucPictureViewModel.class);
        AuctionServiceModel auction = this.auctionService.getAuctionById(auctionId, principal.getName(), this.sellerRole);
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
    
    @PostMapping("/gallery/edit/{auctionId}/{aucItemId}/{itemId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<?> restPostAddPicToAuction(@PathVariable("auctionId") Long auctionId,
            @PathVariable("aucItemId") Long aucItemId,
            @PathVariable("itemId") String itemPic, String description, Principal principal) {
        AucItemPictureWrapperServiceModel aucItemPicture = new AucItemPictureWrapperServiceModel();
        aucItemPicture.setAuctionId(auctionId);
        aucItemPicture.setAuctionedItemId(aucItemId);
        aucItemPicture.setDescription(description);
        
        try {
            aucItemPicture = this.auctionService.addPicture(aucItemPicture, itemPic, principal.getName());
        } catch (ReportToUserException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        EdGallAucPictureViewModel pictureModel = new EdGallAucPictureViewModel();
        pictureModel.setId(aucItemPicture.getId());
        pictureModel.setPictureId(aucItemPicture.getAuctionPicture().getPictureFileID());
        pictureModel.setDescription(aucItemPicture.getDescription());
        pictureModel.setAuctionItemPictureWrapperId(aucItemPicture.getId());
        
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "add pciture to", auctionId));
        return ResponseEntity.ok(pictureModel);
    }
    
    @DeleteMapping("/gallery/edit/{auctionId}/{aucItemId}/{picWrapperId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<?> restDeletePicFromAuction(@PathVariable("auctionId") Long auctionId,
            @PathVariable("aucItemId") Long aucItemId,
            @PathVariable("picWrapperId") Long picWrapperId, Principal principal) {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auctionId);
        pictureWrapper.setAuctionedItemId(aucItemId);
        pictureWrapper.setId(picWrapperId);
        
        try{
            this.auctionService.deletePicture(pictureWrapper, principal.getName());
            LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete picture from", auctionId));
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch(ReportToUserException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
    }

}

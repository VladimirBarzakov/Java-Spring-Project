/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.seller;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.models.DTO.seller.items.create.CreateItemBindingModel;
import Atia.Shop.domain.models.DTO.seller.items.all.AllItemViewModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.domain.models.DTO.seller.items.delete.DeleteAucItemViewModel;
import Atia.Shop.domain.models.DTO.seller.items.delete.DeleteItemViewModel;
import Atia.Shop.domain.models.DTO.seller.items.details.DetailsAucItemViewModel;
import Atia.Shop.domain.models.DTO.seller.items.details.DetailsItemViewModel;
import Atia.Shop.domain.models.DTO.seller.items.details.DetailsItemPictureViewModel;
import Atia.Shop.domain.models.DTO.seller.items.edit.EditItemBindingModel;
import Atia.Shop.domain.models.DTO.seller.items.editGallery.CreateItemPictureBindingModel;
import Atia.Shop.domain.models.DTO.seller.items.editGallery.EdGallItemViewModel;
import Atia.Shop.domain.models.DTO.seller.items.editGallery.EdGallItemPictureViewModel;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
import java.util.List;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Controller
@RequestMapping("/seller/items")
public class SellerItemController extends BaseController {

    private final static String PREFIX_URL = "seller/items";
    private final String LOG_FORMAT = ">>>SELLER<<< Seller with domain mail \"%s\" %s item with ID %d";

    private final ItemService itemService;
    private final UserService userService;
    
    private final UserRolesEnum sellerRole;

    @Autowired
    public SellerItemController( 
            MapperValidatorUtil mapperValidatorUtil,
            ItemService itemService, 
            UserService userService) {
        super(mapperValidatorUtil);
        this.itemService = itemService;
        this.userService = userService;
        this.sellerRole=UserRolesEnum.SELLER;
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getCreateForm(ModelAndView model) {
        model.addObject("createItemBindingModel", new CreateItemBindingModel());
        return this.view(PREFIX_URL + "/create.html", model);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView postCreateForm(@Valid @ModelAttribute CreateItemBindingModel itemToCreate,
            BindingResult bindingResult, ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return this.view(PREFIX_URL + "/create.html", model);
        }
        ItemServiceModel item = this.itemService.createItem(
                this.mapObjectToObject(itemToCreate, ItemServiceModel.class), 
                this.getSeller(principal));
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "create", item.getId()));
        return this.redirect("/" + PREFIX_URL + "/edit?id="+item.getId());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getAll(ModelAndView model, Principal principal) {
        List<AllItemViewModel> itemsList = 
                this.itemService.getAllItemsByCreator(this.getSeller(principal),this.sellerRole)
                .stream()
                .sorted((a, b) -> b.getDateAdded().compareTo(a.getDateAdded()))
                .map(ism -> {
                    AllItemViewModel item = this.mapObjectToObject(ism, AllItemViewModel.class);
                    item.setIsDeletable(this.itemService.isItemDeletable(ism.getId()));
                    item.setIsEditable(this.itemService.isItemEditable(ism.getId()));
                    return item;
                })
                .collect(Collectors.toList());
        model.addObject("itemsList", itemsList);

        return this.view(PREFIX_URL + "/all.html", model);
    }

    @GetMapping("/edit")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getEditForm(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        EditItemBindingModel item =
                this.mapObjectToObject(
                        this.itemService.getItemById(itemId, principal.getName(), this.sellerRole), EditItemBindingModel.class);
        model.addObject("editItemBindingModel", item);

        return this.view(PREFIX_URL + "/edit.html", model);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView postEditForm(@RequestParam("id") Long itemId, @Valid @ModelAttribute EditItemBindingModel item,
            BindingResult bindingResult, ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return this.view(PREFIX_URL + "/edit.html", model);
        }
        ItemServiceModel itemToEdit = this.mapObjectToObject(item, ItemServiceModel.class);
        itemToEdit.setInitialPrice(item.getInitialPrice());
        itemToEdit.setId(itemId);
        this.itemService.updateItem(itemToEdit, principal.getName(), this.sellerRole);
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "edit", item.getId()));
        return this.view(PREFIX_URL + "/edit.html", model);
    }

    @GetMapping("/details")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getDetailsView(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(), this.sellerRole);

        List<DetailsAucItemViewModel> auctionsViews = 
                this.mapAllObjectsToObject(this.itemService.getAllAucItemsByItemId(itemId), DetailsAucItemViewModel.class);
        List<DetailsItemPictureViewModel> itemPictures = 
                this.mapAllObjectsToObject(this.itemService.getAllPicturesByEntity(item), 
                        DetailsItemPictureViewModel.class);
        
        itemPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        DetailsItemViewModel itemViewModel = this.mapObjectToObject(item, DetailsItemViewModel.class);
        itemViewModel.setIsItemEditable(this.itemService.isItemEditable(itemId));
        itemViewModel.setAucItems(auctionsViews);
        itemViewModel.setItemPictures(itemPictures);
        model.addObject("detailsItemViewModel", itemViewModel);

        return this.view(PREFIX_URL + "/details.html", model);
    }

    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getDeleteForm(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.sellerRole);
        List<DeleteAucItemViewModel> auctionsViews = 
                this.mapAllObjectsToObject(this.itemService.getAllAuctionsByItemId(itemId), DeleteAucItemViewModel.class);
        
        DeleteItemViewModel itemViewModel = this.mapObjectToObject(item, DeleteItemViewModel.class);
        itemViewModel.setAucItems(auctionsViews);
        model.addObject("deleteItemViewModel", itemViewModel);

        return this.view(PREFIX_URL + "/delete.html", model);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView postDeleteForm(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        this.itemService.deleteItemById(itemId,principal.getName(),this.sellerRole);
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete", itemId));
        return this.redirect("/" + PREFIX_URL + "/all");
    }

    @GetMapping("/gallery/edit/{itemId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView getEditGalleryForm(@PathVariable("itemId") Long itemId, EdGallItemViewModel itemGallery, 
            ModelAndView model, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.sellerRole);      
        List<EdGallItemPictureViewModel> itemPictures = 
                this.mapAllObjectsToObject(this.itemService.getAllPicturesByEntity(item), 
                        EdGallItemPictureViewModel.class);

        itemPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        itemGallery.setItemPictures(itemPictures);
        model.addObject("createItemPictureBindingModel", new CreateItemPictureBindingModel());
        model.addObject("itemGalleryViewModel", itemGallery);

        return this.view(PREFIX_URL + "/editGallery.html", model);
    }

    @PostMapping("/gallery/edit/{itemId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ModelAndView postEditGalleryFrom(@Valid @ModelAttribute CreateItemPictureBindingModel pictureToCreate,  BindingResult bindingResult, @PathVariable("itemId") Long itemId,
            ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addObject("itemGalleryViewModel", new EdGallItemViewModel());
            return this.view(PREFIX_URL + "/editGallery.html", model);
        }
        ItemPictureServiceModel itemPicture = this.mapObjectToObject(pictureToCreate, ItemPictureServiceModel.class);
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.sellerRole);
        itemPicture.setItem(item);
        try {
            this.itemService.savePicture(itemPicture, pictureToCreate.getFile());
        } catch (ReportToUserException ex) {
            model.addObject("itemGalleryViewModel", new EdGallItemViewModel());
            model.addObject(ValidationMesseges.ATTRIBUTE_NAME_FOR_ERROR_MESSAGE,ex.getMessage());
            return this.view(PREFIX_URL + "/editGallery.html", model);
        }
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "add picture to", itemId));
        return this.redirect("/"+PREFIX_URL+"/gallery/edit/"+itemId);
    }
    
    @DeleteMapping(value="/gallery/edit/{itemId}/{itemPictureId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public @ResponseBody ResponseEntity<DetailsItemPictureViewModel> restDeletePicture(@PathVariable("itemId") Long itemId,
           @PathVariable("itemPictureId") Long itemPictureId, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.sellerRole);
        try{
            this.itemService.deleteSinglePicture(item, itemPictureId);
            LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete picture from", itemId));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping(value="/gallery/edit/{itemId}/{itemPictureId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public @ResponseBody ResponseEntity<DetailsItemPictureViewModel> restEditPicture(@PathVariable("itemId") Long itemId,
            @PathVariable("itemPictureId") Long itemPictureId, String description, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.sellerRole);
        try{
            this.itemService.updateDescriptionSinglePicture(item, itemPictureId, description);
            LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "edit picture from", itemId));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    private UserServiceModel getSeller(Principal principal){
        return this.userService.getUserByDomainEmail(principal.getName());
    }

}

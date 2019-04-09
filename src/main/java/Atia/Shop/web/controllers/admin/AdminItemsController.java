/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.web.controllers.admin;

import static Atia.Shop.ShopApplication.LOGGER;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.web.controllers.base.BaseController;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.DTO.admin.items.all.AllItemViewModel;
import Atia.Shop.domain.models.DTO.admin.items.details.DetailsItemPictureViewModel;
import Atia.Shop.domain.models.DTO.admin.items.details.DetailsAucItemViewModel;
import Atia.Shop.domain.models.DTO.admin.items.details.DetailsItemViewModel;
import Atia.Shop.domain.models.DTO.admin.items.delete.DeleteAucItemViewModel;
import Atia.Shop.domain.models.DTO.admin.items.delete.DeleteItemViewModel;
import Atia.Shop.domain.models.DTO.admin.items.edit.EditItemBindingModel;
import Atia.Shop.domain.models.DTO.admin.items.editGallery.EdGallItemPictureViewModel;
import Atia.Shop.domain.models.DTO.admin.items.editGallery.EdGallItemViewModel;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.utils.mapper.MapperValidatorUtil;
import java.security.Principal;
import java.util.List;
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
@RequestMapping("/admin/items")
public class AdminItemsController extends BaseController {
    
    private final static String PREFIX_URL = "admin/items";
    private final String LOG_FORMAT = ">>>ADMIN<<< Admin with domain mail \"%s\" %s item with ID %d";
     

    private final ItemService itemService;
    
    private final UserRolesEnum adminRole;

    @Autowired
    public AdminItemsController(
            ItemService itemService,
            MapperValidatorUtil mapperValidatorUtil) {
        super(mapperValidatorUtil);
        this.itemService = itemService;
        this.adminRole=UserRolesEnum.ADMIN;
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getAll(ModelAndView model) {
        List<AllItemViewModel> itemsList = 
                this.mapAllObjectsToObject( this.itemService.getAllItems(adminRole), AllItemViewModel.class);
        itemsList.sort((a,b)->b.getDateAdded().compareTo(a.getDateAdded()));
        model.addObject("itemsList", itemsList);

        return this.view(PREFIX_URL + "/all.html", model);
    }
    
    @GetMapping("/details")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getDetailsView(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(), this.adminRole);
     
        List<DetailsAucItemViewModel> auctionsViews = 
                this.mapAllObjectsToObject(this.itemService.getAllAucItemsByItemId(itemId), DetailsAucItemViewModel.class);

        List<DetailsItemPictureViewModel> itemPictures = 
                this.mapAllObjectsToObject(this.itemService.getAllPicturesByEntity(item), 
                        DetailsItemPictureViewModel.class);
        
        itemPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        DetailsItemViewModel itemViewModel = this.mapObjectToObject(item, DetailsItemViewModel.class);
        itemViewModel.setIsItemEditable(this.itemService.isItemEditable(itemId));
        itemViewModel.setAucItem(auctionsViews);
        itemViewModel.setItemPictures(itemPictures);
        model.addObject("detailsItemViewModel", itemViewModel);

        return this.view(PREFIX_URL + "/details.html", model);
    }
    
    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getDeleteForm(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.adminRole);
        List<DeleteAucItemViewModel> auctionsViews = 
                this.mapAllObjectsToObject(this.itemService.getAllAucItemsByItemId(itemId), DeleteAucItemViewModel.class);
        
        DeleteItemViewModel itemViewModel = this.mapObjectToObject(item, DeleteItemViewModel.class);
        itemViewModel.setAucItems(auctionsViews);
        model.addObject("deleteItemViewModel", itemViewModel);
        
        return this.view(PREFIX_URL + "/delete.html", model);
    }
    
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView postDeleteForm(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        this.itemService.deleteItemById(itemId,principal.getName(),this.adminRole);
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete", itemId));
        return this.redirect("/" + PREFIX_URL + "/all");
    }
    
    @GetMapping("/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getEditForm(@RequestParam("id") Long itemId, ModelAndView model, Principal principal) {
        EditItemBindingModel item =
                this.mapObjectToObject(
                        this.itemService.getItemById(itemId, principal.getName(), this.adminRole), EditItemBindingModel.class);
        model.addObject("editItemBindingModel", item);

        return this.view(PREFIX_URL + "/edit.html", model);
    }
    
    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView postEditForm(@RequestParam("id") Long itemId, @Valid @ModelAttribute EditItemBindingModel item,
            BindingResult bindingResult, ModelAndView model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return this.view(PREFIX_URL + "/edit.html", model);
        }
        ItemServiceModel itemToEdit = this.mapObjectToObject(item, ItemServiceModel.class);
        itemToEdit.setInitialPrice(item.getInitialPrice());
        itemToEdit.setId(itemId);
        this.itemService.updateItem(itemToEdit, principal.getName(), this.adminRole);
        LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "edit", itemId));
        return this.view(PREFIX_URL + "/edit.html", model);
    }
    
    @GetMapping("/gallery/edit/{itemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView getEditGalleryForm(@PathVariable("itemId") Long itemId, EdGallItemViewModel itemGallery, 
            ModelAndView model, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.adminRole);      
        List<EdGallItemPictureViewModel> itemPictures = 
                this.mapAllObjectsToObject(this.itemService.getAllPicturesByEntity(item), 
                        EdGallItemPictureViewModel.class);

        itemPictures.sort((a,b)->a.getDateAdded().compareTo(b.getDateAdded()));
        itemGallery.setItemPictures(itemPictures);
        model.addObject("itemGalleryViewModel", itemGallery);

        return this.view(PREFIX_URL + "/editGallery.html", model);
    }
    
    @DeleteMapping(value="/gallery/edit/{itemId}/{itemPictureId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public @ResponseBody ResponseEntity<DetailsItemPictureViewModel> restDeletePicture(@PathVariable("itemId") Long itemId,
           @PathVariable("itemPictureId") Long itemPictureId, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.adminRole);
        try{
            this.itemService.deleteSinglePicture(item, itemPictureId);
            LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "delete picture from", itemId));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping(value="/gallery/edit/{itemId}/{itemPictureId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public @ResponseBody ResponseEntity<DetailsItemPictureViewModel> restEditPicture(@PathVariable("itemId") Long itemId,
            @PathVariable("itemPictureId") Long itemPictureId, String description, Principal principal) {
        ItemServiceModel item = this.itemService.getItemById(itemId, principal.getName(),this.adminRole);
        try{
            this.itemService.updateDescriptionSinglePicture(item, itemPictureId, description);
            LOGGER.info(String.format(LOG_FORMAT, principal.getName(), "edit picture from", itemId));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
}

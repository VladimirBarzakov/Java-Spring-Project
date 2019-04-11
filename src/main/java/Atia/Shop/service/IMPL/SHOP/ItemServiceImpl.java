/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.SHOP;

import Atia.Shop.config.errorMesseges.ReportToUserExceptionMessage;
import Atia.Shop.config.errorMesseges.SystemExceptionMessage;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.AuctionedItem;
import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.domain.repositories.AuctionedItemsRepository;
import Atia.Shop.domain.repositories.ItemRepository;
import Atia.Shop.exeptions.base.InvalidRoleException;
import Atia.Shop.service.API.Pictures.ItemPictureService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final AuctionedItemsRepository aucItemsRepository;
    private final ItemPictureService itemPictureService;
    private final InputValidator inputValidator;

    @Autowired
    public ItemServiceImpl(ModelMapper modelMapper,
            ItemRepository itemRepository,
            AuctionedItemsRepository aucItemsRepository,
            ItemPictureService itemPictureService,
            InputValidator validationUtil) {
        this.modelMapper = modelMapper;
        this.itemRepository = itemRepository;
        this.aucItemsRepository = aucItemsRepository;
        this.itemPictureService = itemPictureService;
        this.inputValidator = validationUtil;

    }

    private Item getItemEntityById(Long id) {
        this.inputValidator.validateObject(id);
        Item item = this.itemRepository.findById(id).orElse(null);
        if (item == null) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.ITEM_INVALID_ID);
        }
        return item;
    }

    private AuctionedItem getAucItemEntityById(Long id) {
        this.inputValidator.validateObject(id);
        AuctionedItem item = this.aucItemsRepository.findById(id).orElse(null);
        if (item == null) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.ITEM_AUCTIONED_INVALID_ID);
        }
        return item;
    }

    private List<AuctionedItem> getAllAuctionedItemsByItemId(Long itemId) {
        return this.aucItemsRepository.findAllByParentId(itemId);
    }

    private void updateAllAucItems(List<AuctionedItem> aucItems) {
        this.aucItemsRepository.saveAll(aucItems);
    }

    @Override
    public ItemServiceModel createItem(ItemServiceModel itemToCreate, UserServiceModel seller) {
        this.inputValidator.validateObject(itemToCreate, seller);
        User user = this.modelMapper.map(seller, User.class);
        Item item = this.modelMapper.map(itemToCreate, Item.class);
        item.setDateAdded(new Date());
        item.setSeller(user);
        item = this.itemRepository.save(item);
        return this.modelMapper.map(item, ItemServiceModel.class);
    }

    private List<ItemServiceModel> getAllItemsBySeller(UserServiceModel seller) {
        User user = this.modelMapper.map(seller, User.class);
        return this.itemRepository.findAllBySeller(user)
                .stream()
                .map(x -> this.modelMapper.map(x, ItemServiceModel.class))
                .collect(Collectors.toList());
    }

    private List<ItemServiceModel> getAllAvailableItemsBySeller(UserServiceModel seller) {
        User user = this.modelMapper.map(seller, User.class);
        return this.itemRepository.findAllBySellerAndQuantityGreaterThan(user, 0)
                .stream()
                .map(x -> this.modelMapper.map(x, ItemServiceModel.class))
                .collect(Collectors.toList());
    }

    private ItemServiceModel getItemById(Long id) {
        return this.modelMapper.map(this.getItemEntityById(id), ItemServiceModel.class);
    }

    private boolean updateItem(ItemServiceModel updatedItem) {
        Item itemEntity = this.getItemEntityById(updatedItem.getId());
        updatedItem.setDateAdded(itemEntity.getDateAdded());
        updatedItem.setSeller(this.modelMapper.map(itemEntity.getSeller(), UserServiceModel.class));
        this.modelMapper.map(updatedItem, itemEntity);
        itemEntity.setInitialPrice(updatedItem.getInitialPrice());
        itemEntity = this.itemRepository.save(itemEntity);
        List<AuctionedItem> aucItems = this.getAllAuctionedItemsByItemId(itemEntity.getId())
                .stream()
                .filter(x -> x.getAuction().getStatus().compareTo(AuctionStatus.SELLED) < 0)
                .collect(Collectors.toList());
        for (AuctionedItem aucItem : aucItems) {
            this.mapAuctionedItemToItem(aucItem, itemEntity);
        }
        this.aucItemsRepository.saveAll(aucItems);
        return true;
    }

    private boolean deleteItemById(Long itemId) {
        Item item = this.getItemEntityById(itemId);
        List<AuctionedItem> auctionedItems = this.getAllAuctionedItemsByItemId(itemId);
        auctionedItems.forEach(x -> x.setParent(null));
            if(item.getThumbnail()!=null){
                item.setThumbnail(null);
                item=this.itemRepository.save(item);
            }
            this.updateAllAucItems(auctionedItems);
            this.itemPictureService.deleteAllPicturesByItem(this.modelMapper.map(item, ItemServiceModel.class));
            this.itemRepository.deleteById(itemId);
        return true;
    }

    @Override
    public boolean isItemEditable(Long id) {
        this.inputValidator.validateObject(id);
        return true;

    }

    @Override
    public boolean isItemDeletable(Long id) {
        this.inputValidator.validateObject(id);
        return !this.getAllAucItemsByItemId(id)
                .stream()
                .anyMatch(auctionedItem
                        -> auctionedItem.getAuction().getStatus().compareTo(AuctionStatus.LIVE) >= 0
                && auctionedItem.getAuction().getStatus().compareTo(AuctionStatus.SELLED) < 0
                );
    }

    @Override
    public List<AuctionedItemServiceModel> findAllAucItemsByAuctionId(Long auctionId) {
        this.inputValidator.validateObject(auctionId);
        return this.aucItemsRepository.findAllByAuctionId(auctionId)
                .stream()
                .map(x -> this.modelMapper.map(x, AuctionedItemServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuctionedItemServiceModel> getAllAucItemsByItemId(Long itemId) {
        this.inputValidator.validateObject(itemId);
        return this.aucItemsRepository.findAllByParentId(itemId)
                .stream()
                .map(x -> this.modelMapper.map(x, AuctionedItemServiceModel.class))
                .collect(Collectors.toList());
    }

    private void mapItemToAuctionedItem(Item item, AuctionedItem aucItem) {
        item.setName(aucItem.getItemName());
        item.setDescription(aucItem.getItemDescription());
        item.setLocation(aucItem.getItemLocation());
        item.setInitialPrice(aucItem.getItemPrice());
    }

    private void mapAuctionedItemToItem(AuctionedItem aucItem, Item item) {
        aucItem.setItemName(item.getName());
        aucItem.setItemDescription(item.getDescription());
        aucItem.setItemPrice(item.getInitialPrice());
        aucItem.setItemLocation(item.getLocation());
    }

    @Override
    public boolean addToAuction(
            List<ItemServiceModel> itemstoAdd,
            AuctionServiceModel auctionToAddTo) throws ReportToUserException {
        this.inputValidator.validateObject(itemstoAdd, auctionToAddTo);
        Auction auction = this.modelMapper.map(auctionToAddTo, Auction.class);
        List<Item> changedItems = new ArrayList();
        List<AuctionedItem> changedAuctionedItem = new ArrayList();
        Item itemEntity = null;
        AuctionedItem auctionedItem = null;
        for (ItemServiceModel itemToAdd : itemstoAdd) {
            if (itemToAdd.getQuantity() < 0) {
                throw new ReportToUserException(ReportToUserExceptionMessage.ITEM_INVALID_QUANTITY_TO_ADD);
            }
            itemEntity = this.getItemEntityById(itemToAdd.getId());
            auctionedItem = this.aucItemsRepository.findOneByParentAndAuction(itemEntity, auction);
            if (auctionedItem == null) {
                auctionedItem = this.modelMapper.map(itemEntity, AuctionedItem.class);
                this.mapItemToAuctionedItem(itemEntity, auctionedItem);
                auctionedItem.setParent(itemEntity);
                auctionedItem.setAuction(auction);
                auctionedItem.setQuantity(0);
            }
            if (itemEntity.getQuantity() - itemToAdd.getQuantity() < 0) {
                throw new ReportToUserException(ReportToUserExceptionMessage.ITEM_TOO_LARGE_QUANTITY_TO_ADD + itemEntity.getName());
            }
            auctionedItem.setQuantity(auctionedItem.getQuantity() + itemToAdd.getQuantity());
            itemEntity.setQuantity(itemEntity.getQuantity() - itemToAdd.getQuantity());
            changedItems.add(itemEntity);
            changedAuctionedItem.add(auctionedItem);
        }

        this.itemRepository.saveAll(changedItems);
        this.aucItemsRepository.saveAll(changedAuctionedItem);

        return true;
    }

    @Override
    public List<AuctionServiceModel> getAllAuctionsByItemId(Long itemId) {
        this.inputValidator.validateObject(itemId);
        return this.getAllAucItemsByItemId(itemId)
                .stream()
                .map(x -> x.getAuction())
                .map(x -> this.modelMapper.map(x, AuctionServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean removeAllFromAuction(List<AuctionedItemServiceModel> aucItemsToremove, boolean deleteAucItemsFlag) {
        this.inputValidator.validateObject(aucItemsToremove);
        List<Item> changedItems = new ArrayList();
        List<AuctionedItem> aucItemsToRemove = new ArrayList();
        List<AuctionedItem> aucItemsToChange = new ArrayList();
        AuctionedItem auctionedItem = null;
        for (AuctionedItemServiceModel aucItemToremove : aucItemsToremove) {
            if (aucItemToremove.getQuantity() < 0) {
                throw new ReporPartlyToUserException(ReportToUserExceptionMessage.ITEM_INVALID_QUANTITY_TO_REMOVE);
            }
            auctionedItem = this.getAucItemEntityById(aucItemToremove.getId());
            if (auctionedItem.getParent() != null && auctionedItem.getAuction().getStatus().compareTo(AuctionStatus.LIVE) <= 0) {
                //Restore Item quantity from auctioned item if item exists
                Item item = this.getItemEntityById(auctionedItem.getParent().getId());
                item.setQuantity(item.getQuantity() + aucItemToremove.getQuantity());
                changedItems.add(item);
            }
            if (deleteAucItemsFlag || auctionedItem.getAuction().getStatus().compareTo(AuctionStatus.LIVE) != 0) {
                //Keep quantity for auctioned items that belogn to auction witch is becomming "SELLED" or "EXPIRED" for archive purpose
                auctionedItem.setQuantity(auctionedItem.getQuantity() - aucItemToremove.getQuantity());
            }
            if (auctionedItem.getQuantity() < 0) {
                throw new ReporPartlyToUserException(SystemExceptionMessage.ITEM_AUCTIONED_TOO_LARGE_QUANTITY_TO_REMOVE + auctionedItem.getItemName());
            }
            if (auctionedItem.getAuction().getStatus().compareTo(AuctionStatus.LIVE) == 0 && !deleteAucItemsFlag) {
                //Auctined items shoud not be removed when updating from "LIVE" to "EXPIRED"
            } else if (auctionedItem.getQuantity() == 0 || deleteAucItemsFlag) {
                aucItemsToRemove.add(auctionedItem);
            } else {
                aucItemsToChange.add(auctionedItem);
            }
        }
        this.itemRepository.saveAll(changedItems);
        this.aucItemsRepository.deleteAll(aucItemsToRemove);
        this.aucItemsRepository.saveAll(aucItemsToChange);
        return true;
    }

    private ItemServiceModel sellerGetItemById(Long itemId, String sellerEmail) {
        ItemServiceModel item = this.modelMapper.map(this.getItemEntityById(itemId), ItemServiceModel.class);
        this.verifyItemSeller(item, sellerEmail);
        return item;
    }

    private boolean sellerUpdateItem(ItemServiceModel itemServiceModel, String sellerEmail) {
        this.verifyItemSeller(itemServiceModel.getId(), sellerEmail);
        this.verifyIsItemEtidable(itemServiceModel.getId());
        return this.updateItem(itemServiceModel);
    }

    private boolean sellerDeleteItemById(Long itemId, String sellerEmail) {
        ItemServiceModel item = this.getItemById(itemId);
        this.verifyItemSeller(item, sellerEmail);
        this.verifyIsItemDeletable(itemId);
        return this.deleteItemById(itemId);
    }

    private void verifyItemSeller(ItemServiceModel itemServiceModel, String sellerEmail) {
        if (!itemServiceModel.getSeller().getDomainEmail().equals(sellerEmail)) {
            throw new ReporPartlyToUserException(sellerEmail + SystemExceptionMessage.ITEM_WRONG_SELLER);
        }
    }

    private void verifyItemSeller(Long itemId, String sellerEmail) {
        if (!this.getItemById(itemId).getSeller().getDomainEmail().equals(sellerEmail)) {
            throw new ReporPartlyToUserException(sellerEmail + SystemExceptionMessage.ITEM_WRONG_SELLER);
        }
    }

    private void verifyIsItemEtidable(Long itemId) {
        if (!this.isItemEditable(itemId)) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.ITEM_IS_NOT_EDITABLE + itemId);
        }
    }

    private void verifyIsItemDeletable(Long itemId) {
        if (!this.isItemDeletable(itemId)) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.ITEM_IS_NOT_DELETABLE + itemId);
        }
    }

    @Override
    public AuctionedItemServiceModel getAucItemById(Long aucItemId) {
        this.inputValidator.validateObject(aucItemId);
        return this.modelMapper.map(this.getAucItemEntityById(aucItemId), AuctionedItemServiceModel.class);
    }

    @Override
    public ItemPictureServiceModel savePicture(ItemPictureServiceModel pictureDetailsFile, MultipartFile file) throws ReportToUserException {
        ItemPictureServiceModel savedPicture = this.itemPictureService.savePicture(pictureDetailsFile, file);
        ItemServiceModel item = pictureDetailsFile.getItem();
        if(item.getThumbnail()==null){
            item.setThumbnail(savedPicture);
            Item updatedItem = this.modelMapper.map(item, Item.class);
            this.itemRepository.save(updatedItem);
        }
        return savedPicture;
    }

    @Override
    public List<ItemPictureServiceModel> getAllPicturesByEntity(ItemServiceModel item) {
        return this.itemPictureService.getAllPicturesByEntity(item);
    }

    @Override
    public boolean deleteSinglePicture(ItemServiceModel item, Long pictureId) {
        if(item.getThumbnail()!=null && item.getThumbnail().getId()==pictureId.longValue()){
            item.setThumbnail(null);
            Item updatedItem = this.modelMapper.map(item, Item.class);
            this.itemRepository.save(updatedItem);
        }
        return this.itemPictureService.deleteSinglePicture(item, pictureId);
    }

    @Override
    public boolean updateDescriptionSinglePicture(ItemServiceModel item, Long pictureId, String description) {
        return this.itemPictureService.updateDescriptionSinglePicture(item, pictureId, description);
    }

    @Override
    public List<ItemServiceModel> getAllItemsByCreator(UserServiceModel seller, UserRolesEnum role) {
        this.inputValidator.validateObject(seller, role);
        if (role.isRole(UserRolesEnum.SELLER)) {
            return this.getAllItemsBySeller(seller);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public List<ItemServiceModel> getAllAvailableItems(UserServiceModel seller, UserRolesEnum role) {
        this.inputValidator.validateObject(seller, role);
        if (role.isRole(UserRolesEnum.SELLER) || role.isRole(UserRolesEnum.ADMIN)) {
            return this.getAllAvailableItemsBySeller(seller);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public ItemServiceModel getItemById(Long itemId, String sellerEmail, UserRolesEnum role) {
        this.inputValidator.validateObject(itemId, role);
        this.inputValidator.validateString(sellerEmail);
        if (role.isRole(UserRolesEnum.SELLER)) {
            return this.sellerGetItemById(itemId, sellerEmail);
        } else if (role.isRole(UserRolesEnum.ADMIN)) {
            return this.getItemById(itemId);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public boolean updateItem(ItemServiceModel updatedItem, String sellerEmail, UserRolesEnum role) {
        this.inputValidator.validateObject(updatedItem, role);
        this.inputValidator.validateString(sellerEmail);
        if (role.isRole(UserRolesEnum.SELLER)) {
            return this.sellerUpdateItem(updatedItem, sellerEmail);
        } else if (role.isRole(UserRolesEnum.ADMIN)) {
            return this.updateItem(updatedItem);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public boolean deleteItemById(Long itemId, String sellerEmail, UserRolesEnum role) {
        this.inputValidator.validateObject(itemId, role);
        this.inputValidator.validateString(sellerEmail);
        if (role.isRole(UserRolesEnum.SELLER)) {
            return this.sellerDeleteItemById(itemId, sellerEmail);
        } else if (role.isRole(UserRolesEnum.ADMIN)) {
            return this.deleteItemById(itemId);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public List<ItemServiceModel> getAllItems(UserRolesEnum role) {
        this.inputValidator.validateObject(role);
        if (role.isRole(UserRolesEnum.ADMIN)) {
            return this.itemRepository.findAll()
                    .stream().map(x -> this.modelMapper.map(x, ItemServiceModel.class))
                    .collect(Collectors.toList());
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }
}

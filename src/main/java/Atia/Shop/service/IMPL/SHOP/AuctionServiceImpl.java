/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.SHOP;

import Atia.Shop.config.errorMesseges.SystemExceptionMessage;
import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.Bid;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.domain.repositories.AuctionRepository;
import Atia.Shop.exeptions.base.InvalidRoleException;
import Atia.Shop.service.API.Pictures.AuctionPictureService;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Service
@Transactional
public class AuctionServiceImpl implements AuctionService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final AuctionRepository auctionRepository;
    private final AuctionPictureService auctionPictureService;
    private final InputValidator inputValidator;

    private enum AuctionStatusCheckKeyWord {
        EDIT, DELETE, BID, SHOW_TO_BUYER, SHOW_TO_WINNER, ARCHIVE
    };

    @Autowired
    public AuctionServiceImpl(
            ModelMapper modelMapper,
            UserService userService,
            ItemService itemService,
            AuctionRepository auctionRepostiory,
            AuctionPictureService auctionPictureService,
            InputValidator inputValidator) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.itemService = itemService;
        this.auctionRepository = auctionRepostiory;
        this.auctionPictureService = auctionPictureService;
        this.inputValidator = inputValidator;
    }

    @Override
    public boolean isAuctionEmpty(Long id) {
        this.inputValidator.validateObject(id);
        return this.itemService.findAllAucItemsByAuctionId(id).isEmpty();
    }

    private Auction getAuctioEntityById(Long id) {
        if (id == null) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_ID);
        }
        Auction auction = this.auctionRepository.findById(id).orElse(null);
        if (auction == null) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_ID);
        }
        return auction;

    }

    private Auction getNonArchiveAuctionEntityById(Long id) {
        Auction auction = this.getAuctioEntityById(id);
        if (auction.getStatus().equals(AuctionStatus.ARCHIVE)) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_ID);
        }
        return auction;
    }

    private Auction getArchiveAuctionEntityById(Long id) {
        Auction auction = this.getAuctioEntityById(id);
        if (!auction.getStatus().equals(AuctionStatus.ARCHIVE)) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_ID);
        }
        return auction;
    }

    @Override
    public AuctionServiceModel createAuction(
            AuctionServiceModel auctionToCreate,
            String sellerEmail) throws ReportToUserException {
        this.inputValidator.validateObject(
                auctionToCreate,
                auctionToCreate.getDateStarted(),
                auctionToCreate.getDateExpired());
        this.inputValidator.validateString(sellerEmail);
        this.verifyStartingDateIs_BEFORE_ExpiredDate(
                auctionToCreate.getDateStarted(),
                auctionToCreate.getDateExpired());
        this.verifyStartingDateIs_AFTER_Now(auctionToCreate.getDateStarted());
        User seller = this.modelMapper.map(this.userService.getUserByDomainEmail(sellerEmail), User.class);
        auctionToCreate.setStatus(AuctionStatus.CREATED);
        Auction auctionEntity = this.modelMapper.map(auctionToCreate, Auction.class);
        auctionEntity.setSeller(seller);
        auctionEntity = this.auctionRepository.saveAndFlush(auctionEntity);
        return this.modelMapper.map(auctionEntity, AuctionServiceModel.class);
    }

    private List<AuctionServiceModel> getAllAuctionsBySeller(String sellerEmail) {
        User seller = this.modelMapper.map(this.userService.getUserByDomainEmail(sellerEmail), User.class);
        return this.auctionRepository.findAllBySellerAndStatusNot(seller, AuctionStatus.ARCHIVE)
                .stream()
                .map(x -> this.modelMapper.map(x, AuctionServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public AuctionServiceModel getAuctionById(Long id) {
        this.inputValidator.validateObject(id);
        return this.modelMapper.map(this.getNonArchiveAuctionEntityById(id), AuctionServiceModel.class);
    }

    @Override
    public boolean updateAuction(
            AuctionServiceModel updatedAuction,
            List<ItemServiceModel> itemtsToAdd,
            List<AuctionedItemServiceModel> aucItemstoRemove) throws ReportToUserException {
        this.inputValidator.validateObject(
                updatedAuction,
                updatedAuction.getDateStarted(),
                updatedAuction.getDateExpired(),
                updatedAuction.getId(),
                itemtsToAdd,
                aucItemstoRemove);
        this.verifyStartingDateIs_BEFORE_ExpiredDate(
                updatedAuction.getDateStarted(),
                updatedAuction.getDateExpired());
        Auction auctionDB = this.getNonArchiveAuctionEntityById(updatedAuction.getId());
        this.mapAuctionToAuction(updatedAuction, auctionDB);

        this.itemService.addToAuction(itemtsToAdd, updatedAuction);
        if (updatedAuction.getStatus() != AuctionStatus.SELLED && updatedAuction.getStatus() != AuctionStatus.EXPIRED) {
            this.removePicturesOfDroppedAuctionedItems(updatedAuction, aucItemstoRemove);
        }
        this.itemService.removeAllFromAuction(aucItemstoRemove, false);
        this.auctionRepository.save(auctionDB);
        return true;
    }

    private void removePicturesOfDroppedAuctionedItems(AuctionServiceModel updatedAuction, List<AuctionedItemServiceModel> aucItemstoRemove) {
        Map<Long, AuctionedItemServiceModel> currentAucItems
                = this.itemService.findAllAucItemsByAuctionId(updatedAuction.getId())
                        .stream()
                        .collect(Collectors.toMap(AuctionedItemServiceModel::getId, x -> x));
        List<AucItemPictureWrapperServiceModel> auctionPictutes = this.getAllPicturesByEntity(updatedAuction);
        AuctionedItemServiceModel currentItem = null;
        for (AuctionedItemServiceModel toRemove : aucItemstoRemove) {
            currentItem = null;
            if (currentAucItems.containsKey(toRemove.getId())) {
                currentItem = currentAucItems.get(toRemove.getId());
            }
            if (currentItem != null && currentItem.getQuantity().equals(toRemove.getQuantity())) {
                auctionPictutes
                        .stream()
                        .filter(x -> x.getAuctionedItemId().equals(toRemove.getId()))
                        .forEach(x -> {
                            try {
                                this.adminDeletePicture(x);
                            } catch (ReportToUserException ex) {
                                //Ignore
                            }
                        });
            }
        }
    }

    private void mapAuctionToAuction(AuctionServiceModel source, Auction destination) {
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());
        destination.setDateStarted(source.getDateStarted());
        destination.setDateExpired(source.getDateExpired());
        destination.setInitialPrice(source.getInitialPrice());
        destination.setStatus(source.getStatus());

        destination.setWinPrice(source.getWinPrice());
        destination.setBestBid(source.getBestBid() == null ? null : this.modelMapper.map(source.getBestBid(), Bid.class));
        destination.setAuctionStrategy(source.getAuctionStrategy());
    }

    @Override
    public boolean isAuctionEditable(Long id) {
        this.inputValidator.validateObject(id);
        return this.getNonArchiveAuctionEntityById(id).getStatus().compareTo(AuctionStatus.SELLED) < 0;
    }

    @Override
    public boolean isAuctionEditable(AuctionServiceModel auctionModel) {
        this.inputValidator.validateObject(auctionModel, auctionModel.getStatus());
        if (auctionModel.getStatus().compareTo(AuctionStatus.ARCHIVE) == 0) {
            throw new ReporPartlyToUserException(
                    SystemExceptionMessage.AUCTION_INVALID_AUCTION_STATUS + AuctionStatus.ARCHIVE.name());
        }
        return auctionModel.getStatus().compareTo(AuctionStatus.SELLED) < 0;
    }

    @Override
    public boolean isAuctionDeletable(Long id) {
        this.inputValidator.validateObject(id);
        Auction auction = this.getNonArchiveAuctionEntityById(id);
        return auction.getStatus().compareTo(AuctionStatus.LIVE) < 0
                || auction.getStatus().compareTo(AuctionStatus.EXPIRED) == 0;
    }

    @Override
    public boolean isAuctionDeletable(AuctionServiceModel auctionModel) {
        this.inputValidator.validateObject(auctionModel, auctionModel.getStatus());
        if (auctionModel.getStatus().compareTo(AuctionStatus.ARCHIVE) == 0) {
            throw new ReporPartlyToUserException(
                    SystemExceptionMessage.AUCTION_INVALID_AUCTION_STATUS + AuctionStatus.ARCHIVE.name());
        }
        return auctionModel.getStatus().compareTo(AuctionStatus.LIVE) < 0
                || auctionModel.getStatus().compareTo(AuctionStatus.EXPIRED) == 0;
    }

    @Override
    public boolean isAuctionBiddable(AuctionServiceModel auctionModel) {
        this.inputValidator.validateObject(auctionModel, auctionModel.getStatus());
        return auctionModel.getStatus().compareTo(AuctionStatus.LIVE) >= 0
                && auctionModel.getStatus().compareTo(AuctionStatus.SELLED) < 0;
    }

    @Override
    public boolean isAuctionWinned(AuctionServiceModel auctionModel) {
        this.inputValidator.validateObject(auctionModel);
        return auctionModel.getAuctionWinner() != null;
    }

    @Override
    public boolean isArchivable(AuctionServiceModel auctionModel) {
        this.inputValidator.validateObject(auctionModel, auctionModel.getStatus());
        if (auctionModel.getStatus().compareTo(AuctionStatus.ARCHIVE) == 0) {
            throw new ReporPartlyToUserException(
                    SystemExceptionMessage.AUCTION_INVALID_AUCTION_STATUS + AuctionStatus.ARCHIVE.name());
        }
        return auctionModel.getStatus().equals(AuctionStatus.SELLED)
                || auctionModel.getStatus().equals(AuctionStatus.EXPIRED);
    }

    @Override
    public List<AuctionServiceModel> getAllAuctions() {
        return this.auctionRepository.findByStatusNot(AuctionStatus.ARCHIVE)
                .stream()
                .map(x -> this.modelMapper.map(x, AuctionServiceModel.class))
                .collect(Collectors.toList());
    }

    private boolean deleteAuction(AuctionServiceModel auctionToDelete) {
        this.inputValidator.validateObject(auctionToDelete, auctionToDelete.getId(), auctionToDelete.getStatus());
        List<AuctionedItemServiceModel> aucItems = this.itemService.findAllAucItemsByAuctionId(auctionToDelete.getId());
        if (auctionToDelete.getThumbnail() != null) {
            auctionToDelete.setThumbnail(null);
            this.auctionRepository.save(this.modelMapper.map(auctionToDelete, Auction.class));
        }
        this.auctionPictureService.deleteAllPicturesFromAuction(auctionToDelete);
        this.itemService.removeAllFromAuction(aucItems, true);
        this.auctionRepository.deleteById(auctionToDelete.getId());

        return true;
    }

    @Override
    public List<AuctionServiceModel> getAllAuctionsByWinner(String sellerEmail) {
        this.inputValidator.validateString(sellerEmail);
        User seller = this.modelMapper.map(this.userService.getUserByDomainEmail(sellerEmail), User.class);
        return this.auctionRepository.findAllByAuctionWinner(seller)
                .stream()
                .map(x -> this.modelMapper.map(x, AuctionServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuctionServiceModel> getAllAuctionsByStatus(AuctionStatus auctionStatus) {
        this.inputValidator.validateObject(auctionStatus);
        return this.auctionRepository.findAllByStatus(auctionStatus)
                .stream()
                .map(x -> this.modelMapper.map(x, AuctionServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean saveAll(List<AuctionServiceModel> auctions) {
        this.inputValidator.validateObject(auctions);
        this.auctionRepository.saveAll(auctions
                .stream()
                .map(x -> this.modelMapper.map(x, Auction.class))
                .collect(Collectors.toList()));
        return true;
    }

    @Override
    public boolean deleteAllAuctions(List<AuctionServiceModel> auctions) {
        this.inputValidator.validateObject(auctions);
        for (AuctionServiceModel auction : auctions) {
            this.deleteAuction(auction);
        }
        return true;
    }

    @Override
    public boolean expireAuction(AuctionServiceModel auction) {
        this.inputValidator.validateObject(auction, auction.getId());
        auction.setStatus(AuctionStatus.EXPIRED);
        this.itemService.removeAllFromAuction(this.itemService.findAllAucItemsByAuctionId(auction.getId()), false);
        this.auctionRepository.save(this.modelMapper.map(auction, Auction.class));
        return true;
    }

    private boolean checkIfDateIsBeforeOtherDate(Date firstDate, Date secondDate) {
        return firstDate.compareTo(secondDate) < 0;
    }

    private boolean checkIfDateIsAfterNow(Date date) {
        return Timestamp.valueOf(LocalDateTime.now()).compareTo(date) < 0;
    }

    private void verifyStartingDateIs_BEFORE_ExpiredDate(Date startDate, Date endDate) throws ReportToUserException {
        if (!this.checkIfDateIsBeforeOtherDate(startDate, endDate)) {
            throw new ReportToUserException(ValidationMesseges.AUCTION_STARTING_DATE_CANNOT_BE_AFTER_EXPIRING_DATE);
        }
    }

    private void verifyStartingDateIs_AFTER_Now(Date startDate) throws ReportToUserException {
        if (!this.checkIfDateIsAfterNow(startDate)) {
            throw new ReportToUserException(ValidationMesseges.AUCTION_STARTING_DATE_CANNOT_BE_BEFORE_NOW);
        }
    }

    private void verifyAuctionCreator(AuctionServiceModel auctionServiceModel, String sellerEmail) {
        if (!auctionServiceModel.getSeller().getDomainEmail().equals(sellerEmail)) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_CREATOR);
        }
    }

    private void verifyAuctionStatus(AuctionServiceModel auction, AuctionStatusCheckKeyWord keyWord) {
        switch (keyWord) {
            case EDIT:
                if (this.isAuctionEditable(auction)) {
                    return;
                }
                break;
            case DELETE:
                if (this.isAuctionDeletable(auction)) {
                    return;
                }
                break;
            case BID:
                if (this.isAuctionBiddable(auction)) {
                    return;
                }
                break;
            case SHOW_TO_WINNER:
                if (this.isAuctionWinned(auction)) {
                    return;
                }
                break;
            case SHOW_TO_BUYER:
                if (auction.getStatus().equals(AuctionStatus.LIVE)) {
                    return;
                }
                break;
            case ARCHIVE:
                if (this.isArchivable(auction)) {
                    return;
                }
                break;
            default:
                throw new ReporPartlyToUserException("No such action");
        }
        throw new ReporPartlyToUserException(
                SystemExceptionMessage.AUCTION_INVALID_AUCTION_STATUS + auction.getStatus().name());
    }

    @Override
    public boolean verifyAuctionWinner(String userEmail, AuctionServiceModel auction) {
        this.inputValidator.validateObject(auction);
        this.inputValidator.validateString(userEmail);
        return this.isAuctionWinned(auction)
                && auction.getAuctionWinner().getDomainEmail().equals(userEmail);
    }

    private boolean sellerUpdateAuction(
            AuctionServiceModel updatedAuction,
            List<ItemServiceModel> itemtsToAdd,
            List<AuctionedItemServiceModel> aucItemstoRemove,
            String sellerEmail) throws ReportToUserException {
        this.verifyAuctionCreator(updatedAuction, sellerEmail);
        this.verifyAuctionStatus(updatedAuction, AuctionStatusCheckKeyWord.EDIT);
        if (updatedAuction.getStatus().equals(AuctionStatus.LIVE)) {
            if (!this.checkIfDateIsAfterNow(updatedAuction.getDateExpired())) {
                throw new ReportToUserException(ValidationMesseges.AUCTION_EXPIRING_DATE_AFTER_THIS_MOMENT);
            }
            Auction auctionEntity = this.getNonArchiveAuctionEntityById(updatedAuction.getId());

            auctionEntity.setDateExpired(updatedAuction.getDateExpired());

            List<ItemServiceModel> emptyItemsList = new ArrayList();
            List<AuctionedItemServiceModel> emptyAucItemsList = new ArrayList();

            return this.updateAuction(
                    this.modelMapper.map(auctionEntity, AuctionServiceModel.class),
                    emptyItemsList,
                    emptyAucItemsList);
        }
        this.verifyStartingDateIs_AFTER_Now(updatedAuction.getDateStarted());
        return this.updateAuction(updatedAuction, itemtsToAdd, aucItemstoRemove);
    }

    private boolean sellerDeleteAuction(AuctionServiceModel auction, String sellerEmail) {
        this.verifyAuctionCreator(auction, sellerEmail);
        this.verifyAuctionStatus(auction, AuctionStatusCheckKeyWord.DELETE);
        return this.deleteAuction(auction);
    }

    @Override
    public List<AucItemPictureWrapperServiceModel> getAllPicturesByEntity(AuctionServiceModel auction) {
        this.inputValidator.validateObject(auction);
        return this.auctionPictureService.getAllPicturesByEntity(auction);
    }

    @Override
    public AucItemPictureWrapperServiceModel addPicture(
            AucItemPictureWrapperServiceModel pictureWrapper,
            String itemPictureId, String sellerMail) throws ReportToUserException {
        this.inputValidator.validateObject(
                pictureWrapper,
                pictureWrapper.getAuctionedItemId(),
                pictureWrapper.getAuctionId());
        this.inputValidator.validateString(itemPictureId, sellerMail);
        AuctionedItemServiceModel aucItem = this.itemService.getAucItemById(pictureWrapper.getAuctionedItemId());
        if (!aucItem.getAuction().getId().equals(pictureWrapper.getAuctionId())) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_AUC_ITEM_DOES_NOT_BELONG_TO_AUCTION);
        }
        if (!aucItem.getAuction().getSeller().getDomainEmail().equals(sellerMail)) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_CREATOR);
        }
        pictureWrapper.setAuctionedItemName(aucItem.getItemName());
        AucItemPictureWrapperServiceModel savedPictureWrapper = this.auctionPictureService.addPicture(pictureWrapper, itemPictureId, sellerMail);
        AuctionServiceModel auction = aucItem.getAuction();
        if (auction.getThumbnail() == null) {
            auction.setThumbnail(savedPictureWrapper.getAuctionPicture());
            this.auctionRepository.save(this.modelMapper.map(auction, Auction.class));
        }
        return savedPictureWrapper;
    }

    @Override
    public boolean deletePicture(
            AucItemPictureWrapperServiceModel pictureWrapper,
            String sellerMail) throws ReportToUserException {
        this.inputValidator.validateObject(pictureWrapper, pictureWrapper.getAuctionedItemId());
        this.inputValidator.validateString(sellerMail);
        AuctionedItemServiceModel aucItem = this.itemService.getAucItemById(pictureWrapper.getAuctionedItemId());
        if (!aucItem.getAuction().getId().equals(pictureWrapper.getAuctionId())) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_AUC_ITEM_DOES_NOT_BELONG_TO_AUCTION);
        }
        if (!aucItem.getAuction().getSeller().getDomainEmail().equals(sellerMail)) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_CREATOR);
        }
        AuctionServiceModel auction = aucItem.getAuction();
        if (auction.getThumbnail() != null) {
            AucItemPictureWrapperServiceModel wrapper = this.auctionPictureService.getPictureWrapperById(pictureWrapper.getId());
            if (auction.getThumbnail() != null && auction.getThumbnail().getId() == wrapper.getAuctionPicture().getId().longValue()) {
                auction.setThumbnail(null);
                this.auctionRepository.saveAndFlush(this.modelMapper.map(auction, Auction.class));
            }
        }
        return this.auctionPictureService.deletePicture(pictureWrapper, sellerMail);
    }

    @Override
    public boolean adminDeletePicture(AucItemPictureWrapperServiceModel pictureWrapper) throws ReportToUserException {
        this.inputValidator.validateObject(pictureWrapper, pictureWrapper.getAuctionedItemId());
        AuctionedItemServiceModel aucItem = this.itemService.getAucItemById(pictureWrapper.getAuctionedItemId());
        if (!aucItem.getAuction().getId().equals(pictureWrapper.getAuctionId())) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_AUC_ITEM_DOES_NOT_BELONG_TO_AUCTION);
        }
        AuctionServiceModel auction = aucItem.getAuction();
        if (auction.getThumbnail() != null) {
            AucItemPictureWrapperServiceModel wrapper = this.auctionPictureService.getPictureWrapperById(pictureWrapper.getId());
            if (auction.getThumbnail() != null && auction.getThumbnail().getId() == wrapper.getAuctionPicture().getId().longValue()) {
                auction.setThumbnail(null);
                this.auctionRepository.saveAndFlush(this.modelMapper.map(auction, Auction.class));
            }
        }
        return this.auctionPictureService.adminDeletePicture(pictureWrapper);
    }

    @Override
    public List<AuctionServiceModel> getAllAuctionsBySeller(String sellerEmail, UserRolesEnum role) {
        this.inputValidator.validateObject(role);
        this.inputValidator.validateString(sellerEmail);
        if (role.isRole(UserRolesEnum.SELLER) || role.isRole(UserRolesEnum.ADMIN)) {
            return this.getAllAuctionsBySeller(sellerEmail);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public AuctionServiceModel getAuctionById(Long id, String userEmail, UserRolesEnum role) {
        this.inputValidator.validateObject(id, role);
        this.inputValidator.validateString(userEmail);
        AuctionServiceModel auction = null;
        if (role.isRole(UserRolesEnum.BUYER)) {
            auction = this.modelMapper.map(this.getAuctioEntityById(id), AuctionServiceModel.class);
            if (this.isAuctionWinned(auction)) {
                if (!this.verifyAuctionWinner(userEmail, auction)) {
                    throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_WINNER);
                }
            } else {
                this.verifyAuctionStatus(auction, AuctionStatusCheckKeyWord.SHOW_TO_BUYER);
            }
        } else {
            auction = this.getAuctionById(id);
            if (role.isRole(UserRolesEnum.SELLER)) {
                this.verifyAuctionCreator(auction, userEmail);
            } else if (role.isRole(UserRolesEnum.ADMIN)) {
                //ADMIN HAVE ACCESS TO ALL AUCTIONS
            } else {
                throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
            }
        }
        return auction;
    }

    @Override
    public boolean updateAuction(
            AuctionServiceModel updatedAuction,
            List<ItemServiceModel> itemtsToAdd,
            List<AuctionedItemServiceModel> aucItemstoRemove,
            String sellerEmail,
            UserRolesEnum role) throws ReportToUserException {
        this.inputValidator.validateObject(updatedAuction, itemtsToAdd, aucItemstoRemove, role);
        this.inputValidator.validateString(sellerEmail);
        if (role.isRole(UserRolesEnum.SELLER)) {
            return this.sellerUpdateAuction(updatedAuction, itemtsToAdd, aucItemstoRemove, sellerEmail);
        } else if (role.isRole(UserRolesEnum.ADMIN)) {
            return this.updateAuction(updatedAuction, itemtsToAdd, aucItemstoRemove);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public boolean deleteAuction(AuctionServiceModel auction, String sellerEmail, UserRolesEnum role) {
        this.inputValidator.validateObject(auction, role);
        this.inputValidator.validateString(sellerEmail);
        if (role.isRole(UserRolesEnum.SELLER)) {
            this.isAuctionDeletable(auction);
            return this.sellerDeleteAuction(auction, sellerEmail);
        } else if (role.isRole(UserRolesEnum.ADMIN)) {
            return this.deleteAuction(auction);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
    }

    @Override
    public boolean archive(AuctionServiceModel auction, String userEmail) {
        this.inputValidator.validateObject(auction);
        this.inputValidator.validateString(userEmail);
        this.verifyAuctionStatus(auction, AuctionStatusCheckKeyWord.ARCHIVE);
        this.verifyAuctionCreator(auction, userEmail);
        auction.setStatus(AuctionStatus.ARCHIVE);
        this.auctionRepository.save(this.modelMapper.map(auction, Auction.class));
        return true;
    }

    @Override
    public List<AuctionServiceModel> getAllArchivesByCreator(String creatorMail, UserRolesEnum role) {
        this.inputValidator.validateObject(role);
        this.inputValidator.validateString(creatorMail);
        Predicate<Auction> filter = null;
        if (role.isRole(UserRolesEnum.SELLER)) {
            filter = auction -> (auction.getSeller().getDomainEmail().equals(creatorMail));
        } else if (role.isRole(UserRolesEnum.ADMIN)) {
            filter = auction -> (true);
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
        User creator = this.modelMapper.map(this.userService.getUserByDomainEmail(creatorMail), User.class);
        return this.auctionRepository.findAllBySellerAndStatus(creator, AuctionStatus.ARCHIVE)
                .stream()
                .filter(filter::test)
                .map(x -> this.modelMapper.map(x, AuctionServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public AuctionServiceModel getArchiveById(Long id, String userEmail, UserRolesEnum role) {
        this.inputValidator.validateObject(id, role);
        this.inputValidator.validateString(userEmail);
        AuctionServiceModel auctionServiceModel
                = this.modelMapper.map(this.getArchiveAuctionEntityById(id), AuctionServiceModel.class);
        if (role.isRole(UserRolesEnum.ADMIN)) {
            //Admin has access to all archives
        } else if (role.isRole(UserRolesEnum.SELLER)) {
            this.verifyAuctionCreator(auctionServiceModel, userEmail);
        } else if (role.isRole(UserRolesEnum.BUYER)) {
            if (!this.verifyAuctionWinner(userEmail, auctionServiceModel)) {
                throw new ReporPartlyToUserException(SystemExceptionMessage.AUCTION_INVALID_WINNER);
            }
        } else {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }

        return auctionServiceModel;
    }

    @Override
    public boolean deleteArchiveById(Long id, UserRolesEnum role) {
        this.inputValidator.validateObject(id, role);
        if (!role.isRole(UserRolesEnum.ADMIN)) {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
        Auction auction = this.getArchiveAuctionEntityById(id);
        
        if(auction.getBestBid()!=null){
            // If archive has best bid, it should be deleted first
            auction.setBestBid(null);
            this.auctionRepository.saveAndFlush(auction);
            return false;
        }
        return this.deleteAuction(this.modelMapper.map(auction, AuctionServiceModel.class));
    }

    @Override
    public List<AuctionServiceModel> getAllArchives(UserRolesEnum role) {
        this.inputValidator.validateObject(role);
        if (!role.isRole(UserRolesEnum.ADMIN)) {
            throw new InvalidRoleException(SystemExceptionMessage.NOT_SUPPORTED_USER_ROLE + role.getRole());
        }
        return this.getAllAuctionsByStatus(AuctionStatus.ARCHIVE);
    }

}

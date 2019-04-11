/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.Pictures;

import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.config.storage.StorageVariables;
import Atia.Shop.domain.entities.pictures.AucItemPictureWrapper;
import Atia.Shop.domain.entities.pictures.AuctionPicture;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.domain.repositories.pictures.AucItemPictureRepository;
import Atia.Shop.domain.repositories.pictures.AuctionPictureRepository;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.service.API.Pictures.AuctionPictureService;
import Atia.Shop.service.IMPL.Storage.AuctionPictureStorageService;
import Atia.Shop.service.IMPL.Storage.ItemPictureStorageService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.util.Date;
import java.util.List;
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
public class AuctionPictureServiceImpl implements AuctionPictureService{
    
    private final ModelMapper modelMapper;
    private final AuctionPictureRepository auctionPictureRepository;
    private final AucItemPictureRepository aucItemPictureRepository;
    private final AuctionPictureStorageService auctionPictureStorage;
    private final ItemPictureStorageService itemPictureStorageService;
    private final InputValidator inputValidator;

    @Autowired
    public AuctionPictureServiceImpl(
            ModelMapper modelMapper,
            AuctionPictureRepository auctionPictureRepository,
            AucItemPictureRepository aucItemPictureRepository, 
            AuctionPictureStorageService auctionPictureStorage, 
            ItemPictureStorageService itemPictureStorageService,
            InputValidator inputValidator) {
        this.modelMapper = modelMapper;
        this.auctionPictureRepository = auctionPictureRepository;
        this.aucItemPictureRepository = aucItemPictureRepository;
        this.auctionPictureStorage = auctionPictureStorage;
        this.itemPictureStorageService = itemPictureStorageService;
        this.inputValidator=inputValidator;
    }
    
    private boolean isItemPictureIsUnique(List<AucItemPictureWrapper> allAuctionPictures, String newPictureFileName){
        return !allAuctionPictures
                .stream()
                .anyMatch(x->x.getOriginalFileName().equals(newPictureFileName));
    }

    @Override
    public AucItemPictureWrapperServiceModel addPicture(AucItemPictureWrapperServiceModel pictureWrapper, String itemPictureFileName,
            String sellerMail) throws ReportToUserException {
        this.inputValidator.validateObject(pictureWrapper, pictureWrapper.getAuctionId());
        this.inputValidator.validateString(itemPictureFileName,pictureWrapper.getAuctionedItemName(),pictureWrapper.getDescription(), sellerMail);
        List<AucItemPictureWrapper> allAuctionPictures = this.aucItemPictureRepository.findAllByAuctionId(pictureWrapper.getAuctionId());
        if(!this.isItemPictureIsUnique(allAuctionPictures, itemPictureFileName)){
            throw new ReportToUserException(ValidationMesseges.DUPLICATE_PICTURE_FILE_NAMES);
        }
        if(allAuctionPictures.size()+1>StorageVariables.AUCTIONS_PICTURES_UPLOAD_LIMIT){
            throw new ReportToUserException(ValidationMesseges.PICTURE_UPLOAD_LIMIT+StorageVariables.AUCTIONS_PICTURES_UPLOAD_LIMIT);
        }
        AuctionPicture auctionPicture = this.auctionPictureRepository.findByPictureFileID(itemPictureFileName);
        if (auctionPicture == null) {
            auctionPicture = new AuctionPicture();
            auctionPicture.setPictureFileID(itemPictureFileName);
            this.auctionPictureStorage.store(
                    this.itemPictureStorageService.load(itemPictureFileName).toFile(), auctionPicture.getPictureFileID());
            auctionPicture.setUsageCounter(auctionPicture.getUsageCounter()+1);
        }else{
            auctionPicture.setUsageCounter(auctionPicture.getUsageCounter()+1);
        }
        auctionPicture = this.auctionPictureRepository.saveAndFlush(auctionPicture);
        
        AucItemPictureWrapper aucItemPicWrapper = this.modelMapper.map(pictureWrapper, AucItemPictureWrapper.class);
        aucItemPicWrapper.setAuctionPicture(auctionPicture);
        aucItemPicWrapper.setDateAdded(new Date());
        aucItemPicWrapper.setAuctionId(pictureWrapper.getAuctionId());
        aucItemPicWrapper.setOriginalFileName(itemPictureFileName);
        aucItemPicWrapper.setDescription(pictureWrapper.getDescription());
        aucItemPicWrapper.setAuctionedItemName(pictureWrapper.getAuctionedItemName());
        aucItemPicWrapper=this.aucItemPictureRepository.saveAndFlush(aucItemPicWrapper);

        return this.modelMapper.map(aucItemPicWrapper, AucItemPictureWrapperServiceModel.class);
    }

    @Override
    public boolean deletePicture(AucItemPictureWrapperServiceModel pictureWrapper, String sellermail) throws ReportToUserException {
        this.inputValidator.validateObject(pictureWrapper, pictureWrapper.getId());
        this.inputValidator.validateString(sellermail);
        AucItemPictureWrapper aucItemPicWrapper = this.aucItemPictureRepository.findById(pictureWrapper.getId()).orElse(null);
        if(aucItemPicWrapper==null){
            throw new ReportToUserException(ValidationMesseges.PICTURE_NO_SUCH_ID);
        }
        return this.deleteSinglePicture(aucItemPicWrapper);
    }
    
    private boolean deleteSinglePicture(AucItemPictureWrapper aucItemPicWrapper){
        AuctionPicture auctionPicture = 
                this.auctionPictureRepository.findById(aucItemPicWrapper.getAuctionPicture().getId()).orElse(null);
        this.aucItemPictureRepository.deleteById(aucItemPicWrapper.getId());
        auctionPicture.setUsageCounter(auctionPicture.getUsageCounter()-1);
        if(auctionPicture.getUsageCounter()<=0){
            this.auctionPictureRepository.delete(auctionPicture);
            this.auctionPictureStorage.deleteSingle(auctionPicture.getPictureFileID());
        }else{
            this.auctionPictureRepository.saveAndFlush(auctionPicture);
        }
        return true; 
    }

    @Override
    public List<AucItemPictureWrapperServiceModel> getAllPicturesByEntity(AuctionServiceModel auction) {
        this.inputValidator.validateObject(auction,auction.getId());
        return this.aucItemPictureRepository.findAllByAuctionId(auction.getId())
                .stream()
                .map(x->this.modelMapper.map(x, AucItemPictureWrapperServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteAllPicturesFromAuction(AuctionServiceModel auction) {
        this.inputValidator.validateObject(auction, auction.getId());
        List<AucItemPictureWrapper> pictureWrappers = this.aucItemPictureRepository.findAllByAuctionId(auction.getId());
        for(AucItemPictureWrapper pictureWrapper : pictureWrappers){
            this.deleteSinglePicture(pictureWrapper);
        }
        return true;
    }

    @Override
    public boolean adminDeletePicture(AucItemPictureWrapperServiceModel pictureWrapper) throws ReportToUserException {
        this.inputValidator.validateObject(pictureWrapper, pictureWrapper.getId());
        AucItemPictureWrapper aucItemPicWrapper = this.aucItemPictureRepository.findById(pictureWrapper.getId()).orElse(null);
        if(aucItemPicWrapper==null){
            throw new ReportToUserException(ValidationMesseges.PICTURE_NO_SUCH_ID);
        }
        return this.deleteSinglePicture(aucItemPicWrapper);
    }

    @Override
    public AucItemPictureWrapperServiceModel getPictureWrapperById(Long id) {
        this.inputValidator.validateObject(id);
         AucItemPictureWrapper wrapper = this.aucItemPictureRepository.findById(id).orElse(null);
         if(wrapper==null){
            throw new ReporPartlyToUserException(ValidationMesseges.PICTURE_NO_SUCH_ID);
        }
        return this.modelMapper.map(wrapper, AucItemPictureWrapperServiceModel.class);
    }
     
}

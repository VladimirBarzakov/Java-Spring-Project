/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.Pictures;

import Atia.Shop.config.errorMesseges.SystemExceptionMessage;
import Atia.Shop.config.errorMesseges.ValidationMesseges;
import Atia.Shop.config.storage.StorageVariables;
import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.pictures.ItemPicture;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.domain.repositories.pictures.ItemPictureRepository;
import Atia.Shop.service.API.Pictures.ItemPictureService;
import Atia.Shop.service.IMPL.Storage.ItemPictureStorageService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Service
@Transactional
public class ItemPictureServiceImpl implements ItemPictureService {

    private final ModelMapper modelMapper;
    private final ItemPictureStorageService itemPictureStorage;
    private final ItemPictureRepository itemPictureRepository;
    private final InputValidator validationUtil;
    
    private final Set<String> VALID_FILE_FORMATS;

    @Autowired
    public ItemPictureServiceImpl(ModelMapper modelMapper,
            ItemPictureStorageService itemPictureStorage,
            ItemPictureRepository itemPictureRepository,
            InputValidator validationUtil) {
        this.modelMapper = modelMapper;
        this.itemPictureStorage = itemPictureStorage;
        this.itemPictureRepository = itemPictureRepository;
        this.validationUtil=validationUtil;
        this.VALID_FILE_FORMATS=Arrays.stream(StorageVariables.VALID_PICTURE_FILE_EXTENSIONS.split("\\,"))
                .map(x->x.trim())
                .filter(x->!x.equals(""))
                .collect(Collectors.toSet());
    }

    private ItemPicture getPictureEntityById(Long id) {
        this.validationUtil.validateObject(id);
        ItemPicture itemPicture = this.itemPictureRepository.findById(id).orElse(null);
        if (itemPicture == null) {
            throw new ReporPartlyToUserException(SystemExceptionMessage.NO_PICTURE_WITH_SUCH_ID);
        }
        return itemPicture;
    }
    
    private void validateFileFormat(String fileName) throws ReportToUserException{
        String fileExtension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
        if(!this.VALID_FILE_FORMATS.contains(fileExtension)){
            throw new ReportToUserException(ValidationMesseges.PICTURE_FILE_EXTENSION);
        }
    }

    @Override
    public ItemPictureServiceModel savePicture(ItemPictureServiceModel pictureServiceModel, MultipartFile file) throws ReportToUserException {
        this.validationUtil.validateObject(pictureServiceModel, pictureServiceModel.getItem(), file);
        this.validationUtil.validateString(file.getOriginalFilename());
        Item item = this.modelMapper.map(pictureServiceModel.getItem(), Item.class);
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        this.validateFileFormat(filename);
        List<ItemPicture> itemPictures = this.itemPictureRepository.findAllByItem(item);
        if (!this.isPictureFileNameUnique(itemPictures, filename)) {
            throw new ReportToUserException(ValidationMesseges.DUPLICATE_PICTURE_FILE_NAMES);
        }
        if (file.isEmpty()) {
            throw new ReportToUserException(ValidationMesseges.PICTURE_EMPTY_MESSAGE);
        }
        if(itemPictures.size()+1>StorageVariables.ITEM_PICTURES_UPLOAD_LIMIT){
            throw new ReportToUserException(ValidationMesseges.PICTURE_UPLOAD_LIMIT+StorageVariables.ITEM_PICTURES_UPLOAD_LIMIT);
        }
        ItemPicture picture = this.modelMapper.map(pictureServiceModel, ItemPicture.class);
        picture.setPictureId(UUID.randomUUID().toString() + ".jpg");
        picture.setDateAdded(new Date());
        picture.setItem(item);
        picture.setOriginalFileName(filename);
        picture = this.itemPictureRepository.save(picture);
        this.itemPictureStorage.store(file, picture.getPictureId());
        return this.modelMapper.map(picture, ItemPictureServiceModel.class);
    }

    private boolean isPictureFileNameUnique(List<ItemPicture> itemPictures, String filename) {
        return !itemPictures
                .stream()
                .map(x -> x.getOriginalFileName())
                .anyMatch(x -> x.equals(filename));
    }

    @Override
    public List<ItemPictureServiceModel> getAllPicturesByEntity(ItemServiceModel itemServiceModel) {
        this.validationUtil.validateObject(itemServiceModel);
        return this.itemPictureRepository.findAllByItem(this.modelMapper.map(itemServiceModel, Item.class))
                .stream()
                .map(x -> this.modelMapper.map(x, ItemPictureServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteSinglePicture(ItemServiceModel entity, Long pictureId) {
        this.validationUtil.validateObject(entity,pictureId);
        ItemPicture picture = this.getPictureEntityById(pictureId);
        if (picture.getItem().getId()!=entity.getId()) {
            throw new ReporPartlyToUserException("Picture is not of that item");
        }
        this.itemPictureStorage.deleteSingle(picture.getPictureId());
        this.itemPictureRepository.delete(picture);
        return true;
    }

    @Override
    public boolean updateDescriptionSinglePicture(ItemServiceModel entity, Long pictureId, String description) {
        this.validationUtil.validateObject(entity,pictureId);
        this.validationUtil.validateString(description);
        ItemPicture picture = this.getPictureEntityById(pictureId);
        if (picture.getItem().getId()!=entity.getId()) {
            throw new ReporPartlyToUserException("Picture is not of that item");
        }
        picture.setDescription(description);
        this.itemPictureRepository.save(picture);
        return true;
    }

    @Override
    public boolean deleteAllPicturesByItem(ItemServiceModel item) {
        this.validationUtil.validateObject(item);
        List<ItemPicture> itemPictures = 
                this.itemPictureRepository.findAllByItem(this.modelMapper.map(item, Item.class));
        for (ItemPicture picture : itemPictures) {
            this.itemPictureStorage.deleteSingle(picture.getPictureId());
            this.itemPictureRepository.delete(picture);
        }
        return true;
    }
}

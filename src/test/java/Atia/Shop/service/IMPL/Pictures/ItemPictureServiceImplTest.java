/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.Pictures;

import Atia.Shop.config.storage.StorageVariables;
import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.domain.repositories.ItemRepository;
import Atia.Shop.domain.repositories.UserRepository;
import Atia.Shop.domain.repositories.pictures.ItemPictureRepository;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.Pictures.ItemPictureService;
import Atia.Shop.service.IMPL.Storage.ItemPictureStorageService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ItemPictureServiceImplTest {

    @Autowired
    private ItemPictureRepository itemPictureRepository;
    private ModelMapper modelMapper;
    private ItemPictureStorageService itemPictureStorage;
    private InputValidator validationUtil;

    private ItemPictureService itemPictureService;

    //////////////////////////////////////////////
    @Autowired
    private ItemRepository itemRepository;
    private Item testItemA;
    private Item testItemB;
    private final String ITEM_NAME = "Item name";
    private ItemServiceModel itemAServiceModel;
    private ItemServiceModel itemBServiceModel;
    //////////////////////////////////////////////

    @Autowired
    private UserRepository userRepository;
    private User testUser;
    //////////////////////////////////////////////

    private final String VALID_FILE_NAME1 = "picture.jpg";
    ;
    private final String INVALID_FILE_NAME = "picture.notsupported";

    public ItemPictureServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.modelMapper = new ModelMapper();
        this.validationUtil = new InputValidator();
        this.itemPictureStorage = Mockito.mock(ItemPictureStorageService.class);
        this.itemPictureService = new ItemPictureServiceImpl(this.modelMapper, this.itemPictureStorage, this.itemPictureRepository, this.validationUtil);

        this.testUser = this.initTestUser();
        this.testItemA = this.createTestItem();
        this.testItemB = this.createTestItem();
        this.itemAServiceModel = this.modelMapper.map(this.testItemA, ItemServiceModel.class);
        this.itemBServiceModel = this.modelMapper.map(this.testItemB, ItemServiceModel.class);
    }

    @After
    public void tearDown() {
    }

    private User initTestUser() {
        User user = new User();
        user.setName("User name");
        user.setDomainEmail("vlado@atia.com");
        user.setIsActive(Boolean.TRUE);
        user.setTestPassword("password");
        user.setAuthorities(new HashSet());
        return this.userRepository.saveAndFlush(user);

    }

    private Item createTestItem() {
        Item item = new Item();
        item.setName(this.ITEM_NAME);
        item.setDescription("Item description");
        item.setDateAdded(new Date());
        item.setInitialPrice(BigDecimal.ONE);
        item.setQuantity(1);
        item.setLocation("Item location");
        item.setSeller(this.testUser);
        return this.itemRepository.saveAndFlush(item);
    }

    /**
     * Test of savePicture method, of class ItemPictureServiceImpl.
     */
    @Test
    public void itemPictureService_savePicture_WithCorrectInputShoudSavePictureDetails() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        ItemPictureServiceModel result = this.itemPictureService.savePicture(pictureServiceModel, file);

        Assert.assertTrue(result.getItem().getId().equals(this.testItemA.getId()));
        assertEquals(this.VALID_FILE_NAME1, result.getOriginalFileName());
    }

    @Test(expected = ReportToUserException.class)
    public void itemPictureService_savePicture_WithUploadMoreThanLimit_SwouldThrowReportToUserException() throws Exception {

        for (int i = 1; i <= StorageVariables.ITEM_PICTURES_UPLOAD_LIMIT; i++) {
            MultipartFile file = Mockito.mock(MultipartFile.class);
            Mockito.when(file.getOriginalFilename()).thenReturn(i + this.VALID_FILE_NAME1);
            Mockito.when(file.isEmpty()).thenReturn(false);
            ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
            pictureServiceModel.setDateAdded(new Date());
            pictureServiceModel.setItem(this.itemAServiceModel);

            this.itemPictureService.savePicture(pictureServiceModel, file);
        }
        Assert.assertTrue(this.itemPictureRepository.findAll().size()==StorageVariables.ITEM_PICTURES_UPLOAD_LIMIT);

        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);
        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel, file);

    }

    @Test(expected = ReportToUserException.class)
    public void itemPictureService_savePicture_UploadSamePictureTwice_SwouldThrowReportToUserException() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel, file);
        this.itemPictureService.savePicture(pictureServiceModel, file);
    }

    @Test(expected = ReportToUserException.class)
    public void itemPictureService_savePicture_WithEmptyFile_SwouldThrowReportToUserException() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(true);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel, file);
    }

    @Test(expected = ReportToUserException.class)
    public void itemPictureService_savePicture_WithNotSupportedFileExtension_SwouldThrowReportToUserException() throws Exception {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.INVALID_FILE_NAME);
        Mockito.when(file.isEmpty()).thenReturn(true);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel, file);
    }

    /**
     * Test of getAllPicturesByEntity method, of class ItemPictureServiceImpl.
     */
    @Test
    public void itemPictureService_getAllPicturesByEntity_WithCorrectValue_ShouldReturnAllPictureDetails() throws ReportToUserException {
        MultipartFile file1 = Mockito.mock(MultipartFile.class);
        Mockito.when(file1.getOriginalFilename()).thenReturn(1 + this.VALID_FILE_NAME1);
        Mockito.when(file1.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel_A = new ItemPictureServiceModel();
        pictureServiceModel_A.setDateAdded(new Date());
        pictureServiceModel_A.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel_A, file1);

        MultipartFile file2 = Mockito.mock(MultipartFile.class);
        Mockito.when(file2.getOriginalFilename()).thenReturn(2 + this.VALID_FILE_NAME1);
        Mockito.when(file2.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel_B = new ItemPictureServiceModel();
        pictureServiceModel_B.setDateAdded(new Date());
        pictureServiceModel_B.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel_B, file2);

        MultipartFile file3 = Mockito.mock(MultipartFile.class);
        Mockito.when(file3.getOriginalFilename()).thenReturn(3 + this.VALID_FILE_NAME1);
        Mockito.when(file3.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel_C = new ItemPictureServiceModel();
        pictureServiceModel_C.setDateAdded(new Date());
        pictureServiceModel_C.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel_C, file3);

        List<ItemPictureServiceModel> itemPictures = this.itemPictureService.getAllPicturesByEntity(this.itemAServiceModel);
        Assert.assertEquals(3, itemPictures.size());
    }

    @Test
    public void itemPictureService_getAllPicturesByEntity_WithCorrectValueOnUnusedItem_ShouldReturnAllPictureDetails() throws ReportToUserException {
        MultipartFile file1 = Mockito.mock(MultipartFile.class);
        Mockito.when(file1.getOriginalFilename()).thenReturn(1 + this.VALID_FILE_NAME1);
        Mockito.when(file1.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel_A = new ItemPictureServiceModel();
        pictureServiceModel_A.setDateAdded(new Date());
        pictureServiceModel_A.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel_A, file1);

        MultipartFile file2 = Mockito.mock(MultipartFile.class);
        Mockito.when(file2.getOriginalFilename()).thenReturn(2 + this.VALID_FILE_NAME1);
        Mockito.when(file2.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel_B = new ItemPictureServiceModel();
        pictureServiceModel_B.setDateAdded(new Date());
        pictureServiceModel_B.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel_B, file2);

        MultipartFile file3 = Mockito.mock(MultipartFile.class);
        Mockito.when(file3.getOriginalFilename()).thenReturn(3 + this.VALID_FILE_NAME1);
        Mockito.when(file3.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel_C = new ItemPictureServiceModel();
        pictureServiceModel_C.setDateAdded(new Date());
        pictureServiceModel_C.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel_C, file3);

        List<ItemPictureServiceModel> itemPictures = this.itemPictureService.getAllPicturesByEntity(this.itemBServiceModel);
        Assert.assertEquals(0, itemPictures.size());
    }

    /**
     * Test of deleteSinglePicture method, of class ItemPictureServiceImpl.
     */
    @Test
    public void itemPictureService_deleteSinglePicture_WithCorrectvalues_ShouldDeletePicture() throws ReportToUserException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        ItemPictureServiceModel result = this.itemPictureService.savePicture(pictureServiceModel, file);
        long expectedPictureCount = 1;
        long actualPictureCount = this.itemPictureRepository.count();
        Assert.assertEquals(expectedPictureCount, actualPictureCount);
        boolean targerResult = this.itemPictureService.deleteSinglePicture(this.itemAServiceModel, result.getId());
        Assert.assertTrue(targerResult);
        expectedPictureCount = 0;
        actualPictureCount = this.itemPictureRepository.count();
        Assert.assertEquals(expectedPictureCount, actualPictureCount);
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void itemPictureService_deleteSinglePicture_WithIncorrectItem_ShouldThrowReporPartlyToUserException() throws ReportToUserException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        ItemPictureServiceModel result = this.itemPictureService.savePicture(pictureServiceModel, file);

        this.itemPictureService.deleteSinglePicture(this.itemBServiceModel, result.getId());
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void itemPictureService_deleteSinglePicture_WithIncorrectPictureId_ShouldThrowReporPartlyToUserException() throws ReportToUserException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel, file);

        this.itemPictureService.deleteSinglePicture(this.itemAServiceModel, -200L);
    }

    /**
     * Test of updateDescriptionSinglePicture method, of class
     * ItemPictureServiceImpl.
     */
    @Test
    public void itemPictureService_updateDescriptionSinglePicture_WithCorrectValues_ShouldUpdatePictureDescrition() throws ReportToUserException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        ItemPictureServiceModel savedPicture = this.itemPictureService.savePicture(pictureServiceModel, file);

        String newDescription = "My new Description";
        boolean result
                = this.itemPictureService.updateDescriptionSinglePicture(this.itemAServiceModel, savedPicture.getId(), newDescription);
        Assert.assertTrue(result);
        savedPicture = this.itemPictureService.getAllPicturesByEntity(this.itemAServiceModel).get(0);
        Assert.assertEquals(newDescription, savedPicture.getDescription());
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void itemPictureService_updateDescriptionSinglePicture_WithWrongItem_ShouldThrowReporPartlyToUserException() throws ReportToUserException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        ItemPictureServiceModel savedPicture = this.itemPictureService.savePicture(pictureServiceModel, file);

        String newDescription = "My new Description";
        this.itemPictureService.updateDescriptionSinglePicture(this.itemBServiceModel, savedPicture.getId(), newDescription);
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void itemPictureService_updateDescriptionSinglePicture_WithWrongPictureId_ShouldThrowReporPartlyToUserException() throws ReportToUserException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn(this.VALID_FILE_NAME1);
        Mockito.when(file.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel = new ItemPictureServiceModel();
        pictureServiceModel.setDateAdded(new Date());
        pictureServiceModel.setItem(this.itemAServiceModel);

        ItemPictureServiceModel savedPicture = this.itemPictureService.savePicture(pictureServiceModel, file);

        String newDescription = "My new Description";
        this.itemPictureService.updateDescriptionSinglePicture(this.itemBServiceModel, -200L, newDescription);
    }

    /**
     * Test of deleteAllPicturesByItem method, of class ItemPictureServiceImpl.
     */
    @Test
    public void itemPictureService_deleteAllPicturesByItem_WithCorrectValues_ShouldWorkCorrectly() throws ReportToUserException {
        MultipartFile file1 = Mockito.mock(MultipartFile.class);
        Mockito.when(file1.getOriginalFilename()).thenReturn(1 + this.VALID_FILE_NAME1);
        Mockito.when(file1.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel1 = new ItemPictureServiceModel();
        pictureServiceModel1.setDateAdded(new Date());
        pictureServiceModel1.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel1, file1);

        MultipartFile file2 = Mockito.mock(MultipartFile.class);
        Mockito.when(file2.getOriginalFilename()).thenReturn(2 + this.VALID_FILE_NAME1);
        Mockito.when(file2.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel2 = new ItemPictureServiceModel();
        pictureServiceModel2.setDateAdded(new Date());
        pictureServiceModel2.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel2, file2);

        MultipartFile file3 = Mockito.mock(MultipartFile.class);
        Mockito.when(file3.getOriginalFilename()).thenReturn(3 + this.VALID_FILE_NAME1);
        Mockito.when(file3.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel3 = new ItemPictureServiceModel();
        pictureServiceModel3.setDateAdded(new Date());
        pictureServiceModel3.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel3, file3);
        long picturesCount = this.itemPictureRepository.count();
        Assert.assertEquals(3, picturesCount);
        boolean result = this.itemPictureService.deleteAllPicturesByItem(this.itemAServiceModel);
        Assert.assertTrue(result);
        picturesCount = this.itemPictureRepository.count();
        Assert.assertEquals(0, picturesCount);
    }

    @Test
    public void itemPictureService_deleteAllPicturesByItem_WithCorrectValuesShouldLeaveotherPictures_ShouldWorkCorrectly() throws ReportToUserException {
        MultipartFile file1 = Mockito.mock(MultipartFile.class);
        Mockito.when(file1.getOriginalFilename()).thenReturn(1 + this.VALID_FILE_NAME1);
        Mockito.when(file1.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel1 = new ItemPictureServiceModel();
        pictureServiceModel1.setDateAdded(new Date());
        pictureServiceModel1.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel1, file1);

        MultipartFile file2 = Mockito.mock(MultipartFile.class);
        Mockito.when(file2.getOriginalFilename()).thenReturn(2 + this.VALID_FILE_NAME1);
        Mockito.when(file2.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel2 = new ItemPictureServiceModel();
        pictureServiceModel2.setDateAdded(new Date());
        pictureServiceModel2.setItem(this.itemAServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel2, file2);

        MultipartFile file3 = Mockito.mock(MultipartFile.class);
        Mockito.when(file3.getOriginalFilename()).thenReturn(3 + this.VALID_FILE_NAME1);
        Mockito.when(file3.isEmpty()).thenReturn(false);

        ItemPictureServiceModel pictureServiceModel3 = new ItemPictureServiceModel();
        pictureServiceModel3.setDateAdded(new Date());
        pictureServiceModel3.setItem(this.itemBServiceModel);

        this.itemPictureService.savePicture(pictureServiceModel3, file3);
        long picturesCount = this.itemPictureRepository.count();
        Assert.assertEquals(3, picturesCount);
        boolean result = this.itemPictureService.deleteAllPicturesByItem(this.itemAServiceModel);
        Assert.assertTrue(result);
        picturesCount = this.itemPictureRepository.count();
        Assert.assertEquals(1, picturesCount);
    }

}

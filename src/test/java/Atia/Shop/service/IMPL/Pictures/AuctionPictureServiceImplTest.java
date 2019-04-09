/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.Pictures;

import Atia.Shop.config.storage.StorageVariables;
import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.AuctionedItem;
import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.domain.repositories.pictures.AucItemPictureRepository;
import Atia.Shop.domain.repositories.pictures.AuctionPictureRepository;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.Pictures.AuctionPictureService;
import Atia.Shop.service.IMPL.Storage.AuctionPictureStorageService;
import Atia.Shop.service.IMPL.Storage.ItemPictureStorageService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AuctionPictureServiceImplTest {

    @Autowired
    private AuctionPictureRepository auctionPictureRepository;
    @Autowired
    private AucItemPictureRepository aucItemPictureRepository;

    private AuctionPictureStorageService auctionPictureStorage;
    private ItemPictureStorageService itemPictureStorageService;
    private InputValidator inputValidator;
    private ModelMapper modelMapper;

    private AuctionPictureService auctionPictureService;

    private final static String VALID_ITEM_PICTURE_FILE_NAME = "item_picture.jpg";
    private final static String VALID_ITEM_PICTURE_DESCRIPTION = "My item picture description";
    private final static String VALID_AUC_ITEM_NAME = "My auctioned item name";

    /////////////////////////////////////
    private final String SELLER_ID = "MySellerId";
    private User seller;

    //////////////////////////////////////
    private final Long ITEM_A_ID = 100L;
    private Item itemA;
    private ItemServiceModel itemAServiceModel;

    private final Long ITEM_B_ID = 101L;
    private Item itemB;
    private ItemServiceModel itemBServiceModel;

    ///////////////////////////////////////
    private final Long AUCTION_A_ID = 1000L;
    private Auction auctionA;
    private AuctionServiceModel auctionAServiceModel;

    private final Long AUCTION_B_ID = 1001L;
    private Auction auctionB;
    private AuctionServiceModel auctionBServiceModel;

    ///////////////////////////////////////
    private final Long AUC_ITEM_A_ID = 10000L;
    private AuctionedItem aucItemA;
    private AuctionedItemServiceModel aucItemAServiceModel;

    private final Long AUC_ITEM_B_ID = 10001L;
    private AuctionedItem aucItemB;
    private AuctionedItemServiceModel aucItemBServiceModel;

    ////////////////////////////////////////
    public AuctionPictureServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private User initSeller() {
        User user = new User();
        user.setName("User name");
        user.setDomainEmail("vlado@atia.com");
        user.setIsActive(Boolean.TRUE);
        user.setTestPassword("password");
        user.setAuthorities(new HashSet());
        return user;
    }

    private Item createTestItem() {
        Item item = new Item();
        item.setName("Item name");
        item.setDescription("Item description");
        item.setDateAdded(new Date());
        item.setInitialPrice(BigDecimal.ONE);
        item.setQuantity(10);
        item.setLocation("Item location");
        item.setSeller(this.seller);
        return item;
    }

    private Auction createTestAuction() {
        Auction auction = new Auction();
        auction.setTitle("Auction title");
        auction.setSeller(this.seller);
        auction.setDescription("My Auction description");
        auction.setInitialPrice(BigDecimal.ONE);
        auction.setDateStarted(new Date());
        auction.setDateExpired(new Date());
        auction.setStatus(AuctionStatus.CREATED);
        return auction;
    }

    private AuctionedItem createTestAuctionedItem() {
        AuctionedItem aucItem = new AuctionedItem();
        aucItem.setQuantity(10);
        aucItem.setItemPrice(BigDecimal.ZERO);
        return aucItem;
    }

    @Before
    public void setUp() {
        this.modelMapper = new ModelMapper();
        this.inputValidator = new InputValidator();

        this.itemPictureStorageService = Mockito.mock(ItemPictureStorageService.class);
        Path path = Mockito.mock(Path.class);
        Mockito.when(path.toFile()).thenReturn(null);
        Mockito.when(this.itemPictureStorageService.load(any())).thenReturn(path);

        this.auctionPictureStorage = Mockito.mock(AuctionPictureStorageService.class);

        this.auctionPictureService = new AuctionPictureServiceImpl(this.modelMapper, this.auctionPictureRepository, this.aucItemPictureRepository,
                this.auctionPictureStorage, this.itemPictureStorageService, this.inputValidator);

        this.seller = this.initSeller();
        this.seller.setId(this.SELLER_ID);

        this.itemA = this.createTestItem();
        this.itemA.setId(this.ITEM_A_ID);
        this.itemAServiceModel = this.modelMapper.map(itemA, ItemServiceModel.class);

        this.itemB = this.createTestItem();
        this.itemB.setId(this.ITEM_B_ID);
        this.itemBServiceModel = this.modelMapper.map(itemB, ItemServiceModel.class);

        this.auctionA = this.createTestAuction();
        this.auctionA.setId(this.AUCTION_A_ID);
        this.auctionAServiceModel = this.modelMapper.map(auctionA, AuctionServiceModel.class);

        this.auctionB = this.createTestAuction();
        this.auctionA.setId(this.AUCTION_B_ID);
        this.auctionBServiceModel = this.modelMapper.map(auctionB, AuctionServiceModel.class);

        this.aucItemA = this.createTestAuctionedItem();
        this.aucItemA.setAuction(this.auctionA);
        this.aucItemA.setParent(this.itemA);
        this.aucItemA.setItemName(this.itemA.getName());
        this.aucItemA.setItemDescription(this.itemA.getDescription());
        this.aucItemA.setItemLocation(this.aucItemA.getItemLocation());
        this.aucItemA.setId(this.AUC_ITEM_A_ID);

        this.aucItemB = this.createTestAuctionedItem();
        this.aucItemB.setAuction(this.auctionB);
        this.aucItemB.setParent(this.itemB);
        this.aucItemB.setItemName(this.itemB.getName());
        this.aucItemB.setItemDescription(this.itemB.getDescription());
        this.aucItemB.setItemLocation(this.aucItemB.getItemLocation());
        this.aucItemB.setId(this.AUC_ITEM_B_ID);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addPicture method, of class AuctionPictureServiceImpl.
     */
    @Test
    public void auctionPictureService_addPicture_WithCorrectValues_ShouldAddPicture() throws Exception {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(this.auctionA.getId());
        pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper.setAuctionedItemId(this.aucItemA.getId());

        pictureWrapper = this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        Assert.assertNotNull(pictureWrapper);
        Assert.assertEquals(1L, this.aucItemPictureRepository.count());
        Assert.assertEquals(VALID_ITEM_PICTURE_FILE_NAME, pictureWrapper.getOriginalFileName());
    }

    @Test(expected = ReportToUserException.class)
    public void auctionPictureService_addPicture_WithCorrectValues_MoreThanUploadLimit_houldThrowReportToUserException() throws Exception {
        for (int i = 1; i <= StorageVariables.AUCTIONS_PICTURES_UPLOAD_LIMIT; i++) {
            AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
            pictureWrapper.setAuctionId(this.auctionA.getId());
            pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
            pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
            pictureWrapper.setAuctionedItemId(this.aucItemA.getId());

            pictureWrapper = this.auctionPictureService.addPicture(pictureWrapper, i + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        }
        Assert.assertEquals(StorageVariables.AUCTIONS_PICTURES_UPLOAD_LIMIT, this.auctionPictureRepository.count());
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(this.auctionA.getId());
        pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper.setAuctionedItemId(this.aucItemA.getId());

        pictureWrapper = this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());

    }

    @Test(expected = ReportToUserException.class)
    public void auctionPictureService_addPictureTwiceOnSameAuction_ShouldThrowReportToUserException() throws Exception {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(this.auctionA.getId());
        pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper.setAuctionedItemId(this.aucItemA.getId());

        this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
    }

    @Test
    public void auctionPictureService_addPictureTwiceOnDiffrentAuctions_ShouldIncreasePictureUsageCounter() throws Exception {
        AucItemPictureWrapperServiceModel pictureWrapper1 = new AucItemPictureWrapperServiceModel();
        pictureWrapper1.setAuctionId(this.auctionA.getId());
        pictureWrapper1.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper1.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper1.setAuctionedItemId(this.aucItemA.getId());

        this.auctionPictureService.addPicture(pictureWrapper1, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        Assert.assertEquals(1, this.auctionPictureRepository.findByPictureFileID(VALID_ITEM_PICTURE_FILE_NAME).getUsageCounter());
        AucItemPictureWrapperServiceModel pictureWrapper2 = new AucItemPictureWrapperServiceModel();
        pictureWrapper2.setAuctionId(this.auctionB.getId());
        pictureWrapper2.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper2.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper2.setAuctionedItemId(this.aucItemB.getId());
        this.auctionPictureService.addPicture(pictureWrapper2, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        Assert.assertEquals(2, this.auctionPictureRepository.findByPictureFileID(VALID_ITEM_PICTURE_FILE_NAME).getUsageCounter());
    }

    /**
     * Test of deletePicture method, of class AuctionPictureServiceImpl.
     */
    @Test
    public void auctionPictureService_deletePicture_WithValidInpit_ShouldDeletePicture() throws Exception {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(this.auctionA.getId());
        pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper.setAuctionedItemId(this.aucItemA.getId());

        pictureWrapper = this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        Assert.assertNotNull(this.auctionPictureRepository.findByPictureFileID(VALID_ITEM_PICTURE_FILE_NAME));
        boolean result = this.auctionPictureService.deletePicture(pictureWrapper, this.seller.getDomainEmail());
        Assert.assertTrue(result);
        Assert.assertNull(this.auctionPictureRepository.findByPictureFileID(VALID_ITEM_PICTURE_FILE_NAME));
    }

    @Test
    public void auctionPictureService_deletePicture_deleteMultiplePictures_ShouldDecreaseCounter() throws Exception {
        AucItemPictureWrapperServiceModel pictureWrapper1 = new AucItemPictureWrapperServiceModel();
        pictureWrapper1.setAuctionId(this.auctionA.getId());
        pictureWrapper1.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper1.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper1.setAuctionedItemId(this.aucItemA.getId());

        pictureWrapper1 = this.auctionPictureService.addPicture(pictureWrapper1, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());

        AucItemPictureWrapperServiceModel pictureWrapper2 = new AucItemPictureWrapperServiceModel();
        pictureWrapper2.setAuctionId(this.auctionB.getId());
        pictureWrapper2.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper2.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper2.setAuctionedItemId(this.aucItemB.getId());
        pictureWrapper2 = this.auctionPictureService.addPicture(pictureWrapper2, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());

        Assert.assertEquals(2, this.auctionPictureRepository.findByPictureFileID(VALID_ITEM_PICTURE_FILE_NAME).getUsageCounter());
        this.auctionPictureService.deletePicture(pictureWrapper2, this.seller.getDomainEmail());
        Assert.assertEquals(1, this.auctionPictureRepository.findByPictureFileID(VALID_ITEM_PICTURE_FILE_NAME).getUsageCounter());
        this.auctionPictureService.deletePicture(pictureWrapper1, this.seller.getDomainEmail());
        Assert.assertNull(this.auctionPictureRepository.findByPictureFileID(VALID_ITEM_PICTURE_FILE_NAME));
    }

    @Test(expected = ReportToUserException.class)
    public void auctionPictureService_deletePicture_deleteNonExistentPictureWrapper_ShouldThrowReportToUserException() throws Exception {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(this.auctionA.getId());
        pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper.setAuctionedItemId(this.aucItemA.getId());
        this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        pictureWrapper.setId(-200L);
        this.auctionPictureService.deletePicture(pictureWrapper, this.seller.getDomainEmail());
    }

    /**
     * Test of getAllPicturesByEntity method, of class
     * AuctionPictureServiceImpl.
     */
    @Test
    public void auctionPictureService_getAllPicturesByEntity_WithCorrectValues_ShouldReturnAllPictures1() throws ReportToUserException {
        AucItemPictureWrapperServiceModel pictureWrapper1 = new AucItemPictureWrapperServiceModel();
        pictureWrapper1.setAuctionId(this.auctionA.getId());
        pictureWrapper1.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper1.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper1.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper2 = new AucItemPictureWrapperServiceModel();
        pictureWrapper2.setAuctionId(this.auctionA.getId());
        pictureWrapper2.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper2.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper2.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper3 = new AucItemPictureWrapperServiceModel();
        pictureWrapper3.setAuctionId(this.auctionA.getId());
        pictureWrapper3.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper3.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper3.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel otherAuctionPicture = new AucItemPictureWrapperServiceModel();
        otherAuctionPicture.setAuctionId(this.auctionB.getId());
        otherAuctionPicture.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        otherAuctionPicture.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        otherAuctionPicture.setAuctionedItemId(this.aucItemB.getId());

        this.auctionPictureService.addPicture(pictureWrapper1, 1 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper2, 2 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper3, 3 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(otherAuctionPicture, 4 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        List<AucItemPictureWrapperServiceModel> allPicturesIfAuctionA = this.auctionPictureService.getAllPicturesByEntity(this.modelMapper.map(this.auctionA, AuctionServiceModel.class));
        Assert.assertEquals(3, allPicturesIfAuctionA.size());
        for (AucItemPictureWrapperServiceModel auctionpicture : allPicturesIfAuctionA) {
            Assert.assertNotNull(auctionpicture);
        }
    }

    @Test
    public void auctionPictureService_getAllPicturesByEntity_WithCorrectValues_ShouldReturnAllPictures2() throws ReportToUserException {
        AucItemPictureWrapperServiceModel pictureWrapper1 = new AucItemPictureWrapperServiceModel();
        pictureWrapper1.setAuctionId(this.auctionA.getId());
        pictureWrapper1.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper1.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper1.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper2 = new AucItemPictureWrapperServiceModel();
        pictureWrapper2.setAuctionId(this.auctionA.getId());
        pictureWrapper2.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper2.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper2.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper3 = new AucItemPictureWrapperServiceModel();
        pictureWrapper3.setAuctionId(this.auctionA.getId());
        pictureWrapper3.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper3.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper3.setAuctionedItemId(this.aucItemA.getId());

        this.auctionPictureService.addPicture(pictureWrapper1, 1 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper2, 2 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper3, 3 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        List<AucItemPictureWrapperServiceModel> allPicturesIfAuctionB = this.auctionPictureService.getAllPicturesByEntity(this.modelMapper.map(this.auctionB, AuctionServiceModel.class));
        Assert.assertEquals(0, allPicturesIfAuctionB.size());
    }

    /**
     * Test of deleteAllPicturesFromAuction method, of class
     * AuctionPictureServiceImpl.
     */
    @Test
    public void auctionPictureService_deleteAllPicturesFromAuction_WithCorrectValues_DeleteAllPictures1() throws ReportToUserException {
        AucItemPictureWrapperServiceModel pictureWrapper1 = new AucItemPictureWrapperServiceModel();
        pictureWrapper1.setAuctionId(this.auctionA.getId());
        pictureWrapper1.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper1.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper1.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper2 = new AucItemPictureWrapperServiceModel();
        pictureWrapper2.setAuctionId(this.auctionA.getId());
        pictureWrapper2.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper2.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper2.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper3 = new AucItemPictureWrapperServiceModel();
        pictureWrapper3.setAuctionId(this.auctionA.getId());
        pictureWrapper3.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper3.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper3.setAuctionedItemId(this.aucItemA.getId());

        this.auctionPictureService.addPicture(pictureWrapper1, 1 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper2, 2 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper3, 3 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());

        this.auctionPictureService.deleteAllPicturesFromAuction(this.modelMapper.map(this.auctionA, AuctionServiceModel.class));

        List<AucItemPictureWrapperServiceModel> allPicturesIfAuctionA = this.auctionPictureService.getAllPicturesByEntity(this.modelMapper.map(this.auctionA, AuctionServiceModel.class));
        Assert.assertEquals(0, allPicturesIfAuctionA.size());
    }

    @Test
    public void auctionPictureService_deleteAllPicturesFromAuction_WithCorrectValues_DeleteAllPictures2() throws ReportToUserException {
        AucItemPictureWrapperServiceModel pictureWrapper1 = new AucItemPictureWrapperServiceModel();
        pictureWrapper1.setAuctionId(this.auctionA.getId());
        pictureWrapper1.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper1.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper1.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper2 = new AucItemPictureWrapperServiceModel();
        pictureWrapper2.setAuctionId(this.auctionA.getId());
        pictureWrapper2.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper2.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper2.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel pictureWrapper3 = new AucItemPictureWrapperServiceModel();
        pictureWrapper3.setAuctionId(this.auctionA.getId());
        pictureWrapper3.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper3.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper3.setAuctionedItemId(this.aucItemA.getId());

        AucItemPictureWrapperServiceModel otherAuctionPicture = new AucItemPictureWrapperServiceModel();
        otherAuctionPicture.setAuctionId(this.auctionB.getId());
        otherAuctionPicture.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        otherAuctionPicture.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        otherAuctionPicture.setAuctionedItemId(this.aucItemB.getId());

        this.auctionPictureService.addPicture(pictureWrapper1, 1 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper2, 2 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(pictureWrapper3, 3 + VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        this.auctionPictureService.addPicture(otherAuctionPicture, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());

        this.auctionPictureService.deleteAllPicturesFromAuction(this.modelMapper.map(this.auctionA, AuctionServiceModel.class));

        List<AucItemPictureWrapperServiceModel> allPicturesIfAuctionA = this.auctionPictureService.getAllPicturesByEntity(this.modelMapper.map(this.auctionA, AuctionServiceModel.class));
        Assert.assertEquals(0, allPicturesIfAuctionA.size());
        Assert.assertEquals(1L, this.auctionPictureRepository.count());
    }

    /**
     * Test of adminDeletePicture method, of class AuctionPictureServiceImpl.
     */
    @Test
    public void auctionPictureService_adminDeletePicture_WithCorrectValues_DeletePicture() throws ReportToUserException {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(this.auctionA.getId());
        pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper.setAuctionedItemId(this.aucItemA.getId());

        pictureWrapper = this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());

        this.auctionPictureService.adminDeletePicture(pictureWrapper);

        Assert.assertEquals(0, this.auctionPictureRepository.count());
    }

    @Test(expected = ReportToUserException.class)
    public void auctionPictureService_adminDeletePicture_WithincorrectValues_ShouldThrowReportToUserException() throws ReportToUserException {
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(this.auctionA.getId());
        pictureWrapper.setDescription(VALID_ITEM_PICTURE_DESCRIPTION);
        pictureWrapper.setAuctionedItemName(VALID_AUC_ITEM_NAME);
        pictureWrapper.setAuctionedItemId(this.aucItemA.getId());

        pictureWrapper = this.auctionPictureService.addPicture(pictureWrapper, VALID_ITEM_PICTURE_FILE_NAME, this.seller.getDomainEmail());
        pictureWrapper.setId(-200L);
        this.auctionPictureService.adminDeletePicture(pictureWrapper);
    }

}

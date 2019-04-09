/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.SHOP;

import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.AuctionedItem;
import Atia.Shop.domain.entities.Item;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.entities.pictures.ItemPicture;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.ItemServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.ItemPictureServiceModel;
import Atia.Shop.domain.repositories.AuctionRepository;
import Atia.Shop.domain.repositories.AuctionedItemsRepository;
import Atia.Shop.domain.repositories.ItemRepository;
import Atia.Shop.domain.repositories.UserRepository;
import Atia.Shop.domain.repositories.pictures.ItemPictureRepository;
import Atia.Shop.exeptions.base.InvalidRoleException;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.Pictures.ItemPictureService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ItemServiceImplTest {
    
    @Autowired
    private ItemPictureRepository itemPictureRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private AuctionedItemsRepository aucItemsRepository;

    private ModelMapper modelMapper;
    private ItemPictureService itemPictureService;
    private InputValidator inputValidator;
    private ItemService itemService;

    /////////////////////////////////////
    @Autowired
    private UserRepository userRepository;
    private final String SELLER_A_EMAIL = "seller@atia.com";
    private User sellerA;
    private UserServiceModel seller_A_SM;

    private final String SELLER_B_EMAIL = "bidder@atia.com";
    private User sellerB;
    private UserServiceModel seller_B_SM;

    //////////////////////////////////////
    @Autowired
    private AuctionRepository auctionRepository;
    private Auction auctionA;
    private AuctionServiceModel auctionAServiceModel;

    private Auction auctionB;
    private AuctionServiceModel auctionBServiceModel;

    ///////////////////////////////////////
    private final Integer DEFAULT_ITEM_QUANTITY = 5;

    ///////////////////////////////////////
    public ItemServiceImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    private User initUser() {
        User user = new User();
        user.setName("User name");
        //user.setDomainEmail("vlado@atia.com");
        user.setIsActive(Boolean.TRUE);
        user.setTestPassword("password");
        user.setAuthorities(new HashSet());
        return user;
    }

    private Auction createTestAuction() {
        Auction auction = new Auction();
        auction.setTitle("Auction title");
        //auction.setSeller(this.seller);
        auction.setAuctionStrategy(AuctionStrategy.OPEN_BIDDING_HIGHEST_BID);
        auction.setDescription("My Auction description");
        auction.setInitialPrice(BigDecimal.ZERO);
        auction.setDateStarted(new Date());
        auction.setDateExpired(new Date());
        auction.setStatus(AuctionStatus.CREATED);
        return auction;
    }

    private Item createItem() {
        Item item = new Item();
        item.setName("My item name");
        item.setDescription("My item description");
        item.setLocation("My item location");
        item.setQuantity(this.DEFAULT_ITEM_QUANTITY);
        item.setInitialPrice(BigDecimal.ONE);
        return item;
    }

    @Before
    public void setUp() {
        this.modelMapper = new ModelMapper();
        this.inputValidator = new InputValidator();
        this.itemPictureService = Mockito.mock(ItemPictureService.class);

        this.itemService = new ItemServiceImpl(this.modelMapper, this.itemRepository, this.aucItemsRepository, this.itemPictureService, this.inputValidator);

        this.sellerA = this.initUser();
        this.sellerA.setDomainEmail(this.SELLER_A_EMAIL);
        this.sellerA = this.userRepository.saveAndFlush(this.sellerA);
        this.seller_A_SM = this.modelMapper.map(this.sellerA, UserServiceModel.class);

        this.sellerB = this.initUser();
        this.sellerB.setDomainEmail(this.SELLER_B_EMAIL);
        this.sellerB = this.userRepository.saveAndFlush(this.sellerB);
        this.seller_B_SM = this.modelMapper.map(this.sellerB, UserServiceModel.class);

        this.auctionA = this.createTestAuction();
        this.auctionA.setSeller(this.sellerA);
        this.auctionA = this.auctionRepository.saveAndFlush(this.auctionA);
        this.auctionAServiceModel = this.modelMapper.map(this.auctionA, AuctionServiceModel.class);
        this.auctionB = this.createTestAuction();
        this.auctionB.setSeller(this.sellerB);
        this.auctionB = this.auctionRepository.saveAndFlush(this.auctionB);
        this.auctionBServiceModel = this.modelMapper.map(this.auctionB, AuctionServiceModel.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createItem method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_createItem_WithCorrectInput_SchouldCreateItem() {
        Item item = this.createItem();

        ItemServiceModel itemSM = this.modelMapper.map(item, ItemServiceModel.class);
        ItemServiceModel actual = this.itemService.createItem(itemSM, this.seller_A_SM);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getDateAdded());
        Assert.assertNotNull(actual.getSeller());
        Assert.assertTrue(this.seller_A_SM.getId().equals(actual.getSeller().getId()));
        Assert.assertTrue(itemSM.getDescription().equals(actual.getDescription()));
        Assert.assertTrue(itemSM.getLocation().equals(actual.getLocation()));
        Assert.assertTrue(itemSM.getName().equals(actual.getName()));
        Assert.assertEquals(itemSM.getQuantity(), actual.getQuantity());
        Assert.assertTrue(itemSM.getInitialPrice().equals(actual.getInitialPrice()));

    }

    /**
     * Test of isItemEditable method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_isItemEditable_WithCorrectInput_ShouldReturnTrue() {
        Item item = this.createItem();

        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        boolean actual = this.itemService.isItemEditable(itemSM.getId());
        Assert.assertTrue(actual);
    }

    /**
     * Test of isItemDeletable method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_isItemDeletable_WithCreatedAuction_ShouldWorkCorrectly() throws ReportToUserException {
        Auction auction = this.createTestAuction();
        auction.setSeller(this.sellerA);
        auction.setStatus(AuctionStatus.CREATED);
        auction = this.auctionRepository.saveAndFlush(auction);
        AuctionServiceModel auctionSM = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(1);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, auctionSM);
        
        boolean result = this.itemService.isItemDeletable(itemSM.getId());
        Assert.assertTrue(result);
    }
    
    @Test
    public void itemService_isItemDeletable_WithLivedAuction_ShouldWorkCorrectly() throws ReportToUserException {
        Auction auction = this.createTestAuction();
        auction.setSeller(this.sellerA);
        auction.setStatus(AuctionStatus.LIVE);
        auction = this.auctionRepository.saveAndFlush(auction);
        AuctionServiceModel auctionSM = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(1);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, auctionSM);
        boolean result = this.itemService.isItemDeletable(itemSM.getId());
        Assert.assertFalse(result);
        
    }
    
    @Test
    public void itemService_isItemDeletable_WithSelledAuction_ShouldWorkCorrectly() throws ReportToUserException {
        Auction auction = this.createTestAuction();
        auction.setSeller(this.sellerA);
        auction.setStatus(AuctionStatus.SELLED);
        auction = this.auctionRepository.saveAndFlush(auction);
        AuctionServiceModel auctionSM = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(1);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, auctionSM);
        
        boolean result = this.itemService.isItemDeletable(itemSM.getId());
        Assert.assertTrue(result);
        
    }
    
    @Test
    public void itemService_isItemDeletable_WithExpiredAuction_ShouldWorkCorrectly() throws ReportToUserException {
        Auction auction = this.createTestAuction();
        auction.setSeller(this.sellerA);
        auction.setStatus(AuctionStatus.EXPIRED);
        auction = this.auctionRepository.saveAndFlush(auction);
        AuctionServiceModel auctionSM = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(1);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, auctionSM);
        
        boolean result = this.itemService.isItemDeletable(itemSM.getId());
        Assert.assertTrue(result);
        
    }
    
    @Test
    public void itemService_isItemDeletable_WithArchiveAuction_ShouldWorkCorrectly() throws ReportToUserException {
        Auction auction = this.createTestAuction();
        auction.setSeller(this.sellerA);
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction = this.auctionRepository.saveAndFlush(auction);
        AuctionServiceModel auctionSM = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(1);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, auctionSM);
        
        boolean result = this.itemService.isItemDeletable(itemSM.getId());
        Assert.assertTrue(result);
    }

    /**
     * Test of findAllAucItemsByAuctionId method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_findAllAucItemsByAuctionId_WithCorrectInput_ShouldReturnAllAucItemsByItemID() throws ReportToUserException {
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 6;

        List<ItemServiceModel> itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionAServiceModel);
        itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionBServiceModel);
        List<AuctionedItemServiceModel> aucitemsByAuctionA = this.itemService.findAllAucItemsByAuctionId(this.auctionAServiceModel.getId());
        Assert.assertEquals(numOfAddedItems, aucitemsByAuctionA.size());
        for(AuctionedItemServiceModel aucItem:aucitemsByAuctionA){
            Assert.assertNotNull(aucItem);
        }
    }
    
    @Test
    public void itemService_findAllAucItemsByAuctionId_WithEmptyAuctuon_ShouldReturnEmptyList() throws ReportToUserException {
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 6;

        List<ItemServiceModel> itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionAServiceModel);
        List<AuctionedItemServiceModel> aucitemsByAuctionA = this.itemService.findAllAucItemsByAuctionId(this.auctionBServiceModel.getId());
        Assert.assertEquals(0, aucitemsByAuctionA.size());
    }

    /**
     * Test of getAllAucItemsByItemId method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_getAllAucItemsByItemId_WithCorrectInput_ShouldReturnCorrectResult() throws ReportToUserException {
        Item item = this.createItem();
        item.setQuantity(20);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        ItemServiceModel itemToAdd1 = new ItemServiceModel();
        itemToAdd1.setId(itemSM.getId());
        itemToAdd1.setDescription(itemSM.getDescription());
        itemToAdd1.setDateAdded(itemSM.getDateAdded());
        itemToAdd1.setQuantity(5);
        itemToAdd1.setSeller(itemSM.getSeller());
        itemToAdd1.setLocation(itemSM.getLocation());
        itemToAdd1.setThumbnail(itemSM.getThumbnail());
        itemToAdd1.setName(itemSM.getName());
        
        this.itemService.addToAuction(new ArrayList(){{add(itemToAdd1);}}, this.auctionAServiceModel);
        this.itemService.addToAuction(new ArrayList(){{add(itemToAdd1);}}, this.auctionBServiceModel);
        
        Item otherItem = this.createItem();
        otherItem.setQuantity(20);
        ItemServiceModel otherItemSM = this.itemService.createItem(this.modelMapper.map(otherItem, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd2 = new ItemServiceModel();
        itemToAdd2.setId(otherItemSM.getId());
        itemToAdd2.setDescription(otherItemSM.getDescription());
        itemToAdd2.setDateAdded(otherItemSM.getDateAdded());
        itemToAdd2.setQuantity(5);
        itemToAdd2.setSeller(otherItemSM.getSeller());
        itemToAdd2.setLocation(otherItemSM.getLocation());
        itemToAdd2.setThumbnail(otherItemSM.getThumbnail());
        itemToAdd2.setName(otherItemSM.getName());
        this.itemService.addToAuction(new ArrayList(){{add(otherItemSM);}}, this.auctionBServiceModel);
         
        List<AuctionedItemServiceModel> items = this.itemService.getAllAucItemsByItemId(itemSM.getId());
        for(AuctionedItemServiceModel fetchedItem: items){
            Assert.assertNotNull(fetchedItem);
        }
        Set<Long> fetchedItemsIDs = items.stream().map(x->x.getId()).collect(Collectors.toSet());
        Assert.assertEquals(2, fetchedItemsIDs.size());
    }
    
    @Test
    public void itemService_getAllAucItemsByItemId_OnEmptyDB_ShouldReturnEmptyList() throws ReportToUserException {
        List<AuctionedItemServiceModel> items = this.itemService.getAllAucItemsByItemId(10000L);
        Assert.assertEquals(0, items.size());
    }

    /**
     * Test of addToAuction method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_addToAuction_WithSingleCorrectInput_ShouldAddItemToAuction() throws Exception {
        Integer itemQuantityToAddToAuction = 4;

        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(itemQuantityToAddToAuction);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        boolean result = this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
        Assert.assertTrue(result);
        Assert.assertEquals(1, this.aucItemsRepository.count());
        AuctionedItem aucItem = this.aucItemsRepository.findAll().get(0);
        Assert.assertEquals(itemSM.getId().longValue(), aucItem.getParent().getId());
        Assert.assertTrue(itemToAdd.getName().equals(aucItem.getItemName()));
        Assert.assertTrue(itemToAdd.getDescription().equals(aucItem.getItemDescription()));
        Assert.assertTrue(itemToAdd.getLocation().equals(aucItem.getItemLocation()));
        Assert.assertEquals(aucItem.getAuction().getId(), this.auctionAServiceModel.getId().longValue());
        Assert.assertTrue(item.getInitialPrice().compareTo(aucItem.getItemPrice()) == 0);
        Assert.assertEquals(itemQuantityToAddToAuction, aucItem.getQuantity());

        Item changedItem = this.itemRepository.findAll().get(0);
        Assert.assertEquals(item.getQuantity() - itemQuantityToAddToAuction, changedItem.getQuantity().intValue());
    }

    @Test
    public void itemService_addToAuction_OnSameItemMultipleTimes_ShouldLowerItemCountCorrectly() throws Exception {
        Integer itemQuantityToAddToAuctionA = 2;
        Integer itemQuantityToAddToAuctionB = 1;

        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd1 = new ItemServiceModel();
        itemToAdd1.setId(itemSM.getId());
        itemToAdd1.setDescription(itemSM.getDescription());
        itemToAdd1.setDateAdded(itemSM.getDateAdded());
        itemToAdd1.setQuantity(itemQuantityToAddToAuctionA);
        itemToAdd1.setSeller(itemSM.getSeller());
        itemToAdd1.setLocation(itemSM.getLocation());
        itemToAdd1.setThumbnail(itemSM.getThumbnail());
        itemToAdd1.setName(itemSM.getName());

        ItemServiceModel itemToAdd2 = new ItemServiceModel();
        itemToAdd2.setId(itemSM.getId());
        itemToAdd2.setDescription(itemSM.getDescription());
        itemToAdd2.setDateAdded(itemSM.getDateAdded());
        itemToAdd2.setQuantity(itemQuantityToAddToAuctionB);
        itemToAdd2.setSeller(itemSM.getSeller());
        itemToAdd2.setLocation(itemSM.getLocation());
        itemToAdd2.setThumbnail(itemSM.getThumbnail());
        itemToAdd2.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {
            {
                add(itemToAdd1);
            }
        }, this.auctionAServiceModel);
        this.itemService.addToAuction(new ArrayList() {
            {
                add(itemToAdd2);
            }
        }, this.auctionBServiceModel);

        Item changedItem = this.itemRepository.findAll().get(0);
        Assert.assertEquals(item.getQuantity() - itemQuantityToAddToAuctionA - itemQuantityToAddToAuctionB, changedItem.getQuantity().intValue());
    }

    @Test(expected = ReportToUserException.class)
    public void itemService_addToAuction_AddingMoreItemsThanYouOwn_ShouldThrowReportToUserException() throws Exception {
        Integer itemQuantityToAddToAuctionA = 20;

        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(itemQuantityToAddToAuctionA);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void itemService_addToAuction_WithNonExistingItem_ShouldThrowReporPartlyToUserException() throws Exception {
        Integer itemQuantityToAddToAuctionA = 3;
        Item item = this.createItem();
        item.setId(Long.MAX_VALUE);
        ItemServiceModel itemSM = this.modelMapper.map(item, ItemServiceModel.class);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(itemQuantityToAddToAuctionA);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
    }

    @Test(expected = ReportToUserException.class)
    public void itemService_addToAuction_WithInvalidQuantity_ShouldThrowReportToUserException() throws Exception {
        Integer itemQuantityToAddToAuctionA = -3;

        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(itemQuantityToAddToAuctionA);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
    }

    @Test
    public void itemService_addToAuction_WithMultipleCorrectInputs_ShouldAddItemToAuction() throws Exception {
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 6;

        List<ItemServiceModel> itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }

        boolean result = this.itemService.addToAuction(itemsToAdd, this.auctionAServiceModel);
        Assert.assertTrue(result);
        Assert.assertEquals(numOfAddedItems, this.aucItemsRepository.count());
        List<AuctionedItem> aucItems = this.aucItemsRepository.findAll();
        for (AuctionedItem aucItem : aucItems) {
            Assert.assertNotNull(aucItem);
        }
    }

    @Test
    public void itemService_addToAuction_OnExistingAuctionedItem_ShouldChangeExistingAuctionedItem() throws Exception {
        Integer itemQuantityToAddToAuction = 1;

        Item item = this.createItem();
        item.setQuantity(10);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(itemQuantityToAddToAuction);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        boolean result = this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
        Assert.assertTrue(result);
        Assert.assertEquals(1, this.aucItemsRepository.count());

        result = this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
        Assert.assertTrue(result);
        Assert.assertEquals(1, this.aucItemsRepository.count());

        result = this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
        Assert.assertTrue(result);
        Assert.assertEquals(1, this.aucItemsRepository.count());

        result = this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
        Assert.assertTrue(result);
        Assert.assertEquals(1, this.aucItemsRepository.count());

        AuctionedItem aucItem = this.aucItemsRepository.findAll().get(0);
        Assert.assertEquals(4 * itemQuantityToAddToAuction, aucItem.getQuantity().longValue());
    }

    @Test
    public void itemService_addToAuction_SameItemTwoAuctions_ShouldCreateSeparateAuctionedItems() throws Exception {
        Integer itemQuantityToAddToAuction = 1;

        Item item = this.createItem();
        item.setQuantity(10);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(itemQuantityToAddToAuction);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());

        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionAServiceModel);
        this.itemService.addToAuction(new ArrayList() {{add(itemToAdd);}}, this.auctionBServiceModel);
        List<AuctionedItem> aucItems = this.aucItemsRepository.findAll();
        Assert.assertEquals(2, aucItems.size());
        for (AuctionedItem aucItem : aucItems) {
            Assert.assertNotNull(aucItem);
        }
    }

    /**
     * Test of getAllAuctionsByItemId method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_getAllAuctionsByItemId_WtichCorrectInput_ShouldReturnAllAuctionsByItem() throws ReportToUserException {
        Item item = this.createItem();
        item.setQuantity(20);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        ItemServiceModel itemToAdd1 = new ItemServiceModel();
        itemToAdd1.setId(itemSM.getId());
        itemToAdd1.setDescription(itemSM.getDescription());
        itemToAdd1.setDateAdded(itemSM.getDateAdded());
        itemToAdd1.setQuantity(5);
        itemToAdd1.setSeller(itemSM.getSeller());
        itemToAdd1.setLocation(itemSM.getLocation());
        itemToAdd1.setThumbnail(itemSM.getThumbnail());
        itemToAdd1.setName(itemSM.getName());
        
        this.itemService.addToAuction(new ArrayList(){{add(itemToAdd1);}}, this.auctionAServiceModel);
        this.itemService.addToAuction(new ArrayList(){{add(itemToAdd1);}}, this.auctionBServiceModel);
        
        Item otherItem = this.createItem();
        otherItem.setQuantity(20);
        ItemServiceModel otherItemSM = this.itemService.createItem(this.modelMapper.map(otherItem, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd2 = new ItemServiceModel();
        itemToAdd2.setId(otherItemSM.getId());
        itemToAdd2.setDescription(otherItemSM.getDescription());
        itemToAdd2.setDateAdded(otherItemSM.getDateAdded());
        itemToAdd2.setQuantity(5);
        itemToAdd2.setSeller(otherItemSM.getSeller());
        itemToAdd2.setLocation(otherItemSM.getLocation());
        itemToAdd2.setThumbnail(otherItemSM.getThumbnail());
        itemToAdd2.setName(otherItemSM.getName());
        this.itemService.addToAuction(new ArrayList(){{add(otherItemSM);}}, this.auctionBServiceModel);
         
        List<AuctionServiceModel> auctions = this.itemService.getAllAuctionsByItemId(itemSM.getId());
        for(AuctionServiceModel auction: auctions){
            Assert.assertNotNull(auction);
        }
        Set<Long> auctionsIDs = auctions.stream().map(x->x.getId()).collect(Collectors.toSet());
        Assert.assertEquals(2, auctionsIDs.size());
    }
    
    @Test
    public void itemService_getAllAuctionsByItemId_OnNonExistingItem_ShouldReturnEmptyList() throws ReportToUserException {
        Item item = this.createItem();
        item.setQuantity(20);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        ItemServiceModel itemToAdd1 = new ItemServiceModel();
        itemToAdd1.setId(itemSM.getId());
        itemToAdd1.setDescription(itemSM.getDescription());
        itemToAdd1.setDateAdded(itemSM.getDateAdded());
        itemToAdd1.setQuantity(5);
        itemToAdd1.setSeller(itemSM.getSeller());
        itemToAdd1.setLocation(itemSM.getLocation());
        itemToAdd1.setThumbnail(itemSM.getThumbnail());
        itemToAdd1.setName(itemSM.getName());
        
        this.itemService.addToAuction(new ArrayList(){{add(itemToAdd1);}}, this.auctionAServiceModel);
        this.itemService.addToAuction(new ArrayList(){{add(itemToAdd1);}}, this.auctionBServiceModel);
        
        List<AuctionServiceModel> auctions = this.itemService.getAllAuctionsByItemId(100000L);
        Assert.assertEquals(0, auctions.size());
    }

    /**
     * Test of removeAllFromAuction method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_removeAllFromAuction_NotFullRemoveWithNotNullParent_ShouldChangeItemAndAucItem() throws ReportToUserException {
        Integer INITIAL_ITEM_QUANTITY = 20;
        Integer ADD_ITEM_QUANTITY = 5;
        Integer REMOVE_ITEM_QUANTITY = 3;
        Item item = this.createItem();
        item.setQuantity(INITIAL_ITEM_QUANTITY);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        ItemServiceModel itemDAO = new ItemServiceModel();
        itemDAO.setId(itemSM.getId());
        itemDAO.setDescription(itemSM.getDescription());
        itemDAO.setDateAdded(itemSM.getDateAdded());
        itemDAO.setQuantity(ADD_ITEM_QUANTITY);
        itemDAO.setSeller(itemSM.getSeller());
        itemDAO.setLocation(itemSM.getLocation());
        itemDAO.setThumbnail(itemSM.getThumbnail());
        itemDAO.setName(itemSM.getName());
        
        this.itemService.addToAuction(new ArrayList(){{add(itemDAO);}}, this.auctionAServiceModel);
        
        Item itemDB = this.itemRepository.findAll().get(0);
        Assert.assertEquals(INITIAL_ITEM_QUANTITY-ADD_ITEM_QUANTITY, itemDB.getQuantity().longValue());
        AuctionedItem aucItemDB = this.aucItemsRepository.findAll().get(0);
        Assert.assertEquals(ADD_ITEM_QUANTITY.longValue(), aucItemDB.getQuantity().longValue());
        
        AuctionedItemServiceModel aucItemDAO = this.modelMapper.map(aucItemDB, AuctionedItemServiceModel.class);
        aucItemDAO.setQuantity(REMOVE_ITEM_QUANTITY);
        boolean result = this.itemService.removeAllFromAuction(new ArrayList(){{add(aucItemDAO);}}, false);
        Assert.assertTrue(result);
        itemDB = this.itemRepository.findAll().get(0);
        Assert.assertEquals(INITIAL_ITEM_QUANTITY-ADD_ITEM_QUANTITY+REMOVE_ITEM_QUANTITY, itemDB.getQuantity().longValue());
        aucItemDB = this.aucItemsRepository.findAll().get(0);
        Assert.assertEquals(ADD_ITEM_QUANTITY-REMOVE_ITEM_QUANTITY, aucItemDB.getQuantity().longValue());
    }
    
    @Test
    public void itemService_removeAllFromAuction_FullRemoveWithNotNullParent_ShouldRemoveAucItem() throws ReportToUserException {
        Integer INITIAL_ITEM_QUANTITY = 20;
        Integer ADD_ITEM_QUANTITY = 5;
        Integer REMOVE_ITEM_QUANTITY = 5;
        Item item = this.createItem();
        item.setQuantity(INITIAL_ITEM_QUANTITY);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        ItemServiceModel itemDAO = new ItemServiceModel();
        itemDAO.setId(itemSM.getId());
        itemDAO.setDescription(itemSM.getDescription());
        itemDAO.setDateAdded(itemSM.getDateAdded());
        itemDAO.setQuantity(ADD_ITEM_QUANTITY);
        itemDAO.setSeller(itemSM.getSeller());
        itemDAO.setLocation(itemSM.getLocation());
        itemDAO.setThumbnail(itemSM.getThumbnail());
        itemDAO.setName(itemSM.getName());
        
        this.itemService.addToAuction(new ArrayList(){{add(itemDAO);}}, this.auctionAServiceModel);
        
        Item itemDB = this.itemRepository.findAll().get(0);
        Assert.assertEquals(INITIAL_ITEM_QUANTITY-ADD_ITEM_QUANTITY, itemDB.getQuantity().longValue());
        AuctionedItem aucItemDB = this.aucItemsRepository.findAll().get(0);
        Assert.assertEquals(ADD_ITEM_QUANTITY.longValue(), aucItemDB.getQuantity().longValue());
        
        AuctionedItemServiceModel aucItemDAO = this.modelMapper.map(aucItemDB, AuctionedItemServiceModel.class);
        aucItemDAO.setQuantity(REMOVE_ITEM_QUANTITY);
        boolean result = this.itemService.removeAllFromAuction(new ArrayList(){{add(aucItemDAO);}}, false);
        Assert.assertTrue(result);
        itemDB = this.itemRepository.findAll().get(0);
        Assert.assertEquals(INITIAL_ITEM_QUANTITY-ADD_ITEM_QUANTITY+REMOVE_ITEM_QUANTITY, itemDB.getQuantity().longValue());
        Assert.assertEquals(0, this.aucItemsRepository.findAll().size());
    }
     
    @Test
    public void itemService_removeAllFromAuction_RemoveFromLiveAuction_WithDeleteAucItemFlagOn_ShouldDeleteAucItemAndRestoreItemIfNotNull() throws ReportToUserException {
        Integer INITIAL_ITEM_QUANTITY = 20;
        Integer ADD_ITEM_QUANTITY = 5;
        Integer REMOVE_ITEM_QUANTITY = 5;
        Item item = this.createItem();
        item.setQuantity(INITIAL_ITEM_QUANTITY);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        ItemServiceModel itemDAO = new ItemServiceModel();
        itemDAO.setId(itemSM.getId());
        itemDAO.setDescription(itemSM.getDescription());
        itemDAO.setDateAdded(itemSM.getDateAdded());
        itemDAO.setQuantity(ADD_ITEM_QUANTITY);
        itemDAO.setSeller(itemSM.getSeller());
        itemDAO.setLocation(itemSM.getLocation());
        itemDAO.setThumbnail(itemSM.getThumbnail());
        itemDAO.setName(itemSM.getName());
        
        Auction auction = this.createTestAuction();
        auction.setSeller(this.sellerA);
        auction = this.auctionRepository.saveAndFlush(auction);
        AuctionServiceModel auctionSM = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        
        this.itemService.addToAuction(new ArrayList(){{add(itemDAO);}}, auctionSM);
        
        Item itemDB = this.itemRepository.findAll().get(0);
        Assert.assertEquals(INITIAL_ITEM_QUANTITY-ADD_ITEM_QUANTITY, itemDB.getQuantity().longValue());
        AuctionedItem aucItemDB = this.aucItemsRepository.findAll().get(0);
        Assert.assertEquals(ADD_ITEM_QUANTITY.longValue(), aucItemDB.getQuantity().longValue());
        
        auction.setStatus(AuctionStatus.LIVE);
        this.auctionRepository.saveAndFlush(auction);
        
        AuctionedItemServiceModel aucItemDAO = this.modelMapper.map(aucItemDB, AuctionedItemServiceModel.class);
        aucItemDAO.setQuantity(REMOVE_ITEM_QUANTITY);
        boolean result = this.itemService.removeAllFromAuction(new ArrayList(){{add(aucItemDAO);}},true);
        
        Assert.assertTrue(result);
        itemDB = this.itemRepository.findAll().get(0);
        Assert.assertEquals(INITIAL_ITEM_QUANTITY.longValue(), itemDB.getQuantity().longValue());
        List<AuctionedItem> aucItems = this.aucItemsRepository.findAll();
        Assert.assertEquals(0, aucItems.size());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void itemService_removeAllFromAuction_WithNonExistingAucItem_ShouldThrowReporPartlyToUserException() throws ReportToUserException {
        Integer INITIAL_ITEM_QUANTITY = 20;
        Integer ADD_ITEM_QUANTITY = 5;
        Integer REMOVE_ITEM_QUANTITY = 5;
        Item item = this.createItem();
        item.setQuantity(INITIAL_ITEM_QUANTITY);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        ItemServiceModel itemDAO = new ItemServiceModel();
        itemDAO.setId(itemSM.getId());
        itemDAO.setDescription(itemSM.getDescription());
        itemDAO.setDateAdded(itemSM.getDateAdded());
        itemDAO.setQuantity(ADD_ITEM_QUANTITY);
        itemDAO.setSeller(itemSM.getSeller());
        itemDAO.setLocation(itemSM.getLocation());
        itemDAO.setThumbnail(itemSM.getThumbnail());
        itemDAO.setName(itemSM.getName());
        
        this.itemService.addToAuction(new ArrayList(){{add(itemDAO);}}, this.auctionAServiceModel);
        
        AuctionedItem aucItemDB = this.aucItemsRepository.findAll().get(0);
         
        AuctionedItemServiceModel aucItemDAO = this.modelMapper.map(aucItemDB, AuctionedItemServiceModel.class);
        aucItemDAO.setId(Long.MAX_VALUE);
        aucItemDAO.setQuantity(REMOVE_ITEM_QUANTITY);
        this.itemService.removeAllFromAuction(new ArrayList(){{add(aucItemDAO);}},false);
    }

    /**
     * Test of getAucItemById method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_getAucItemById_WithCorrectInput_ShouldReturnAucItem() throws ReportToUserException {
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 6;

        List<ItemServiceModel> itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionAServiceModel);
        List<AuctionedItem> aucItemDB = this.aucItemsRepository.findAll();
        for(AuctionedItem aucItem: aucItemDB){
            AuctionedItemServiceModel fetchedAucItem = this.itemService.getAucItemById(aucItem.getId());
            Assert.assertNotNull(fetchedAucItem);
        }
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void itemService_getAucItemById_WithNoNExtingAucItemID_ShouldThrowReporPartlyToUserException() throws ReportToUserException {
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 6;

        List<ItemServiceModel> itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionAServiceModel);
        this.itemService.getAucItemById(Long.MAX_VALUE);
    }

    /**
     * Test of getAllItemsByCreator method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_getAllItemsByCreator_WithCorrectInput_ShouldReturnAllItems() throws ReportToUserException {
        int numOfAddedItems = 6;
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        }

        Item item = this.createItem();
        item.setQuantity(20);
        this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_B_SM);

        List<ItemServiceModel> sellerA_Items = this.itemService.getAllItemsByCreator(this.seller_A_SM, UserRolesEnum.SELLER);
        Assert.assertEquals(numOfAddedItems, sellerA_Items.size());
        for (ItemServiceModel sellerItem : sellerA_Items) {
            Assert.assertNotNull(sellerItem);
        }
    }

    @Test(expected = InvalidRoleException.class)
    public void itemService_getAllItemsByCreator_WithUnSupportedRole_ShouldThrowInvalidRoleException() throws ReportToUserException {
        Item item = this.createItem();
        item.setQuantity(20);
        this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_B_SM);

        this.itemService.getAllItemsByCreator(this.seller_A_SM, UserRolesEnum.BUYER);
    }

    /**
     * Test of getAllAvailableItems method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_getAllAvailableItems_WithCorrectInput1_ShouldReturnCorrectResult1() {
        Item item1 = this.createItem();
        item1.setQuantity(1);
        ItemServiceModel itemSM1 = this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        
        Item item2 = this.createItem();
        item2.setQuantity(0);
        this.itemService.createItem(this.modelMapper.map(item2, ItemServiceModel.class), this.seller_A_SM);
        
        List<ItemServiceModel> availableItems = this.itemService.getAllAvailableItems(this.seller_A_SM, UserRolesEnum.SELLER);
        Assert.assertEquals(1, availableItems.size());
        ItemServiceModel availableItem = availableItems.get(0);
        Assert.assertEquals(itemSM1.getId(), availableItem.getId());
    }
    
    @Test
    public void itemService_getAllAvailableItems_WithCorrectInput2_ShouldReturnCorrectResult2() {
        Item item1 = this.createItem();
        item1.setQuantity(1);
        this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        
        Item item2 = this.createItem();
        item2.setQuantity(0);
        this.itemService.createItem(this.modelMapper.map(item2, ItemServiceModel.class), this.seller_A_SM);
        
        List<ItemServiceModel> availableItems = this.itemService.getAllAvailableItems(this.seller_B_SM, UserRolesEnum.SELLER);
        Assert.assertEquals(0, availableItems.size());
    }
    
    @Test
    public void itemService_getAllAvailableItems_WithCorrectInputAndAdminRole_ShouldReturnCorrectResult() {
        Item item1 = this.createItem();
        item1.setQuantity(1);
        ItemServiceModel itemSM1 = this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        
        Item item2 = this.createItem();
        item2.setQuantity(0);
        this.itemService.createItem(this.modelMapper.map(item2, ItemServiceModel.class), this.seller_A_SM);
        
        List<ItemServiceModel> availableItems = this.itemService.getAllAvailableItems(this.seller_A_SM, UserRolesEnum.ADMIN);
        Assert.assertEquals(1, availableItems.size());
        ItemServiceModel availableItem = availableItems.get(0);
        Assert.assertEquals(itemSM1.getId(), availableItem.getId());
    }
    
    @Test(expected = InvalidRoleException.class)
    public void itemService_getAllAvailableItems_WithUnSupportedRole_ShouldThrowInvalidRoleException() {
        Item item1 = this.createItem();
        item1.setQuantity(1);
        this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        
        Item item2 = this.createItem();
        item2.setQuantity(0);
        this.itemService.createItem(this.modelMapper.map(item2, ItemServiceModel.class), this.seller_A_SM);
        
        this.itemService.getAllAvailableItems(this.seller_A_SM, UserRolesEnum.BUYER);
    }

    /**
     * Test of getItemById method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_getItemById_WithSellerRoleAndCorrectInput_ShouldReturnItem() {
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel actual = this.itemService.getItemById(itemSM.getId(), this.SELLER_A_EMAIL, UserRolesEnum.SELLER);
        Assert.assertNotNull(actual);
    }

    @Test
    public void itemService_getItemById_WithRooTAdminAndCorrectInput_ShouldReturnItem() {
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel actual = this.itemService.getItemById(itemSM.getId(), this.SELLER_B_EMAIL, UserRolesEnum.ADMIN);
        Assert.assertNotNull(actual);
    }

    @Test(expected = InvalidRoleException.class)
    public void itemService_getItemById_WithNotSupportedRole_ShouldThrowInvalidRoleException() {
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        this.itemService.getItemById(itemSM.getId(), this.SELLER_B_EMAIL, UserRolesEnum.BUYER);
    }

    @Test(expected = InvalidRoleException.class)
    public void itemService_getItemById_WithNotExistingId_ShouldThrowInvalidRoleException() {
        Item item = this.createItem();
        this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        this.itemService.getItemById(Long.MAX_VALUE, this.SELLER_B_EMAIL, UserRolesEnum.ROOT);
    }

    @Test(expected = ReporPartlyToUserException.class)
    public void itemService_getItemById_WithSellerRole_ShouldThrowReporPartlyToUserException() {
        Item item = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel actual = this.itemService.getItemById(itemSM.getId(), this.SELLER_B_EMAIL, UserRolesEnum.SELLER);
        Assert.assertNotNull(actual);
    }

    /**
     * Test of updateItem method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_updateItem_WithCorrectInputAndSellerUserRole_ShouldUpdateItem() {
        Item item = new Item();
        item.setName("Default name");
        item.setDescription("Default Description");
        item.setInitialPrice(BigDecimal.ONE);
        item.setLocation("Default location");
        item.setQuantity(5);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        String NEW_NAME_PARAM = "New Name";
        String NEW_DESCRIPTION_PARAM = "New Description";
        String NEW_LOCATION_PARAM = "New Location";
        BigDecimal NEW_PRICE=BigDecimal.TEN;
        Integer NEW_QUANTITY=10;
        
        ItemServiceModel itemtoChange = this.modelMapper.map(item, ItemServiceModel.class);
        itemtoChange.setId(itemSM.getId());
        itemtoChange.setName(NEW_NAME_PARAM);
        itemtoChange.setDescription(NEW_DESCRIPTION_PARAM);
        itemtoChange.setLocation(NEW_LOCATION_PARAM);
        itemtoChange.setInitialPrice(NEW_PRICE);
        itemtoChange.setQuantity(NEW_QUANTITY);
        
        boolean result = this.itemService.updateItem(itemtoChange, this.seller_A_SM.getDomainEmail(), UserRolesEnum.SELLER);
        Assert.assertTrue(result);
        Item shouldBeUpdated = this.itemRepository.findAll().get(0);
        Assert.assertEquals(itemtoChange.getName(), shouldBeUpdated.getName());
        Assert.assertEquals(itemtoChange.getDescription(), shouldBeUpdated.getDescription());
        Assert.assertEquals(itemtoChange.getLocation(), shouldBeUpdated.getLocation());
        Assert.assertTrue(itemtoChange.getInitialPrice().compareTo( shouldBeUpdated.getInitialPrice())==0);
        Assert.assertEquals(itemtoChange.getQuantity().intValue(), shouldBeUpdated.getQuantity().intValue());
        Assert.assertTrue(itemSM.getDateAdded().compareTo(shouldBeUpdated.getDateAdded())==0);
        Assert.assertTrue(itemSM.getSeller().getName().equals(shouldBeUpdated.getSeller().getName()));
    }
    
    @Test
    public void itemService_updateItem_WithCorrectInputAndAdminUserRole_ShouldUpdateItemRegardlessOfCreator() {
        Item item = new Item();
        item.setName("Default name");
        item.setDescription("Default Description");
        item.setInitialPrice(BigDecimal.ONE);
        item.setLocation("Default location");
        item.setQuantity(5);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        String NEW_NAME_PARAM = "New Name";
        String NEW_DESCRIPTION_PARAM = "New Description";
        String NEW_LOCATION_PARAM = "New Location";
        BigDecimal NEW_PRICE=BigDecimal.TEN;
        Integer NEW_QUANTITY=10;
        
        ItemServiceModel itemtoChange = this.modelMapper.map(item, ItemServiceModel.class);
        itemtoChange.setId(itemSM.getId());
        itemtoChange.setName(NEW_NAME_PARAM);
        itemtoChange.setDescription(NEW_DESCRIPTION_PARAM);
        itemtoChange.setLocation(NEW_LOCATION_PARAM);
        itemtoChange.setInitialPrice(NEW_PRICE);
        itemtoChange.setQuantity(NEW_QUANTITY);
        
        boolean result = this.itemService.updateItem(itemtoChange, this.seller_B_SM.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertTrue(result);
        Item shouldBeUpdated = this.itemRepository.findAll().get(0);
        Assert.assertEquals(itemtoChange.getName(), shouldBeUpdated.getName());
        Assert.assertEquals(itemtoChange.getDescription(), shouldBeUpdated.getDescription());
        Assert.assertEquals(itemtoChange.getLocation(), shouldBeUpdated.getLocation());
        Assert.assertTrue(itemtoChange.getInitialPrice().compareTo( shouldBeUpdated.getInitialPrice())==0);
        Assert.assertEquals(itemtoChange.getQuantity().intValue(), shouldBeUpdated.getQuantity().intValue());
        Assert.assertTrue(itemSM.getDateAdded().compareTo(shouldBeUpdated.getDateAdded())==0);
        Assert.assertTrue(itemSM.getSeller().getName().equals(shouldBeUpdated.getSeller().getName()));
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void itemService_updateItem_WithWrongEmailAndSellerUserRole_ShouldThrowReporPartlyToUserException() {
        Item item = new Item();
        item.setName("Default name");
        item.setDescription("Default Description");
        item.setInitialPrice(BigDecimal.ONE);
        item.setLocation("Default location");
        item.setQuantity(5);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        String NEW_NAME_PARAM = "New Name";
        String NEW_DESCRIPTION_PARAM = "New Description";
        String NEW_LOCATION_PARAM = "New Location";
        BigDecimal NEW_PRICE=BigDecimal.TEN;
        Integer NEW_QUANTITY=10;
        
        ItemServiceModel itemtoChange = this.modelMapper.map(item, ItemServiceModel.class);
        itemtoChange.setId(itemSM.getId());
        itemtoChange.setName(NEW_NAME_PARAM);
        itemtoChange.setDescription(NEW_DESCRIPTION_PARAM);
        itemtoChange.setLocation(NEW_LOCATION_PARAM);
        itemtoChange.setInitialPrice(NEW_PRICE);
        itemtoChange.setQuantity(NEW_QUANTITY);
        
        itemService.updateItem(itemtoChange, this.seller_B_SM.getDomainEmail(), UserRolesEnum.SELLER);
    }
    
    @Test(expected=InvalidRoleException.class)
    public void itemService_updateItem_WithCorrectEmailAndUnsupportedUserRole_ShouldThrowInvalidRoleException() {
        Item item = new Item();
        item.setName("Default name");
        item.setDescription("Default Description");
        item.setInitialPrice(BigDecimal.ONE);
        item.setLocation("Default location");
        item.setQuantity(5);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        String NEW_NAME_PARAM = "New Name";
        String NEW_DESCRIPTION_PARAM = "New Description";
        String NEW_LOCATION_PARAM = "New Location";
        BigDecimal NEW_PRICE=BigDecimal.TEN;
        Integer NEW_QUANTITY=10;
        
        ItemServiceModel itemtoChange = this.modelMapper.map(item, ItemServiceModel.class);
        itemtoChange.setId(itemSM.getId());
        itemtoChange.setName(NEW_NAME_PARAM);
        itemtoChange.setDescription(NEW_DESCRIPTION_PARAM);
        itemtoChange.setLocation(NEW_LOCATION_PARAM);
        itemtoChange.setInitialPrice(NEW_PRICE);
        itemtoChange.setQuantity(NEW_QUANTITY);
        
        itemService.updateItem(itemtoChange, this.seller_A_SM.getDomainEmail(), UserRolesEnum.ROOT);
    }
    
    @Test
    public void itemService_updateItem_WithCorrectInputAndSellerUserRole_WithAuctionedItems_ShouldUpdateAllAuctionedItems_WithAuctionsBeforeSelledStatus() throws ReportToUserException {
        String OLD_NAME_PARAM = "Default name";
        String OLD_DESCRIPTION_PARAM = "Default Description";
        String OLD_LOCATION_PARAM = "Default location";
        BigDecimal OLD_PRICE=BigDecimal.ONE;
        Integer OLD_QUANTITY=20;
        
        Item item = new Item();
        item.setName(OLD_NAME_PARAM);
        item.setDescription(OLD_DESCRIPTION_PARAM);
        item.setInitialPrice(OLD_PRICE);
        item.setLocation(OLD_LOCATION_PARAM);
        item.setQuantity(OLD_QUANTITY);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        
        Auction createdAuction = this.createTestAuction();
        createdAuction.setSeller(this.sellerA);
        createdAuction.setStatus(AuctionStatus.CREATED);
        createdAuction = this.auctionRepository.saveAndFlush(createdAuction);
        
        Auction liveAuction = this.createTestAuction();
        liveAuction.setSeller(this.sellerA);
        liveAuction.setStatus(AuctionStatus.LIVE);
        liveAuction = this.auctionRepository.saveAndFlush(liveAuction);
        
        Auction selledAuction = this.createTestAuction();
        selledAuction.setSeller(this.sellerA);
        selledAuction.setStatus(AuctionStatus.SELLED);
        selledAuction = this.auctionRepository.saveAndFlush(selledAuction);
        
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setSeller(this.sellerA);
        archiveAuction.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction = this.auctionRepository.saveAndFlush(archiveAuction);
        
        Auction expiredAuction = this.createTestAuction();
        expiredAuction.setSeller(this.sellerA);
        expiredAuction.setStatus(AuctionStatus.EXPIRED);
        expiredAuction = this.auctionRepository.saveAndFlush(expiredAuction);
        List<Auction> auctions = new ArrayList();
        auctions.add(createdAuction);
        auctions.add(liveAuction);
        auctions.add(selledAuction);
        auctions.add(archiveAuction);
        auctions.add(expiredAuction);
        
        
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 5;
        for (int i = 0; i < numOfAddedItems; i++) {
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            this.itemService.addToAuction(new ArrayList(){{add(itemToAdd);}}, this.modelMapper.map(auctions.get(0), AuctionServiceModel.class));
        }

        String NEW_NAME_PARAM = "New Name";
        String NEW_DESCRIPTION_PARAM = "New Description";
        String NEW_LOCATION_PARAM = "New Location";
        BigDecimal NEW_PRICE=BigDecimal.TEN;
        Integer NEW_QUANTITY=10;
        
        ItemServiceModel itemtoChange = this.modelMapper.map(item, ItemServiceModel.class);
        itemtoChange.setId(itemSM.getId());
        itemtoChange.setName(NEW_NAME_PARAM);
        itemtoChange.setDescription(NEW_DESCRIPTION_PARAM);
        itemtoChange.setLocation(NEW_LOCATION_PARAM);
        itemtoChange.setInitialPrice(NEW_PRICE);
        itemtoChange.setQuantity(NEW_QUANTITY);
        
        boolean result = this.itemService.updateItem(itemtoChange, this.seller_A_SM.getDomainEmail(), UserRolesEnum.SELLER);
        Assert.assertTrue(result);
        List<AuctionedItem> aucItems = this.aucItemsRepository.findAll();
        for(AuctionedItem aucItem:aucItems){
            if(aucItem.getAuction().getStatus().compareTo(AuctionStatus.SELLED)<0){
                Assert.assertEquals(aucItem.getItemName(), NEW_NAME_PARAM);
                Assert.assertEquals(aucItem.getItemDescription(), NEW_DESCRIPTION_PARAM);
                Assert.assertEquals(aucItem.getItemLocation(), NEW_LOCATION_PARAM);
                Assert.assertEquals(aucItem.getItemName(), NEW_NAME_PARAM);
                Assert.assertTrue(aucItem.getItemPrice().compareTo(NEW_PRICE)==0);
            }else{
                Assert.assertEquals(aucItem.getItemName(), OLD_NAME_PARAM);
                Assert.assertEquals(aucItem.getItemLocation(), OLD_LOCATION_PARAM);
                Assert.assertEquals(aucItem.getItemName(), OLD_NAME_PARAM);
                Assert.assertTrue(aucItem.getItemPrice().compareTo(OLD_PRICE)==0);
            }
        }
    }

    /**
     * Test of deleteItemById method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_deleteItemById_WithCorrectIdAndSellerUserRole_ShouldDeleteItem() {
        Item item1 = this.createItem();
        ItemServiceModel itemSM1 = this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        Item item2 = this.createItem();
        this.itemService.createItem(this.modelMapper.map(item2, ItemServiceModel.class), this.seller_A_SM);
        Assert.assertEquals(2, this.itemRepository.findAll().size());
        boolean result = this.itemService.deleteItemById(itemSM1.getId(), this.seller_A_SM.getDomainEmail(), UserRolesEnum.SELLER);
        Assert.assertTrue(result);
        Assert.assertEquals(1, this.itemRepository.findAll().size());
        Item deletedItem = this.itemRepository.findById(itemSM1.getId()).orElse(null);
        Assert.assertNull(deletedItem);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void itemService_deleteItemById_WithCorrectIdAndSellerUserRoleWithInCorrectEmail_ShouldThrowReporPartlyToUserException() {
        Item item1 = this.createItem();
        ItemServiceModel itemSM1 = this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        this.itemService.deleteItemById(itemSM1.getId(), this.seller_B_SM.getDomainEmail(), UserRolesEnum.SELLER);
    }
    
    @Test(expected=InvalidRoleException.class)
    public void itemService_deleteItemById_WithCorrectIdAndUnSupportedUserRole_ShouldThrowInvalidRoleException() {
        Item item1 = this.createItem();
        ItemServiceModel itemSM1 = this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        this.itemService.deleteItemById(itemSM1.getId(), this.seller_B_SM.getDomainEmail(), UserRolesEnum.ROOT);
    }
    
    @Test
    public void itemService_deleteItemById_WithCorrectIdAndAdminUserRole_ShouldDeleteItemRegardlessOfCreator() {
        Item item1 = this.createItem();
        ItemServiceModel itemSM1 = this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        Assert.assertEquals(1, this.itemRepository.findAll().size());
        boolean result = this.itemService.deleteItemById(itemSM1.getId(), this.sellerB.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertTrue(result);
        Assert.assertEquals(0, this.itemRepository.findAll().size());
        Item deletedItem = this.itemRepository.findById(itemSM1.getId()).orElse(null);
        Assert.assertNull(deletedItem);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void itemService_deleteItemById_WithInCorrectIdAndSupportedUserRole_ShouldThrowReporPartlyToUserException() {
        Item item1 = this.createItem();
        this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        this.itemService.deleteItemById(Long.MAX_VALUE, this.seller_B_SM.getDomainEmail(), UserRolesEnum.ADMIN);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void itemService_deleteItemById_WithCorrectIdAndSellerUserRole_WithNonDeletableItem_ShouldThrowReporPartlyToUserException() throws ReportToUserException {
        Item item1 = this.createItem();
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item1, ItemServiceModel.class), this.seller_A_SM);
        ItemServiceModel itemToAdd = new ItemServiceModel();
        
        Auction liveAuction = this.createTestAuction();
        liveAuction.setSeller(this.sellerA);
        liveAuction.setStatus(AuctionStatus.LIVE);
        liveAuction = this.auctionRepository.saveAndFlush(liveAuction);
        AuctionServiceModel auctionSM = this.modelMapper.map(liveAuction, AuctionServiceModel.class);
        
        itemToAdd.setId(itemSM.getId());
        itemToAdd.setDescription(itemSM.getDescription());
        itemToAdd.setDateAdded(itemSM.getDateAdded());
        itemToAdd.setQuantity(1);
        itemToAdd.setSeller(itemSM.getSeller());
        itemToAdd.setLocation(itemSM.getLocation());
        itemToAdd.setThumbnail(itemSM.getThumbnail());
        itemToAdd.setName(itemSM.getName());
        this.itemService.addToAuction(new ArrayList(){{add(itemToAdd);}}, auctionSM);
        
        this.itemService.deleteItemById(Long.MAX_VALUE, this.seller_A_SM.getDomainEmail(), UserRolesEnum.SELLER);
    }
    
    @Test
    public void itemService_deleteItemById_WithorrectIdAndSupportedUserRole_WithAuctionedItems_ShouldDeleteItemAndSetAllAuctionedItemsParentToNull() throws ReportToUserException {
        Item item = this.createItem();
        item.setQuantity(20);
        ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 6;

        List<ItemServiceModel> itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionAServiceModel);
        
        boolean result = this.itemService.deleteItemById(itemSM.getId(), this.seller_B_SM.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertTrue(result);
        List<AuctionedItem> auctionedItems = this.aucItemsRepository.findAll();
        Assert.assertEquals(numOfAddedItems, auctionedItems.size());
        for(AuctionedItem aucItem:auctionedItems){
            Assert.assertNull(aucItem.getParent());
            Assert.assertNotNull(aucItem.getItemName());
            Assert.assertNotNull(aucItem.getItemDescription());
            Assert.assertNotNull(aucItem.getItemLocation());
            Assert.assertNotNull(aucItem.getQuantity());
        }
    }

    /**
     * Test of getAllItems method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_getAllItems_WithCorrectInput_ShouldReturnAllItems() throws ReportToUserException {
        Integer itemQuantityToAddToAuction = 1;
        int numOfAddedItems = 6;
        List<ItemServiceModel> itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionAServiceModel);
        itemsToAdd = new ArrayList();
        for (int i = 1; i <= numOfAddedItems; i++) {
            Item item = this.createItem();
            item.setQuantity(20);
            ItemServiceModel itemSM = this.itemService.createItem(this.modelMapper.map(item, ItemServiceModel.class), this.seller_A_SM);
            ItemServiceModel itemToAdd = new ItemServiceModel();
            itemToAdd.setId(itemSM.getId());
            itemToAdd.setDescription(itemSM.getDescription());
            itemToAdd.setDateAdded(itemSM.getDateAdded());
            itemToAdd.setQuantity(itemQuantityToAddToAuction);
            itemToAdd.setSeller(itemSM.getSeller());
            itemToAdd.setLocation(itemSM.getLocation());
            itemToAdd.setThumbnail(itemSM.getThumbnail());
            itemToAdd.setName(itemSM.getName());
            itemsToAdd.add(itemToAdd);
        }
        this.itemService.addToAuction(itemsToAdd, this.auctionBServiceModel);
        List<ItemServiceModel> allItems = this.itemService.getAllItems(UserRolesEnum.ADMIN);
        Assert.assertEquals(numOfAddedItems*2, allItems.size());
        for(ItemServiceModel item:allItems){
            Assert.assertNotNull(item);
        }
    }
    
    @Test(expected=InvalidRoleException.class)
    public void itemService_getAllItems_WithUnSupportedRole_ShouldThrowInvalidRoleException() throws ReportToUserException {
        this.itemService.getAllItems(UserRolesEnum.BUYER);
    }
    
    /**
     * Test of savePicture method, of class ItemServiceImpl.
     */
    @Test
    public void itemService_savePicture_WithNullThumbnail_Should_ShouldSaveThumbnail()throws ReportToUserException{
        Item item = this.createItem();

        ItemServiceModel itemSM = this.modelMapper.map(item, ItemServiceModel.class);
        ItemServiceModel actual = this.itemService.createItem(itemSM, this.seller_A_SM);
        
        ItemPicture itemPicture = new ItemPicture();
        itemPicture.setDateAdded(new Date());
        itemPicture.setDescription("Some description");
        itemPicture.setItem(this.modelMapper.map(actual, Item.class));
        itemPicture.setOriginalFileName("Original file name");
        itemPicture.setPictureId("Picture id");
        
        ItemPictureServiceModel pictureDetailsFile = 
                this.modelMapper.map(this.itemPictureRepository.save(itemPicture), ItemPictureServiceModel.class);
        
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(this.itemPictureService.savePicture(any(), any())).thenReturn(pictureDetailsFile);
        this.itemService.savePicture(pictureDetailsFile, file);
        Item updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNotNull(updatedItem.getThumbnail());
    }
    
    @Test
    public void itemService_savePicture_WithNoTNullThumbnail_Should_NotChangeThumbnail()throws ReportToUserException{
        Item item = this.createItem();

        ItemServiceModel itemSM = this.modelMapper.map(item, ItemServiceModel.class);
        ItemServiceModel actual = this.itemService.createItem(itemSM, this.seller_A_SM);
        
        ItemPicture itemPicture = new ItemPicture();
        itemPicture.setDateAdded(new Date());
        itemPicture.setDescription("Some description");
        itemPicture.setItem(this.modelMapper.map(actual, Item.class));
        itemPicture.setOriginalFileName("Original file name");
        itemPicture.setPictureId("Picture id");
        
        ItemPictureServiceModel pictureDetailsFile = 
                this.modelMapper.map(this.itemPictureRepository.save(itemPicture), ItemPictureServiceModel.class);
        
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(this.itemPictureService.savePicture(any(), any())).thenReturn(pictureDetailsFile);
        this.itemService.savePicture(pictureDetailsFile, file);
        Item updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNotNull(updatedItem.getThumbnail());
        
        ItemPicture newPicture = new ItemPicture();
        newPicture.setDateAdded(new Date());
        newPicture.setDescription("Some description");
        newPicture.setItem(updatedItem);
        newPicture.setOriginalFileName("Original file name");
        newPicture.setPictureId("Picture id");
        ItemPictureServiceModel newPictureDetailsFile = 
                this.modelMapper.map(this.itemPictureRepository.save(newPicture), ItemPictureServiceModel.class);
        Mockito.when(this.itemPictureService.savePicture(any(), any())).thenReturn(newPictureDetailsFile);
        this.itemService.savePicture(newPictureDetailsFile, file);
        updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNotEquals(updatedItem.getThumbnail().getId(),newPictureDetailsFile.getId().longValue());
        
    }
    
    /**
     * Test of deleteSinglePicture method, of class ItemServiceImpl.
     */
    @Test 
    public void itemService_deleteSinglePicture_WithThumbnail_ShouldDeleteThumbnail() throws ReportToUserException{
        Item item = this.createItem();

        ItemServiceModel itemSM = this.modelMapper.map(item, ItemServiceModel.class);
        ItemServiceModel actual = this.itemService.createItem(itemSM, this.seller_A_SM);
        
        ItemPicture itemPicture = new ItemPicture();
        itemPicture.setDateAdded(new Date());
        itemPicture.setDescription("Some description");
        itemPicture.setItem(this.modelMapper.map(actual, Item.class));
        itemPicture.setOriginalFileName("Original file name");
        itemPicture.setPictureId("Picture id");
        
        ItemPictureServiceModel pictureDetailsFile = 
                this.modelMapper.map(this.itemPictureRepository.save(itemPicture), ItemPictureServiceModel.class);
        
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(this.itemPictureService.savePicture(any(), any())).thenReturn(pictureDetailsFile);
        this.itemService.savePicture(pictureDetailsFile, file);
        Item updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNotNull(updatedItem.getThumbnail());
        
        Mockito.when(this.itemPictureService.deleteSinglePicture(any(), any())).thenReturn(true);
        this.itemService.deleteSinglePicture(this.modelMapper.map(updatedItem, ItemServiceModel.class), pictureDetailsFile.getId());
        updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNull(updatedItem.getThumbnail());
    }
    
    @Test
    public void itemService_deleteSinglePicture_WithNoTNullThumbnail_Should_NotChangeThumbnail()throws ReportToUserException{
        Item item = this.createItem();

        ItemServiceModel itemSM = this.modelMapper.map(item, ItemServiceModel.class);
        ItemServiceModel actual = this.itemService.createItem(itemSM, this.seller_A_SM);
        
        ItemPicture itemPicture = new ItemPicture();
        itemPicture.setDateAdded(new Date());
        itemPicture.setDescription("Some description");
        itemPicture.setItem(this.modelMapper.map(actual, Item.class));
        itemPicture.setOriginalFileName("Original file name");
        itemPicture.setPictureId("Picture id");
        
        ItemPictureServiceModel pictureDetailsFile = 
                this.modelMapper.map(this.itemPictureRepository.save(itemPicture), ItemPictureServiceModel.class);
        
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(this.itemPictureService.savePicture(any(), any())).thenReturn(pictureDetailsFile);
        this.itemService.savePicture(pictureDetailsFile, file);
        Item updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNotNull(updatedItem.getThumbnail());
        
        ItemPicture newPicture = new ItemPicture();
        newPicture.setDateAdded(new Date());
        newPicture.setDescription("Some description");
        newPicture.setItem(updatedItem);
        newPicture.setOriginalFileName("Original file name");
        newPicture.setPictureId("Picture id");
        ItemPictureServiceModel newPictureDetailsFile = 
                this.modelMapper.map(this.itemPictureRepository.save(newPicture), ItemPictureServiceModel.class);
        Mockito.when(this.itemPictureService.savePicture(any(), any())).thenReturn(newPictureDetailsFile);
        this.itemService.savePicture(newPictureDetailsFile, file);
        updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNotEquals(updatedItem.getThumbnail().getId(),newPictureDetailsFile.getId().longValue());
        
        Mockito.when(this.itemPictureService.deleteSinglePicture(any(), any())).thenReturn(true);
        this.itemService.deleteSinglePicture(this.modelMapper.map(updatedItem, ItemServiceModel.class), newPictureDetailsFile.getId());
        updatedItem = this.itemRepository.findAll().get(0);
        Assert.assertNotNull(updatedItem.getThumbnail());
    }

}

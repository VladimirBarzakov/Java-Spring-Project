/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.SHOP;

import Atia.Shop.config.userRoles.UserRolesEnum;
import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.entities.pictures.AuctionPicture;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.AuctionedItemServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AucItemPictureWrapperServiceModel;
import Atia.Shop.domain.models.serviceModels.pictures.AuctionPictureServiceModel;
import Atia.Shop.domain.repositories.AuctionRepository;
import Atia.Shop.domain.repositories.UserRepository;
import Atia.Shop.domain.repositories.pictures.AuctionPictureRepository;
import Atia.Shop.exeptions.base.InvalidRoleException;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.Pictures.AuctionPictureService;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.service.API.SHOP.ItemService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.doAnswer;
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
public class AuctionServiceImplTest {
    @Autowired
    private AuctionPictureRepository auctionPictureRepository;
    
    @Autowired
    private AuctionRepository auctionRepository;
    
    private ModelMapper modelMapper;
    private UserService userService;
    private ItemService itemService;
    
    private AuctionPictureService auctionPictureService;
    private InputValidator inputValidator;
    
    private AuctionService auctionService;
    
    /////////////////////////////////////
    @Autowired
    private UserRepository userRepository;
    private final String USER_A_EMAIL = "seller@atia.com";
    private User user_A;
    private UserServiceModel user_A_SM;

    private final String USER_B_EMAIL = "bidder@atia.com";
    private User user_B;
    private UserServiceModel user_B_SM;
    
    /////////////////////////////////////
    
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    boolean DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
    boolean DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
    boolean ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
    
    public AuctionServiceImplTest() {
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
        this.inputValidator=new InputValidator();
        this.auctionPictureService = Mockito.mock(AuctionPictureService.class);
        this.itemService = Mockito.mock(ItemService.class);
        this.userService = Mockito.mock(UserService.class);
        this.auctionService=new AuctionServiceImpl(
                this.modelMapper,
                this.userService, 
                this.itemService,
                this.auctionRepository,
                this.auctionPictureService,
                this.inputValidator);
        
        this.user_A = this.initUser();
        this.user_A.setDomainEmail(this.USER_A_EMAIL);
        this.user_A = this.userRepository.saveAndFlush(this.user_A);
        this.user_A_SM = this.modelMapper.map(this.user_A, UserServiceModel.class);

        this.user_B = this.initUser();
        this.user_B.setDomainEmail(this.USER_B_EMAIL);
        this.user_B = this.userRepository.saveAndFlush(this.user_B);
        this.user_B_SM = this.modelMapper.map(this.user_B, UserServiceModel.class);
        
        this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag = false;
        this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=false;
        this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=false;
        
    }
    
    @After
    public void tearDown() {
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
    
    private AuctionServiceModel createAuctionService(User creator){
        Auction auction = this.createTestAuction();
        auction.setSeller(this.user_A);
        auction = this.auctionRepository.saveAndFlush(auction);
        return this.modelMapper.map(auction, AuctionServiceModel.class);
    }
    
    private AuctionServiceModel createAndSaveAuctionService(UserServiceModel creator) throws ParseException, ReportToUserException{
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Auction auction = new Auction();
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setAuctionStrategy(AuctionStrategy.OPEN_BIDDING_HIGHEST_BID);
        
        Date validStartDate = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date validExpiredDate = this.DATE_FORMAT.parse("2030-01-15 05:00:00");
        auction.setDateStarted(validStartDate);
        auction.setDateExpired(validExpiredDate);
        AuctionServiceModel auctionToSave = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(creator.getDomainEmail())).thenReturn(creator);
        AuctionServiceModel result = this.auctionService.createAuction(auctionToSave, creator.getDomainEmail());
        return this.modelMapper.map(result, AuctionServiceModel.class);
    }

    /**
     * Test of isAuctionEmpty method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isAuctionEmpty_WithNoNEmptyAuctionedItemsList_ShouldReturnFalse() {
        AuctionServiceModel auctionSM = this.createAuctionService(this.user_A);
        List<AuctionedItemServiceModel> nonEmptyList = new ArrayList(){{add(new AuctionedItemServiceModel());}};
        Mockito.when(this.itemService.findAllAucItemsByAuctionId(any())).thenReturn(nonEmptyList);
        boolean result = this.auctionService.isAuctionEmpty(auctionSM.getId());
        Assert.assertFalse(result);
    }
    
    @Test
    public void auctionService_isAuctionEmpty_WithEmptyAuctionedItemsList_ShouldReturnTrue() {
        AuctionServiceModel auctionSM = this.createAuctionService(this.user_A);
        List<AuctionedItemServiceModel> emptyList = new ArrayList();
        Mockito.when(this.itemService.findAllAucItemsByAuctionId(any())).thenReturn(emptyList);
        boolean result = this.auctionService.isAuctionEmpty(auctionSM.getId());
        Assert.assertTrue(result);
    }

    /**
     * Test of createAuction method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_createAuction_WithCorrectInput_ShouldCreateAuction() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Auction auction = new Auction();
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setAuctionStrategy(AuctionStrategy.OPEN_BIDDING_HIGHEST_BID);
        
        Date validStartDate = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date validExpiredDate = this.DATE_FORMAT.parse("2030-01-15 05:00:00");
        auction.setDateStarted(validStartDate);
        auction.setDateExpired(validExpiredDate);
        AuctionServiceModel auctionToSave = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.user_A_SM.getDomainEmail())).thenReturn(this.user_A_SM);
        AuctionServiceModel result = this.auctionService.createAuction(auctionToSave, this.user_A_SM.getDomainEmail());
        Assert.assertNotNull(result);
        Assert.assertEquals(AUCTION_TITLE, result.getTitle());
        Assert.assertEquals(AUCTION_DESCRIPTION, result.getDescription());
        Assert.assertTrue(result.getStatus().compareTo(AuctionStatus.CREATED)==0);
        Assert.assertTrue(result.getInitialPrice().compareTo(AUCTION_PRICE)==0);
        Assert.assertTrue(result.getDateStarted().compareTo(validStartDate)==0);
        Assert.assertTrue(result.getDateExpired().compareTo(validExpiredDate)==0);
        Assert.assertEquals(this.user_A_SM.getId(), result.getSeller().getId());
        Assert.assertEquals(1, this.auctionRepository.findAll().size());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_createAuction_WithnonExistingUser_ShouldThrowReporPartlyToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Auction auction = new Auction();
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        
        Date validStartDate = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date validExpiredDate = this.DATE_FORMAT.parse("2030-01-15 05:00:00");
        auction.setDateStarted(validStartDate);
        auction.setDateExpired(validExpiredDate);
        AuctionServiceModel auctionToSave = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(any())).thenThrow(new ReporPartlyToUserException());
        this.auctionService.createAuction(auctionToSave, this.user_A_SM.getDomainEmail());
    }
    
    @Test(expected=ReportToUserException.class)
    public void auctionService_createAuction_WithExpiredDateBeforeStartDate_ShouldThrowReportToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Auction auction = new Auction();
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        
        Date validStartDate = this.DATE_FORMAT.parse("2030-01-30 05:00:00");
        Date invalidExpiredDate = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        auction.setDateStarted(validStartDate);
        auction.setDateExpired(invalidExpiredDate);
        AuctionServiceModel auctionToSave = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.user_A_SM.getDomainEmail())).thenReturn(this.user_A_SM);
        this.auctionService.createAuction(auctionToSave, this.user_A_SM.getDomainEmail());
    }
    
    @Test(expected=ReportToUserException.class)
    public void auctionService_createAuction_WithStartDateBeforeNow_ShouldThrowReportToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Auction auction = new Auction();
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        
        Date invalidStartDate = this.DATE_FORMAT.parse("1999-01-30 05:00:00");
        Date validExpiredDate = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        auction.setDateStarted(invalidStartDate);
        auction.setDateExpired(validExpiredDate);
        AuctionServiceModel auctionToSave = this.modelMapper.map(auction, AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.user_A_SM.getDomainEmail())).thenReturn(this.user_A_SM);
        this.auctionService.createAuction(auctionToSave, this.user_A_SM.getDomainEmail());
    }

    /**
     * Test of getAuctionById method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAuctionById_WithValidInput_ShouldReturnCorrectResult() throws ParseException, ReportToUserException {
        AuctionServiceModel auction = this.createAndSaveAuctionService(this.user_A_SM);
        AuctionServiceModel result = this.auctionService.getAuctionById(auction.getId());
        Assert.assertNotNull(result);
        Assert.assertEquals(auction.getTitle(), result.getTitle());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getAuctionById_WithNonExistingInput_ShouldThrowReporPartlyToUserException() throws ParseException, ReportToUserException {
        AuctionServiceModel auction = this.createAndSaveAuctionService(this.user_A_SM);
        AuctionServiceModel result = this.auctionService.getAuctionById(Long.MAX_VALUE);
    }

    /**
     * Test of updateAuction method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_updateAuction_3args_WithValidInput_ShouldUpdateAuction() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        boolean result = this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList());
        Assert.assertTrue(result);
        Auction changedAuction = this.auctionRepository.findAll().get(0);
        Assert.assertEquals(NEW_AUCTION_TITLE, changedAuction.getTitle());
        Assert.assertEquals(NEW_AUCTION_DESCRIPTION, changedAuction.getDescription());
        Assert.assertTrue(changedAuction.getInitialPrice().compareTo(NEW_AUCTION_PRICE)==0);
        Assert.assertTrue(changedAuction.getDateStarted().compareTo(NEW_START_DATE)==0);
        Assert.assertTrue(changedAuction.getDateExpired().compareTo(NEW_EXPIRED_DATE)==0);
        Assert.assertTrue(this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
    }
    
    @Test(expected = ReportToUserException.class)
    public void auctionService_updateAuction_3args_WithStartAfterExpiredDate_1_ShouldThrowReportToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-03-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList());
    }
    
    @Test(expected = ReportToUserException.class)
    public void auctionService_updateAuction_3args_WithStartAfterExpiredDate_2_ShouldThrowReportToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList());
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void auctionService_updateAuction_3args_WithArchiveAuction_ShouldThrowReporPartlyToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-20 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList());
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void auctionService_updateAuction_3args_WithNonExistingId_ShouldThrowReporPartlyToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        auction_SM.setId(Long.MAX_VALUE);
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-20 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList());
    }

    /**
     * Test of isAuctionEditable method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isAuctionEditable_Long_WithBeforeSelledStatusAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionEditable(auction_SM.getId());
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionEditable_Long_WithAfterSelledStatusAuction_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionEditable(auction_SM.getId());
        Assert.assertFalse(result);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_isAuctionEditable_Long_WithNonExistingAuctionId_ShouldThrowReporPartlyToUserException() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        this.auctionService.isAuctionEditable(Long.MAX_VALUE);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_isAuctionEditable_Long_WithNonArchiveAuctionId_ShouldThrowReporPartlyToUserException() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        this.auctionService.isAuctionEditable(auction_SM.getId());
    }

    /**
     * Test of isAuctionEditable method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isAuctionEditable_AuctionServiceModel_WithBeforeSelledStatusAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionEditable(auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionEditable_AuctionServiceModel_WithAfterSelledStatusAuction_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionEditable(auction_SM);
        Assert.assertFalse(result);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_isAuctionEditable_AuctionServiceModel_WithArchiveStatusAuction_ShouldThrowReporPartlyToUserException() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionEditable(auction_SM);
        Assert.assertFalse(result);
    }

    /**
     * Test of isAuctionDeletable method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isAuctionDeletable_Long_WithBeforeLiveStatusAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionDeletable(auction_SM.getId());
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionDeletable_Long_WithExpiredStatusAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.EXPIRED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionDeletable(auction_SM.getId());
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionDeletable_Long_WithAfterLiveStatusAuction_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionDeletable(auction_SM.getId());
        Assert.assertFalse(result);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_isAuctionDeletable_Long_WithNonExistingAuctionId_ShouldThrowReporPartlyToUserException() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        this.auctionService.isAuctionDeletable(Long.MAX_VALUE);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_isAuctionDeletable_Long_WithArchibeAuctionStatus_ShouldThrowReporPartlyToUserException() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        this.auctionService.isAuctionDeletable(auction_SM.getId());
    }

    /**
     * Test of isAuctionDeletable method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isAuctionDeletable_AuctionServiceModel_WithBeforeLiveAuctionStatus_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionDeletable(auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionDeletable_AuctionServiceModel_WithExpiredAuctionStatus_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.EXPIRED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionDeletable(auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionDeletable_AuctionServiceModel_WithAfterLiveAuctionStatus_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionDeletable(auction_SM);
        Assert.assertFalse(result);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_isAuctionDeletable_AuctionServiceModel_WithArchiveAuctionStatus_ShouldThrowReporPartlyToUserException() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        this.auctionService.isAuctionDeletable(auction_SM);
    }

    /**
     * Test of isAuctionBiddable method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isAuctionBiddable_WithLiveAuctionStatusAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionBiddable(auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionBiddable_WithBeforeLiveAuctionStatusAuction_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionBiddable(auction_SM);
        Assert.assertFalse(result);
    }
    
    @Test
    public void auctionService_isAuctionBiddable_WithAfterLiveAuctionStatusAuction_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionBiddable(auction_SM);
        Assert.assertFalse(result);
    }
    

    /**
     * Test of isAuctionWinned method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isAuctionWinned_WithWinnedAuction_ShouldRetunTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        auction.setAuctionWinner(this.user_B);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionWinned(auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isAuctionWinned_WithNoNWinnedAuction_ShouldRetunFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isAuctionWinned(auction_SM);
        Assert.assertFalse(result);
    }

    /**
     * Test of isArchivable method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_isArchivable_WithSelledStatusAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isArchivable(auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isArchivable_WithExpiredStatusAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.EXPIRED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isArchivable(auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_isArchivable_WithBeforeSelledStatusAuction_SouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.isArchivable(auction_SM);
        Assert.assertFalse(result);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_isArchivable_WithArchiveStatusAuction_SouldThrowReporPartlyToUserException() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.ARCHIVE);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        this.auctionService.isArchivable(auction_SM);
    }

    /**
     * Test of getAllAuctions method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAllAuctions_OnEmptyDN_ShouldReturnEmptyList() {
        List<AuctionServiceModel> result = this.auctionService.getAllAuctions();
        Assert.assertTrue(result.isEmpty());
    }
    
    @Test
    public void auctionService_getAllAuctions_FromAlLAuctionStatus_ShouldReturnAllAuctionsExceptArchive() {
        Auction createdAuction = this.createTestAuction();
        createdAuction.setStatus(AuctionStatus.CREATED);
        createdAuction.setSeller(this.user_A);
        AuctionServiceModel createdAuction_SM = this.modelMapper.map(this.auctionRepository.save(createdAuction), AuctionServiceModel.class);
        
        Auction liveAuction = this.createTestAuction();
        liveAuction.setStatus(AuctionStatus.LIVE);
        liveAuction.setSeller(this.user_A);
        AuctionServiceModel liveAuction_SM = this.modelMapper.map(this.auctionRepository.save(liveAuction), AuctionServiceModel.class);
        
        Auction selledAuction = this.createTestAuction();
        selledAuction.setStatus(AuctionStatus.SELLED);
        selledAuction.setSeller(this.user_A);
        AuctionServiceModel selledAuction_SM = this.modelMapper.map(this.auctionRepository.save(selledAuction), AuctionServiceModel.class);
        
        Auction expiredAuction = this.createTestAuction();
        expiredAuction.setStatus(AuctionStatus.EXPIRED);
        expiredAuction.setSeller(this.user_A);
        AuctionServiceModel exporedAuction_SM = this.modelMapper.map(this.auctionRepository.save(expiredAuction), AuctionServiceModel.class);
        
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        List<AuctionServiceModel> result = this.auctionService.getAllAuctions();
        Assert.assertEquals(4,result.size());
        for(AuctionServiceModel auction:result){
            Assert.assertNotNull(auction);
            Assert.assertTrue(auction.getStatus().compareTo(AuctionStatus.ARCHIVE)!=0);
        }
    }

    /**
     * Test of getAllAuctionsByWinner method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAllAuctionsByWinner_ShouldgetreturnAllAuctionsByWinner() {
        Auction createdAuction = this.createTestAuction();
        createdAuction.setStatus(AuctionStatus.CREATED);
        createdAuction.setSeller(this.user_A);
        AuctionServiceModel createdAuction_SM = this.modelMapper.map(this.auctionRepository.save(createdAuction), AuctionServiceModel.class);
        
        Auction winnedAuction1 = this.createTestAuction();
        winnedAuction1.setStatus(AuctionStatus.SELLED);
        winnedAuction1.setSeller(this.user_A);
        winnedAuction1.setAuctionWinner(this.user_B);
        AuctionServiceModel winnedAuctionAuction_SM1 = this.modelMapper.map(this.auctionRepository.save(winnedAuction1), AuctionServiceModel.class);
        
        Auction winnedAuction2 = this.createTestAuction();
        winnedAuction2.setStatus(AuctionStatus.SELLED);
        winnedAuction2.setSeller(this.user_A);
        winnedAuction2.setAuctionWinner(this.user_B);
        AuctionServiceModel winnedAuctionAuction_SM2 = this.modelMapper.map(this.auctionRepository.save(winnedAuction2), AuctionServiceModel.class);
        
        
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.USER_B_EMAIL)).thenReturn(this.user_B_SM);
        List<AuctionServiceModel> result = this.auctionService.getAllAuctionsByWinner(this.USER_B_EMAIL);
        Assert.assertEquals(2, result.size());
        for(AuctionServiceModel auction : result){
            Assert.assertNotNull(auction);
            Assert.assertTrue(Objects.equals(auction.getId(), winnedAuctionAuction_SM1.getId()) || Objects.equals(auction.getId(), winnedAuctionAuction_SM2.getId()));
        }
    }

    /**
     * Test of getAllAuctionsByStatus method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAllAuctionsByStatus_ShouldgetAllAuctionsWithCorrectStatus() {
        Auction createdAuction = this.createTestAuction();
        createdAuction.setStatus(AuctionStatus.CREATED);
        createdAuction.setSeller(this.user_A);
        AuctionServiceModel createdAuction_SM = this.modelMapper.map(this.auctionRepository.save(createdAuction), AuctionServiceModel.class);
        
        Auction liveAuction1 = this.createTestAuction();
        liveAuction1.setStatus(AuctionStatus.LIVE);
        liveAuction1.setSeller(this.user_A);
        AuctionServiceModel liveAuction_SM1 = this.modelMapper.map(this.auctionRepository.save(liveAuction1), AuctionServiceModel.class);
        
        Auction liveAuction2 = this.createTestAuction();
        liveAuction2.setStatus(AuctionStatus.LIVE);
        liveAuction2.setSeller(this.user_A);
        AuctionServiceModel selledAuction_SM2 = this.modelMapper.map(this.auctionRepository.save(liveAuction2), AuctionServiceModel.class);
        
        Auction expiredAuction = this.createTestAuction();
        expiredAuction.setStatus(AuctionStatus.EXPIRED);
        expiredAuction.setSeller(this.user_A);
        AuctionServiceModel exporedAuction_SM = this.modelMapper.map(this.auctionRepository.save(expiredAuction), AuctionServiceModel.class);
        
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        List<AuctionServiceModel> result = this.auctionService.getAllAuctionsByStatus(AuctionStatus.LIVE);
        Assert.assertEquals(2, result.size());
        for(AuctionServiceModel auction : result){
            Assert.assertNotNull(auction);
            Assert.assertTrue(Objects.equals(auction.getId(), liveAuction_SM1.getId()) || Objects.equals(auction.getId(), selledAuction_SM2.getId()));
        }
    }

    /**
     * Test of saveAll method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_saveAll_WithNonEmptyList_ShouldSaveAll() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.CREATED);
        auction1.setSeller(this.user_A);
        AuctionServiceModel auction_SM1 = this.modelMapper.map(auction1, AuctionServiceModel.class);
        
        Auction auction2 = this.createTestAuction();
        auction2.setStatus(AuctionStatus.CREATED);
        auction2.setSeller(this.user_A);
        AuctionServiceModel auction_SM2 = this.modelMapper.map(auction2, AuctionServiceModel.class);
        
        Auction auction3 = this.createTestAuction();
        auction3.setStatus(AuctionStatus.CREATED);
        auction3.setSeller(this.user_A);
        AuctionServiceModel auction_SM3 = this.modelMapper.map(auction3, AuctionServiceModel.class);
        
        List<AuctionServiceModel> tobeSaved = new ArrayList();
        tobeSaved.add(auction_SM1);
        tobeSaved.add(auction_SM2);
        tobeSaved.add(auction_SM3);
        boolean result = this.auctionService.saveAll(tobeSaved);
        Assert.assertTrue(result);
        Assert.assertEquals(3,this.auctionRepository.findAll().size());
    }
    
    @Test
    public void auctionService_saveAll_WithEmptyList_ShouldSaveAll() {
        List<AuctionServiceModel> tobeSaved = new ArrayList();

        boolean result = this.auctionService.saveAll(tobeSaved);
        Assert.assertTrue(result);
        Assert.assertEquals(0,this.auctionRepository.findAll().size());
    }
    

    /**
     * Test of deleteAllAuctions method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_deleteAllAuctions_WithNonEmptyList_ShouldDeleteAll() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.CREATED);
        auction1.setSeller(this.user_A);
        AuctionServiceModel auction_SM1 = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        Auction auction2 = this.createTestAuction();
        auction2.setStatus(AuctionStatus.CREATED);
        auction2.setSeller(this.user_A);
        AuctionServiceModel auction_SM2 = this.modelMapper.map(this.auctionRepository.save(auction2), AuctionServiceModel.class);
        
        Auction auction3 = this.createTestAuction();
        auction3.setStatus(AuctionStatus.CREATED);
        auction3.setSeller(this.user_A);
        AuctionServiceModel auction_SM3 = this.modelMapper.map(this.auctionRepository.save(auction3), AuctionServiceModel.class);
        
        List<AuctionServiceModel> toBeDeleted = new ArrayList();
        toBeDeleted.add(auction_SM1);
        toBeDeleted.add(auction_SM2);
        
        boolean result = this.auctionService.deleteAllAuctions(toBeDeleted);
        Assert.assertTrue(result);
        List<Auction> DBSnapshot = this.auctionRepository.findAll();
        Assert.assertEquals(1,DBSnapshot.size());
        Assert.assertEquals(auction_SM3.getId().longValue(),DBSnapshot.get(0).getId());
    }
    
    @Test
    public void auctionService_deleteAllAuctions_WithEmptyList_ShouldDeleteAll() {
        Auction auction3 = this.createTestAuction();
        auction3.setStatus(AuctionStatus.CREATED);
        auction3.setSeller(this.user_A);
        AuctionServiceModel auction_SM3 = this.modelMapper.map(this.auctionRepository.save(auction3), AuctionServiceModel.class);
        
        List<AuctionServiceModel> toBeDeleted = new ArrayList();
        
        boolean result = this.auctionService.deleteAllAuctions(toBeDeleted);
        Assert.assertTrue(result);
        List<Auction> DBSnapshot = this.auctionRepository.findAll();
        Assert.assertEquals(1,DBSnapshot.size());
        Assert.assertEquals(auction_SM3.getId().longValue(),DBSnapshot.get(0).getId());
    }

    /**
     * Test of expireAuction method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_expireAuction_ShouldExpireAuction() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(),eq(false));
        boolean result = this.auctionService.expireAuction(auction_SM);
        Assert.assertTrue(result);
        Auction auctionDB = this.auctionRepository.findAll().get(0);
        Assert.assertTrue(result);
        Assert.assertTrue(auctionDB.getStatus().compareTo(AuctionStatus.EXPIRED)==0);
        Assert.assertTrue(DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
    }

    /**
     * Test of verifyAuctionWinner method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_verifyAuctionWinner_WithWinnedAuction_ShouldReturnTrue() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        auction.setAuctionWinner(this.user_B);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.verifyAuctionWinner(this.user_B.getDomainEmail(), auction_SM);
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_verifyAuctionWinner_WithNoNWinnedAuction_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.verifyAuctionWinner(this.user_B.getDomainEmail(), auction_SM);
        Assert.assertFalse(result);
    }
    
    @Test
    public void auctionService_verifyAuctionWinner_WithWinnedAuction_WithDiffrentWinner_ShouldReturnFalse() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        auction.setAuctionWinner(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        boolean result = this.auctionService.verifyAuctionWinner(this.user_B.getDomainEmail(), auction_SM);
        Assert.assertFalse(result);
    }

    /**
     * Test of getAllPicturesByEntity method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAllPicturesByEntity_ShouldReturnAllPictures() {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(auction, AuctionServiceModel.class);
        List<AucItemPictureWrapperServiceModel> toBeExpected = new ArrayList();
        toBeExpected.add(new AucItemPictureWrapperServiceModel());
        Mockito.when(this.auctionPictureService.getAllPicturesByEntity(any())).thenReturn(toBeExpected);
        List<AucItemPictureWrapperServiceModel> actual = this.auctionService.getAllPicturesByEntity(auction_SM);
        Assert.assertEquals(1, actual.size());
    }

    /**
     * Test of addPicture method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_AddPicture_WithCorrectinput_ShouldSavePicture() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setItemName("TEST");
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        Mockito.when(this.auctionPictureService.addPicture(any(), eq("PictureId"),eq(this.user_A.getDomainEmail()))).thenReturn(pictureWrapper);
        
        AucItemPictureWrapperServiceModel result = this.auctionService.addPicture(pictureWrapper,"PictureId",this.user_A.getDomainEmail());
        Assert.assertNotNull(result);
        Assert.assertEquals("TEST", result.getAuctionedItemName());
    }
    
    @Test
    public void auctionService_AddPicture_WithCorrectinput_WithNullThumbnailShouldSaveThumbnail() throws Exception {
        
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setItemName("TEST");
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        
        AuctionPictureServiceModel auctionPicture = new AuctionPictureServiceModel();
        auctionPicture.setPictureFileID("SomePictureFileId");
        auctionPicture.setUsageCounter(0);
        auctionPicture = 
                this.modelMapper.map(
                        this.auctionPictureRepository.save(this.modelMapper.map(auctionPicture, AuctionPicture.class)), 
                        AuctionPictureServiceModel.class);
        pictureWrapper.setAuctionPicture(auctionPicture);
        Mockito.when(this.auctionPictureService.addPicture(any(), eq("PictureId"),eq(this.user_A.getDomainEmail()))).thenReturn(pictureWrapper);
        
        AucItemPictureWrapperServiceModel result = this.auctionService.addPicture(pictureWrapper,"PictureId",this.user_A.getDomainEmail());
        Auction auctionEntity = this.auctionRepository.getOne(auction_SM.getId());
        Assert.assertNotNull(auctionEntity.getThumbnail());
        Assert.assertEquals(result.getAuctionPicture().getId().longValue(), auctionEntity.getThumbnail().getId());
    }
    
    @Test
    public void auctionService_AddPicture_WithCorrectinput_WithNotNullThumbnailShouldNotChangeThumbnail() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setItemName("TEST");
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        
        AuctionPictureServiceModel auctionPicture = new AuctionPictureServiceModel();
        auctionPicture.setPictureFileID("SomePictureFileId");
        auctionPicture.setUsageCounter(0);
        auctionPicture = 
                this.modelMapper.map(
                        this.auctionPictureRepository.save(this.modelMapper.map(auctionPicture, AuctionPicture.class)), 
                        AuctionPictureServiceModel.class);
        pictureWrapper.setAuctionPicture(auctionPicture);
        Mockito.when(this.auctionPictureService.addPicture(any(), eq("PictureId"),eq(this.user_A.getDomainEmail()))).thenReturn(pictureWrapper);
        
        AucItemPictureWrapperServiceModel result = this.auctionService.addPicture(pictureWrapper,"PictureId",this.user_A.getDomainEmail());
        Auction auctionEntity = this.auctionRepository.getOne(auction_SM.getId());
        Assert.assertNotNull(auctionEntity.getThumbnail());
        Assert.assertEquals(result.getAuctionPicture().getId().longValue(), auctionEntity.getThumbnail().getId());
        
        AucItemPictureWrapperServiceModel otherPictureWrapper = new AucItemPictureWrapperServiceModel();
        otherPictureWrapper.setAuctionId(auction_SM.getId());
        otherPictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        
        AuctionPictureServiceModel otherAuctionPicture = new AuctionPictureServiceModel();
        otherAuctionPicture.setPictureFileID("SomePictureFileId");
        otherAuctionPicture.setUsageCounter(0);
        otherAuctionPicture = 
                this.modelMapper.map(
                        this.auctionPictureRepository.save(this.modelMapper.map(otherAuctionPicture, AuctionPicture.class)), 
                        AuctionPictureServiceModel.class);
        otherPictureWrapper.setAuctionPicture(otherAuctionPicture);
        Mockito.when(this.auctionPictureService.addPicture(any(), eq("PictureId"),eq(this.user_A.getDomainEmail()))).thenReturn(otherPictureWrapper);
        
        AucItemPictureWrapperServiceModel newResult = this.auctionService.addPicture(pictureWrapper,"PictureId",this.user_A.getDomainEmail());
        auctionEntity = this.auctionRepository.getOne(auction_SM.getId());
        Assert.assertNotNull(auctionEntity.getThumbnail());
        Assert.assertEquals(result.getAuctionPicture().getId().longValue(), auctionEntity.getThumbnail().getId());
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void auctionService_AddPicture_WithDiffrentAuction_ShouldThrowReporPartlyToUserException() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        
        pictureWrapper.setAuctionId(Long.MAX_VALUE);
        
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        Mockito.when(this.auctionPictureService.addPicture(any(), eq("PictureId"),eq(this.user_A.getDomainEmail()))).thenReturn(pictureWrapper);
        this.auctionService.addPicture(pictureWrapper,"PictureId",this.user_A.getDomainEmail());
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void auctionService_AddPicture_WithDiffrentSeller_ShouldThrowReporPartlyToUserException() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        Mockito.when(this.auctionPictureService.addPicture(any(), eq("PictureId"),eq(this.user_A.getDomainEmail()))).thenReturn(pictureWrapper);
        this.auctionService.addPicture(pictureWrapper,"PictureId",this.user_B.getDomainEmail());
    }

    /**
     * Test of deletePicture method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_deletePicture_WithCorrectInput_ShouldDeletePicture() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        Mockito.when(this.auctionPictureService.deletePicture(any(), eq(this.user_A.getDomainEmail()))).thenReturn(true);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        boolean result = this.auctionService.deletePicture(pictureWrapper,this.user_A.getDomainEmail());
        Assert.assertTrue(result);
    }
    
    @Test
    public void auctionService_deletePicture_WithCorrectInput_WithNotNullThumbnail_AndPictureIsThumbnailParent_ShouldDeleteThumbnail() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        
        AuctionPicture auctionPicture = new AuctionPicture();
        auctionPicture.setPictureFileID("SomePictureFileId");
        auctionPicture.setUsageCounter(0);
        auctionPicture = this.auctionPictureRepository.save(auctionPicture);
        auction.setThumbnail(auctionPicture);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        Assert.assertNotNull(auction_SM.getThumbnail());
        
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionPicture(auction_SM.getThumbnail());
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        Mockito.when(this.auctionPictureService.deletePicture(any(), eq(this.user_A.getDomainEmail()))).thenReturn(true);
        Mockito.when(this.auctionPictureService.getPictureWrapperById(pictureWrapper.getId())).thenReturn(pictureWrapper);
        this.auctionService.deletePicture(pictureWrapper,this.user_A.getDomainEmail());
        Auction auctionEntity = this.auctionRepository.getOne(auction_SM.getId());
        Assert.assertNull(auctionEntity.getThumbnail());
    }
    
    @Test
    public void auctionService_deletePicture_WithCorrectInput_WithNotNullThumbnail_AndDifferentThumbnailParrent_ShouldNotDeleteThumbnail() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        
        AuctionPicture expectedPicture = new AuctionPicture();
        expectedPicture.setPictureFileID("SomePictureFileId");
        expectedPicture.setUsageCounter(0);
        expectedPicture = this.auctionPictureRepository.save(expectedPicture);
        
        auction.setThumbnail(expectedPicture);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        Assert.assertNotNull(auction_SM.getThumbnail());
        
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        
        AuctionPictureServiceModel otherAuctionPicture = new AuctionPictureServiceModel();
        otherAuctionPicture.setId(Long.MAX_VALUE);
        
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionPicture(otherAuctionPicture);
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        Mockito.when(this.auctionPictureService.deletePicture(any(), eq(this.user_A.getDomainEmail()))).thenReturn(true);
        Mockito.when(this.auctionPictureService.getPictureWrapperById(pictureWrapper.getId())).thenReturn(pictureWrapper);
        
        this.auctionService.deletePicture(pictureWrapper,this.user_A.getDomainEmail());
        
        Auction auctionEntity = this.auctionRepository.getOne(auction_SM.getId());
        Assert.assertNotNull(auctionEntity.getThumbnail());
        Assert.assertEquals(expectedPicture.getId(), auctionEntity.getThumbnail().getId());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_deletePicture_WithDiffrentAuction_ShouldthrowReporPartlyToUserException() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        Mockito.when(this.auctionPictureService.deletePicture(any(), eq(this.user_A.getDomainEmail()))).thenReturn(true);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        
        pictureWrapper.setAuctionId(Long.MAX_VALUE);
        
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        this.auctionService.deletePicture(pictureWrapper,this.user_A.getDomainEmail());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_deletePicture_WithDiffrentSeller_ShouldThrowReporPartlyToUserException() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        Mockito.when(this.auctionPictureService.deletePicture(any(), eq(this.user_A.getDomainEmail()))).thenReturn(true);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        this.auctionService.deletePicture(pictureWrapper,this.user_B.getDomainEmail());
    }

    /**
     * Test of adminDeletePicture method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_adminDeletePicture_ShouldDeletePictureRegardlessOfOwnership() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        Mockito.when(this.auctionPictureService.adminDeletePicture(any())).thenReturn(true);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        pictureWrapper.setAuctionId(auction_SM.getId());
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        boolean result = this.auctionService.adminDeletePicture(pictureWrapper);
        Assert.assertTrue(result);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_adminDeletePicture_WithDiffrentAuction_ShouldThrowReporPartlyToUserException() throws Exception {
        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        AuctionedItemServiceModel aucItem = new AuctionedItemServiceModel();
        aucItem.setAuction(auction_SM);
        Mockito.when(this.itemService.getAucItemById(any())).thenReturn(aucItem);
        Mockito.when(this.auctionPictureService.adminDeletePicture(any())).thenReturn(true);
        AucItemPictureWrapperServiceModel pictureWrapper = new AucItemPictureWrapperServiceModel();
        
        pictureWrapper.setAuctionId(Long.MAX_VALUE);
        
        pictureWrapper.setAuctionedItemId(Long.MAX_VALUE);
        this.auctionService.adminDeletePicture(pictureWrapper);
    }

    /**
     * Test of getAllAuctionsBySeller method, of class AuctionServiceImpl.
     */
    @Test
    public void autionService_getAllAuctionsBySeller_WithSellerUserRole_ShouldgetAllAuctionsBySeller() {
        Auction createdAuction = this.createTestAuction();
        createdAuction.setStatus(AuctionStatus.CREATED);
        createdAuction.setSeller(this.user_A);
        AuctionServiceModel createdAuction_SM = this.modelMapper.map(this.auctionRepository.save(createdAuction), AuctionServiceModel.class);
        
        Auction winnedAuction1 = this.createTestAuction();
        winnedAuction1.setStatus(AuctionStatus.SELLED);
        winnedAuction1.setSeller(this.user_B);
        AuctionServiceModel targer1 = this.modelMapper.map(this.auctionRepository.save(winnedAuction1), AuctionServiceModel.class);
        
        Auction winnedAuction2 = this.createTestAuction();
        winnedAuction2.setStatus(AuctionStatus.SELLED);
        winnedAuction2.setSeller(this.user_B);
        AuctionServiceModel targer2 = this.modelMapper.map(this.auctionRepository.save(winnedAuction2), AuctionServiceModel.class);
        
        
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction.setSeller(this.user_B);
        AuctionServiceModel archiveAuction_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.USER_B_EMAIL)).thenReturn(this.user_B_SM);
        List<AuctionServiceModel> result = this.auctionService.getAllAuctionsBySeller(this.USER_B_EMAIL, UserRolesEnum.SELLER);
        Assert.assertEquals(2, result.size());
        for(AuctionServiceModel auction : result){
            Assert.assertNotNull(auction);
            Assert.assertTrue(Objects.equals(auction.getId(), targer1.getId()) || Objects.equals(auction.getId(), targer2.getId()));
        }
    }
    
    @Test
    public void autionService_getAllAuctionsBySeller_WithAdminUserRole_ShouldgetAllAuctionsBySeller() {
        Auction createdAuction = this.createTestAuction();
        createdAuction.setStatus(AuctionStatus.CREATED);
        createdAuction.setSeller(this.user_A);
        AuctionServiceModel createdAuction_SM = this.modelMapper.map(this.auctionRepository.save(createdAuction), AuctionServiceModel.class);
        
        Auction winnedAuction1 = this.createTestAuction();
        winnedAuction1.setStatus(AuctionStatus.SELLED);
        winnedAuction1.setSeller(this.user_B);
        AuctionServiceModel targer1 = this.modelMapper.map(this.auctionRepository.save(winnedAuction1), AuctionServiceModel.class);
        
        Auction winnedAuction2 = this.createTestAuction();
        winnedAuction2.setStatus(AuctionStatus.SELLED);
        winnedAuction2.setSeller(this.user_B);
        AuctionServiceModel targer2 = this.modelMapper.map(this.auctionRepository.save(winnedAuction2), AuctionServiceModel.class);
        
        
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction.setSeller(this.user_B);
        AuctionServiceModel archiveAuction_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.USER_B_EMAIL)).thenReturn(this.user_B_SM);
        List<AuctionServiceModel> result = this.auctionService.getAllAuctionsBySeller(this.USER_B_EMAIL, UserRolesEnum.ADMIN);
        Assert.assertEquals(2, result.size());
        for(AuctionServiceModel auction : result){
            Assert.assertNotNull(auction);
            Assert.assertTrue(Objects.equals(auction.getId(), targer1.getId()) || Objects.equals(auction.getId(), targer2.getId()));
        }
    }

    /**
     * Test of getAuctionById method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAuctionById_WithValidInput_CreatorMail_SellerUserRole_ShouldReturnAuction() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.SELLED);
        auction1.setSeller(this.user_A);
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        Auction auction2 = this.createTestAuction();
        auction2.setStatus(AuctionStatus.SELLED);
        auction2.setSeller(this.user_B);
        AuctionServiceModel auctionSM = this.modelMapper.map(this.auctionRepository.save(auction2), AuctionServiceModel.class);
        
        AuctionServiceModel actual = this.auctionService.getAuctionById(targer.getId(), this.user_A.getDomainEmail(), UserRolesEnum.SELLER);
        Assert.assertNotNull(actual);
        Assert.assertEquals(targer.getId(), actual.getId());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getAuctionById_WithValidInput_WithNoNCreatorMail_SellerUserRole_ShouldThrowReporPartlyToUserException() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.SELLED);
        auction1.setSeller(this.user_A);
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        this.auctionService.getAuctionById(targer.getId(), this.user_B.getDomainEmail(), UserRolesEnum.SELLER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getAuctionById_WithArchiveAuctionId_WithCreatorMail_SellerUserRole_ShouldThrowReporPartlyToUserException() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.ARCHIVE);
        auction1.setSeller(this.user_A);
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        this.auctionService.getAuctionById(targer.getId(), this.user_A.getDomainEmail(), UserRolesEnum.SELLER);
    }
    
    @Test(expected=InvalidRoleException.class)
    public void auctionService_getAuctionById_WithValidInput_WithUnsupportedRole_ShouldThrowInvalidRoleException() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.SELLED);
        auction1.setSeller(this.user_A);
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        this.auctionService.getAuctionById(targer.getId(), this.user_A.getDomainEmail(), UserRolesEnum.ROOT);
    }
    
    @Test
    public void auctionService_getAuctionById_WithLiveAuction_WithBuyerUserRole_ShouldReturnAuction() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.LIVE);
        auction1.setSeller(this.user_A);
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        AuctionServiceModel actual = this.auctionService.getAuctionById(targer.getId(), this.user_B.getDomainEmail(), UserRolesEnum.BUYER);
        
        Assert.assertNotNull(actual);
        Assert.assertEquals(targer.getId(), actual.getId());
    }
    
    @Test
    public void auctionService_getAuctionById_WithWinnedAuction_WithBuyerUserRole_WithWinnerEmail_ShouldReturnAuction() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.SELLED);
        auction1.setAuctionWinner(this.user_B);
        auction1.setSeller(this.user_A);
        
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        AuctionServiceModel actual = this.auctionService.getAuctionById(targer.getId(), this.user_B.getDomainEmail(), UserRolesEnum.BUYER);
        
        Assert.assertNotNull(actual);
        Assert.assertEquals(targer.getId(), actual.getId());
    }
    
    @Test
    public void auctionService_getAuctionById_WithArchiveAuction_WithBuyerUserRole_WithWinnerEmail_ShouldReturnAuction() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.ARCHIVE);
        auction1.setAuctionWinner(this.user_B);
        auction1.setSeller(this.user_A);
        
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        AuctionServiceModel actual = this.auctionService.getAuctionById(targer.getId(), this.user_B.getDomainEmail(), UserRolesEnum.BUYER);
        
        Assert.assertNotNull(actual);
        Assert.assertEquals(targer.getId(), actual.getId());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getAuctionById_WithCreatedAuction_WithBuyerUserRole_ShouldThrowReporPartlyToUserException() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.CREATED);
        auction1.setSeller(this.user_A);
        
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        this.auctionService.getAuctionById(targer.getId(), this.user_B.getDomainEmail(), UserRolesEnum.BUYER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getAuctionById_WithWinnedAuction_WithBuyerUserRole_WithDiffrentMailThanWinner_ShouldThrowReporPartlyToUserException() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.SELLED);
        auction1.setSeller(this.user_B);
        auction1.setAuctionWinner(this.user_A);
        
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        this.auctionService.getAuctionById(targer.getId(), this.user_B.getDomainEmail(), UserRolesEnum.BUYER);
    }
    
    @Test
    public void auctionService_getAuctionById_WithValidInput_WithAdminUserRole_WithDiffrentMailThanCreator_ShouldReturnAuction() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.CREATED);
        auction1.setSeller(this.user_A);
        
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        AuctionServiceModel actual = this.auctionService.getAuctionById(targer.getId(), this.user_B.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertNotNull(actual);
        Assert.assertEquals(targer.getId(), actual.getId());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getAuctionById_WithNonExistingAuctionID_WithAdminUserRole_ShouldThrowReporPartlyToUserException() {
        Auction auction1 = this.createTestAuction();
        auction1.setStatus(AuctionStatus.CREATED);
        auction1.setSeller(this.user_A);
        
        AuctionServiceModel targer = this.modelMapper.map(this.auctionRepository.save(auction1), AuctionServiceModel.class);
        
        this.auctionService.getAuctionById(Long.MAX_VALUE, this.user_B.getDomainEmail(), UserRolesEnum.ADMIN);
    }

    /**
     * Test of updateAuction method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_updateAuction_5args_WithAdminUserRole_ShouldUpdateAuctionregardlessOfCreatorAndStatus() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        auction_SM.setAuctionStrategy(AuctionStrategy.SECRET_BIDDING_FIRST_BID);
        
        
        boolean result = this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_B_EMAIL, UserRolesEnum.ADMIN);
        Assert.assertTrue(result);
        Auction changedAuction = this.auctionRepository.findAll().get(0);
        Assert.assertEquals(NEW_AUCTION_TITLE, changedAuction.getTitle());
        Assert.assertEquals(NEW_AUCTION_DESCRIPTION, changedAuction.getDescription());
        Assert.assertTrue(changedAuction.getInitialPrice().compareTo(NEW_AUCTION_PRICE)==0);
        Assert.assertTrue(changedAuction.getDateStarted().compareTo(NEW_START_DATE)==0);
        Assert.assertTrue(changedAuction.getDateExpired().compareTo(NEW_EXPIRED_DATE)==0);
        Assert.assertTrue(this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
        Assert.assertEquals(changedAuction.getAuctionStrategy(), AuctionStrategy.SECRET_BIDDING_FIRST_BID);
    }
    
    @Test(expected = InvalidRoleException.class)
    public void auctionService_updateAuction_5args_WithUnSupportedRole_ShouldThrowInvalidRoleException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_B_EMAIL, UserRolesEnum.BUYER);
    }
    
    @Test
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithValidInput_WithEditableAuction_WithBeforeLiveStatus_ShouldUpdateAllFields() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        boolean result = this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_A_EMAIL, UserRolesEnum.SELLER);
        Assert.assertTrue(result);
        Auction changedAuction = this.auctionRepository.findAll().get(0);
        Assert.assertEquals(NEW_AUCTION_TITLE, changedAuction.getTitle());
        Assert.assertEquals(NEW_AUCTION_DESCRIPTION, changedAuction.getDescription());
        Assert.assertTrue(changedAuction.getInitialPrice().compareTo(NEW_AUCTION_PRICE)==0);
        Assert.assertTrue(changedAuction.getDateStarted().compareTo(NEW_START_DATE)==0);
        Assert.assertTrue(changedAuction.getDateExpired().compareTo(NEW_EXPIRED_DATE)==0);
        Assert.assertTrue(this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
    }
    
    @Test
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithValidInput_WithEditableAuction_WithLiveStatus_ShouldUpdateSelectedFields() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        boolean result = this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_A_EMAIL, UserRolesEnum.SELLER);
        Assert.assertTrue(result);
        Auction changedAuction = this.auctionRepository.findAll().get(0);
        Assert.assertEquals(AUCTION_TITLE, changedAuction.getTitle());
        Assert.assertEquals(AUCTION_DESCRIPTION, changedAuction.getDescription());
        Assert.assertTrue(changedAuction.getInitialPrice().compareTo(AUCTION_PRICE)==0);
        Assert.assertTrue(changedAuction.getDateStarted().compareTo(START_DATE)==0);
        
        // Should change only these fields!!
        Assert.assertTrue(changedAuction.getDateExpired().compareTo(NEW_EXPIRED_DATE)==0);
        
        
        Assert.assertTrue(this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
    }
    
    @Test(expected=ReportToUserException.class)
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithStartDateBeforeNow_WithEditableAuction_WithLiveStatus_ShouldThrowReportToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.LIVE);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("1990-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_A_EMAIL, UserRolesEnum.SELLER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithDiffrentCreator_WithEditableAuction_WithLiveStatus_ShouldThrowReporPartlyToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_B_EMAIL, UserRolesEnum.SELLER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithValidInput_WithNoNEditableAuction_WithLiveStatus_ShouldThrowReporPartlyToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.SELLED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_A_EMAIL, UserRolesEnum.SELLER);
    }
    
    @Test(expected=ReportToUserException.class)
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithInvalidStartDate_WithEditableAuction_WithLiveStatus_ShouldThrowReportToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("1990-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_A_EMAIL, UserRolesEnum.SELLER);
    }
    
    @Test(expected=ReportToUserException.class)
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithExpiredDatebeforeStartDate_WithEditableAuction_WithLiveStatus_ShouldThrowReportToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2030-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2020-02-15 05:00:00");
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_A_EMAIL, UserRolesEnum.SELLER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_updateAuction_5args_WithSellerUserRole_WithnonExistingID_ShouldThrowReporPartlyToUserException() throws Exception {
        String AUCTION_TITLE = "Auction title";
        String AUCTION_DESCRIPTION = "My Auction description";
        BigDecimal AUCTION_PRICE = BigDecimal.ONE;
        Date START_DATE = this.DATE_FORMAT.parse("2030-01-10 05:00:00");
        Date EXPIRED_DATE = this.DATE_FORMAT.parse("2030-01-15 05:00:00");

        Auction auction = this.createTestAuction();
        auction.setStatus(AuctionStatus.CREATED);
        auction.setSeller(this.user_A);
        auction.setTitle(AUCTION_TITLE);
        auction.setDescription(AUCTION_DESCRIPTION);
        auction.setInitialPrice(AUCTION_PRICE);
        auction.setDateStarted(START_DATE);
        auction.setDateExpired(EXPIRED_DATE);

        AuctionServiceModel auction_SM = this.modelMapper.map(this.auctionRepository.save(auction), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(false));
        
        doAnswer(invocation->{
            this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag=true;
            return this.ADD_ALL_AUCTIONED_ITEMS_TO_AUCTION_Flag;
        }).when(this.itemService).addToAuction(any(),any());
        
        String NEW_AUCTION_TITLE = "New Auction title";
        String NEW_AUCTION_DESCRIPTION = "New my Auction description";
        BigDecimal NEW_AUCTION_PRICE = BigDecimal.TEN;
        Date NEW_START_DATE = this.DATE_FORMAT.parse("2020-02-10 05:00:00");
        Date NEW_EXPIRED_DATE = this.DATE_FORMAT.parse("2030-02-15 05:00:00");
        
        auction_SM.setId(Long.MAX_VALUE);
        
        auction_SM.setTitle(NEW_AUCTION_TITLE);
        auction_SM.setDescription(NEW_AUCTION_DESCRIPTION);
        auction_SM.setInitialPrice(NEW_AUCTION_PRICE);
        auction_SM.setDateStarted(NEW_START_DATE);
        auction_SM.setDateExpired(NEW_EXPIRED_DATE);
        
        this.auctionService.updateAuction(auction_SM, new ArrayList(), new ArrayList(),this.USER_A_EMAIL, UserRolesEnum.SELLER);
    }

    /**
     * Test of deleteAuction method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_deleteAuction_WithSupportedAdminUserRole_And_DeletableAuction_ShouldDeleteAuction() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.CREATED);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
        }).when(this.auctionPictureService).deleteAllPicturesFromAuction(any());
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(),eq(true));

        boolean result = this.auctionService.deleteAuction(archiveAuction_1_SM,this.user_B_SM.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertTrue(result);
        Assert.assertTrue(this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
        Assert.assertEquals(0, this.auctionRepository.findAll().size());
    }
    
    @Test
    public void auctionService_deleteAuction_WithSupportedAdminUserRole_And_NoNDeletableAuction_ShouldDeleteAuction() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.LIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
        }).when(this.auctionPictureService).deleteAllPicturesFromAuction(any());
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(true));

        boolean result = this.auctionService.deleteAuction(archiveAuction_1_SM,this.user_B_SM.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertTrue(result);
        Assert.assertTrue(this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
        Assert.assertEquals(0, this.auctionRepository.findAll().size());
    }
    
    @Test
    public void auctionService_deleteAuction_WithSupportedUserUserRole_And_DeletableAuction_OwnAuction_ShouldDeleteAuction() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.CREATED);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
        }).when(this.auctionPictureService).deleteAllPicturesFromAuction(any());
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(true));

        boolean result = this.auctionService.deleteAuction(archiveAuction_1_SM,this.user_A_SM.getDomainEmail(), UserRolesEnum.SELLER);
        Assert.assertTrue(result);
        Assert.assertTrue(this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
        Assert.assertEquals(0, this.auctionRepository.findAll().size());
    }
    
    @Test(expected=InvalidRoleException.class)
    public void auctionService_deleteAuction_WithUnSupportedUserUserRole_ShouldThrowInvalidRoleException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.CREATED);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
       
        this.auctionService.deleteAuction(archiveAuction_1_SM,this.user_A_SM.getDomainEmail(), UserRolesEnum.BUYER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_deleteAuction_WithSupportedUserUserRole_And_DeletableAuction_NotOwnAuction_ShouldThrowReporPartlyToUserException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.CREATED);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
        }).when(this.auctionPictureService).deleteAllPicturesFromAuction(any());
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(true));

        this.auctionService.deleteAuction(archiveAuction_1_SM,this.user_B_SM.getDomainEmail(), UserRolesEnum.SELLER);
    }
    
    @Test(expected=InvalidRoleException.class)
    public void auctionService_deleteAuction_WithSupportedUserUserRole_And_DeletableAuction_InvalidAuctionID_ShouldThrowInvalidRoleException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.CREATED);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        archiveAuction_1_SM.setId(Long.MAX_VALUE);
        
        doAnswer(invocation->{
            this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
        }).when(this.auctionPictureService).deleteAllPicturesFromAuction(any());
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(true));

        this.auctionService.deleteAuction(archiveAuction_1_SM,this.user_A_SM.getDomainEmail(), UserRolesEnum.BUYER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_deleteAuction_WithSupportedUserUserRole_And_NoNDeletableAuction_OwnAuction_ShouldReporPartlyToUserException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.LIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
        }).when(this.auctionPictureService).deleteAllPicturesFromAuction(any());
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(true));

        this.auctionService.deleteAuction(archiveAuction_1_SM,this.user_A_SM.getDomainEmail(), UserRolesEnum.SELLER);
    }

    /**
     * Test of archive method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_archive_WithCorrectStatus_AndCorrectEmail_ShouldArchiveAuction() {
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.SELLED);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        boolean result = this.auctionService.archive(archiveAuction_1_SM, this.user_A.getDomainEmail());
        Assert.assertTrue(result);
        Auction changedAuction = this.auctionRepository.getOne(archiveAuction_1_SM.getId());
        Assert.assertTrue(changedAuction.getStatus().compareTo(AuctionStatus.ARCHIVE)==0);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_archive_WithINCorrectInStatus_AndCorrectEmail_ShouldThrowReporPartlyToUserException() {
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.CREATED);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        this.auctionService.archive(archiveAuction_1_SM, this.user_A.getDomainEmail());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_archive_WithCorrectStatus_AndNOTCorrectEmail_ShouldThrowReporPartlyToUserException() {
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.CREATED);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        this.auctionService.archive(archiveAuction_1_SM, this.user_B.getDomainEmail());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_archive_NonExistingID_AndCorrectEmail_ShouldThrowReporPartlyToUserException() {
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.CREATED);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        archiveAuction_1_SM.setId(Long.MAX_VALUE);
        this.auctionService.archive(archiveAuction_1_SM, this.user_A.getDomainEmail());
    }
    

    /**
     * Test of getAllArchivesByCreator method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAllArchivesByCreator_WithAdminUserRole_AndCorrectEmail_ShouldGetAllArchivesRegardlessOfCreator() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.modelMapper.map(this.user_A_SM, User.class));
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        Auction archiveAuction2 = this.createTestAuction();
        archiveAuction2.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction2.setSeller(this.modelMapper.map(this.user_B_SM, User.class));
        AuctionServiceModel archiveAuction_2_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction2), AuctionServiceModel.class);
        
        Auction archiveAuction3 = this.createTestAuction();
        archiveAuction3.setStatus(AuctionStatus.LIVE);
        archiveAuction3.setSeller(this.modelMapper.map(this.user_A_SM, User.class));
        AuctionServiceModel archiveAuction_3_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction3), AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.user_A.getDomainEmail())).thenReturn(this.user_A_SM);
        
        List<AuctionServiceModel> result = this.auctionService.getAllArchivesByCreator(this.user_A.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertEquals(1, result.size());
        for(AuctionServiceModel auction:result){
            Assert.assertNotNull(auction);
            Assert.assertEquals(archiveAuction_1_SM.getId(), auction.getId());
        }
    }
    
    @Test
    public void auctionService_getAllArchivesByCreator_WithSellerUserRole_AndCorrectEmail_ShouldGetAllArchivesOfCreator() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        Auction archiveAuction2 = this.createTestAuction();
        archiveAuction2.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction2.setSeller(this.user_B);
        AuctionServiceModel archiveAuction_2_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction2), AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.user_A.getDomainEmail())).thenReturn(this.user_A_SM);
        
        List<AuctionServiceModel> result = this.auctionService.getAllArchivesByCreator(this.user_A.getDomainEmail(), UserRolesEnum.SELLER);
        Assert.assertEquals(1, result.size());
        for(AuctionServiceModel auction:result){
            Assert.assertNotNull(auction);
            Assert.assertEquals(archiveAuction_1_SM.getId(), auction.getId());
        }
    }
    
    @Test(expected=InvalidRoleException.class)
    public void auctionService_getAllArchivesByCreator_WithUnsupportedUserRole_AndCorrectEmail_ShouldThrowInvalidRoleException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        Auction archiveAuction2 = this.createTestAuction();
        archiveAuction2.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction2.setSeller(this.user_B);
        AuctionServiceModel archiveAuction_2_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction2), AuctionServiceModel.class);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.user_A.getDomainEmail())).thenReturn(this.user_A_SM);
        
        this.auctionService.getAllArchivesByCreator(this.user_A.getDomainEmail(), UserRolesEnum.BUYER);
    }

    /**
     * Test of getArchiveById method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getArchiveById_WithCorrectIdAndUserRole_SellerAndCorrectEmail_ShouldReturnAuction() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        Auction archiveAuction2 = this.createTestAuction();
        archiveAuction2.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction2.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_2_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction2), AuctionServiceModel.class);
        
        AuctionServiceModel result = this.auctionService.getArchiveById(archiveAuction2.getId(), this.user_A.getDomainEmail(), UserRolesEnum.SELLER);
        Assert.assertNotNull(result);
        Assert.assertEquals(archiveAuction_2_SM.getId(), result.getId());
    }
    
    @Test
    public void auctionService_getArchiveById_WithCorrectIdAndUserRole_AdminAndCorrectEmail_ShouldReturnAuctionRegardlessofCreator() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        Auction archiveAuction2 = this.createTestAuction();
        archiveAuction2.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction2.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_2_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction2), AuctionServiceModel.class);
        
        AuctionServiceModel result = this.auctionService.getArchiveById(archiveAuction2.getId(), this.user_B.getDomainEmail(), UserRolesEnum.ADMIN);
        Assert.assertNotNull(result);
        Assert.assertEquals(archiveAuction_2_SM.getId(), result.getId());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getArchiveById_WithCorrectIdAndUserRole_SellerAndInCorrectEmail_ShouldThrowReporPartlyToUserException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
       
       this.auctionService.getArchiveById(archiveAuction_1_SM.getId(), this.user_B.getDomainEmail(), UserRolesEnum.SELLER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getArchiveById_NonExistingId_ShouldThrowReporPartlyToUserException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
       
       this.auctionService.getArchiveById(Long.MAX_VALUE, this.user_A.getDomainEmail(), UserRolesEnum.SELLER);
    }
       
    @Test
    public void auctionService_getArchiveById_WithCorrectId_AndUserRole_BUYER__AndCorrectMail_ShouldreturnArchive(){
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        archiveAuction1.setAuctionWinner(this.user_B);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
       
        AuctionServiceModel result = this.auctionService.getArchiveById(archiveAuction_1_SM.getId(), this.user_B.getDomainEmail(), UserRolesEnum.BUYER);
        Assert.assertNotNull(result);
        Assert.assertEquals(archiveAuction_1_SM.getId(), result.getId());
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_getArchiveById_WithCorrectId_AndUserRole_BUYER_AndInCorrectMail_ShouldThrowReporPartlyToUserException(){
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        archiveAuction1.setAuctionWinner(this.user_B);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
       
        this.auctionService.getArchiveById(archiveAuction_1_SM.getId(), this.user_A.getDomainEmail(), UserRolesEnum.BUYER);
    }
    
    @Test(expected=InvalidRoleException.class)
    public void auctionService_getArchiveById_UnsupportedUserRole_ShouldThrowInvalidRoleException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
       
        this.auctionService.getArchiveById(archiveAuction_1_SM.getId(), this.user_A.getDomainEmail(), UserRolesEnum.ROOT);
    }

    /**
     * Test of deleteArchiveById method, of class AuctionServiceImpl.
     */
    @Test(expected=InvalidRoleException.class)
    public void auctionService_deleteArchiveById_WithNotSupportedRole_ShouldThrowInvalidRoleException() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        this.auctionService.deleteArchiveById(archiveAuction_1_SM.getId(), UserRolesEnum.BUYER);
    }
    
    @Test(expected=ReporPartlyToUserException.class)
    public void auctionService_deleteArchiveById_WithSupportedRole_AndNonArchiveID_ShouldThrowReporPartlyToUserException() {
        Auction createdAuction1 = this.createTestAuction();
        createdAuction1.setStatus(AuctionStatus.CREATED);
        createdAuction1.setSeller(this.user_A);
        AuctionServiceModel createdAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(createdAuction1), AuctionServiceModel.class);
        
        this.auctionService.deleteArchiveById(createdAuction_1_SM.getId(), UserRolesEnum.ADMIN);
    }
    
    @Test
    public void auctionService_deleteArchiveById_WithSupportedRole_AndArchiveID_ShouldDeleteAuction() {
        Auction archiveAuction1 = this.createTestAuction();
        archiveAuction1.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction1.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_1_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction1), AuctionServiceModel.class);
        
        doAnswer(invocation->{
            this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag;
        }).when(this.auctionPictureService).deleteAllPicturesFromAuction(any());
        
        doAnswer(invocation->{
            this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag=true;
            return this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag;
        }).when(this.itemService).removeAllFromAuction(any(), eq(true));

        boolean result = this.auctionService.deleteArchiveById(archiveAuction_1_SM.getId(), UserRolesEnum.ADMIN);
        Assert.assertTrue(result);
        Assert.assertTrue(this.DELETE_ALL_PICTURES_FROM_AUCTION_Flag);
        Assert.assertTrue(this.DELETE_ALL_AUCTIONED_ITEMS_FROM_AUCTION_Flag);
        Assert.assertEquals(0, this.auctionRepository.findAll().size());
    }

    /**
     * Test of getAllArchives method, of class AuctionServiceImpl.
     */
    @Test
    public void auctionService_getAllArchives_onEmptyDB_ShouldReturnEmptyList() {
        List<AuctionServiceModel> result = this.auctionService.getAllArchives(UserRolesEnum.ADMIN);
        Assert.assertTrue(result.isEmpty());
    }
    
    @Test
    public void auctionService_getAllArchives_FromAllAuctionStatus_ShouldOnlyArchivedAuctions() {
        Auction createdAuction = this.createTestAuction();
        createdAuction.setStatus(AuctionStatus.CREATED);
        createdAuction.setSeller(this.user_A);
        AuctionServiceModel createdAuction_SM = this.modelMapper.map(this.auctionRepository.save(createdAuction), AuctionServiceModel.class);
        
        Auction liveAuction = this.createTestAuction();
        liveAuction.setStatus(AuctionStatus.LIVE);
        liveAuction.setSeller(this.user_A);
        AuctionServiceModel liveAuction_SM = this.modelMapper.map(this.auctionRepository.save(liveAuction), AuctionServiceModel.class);
        
        Auction selledAuction = this.createTestAuction();
        selledAuction.setStatus(AuctionStatus.SELLED);
        selledAuction.setSeller(this.user_A);
        AuctionServiceModel selledAuction_SM = this.modelMapper.map(this.auctionRepository.save(selledAuction), AuctionServiceModel.class);
        
        Auction expiredAuction = this.createTestAuction();
        expiredAuction.setStatus(AuctionStatus.EXPIRED);
        expiredAuction.setSeller(this.user_A);
        AuctionServiceModel exporedAuction_SM = this.modelMapper.map(this.auctionRepository.save(expiredAuction), AuctionServiceModel.class);
        
        Auction archiveAuction = this.createTestAuction();
        archiveAuction.setStatus(AuctionStatus.ARCHIVE);
        archiveAuction.setSeller(this.user_A);
        AuctionServiceModel archiveAuction_SM = this.modelMapper.map(this.auctionRepository.save(archiveAuction), AuctionServiceModel.class);
        
        List<AuctionServiceModel> result = this.auctionService.getAllArchives(UserRolesEnum.ADMIN);
        Assert.assertEquals(1,result.size());
        for(AuctionServiceModel auction:result){
            Assert.assertNotNull(auction);
            Assert.assertTrue(auction.getStatus().compareTo(AuctionStatus.ARCHIVE)==0);
            Assert.assertEquals(auction.getId(), archiveAuction_SM.getId());
        }
    }
    
    @Test(expected=InvalidRoleException.class)
    public void auctionService_getAllArchives_WithUnSupportedUserRile_ShouldThrowInvalidRoleException() {
        List<AuctionServiceModel> result = this.auctionService.getAllArchives(UserRolesEnum.BUYER);
        Assert.assertTrue(result.isEmpty());
    }
    
}

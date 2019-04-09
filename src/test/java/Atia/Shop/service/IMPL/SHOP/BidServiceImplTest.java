/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.SHOP;

import Atia.Shop.domain.entities.Auction;
import Atia.Shop.domain.entities.Bid;
import Atia.Shop.domain.entities.User;
import Atia.Shop.domain.entities.enums.AuctionStatus;
import Atia.Shop.domain.entities.enums.AuctionStrategy;
import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.domain.repositories.AuctionRepository;
import Atia.Shop.domain.repositories.BidRepository;
import Atia.Shop.domain.repositories.UserRepository;
import Atia.Shop.exeptions.base.ReporPartlyToUserException;
import Atia.Shop.exeptions.base.ReportToUserException;
import Atia.Shop.service.API.SHOP.AuctionService;
import Atia.Shop.service.API.SHOP.BidService;
import Atia.Shop.service.API.Users.UserService;
import Atia.Shop.utils.valdiation.InputValidator;
import java.math.BigDecimal;
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
public class BidServiceImplTest {
    
    @Autowired
    private BidRepository bidRepository;
    
    private ModelMapper modelMapper;
    private AuctionService auctionService;
    private UserService userService;
    private InputValidator inputValidator;
    
    private BidService bidservice;
    
    int counterUpdate = 0;
    
    /////////////////////////////////////
    @Autowired
    private UserRepository userRepository;
    private final String SELLER_EMAIL="seller@atia.com";
    private User seller;
    private UserServiceModel sellerSM;
    
    private final String BIDDERR_EMAIL="bidder@atia.com";
    private User bidder;
    private UserServiceModel bidderSM;
    
    //////////////////////////////////////
    @Autowired
    private AuctionRepository auctionRepository;
    private Auction auctionA;
    private AuctionServiceModel auctionAServiceModel;
    
    private Auction auctionB;
    private AuctionServiceModel auctionBServiceModel;
    
    ///////////////////////////////////////
    
    
    public BidServiceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.modelMapper=new ModelMapper();
        this.inputValidator=new InputValidator();
        
        this.auctionService = Mockito.mock(AuctionService.class);
        Mockito.when(this.auctionService.isAuctionBiddable(any())).thenReturn(true);
        this.userService=Mockito.mock(UserService.class);
        
        this.bidservice=new BidServiceImpl(this.modelMapper, this.auctionService, this.userService, this.bidRepository, this.inputValidator);
        
        this.seller=this.initUser();
        this.seller.setDomainEmail(this.SELLER_EMAIL);
        this.seller = this.userRepository.saveAndFlush(this.seller);
        this.sellerSM=this.modelMapper.map(this.seller, UserServiceModel.class);
        
        this.bidder=this.initUser();
        this.bidder.setDomainEmail(this.BIDDERR_EMAIL);
        this.bidder=this.userRepository.saveAndFlush(this.bidder);
        this.bidderSM=this.modelMapper.map(this.bidder, UserServiceModel.class);
        
        this.auctionA=this.createTestAuction();
        this.auctionA.setSeller(this.seller);
        this.auctionA=this.auctionRepository.saveAndFlush(this.auctionA);
        this.auctionAServiceModel=this.modelMapper.map(this.auctionA, AuctionServiceModel.class);
        this.auctionB=this.createTestAuction();
        this.auctionB.setSeller(this.seller);
        this.auctionB=this.auctionRepository.saveAndFlush(this.auctionB);
        this.auctionBServiceModel=this.modelMapper.map(this.auctionB, AuctionServiceModel.class);
        
        this.counterUpdate=0;
    }
    
    @After
    public void tearDown() {
    }
    
    private User initUser(){
        User user = new User();
        user.setName("User name");
        //user.setDomainEmail("vlado@atia.com");
        user.setIsActive(Boolean.TRUE);
        user.setTestPassword("password");
        user.setAuthorities(new HashSet());
        return user;
    }
    
    private Auction createTestAuction(){
        Auction auction = new Auction();
        auction.setTitle("Auction title");
        //auction.setSeller(this.seller);
        auction.setAuctionStrategy(AuctionStrategy.OPEN_BIDDING_HIGHEST_BID);
        auction.setDescription("My Auction description");
        auction.setInitialPrice(BigDecimal.ONE);
        auction.setDateStarted(new Date());
        auction.setDateExpired(new Date());
        auction.setStatus(AuctionStatus.LIVE);
        return auction;
    }
    
    private BidServiceModel initBidServiceModel()
    {
        BidServiceModel bibServiceModel = new BidServiceModel();
        bibServiceModel.setAmount(BigDecimal.TEN);
        return bibServiceModel;
    }

    /**
     * Test of placeBid method, of class BidServiceImpl.
     */
    @Test
    public void bidService_placeBid_WithCorrectValues_ShouldPlaceBid() throws Exception {
        BidServiceModel bibServiceModel = this.initBidServiceModel();
        bibServiceModel.setAuction(this.auctionAServiceModel);
        bibServiceModel.setBidder(this.bidderSM);
        bibServiceModel = this.bidservice.placeBid(bibServiceModel);
        Bid bid = this.bidRepository.findById(bibServiceModel.getId()).orElse(null);
        Assert.assertNotNull(bid);
        Assert.assertEquals(this.BIDDERR_EMAIL, bid.getBidder().getDomainEmail());
    }
    
    @Test(expected = ReportToUserException.class)
    public void bidService_placeBid_SellerPlaceBidHisOwnAuction_ShouldThrowReportToUserException() throws Exception {
        BidServiceModel bibServiceModel = this.initBidServiceModel();
        bibServiceModel.setAuction(this.auctionAServiceModel);
        bibServiceModel.setBidder(this.sellerSM);
        this.bidservice.placeBid(bibServiceModel);
    }
    
    @Test(expected = ReporPartlyToUserException.class)
    public void bidService_placeBid_InvalidAuctionStatus_ShouldThrowReporPartlyToUserException() throws Exception {
        BidServiceModel bibServiceModel = this.initBidServiceModel();
        this.auctionAServiceModel.setStatus(AuctionStatus.CREATED);
        Mockito.when(this.auctionService.isAuctionBiddable(this.auctionAServiceModel)).thenReturn(false);
        bibServiceModel.setAuction(this.auctionAServiceModel);
        bibServiceModel.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel);
    }
    
    @Test
    public void bidService_placeBid_PlaceHigherBidShouldChangeBestBid_ShouldChangeAuctionBestBid() throws Exception {
        BidServiceModel bibServiceModel1 = this.initBidServiceModel();
        bibServiceModel1.setAuction(this.auctionAServiceModel);
        bibServiceModel1.setBidder(this.bidderSM);
        doAnswer(invocation->{
            this.counterUpdate++;
            return true;
        }).when(this.auctionService).saveAll(any());
        this.bidservice.placeBid(bibServiceModel1);
        Assert.assertEquals(1, this.counterUpdate);
        
        BidServiceModel bibServiceModel2 = this.initBidServiceModel();
        bibServiceModel2.setAuction(this.auctionAServiceModel);
        bibServiceModel2.setAmount(BigDecimal.valueOf(100));
        bibServiceModel2.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel2);
        Assert.assertEquals(2, this.counterUpdate);
        
        BidServiceModel bibServiceModel3 = this.initBidServiceModel();
        bibServiceModel3.setAuction(this.auctionAServiceModel);
        bibServiceModel3.setAmount(BigDecimal.valueOf(50));
        bibServiceModel3.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel3);
        Assert.assertEquals(2, this.counterUpdate);
    }
    

    /**
     * Test of getAllByAuctionId method, of class BidServiceImpl.
     */
    @Test
    public void bidService_getAllByAuctionId_getAllBidsByAuction1_ShouldGetAllBidsByAuction() throws ReportToUserException {
        BidServiceModel bibServiceModel1 = this.initBidServiceModel();
        bibServiceModel1.setAuction(this.auctionAServiceModel);
        bibServiceModel1.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel1);
        
        BidServiceModel bibServiceModel2 = this.initBidServiceModel();
        bibServiceModel2.setAuction(this.auctionAServiceModel);
        bibServiceModel2.setAmount(BigDecimal.valueOf(100));
        bibServiceModel2.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel2);
        
        BidServiceModel bibServiceModel3 = this.initBidServiceModel();
        bibServiceModel3.setAuction(this.auctionAServiceModel);
        bibServiceModel3.setAmount(BigDecimal.valueOf(50));
        bibServiceModel3.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel3);
        
        BidServiceModel otherBid = this.initBidServiceModel();
        otherBid.setAuction(this.auctionBServiceModel);
        otherBid.setAmount(BigDecimal.valueOf(50));
        otherBid.setBidder(this.bidderSM);
        this.bidservice.placeBid(otherBid);
        
        Mockito.when(this.auctionService.getAuctionById(this.auctionAServiceModel.getId())).thenReturn(this.auctionAServiceModel);
        
        List<BidServiceModel> result = this.bidservice.getAllByAuctionId(this.auctionA.getId());
        Assert.assertEquals(3, result.size());
        for(BidServiceModel bid:result){
            Assert.assertNotNull(bid);
        }
    }
    
    @Test
    public void bidService_getAllByAuctionId_getAllBidsByAuction2_ShouldGetAllBidsByAuction() throws ReportToUserException {
        BidServiceModel bibServiceModel1 = this.initBidServiceModel();
        bibServiceModel1.setAuction(this.auctionAServiceModel);
        bibServiceModel1.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel1);
        
        BidServiceModel bibServiceModel2 = this.initBidServiceModel();
        bibServiceModel2.setAuction(this.auctionAServiceModel);
        bibServiceModel2.setAmount(BigDecimal.valueOf(100));
        bibServiceModel2.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel2);
        
        BidServiceModel bibServiceModel3 = this.initBidServiceModel();
        bibServiceModel3.setAuction(this.auctionAServiceModel);
        bibServiceModel3.setAmount(BigDecimal.valueOf(50));
        bibServiceModel3.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel3);
        
        
        Mockito.when(this.auctionService.getAuctionById(this.auctionBServiceModel.getId())).thenReturn(this.auctionBServiceModel);
        
        List<BidServiceModel> result = this.bidservice.getAllByAuctionId(this.auctionBServiceModel.getId());
        Assert.assertEquals(0, result.size());
    }

    /**
     * Test of getALLByBidderMail method, of class BidServiceImpl.
     */
    @Test
    public void bidService_getAllByBidderMail_getAllBidsByBidder1_ShouldGetAllBidsByNidder() throws ReportToUserException {
        BidServiceModel bibServiceModel1 = this.initBidServiceModel();
        bibServiceModel1.setAuction(this.auctionAServiceModel);
        bibServiceModel1.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel1);
        
        BidServiceModel bibServiceModel2 = this.initBidServiceModel();
        bibServiceModel2.setAuction(this.auctionAServiceModel);
        bibServiceModel2.setAmount(BigDecimal.valueOf(100));
        bibServiceModel2.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel2);
        
        BidServiceModel bibServiceModel3 = this.initBidServiceModel();
        bibServiceModel3.setAuction(this.auctionAServiceModel);
        bibServiceModel3.setAmount(BigDecimal.valueOf(50));
        bibServiceModel3.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel3);
        
        this.auctionBServiceModel.setSeller(this.bidderSM);
        
        BidServiceModel otherBid = this.initBidServiceModel();
        otherBid.setAuction(this.auctionBServiceModel);
        otherBid.setAmount(BigDecimal.valueOf(50));
        otherBid.setBidder(this.sellerSM);
        this.bidservice.placeBid(otherBid);
        
        Mockito.when(this.userService.getUserByDomainEmail(this.bidderSM.getDomainEmail())).thenReturn(this.bidderSM);
        
        List<BidServiceModel> result = this.bidservice.getALLByBidderMail(this.bidderSM.getDomainEmail());
        Assert.assertEquals(3, result.size());
        for(BidServiceModel bid:result){
            Assert.assertNotNull(bid);
        }
    }
    
    @Test
    public void bidService_getAllByBidderMail_getAllBidsByBidder2_ShouldGetAllBidsByBidder() throws ReportToUserException {
        BidServiceModel bibServiceModel1 = this.initBidServiceModel();
        bibServiceModel1.setAuction(this.auctionAServiceModel);
        bibServiceModel1.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel1);
        
        BidServiceModel bibServiceModel2 = this.initBidServiceModel();
        bibServiceModel2.setAuction(this.auctionAServiceModel);
        bibServiceModel2.setAmount(BigDecimal.valueOf(100));
        bibServiceModel2.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel2);
        
        BidServiceModel bibServiceModel3 = this.initBidServiceModel();
        bibServiceModel3.setAuction(this.auctionAServiceModel);
        bibServiceModel3.setAmount(BigDecimal.valueOf(50));
        bibServiceModel3.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel3);

        Mockito.when(this.userService.getUserByDomainEmail(this.sellerSM.getDomainEmail())).thenReturn(this.sellerSM);
        
        List<BidServiceModel> result = this.bidservice.getALLByBidderMail(this.sellerSM.getDomainEmail());
        Assert.assertEquals(0, result.size());
    }

    /**
     * Test of adminGetALL method, of class BidServiceImpl.
     */
    @Test
    public void bidService_adminGetALL_getAllBidsByAdmin_ShouldGetAllBids() throws ReportToUserException {
        BidServiceModel bibServiceModel1 = this.initBidServiceModel();
        bibServiceModel1.setAuction(this.auctionAServiceModel);
        bibServiceModel1.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel1);
        
        BidServiceModel bibServiceModel2 = this.initBidServiceModel();
        bibServiceModel2.setAuction(this.auctionAServiceModel);
        bibServiceModel2.setAmount(BigDecimal.valueOf(100));
        bibServiceModel2.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel2);
        
        BidServiceModel bibServiceModel3 = this.initBidServiceModel();
        bibServiceModel3.setAuction(this.auctionAServiceModel);
        bibServiceModel3.setAmount(BigDecimal.valueOf(50));
        bibServiceModel3.setBidder(this.bidderSM);
        this.bidservice.placeBid(bibServiceModel3);
        
        this.auctionBServiceModel.setSeller(this.bidderSM);
        
        BidServiceModel otherBid = this.initBidServiceModel();
        otherBid.setAuction(this.auctionBServiceModel);
        otherBid.setAmount(BigDecimal.valueOf(50));
        otherBid.setBidder(this.sellerSM);
        this.bidservice.placeBid(otherBid);

        List<BidServiceModel> result = this.bidservice.adminGetALL();
        Assert.assertEquals(4, result.size());
        for(BidServiceModel bid:result){
            Assert.assertNotNull(bid);
        }
    }
    
    @Test
    public void bidService_adminGetALL_getAllBidsByAdminOnEmptyDB_ShouldreturnAmptyList() throws ReportToUserException {
        List<BidServiceModel> result = this.bidservice.adminGetALL();
        Assert.assertEquals(0, result.size());
    }
    
}

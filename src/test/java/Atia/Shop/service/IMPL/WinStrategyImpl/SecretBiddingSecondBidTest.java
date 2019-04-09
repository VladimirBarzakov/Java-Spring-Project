/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.service.IMPL.WinStrategyImpl;

import Atia.Shop.domain.models.serviceModels.AuctionServiceModel;
import Atia.Shop.domain.models.serviceModels.BidServiceModel;
import Atia.Shop.domain.models.serviceModels.UserServiceModel;
import Atia.Shop.service.API.WinStrategy.WinStrategyExecutor;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@RunWith(SpringRunner.class)
public class SecretBiddingSecondBidTest {
    
    private WinStrategyExecutor strategyExecutor;
    private List<BidServiceModel> allAuctionBids;

    /////////////////////////////////////
    private UserServiceModel bidder_b_SM;
    private UserServiceModel bidder_a_SM;

    private UserServiceModel seller_SM;

    //////////////////////////////////////
    private AuctionServiceModel auction;

    //////////////////////////////////////
    
    public SecretBiddingSecondBidTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        strategyExecutor = new SecretBiddingSecondBid();

        bidder_a_SM = new UserServiceModel();
        bidder_a_SM.setId("bidder_a");
        bidder_a_SM.setDomainEmail("bidder_a@atia.com");
        bidder_a_SM.setName("bidder_a");

        bidder_b_SM = new UserServiceModel();
        bidder_b_SM.setId("bidder_b");
        bidder_b_SM.setDomainEmail("bidder_b@atia.com");
        bidder_b_SM.setName("bidder_b");

        seller_SM = new UserServiceModel();
        seller_SM.setId("seller_a");
        seller_SM.setDomainEmail("seller_a@atia.com");
        seller_SM.setName("seller_a");

        auction = new AuctionServiceModel();
        auction.setId(Long.MAX_VALUE);
        auction.setSeller(seller_SM);
        
        allAuctionBids=new ArrayList();

        for (int i = 1; i <= 6; i++) {
            BidServiceModel bid = new BidServiceModel();
            if (i % 2 == 0) {
                bid.setBidder(bidder_a_SM);
            } else {
                bid.setBidder(bidder_b_SM);
            }
            bid.setId((long)i);
            bid.setBidDate(new Date());
            bid.setAuction(auction);
            bid.setAmount(new BigDecimal(i));
            allAuctionBids.add(bid);
        }
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of electBestBidAndWinnerAndWinPrice method, of class SecretBiddingSecondBid.
     */
    @Test
    public void secretBiddingSecondBid_electBestBidAndWinnerAndWinPrice_ShouldWorkCorrectly() {
        BidServiceModel highestBid = allAuctionBids.get(3);
        highestBid.setAmount(new BigDecimal(100));
        BidServiceModel secondHighest = allAuctionBids.get(4);
        secondHighest.setAmount(new BigDecimal(50));
        BidServiceModel bestBid = null;
        UserServiceModel winner=null;
        BigDecimal winPrice=null;
        this.strategyExecutor.electBestBidAndWinnerAndWinPrice(allAuctionBids);
        bestBid = this.strategyExecutor.getBestBid();
        winner = this.strategyExecutor.getWinner();
        winPrice = this.strategyExecutor.getWinPrice();
        
        Assert.assertNotNull(bestBid);
        Assert.assertNotNull(winner);
        Assert.assertNotNull(winPrice);
        
        Assert.assertEquals(winPrice, secondHighest.getAmount());
        Assert.assertEquals(bestBid.getId(), highestBid.getId());
        Assert.assertEquals(winner.getId(), highestBid.getBidder().getId());
    }
    
    @Test
    public void secretBiddingSecondBid_electBestBidAndWinnerAndWinPrice_OnEqualBidsShouldPickEarliest() throws ParseException {
        BidServiceModel highestBid = allAuctionBids.get(3);
        highestBid.setAmount(new BigDecimal(100));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date futureDate = sdf.parse("2200-12-31");
        BidServiceModel secondHighest = allAuctionBids.get(4);
        secondHighest.setAmount(new BigDecimal(100));
        secondHighest.setBidDate(futureDate);
        BidServiceModel bestBid = null;
        UserServiceModel winner=null;
        BigDecimal winPrice=null;
        this.strategyExecutor.electBestBidAndWinnerAndWinPrice(allAuctionBids);
        bestBid = this.strategyExecutor.getBestBid();
        winner = this.strategyExecutor.getWinner();
        winPrice = this.strategyExecutor.getWinPrice();
        
        Assert.assertNotNull(bestBid);
        Assert.assertNotNull(winner);
        Assert.assertNotNull(winPrice);
        Assert.assertEquals(winPrice, secondHighest.getAmount());
        Assert.assertEquals(bestBid.getId(), highestBid.getId());
        Assert.assertEquals(winner.getId(), highestBid.getBidder().getId());
    }
    @Test
    public void secretBiddingSecondBid_electBestBidAndWinnerAndWinPrice_OnSingleBid_ShouldWorkCorrectly() {
        BidServiceModel singleBid = allAuctionBids.get(3);
        singleBid.setAmount(new BigDecimal(100));
        allAuctionBids= Arrays.asList(singleBid);
        BidServiceModel bestBid = null;
        UserServiceModel winner=null;
        BigDecimal winPrice=null;
        this.strategyExecutor.electBestBidAndWinnerAndWinPrice(allAuctionBids);
        bestBid = this.strategyExecutor.getBestBid();
        winner = this.strategyExecutor.getWinner();
        winPrice = this.strategyExecutor.getWinPrice();
        
        Assert.assertNotNull(bestBid);
        Assert.assertNotNull(winner);
        Assert.assertNotNull(winPrice);
        Assert.assertEquals(winPrice, singleBid.getAmount());
        Assert.assertEquals(bestBid.getId(), singleBid.getId());
        Assert.assertEquals(winner.getId(), singleBid.getBidder().getId());
    }
    
}

package feri.mazgon.auction.core.domain;

import static feri.mazgon.auction.util.test.TestUtil.*;
import static org.fest.assertions.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import feri.mazgon.auction.core.mail.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/auction-context.xml", "/auction-hibernate.xml"})
public class MinPriceAuctionSpringTest {
	@Autowired
	private MailServiceStub mailService;
	
	@After
	public void resetMailService() {
		mailService.clear();
	}
	
	@Test
	public void setPositiveMinPrice() {
		Auction a = createLegitAuction();
		long minPrice = 200;
		a.setMinPrice(minPrice);
		assertThat(a.getMinPrice()).isEqualTo(minPrice);
	}
	
	@Test
	public void setNegativeMinPrice() {
		Auction a = createLegitAuction();
		a.setMinPrice(-1);
		assertThat(a.getMinPrice()).isEqualTo(0);
	}
	
	@Test
	public void bidOnAuctionLowerThanMinPrice() {
		Auction a = createLegitAuction();
		a.setMinPrice(600);
		a.saveAndStart();
		
		Bid b = createLegitBid(400);
		a.bid(b);
		
		assertThat(a.getCurrentPrice()).isEqualTo(b.getAmount());
		assertThat(a.isMinPriceReached()).isFalse();
	}
	
	@Test
	public void bidOnAuctionHigherThanMinPrice() {
		Auction a = createLegitAuction();
		
		long minPrice = 600;
		a.setMinPrice(minPrice);
		a.saveAndStart();
		
		long bidAmount = 640;
		Bid b = createLegitBid(bidAmount);
		a.bid(b);
		
		assertThat(a.getCurrentPrice()).isEqualTo(minPrice);
		assertThat(a.isMinPriceReached()).isTrue();
		assertThat(a.getHiddenBid()).isNotNull();
		assertThat(a.getHiddenBid().getAmount()).isEqualTo(bidAmount);
	}
	
	@Test
	public void bidOnAuctionCurrentPriceRightUnderMinPrice() {
		Auction a = createLegitAuction();
		
		long minPrice = 500;
		a.setMinPrice(minPrice);
		a.saveAndStart();
		
		Bid b1 = createLegitBid(488);
		a.bid(b1);
		
		Bid b2 = createLegitBid(600);
		a.bid(b2);
		
		assertThat(a.getCurrentPrice()).isEqualTo(588);
		assertThat(a.isMinPriceReached()).isTrue();
		assertThat(a.getHiddenBid()).isNotNull();
		assertThat(a.getHiddenBid().getAmount()).isEqualTo(600);
	}
	
	public void raiseOwnBidUnderMinPrice() {
		Auction a = createLegitAuction();

		long minPrice = 500;
		a.setMinPrice(minPrice);
		a.saveAndStart();
		
		Bid b1 = createLegitBid(100);
		a.bid(b1);
		
		Bid b2 = new Bid(b1);
		b2.setAmount(400);
		a.bid(b2);
		
		assertThat(a.getCurrentPrice()).isEqualTo(400);
	}
	
	@Test
	public void raiseOwnBidExceedMinPrice() {
		Auction a = createLegitAuction();

		long minPrice = 500;
		a.setMinPrice(minPrice);
		a.saveAndStart();
		
		long bid1Amount=499;
		long bid2Amount = 502;
		
		Bid b1 = createLegitBid(bid1Amount);
		Bid b2 = new Bid(b1);
		b2.setAmount(bid2Amount);
		
		a.bid(b1);
		a.bid(b2);
		
		assertThat(a.getCurrentPrice()).isEqualTo(minPrice);
		assertThat(a.getHiddenBid()).isNotNull();
		assertThat(a.getHiddenBid().getAmount()).isEqualTo(bid2Amount);
	}
	
	@Test
	public void sendTwoEmailsIfMinPriceNotReached() {
		Auction a = createLegitAuction();
		a.setMinPrice(1000);
		a.saveAndStart();
		
		Bid b = createLegitBid(500);
		a.bid(b);
		
		a.end();
		assertThat(mailService).hasSize(2);
	}
	
	@Test
	public void buyerEmailIfMinPriceNotReached() {
		Auction a = createLegitAuction();
		a.setMinPrice(1000);
		a.saveAndStart();
		
		Bid b = createLegitBid(500);
		a.bid(b);
		
		a.end();
		Email buyerEmail = mailService.findByRecipient(a.getLastBid().getBidder().getEmail());
		assertThat(buyerEmail).isNotNull().isInstanceOf(BuyerFailEmail.class);
	}
	
	@Test
	public void sellerEmailIfMinPriceNotReached() {
		Auction a = createLegitAuction();
		a.setMinPrice(1000);
		a.saveAndStart();
		
		Bid b = createLegitBid(500);
		a.bid(b);
		
		a.end();
		Email sellerEmail = mailService.findByRecipient(a.getSeller().getEmail());
		assertThat(sellerEmail).isNotNull().isInstanceOf(SellerFailEmail.class);
	}
}

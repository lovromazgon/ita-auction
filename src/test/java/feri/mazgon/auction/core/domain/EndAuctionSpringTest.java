package feri.mazgon.auction.core.domain;

import static org.junit.Assert.fail;
import static org.fest.assertions.Assertions.*;
import static feri.mazgon.auction.util.test.TestUtil.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import feri.mazgon.auction.core.mail.BuyerSuccessEmail;
import feri.mazgon.auction.core.mail.Email;
import feri.mazgon.auction.core.mail.MailServiceStub;
import feri.mazgon.auction.core.mail.SellerFailEmail;
import feri.mazgon.auction.core.mail.SellerSuccessEmail;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/auction-context.xml", "/auction-hibernate.xml"})
public class EndAuctionSpringTest {
	@Autowired
	private MailServiceStub mailService;
	
	@After
	public void resetMailService() {
		mailService.clear();
	}
	
	@Test
	public void auctionInactiveAfterEnd() {
		Auction a = createLegitAuction();
		a.saveAndStart();

		a.end();
		assertThat(a.isActive()).isFalse();
	}
	
	@Test
	public void cannotStartCompletedAuction() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		a.end();
		
		try {
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualToIgnoringCase(Auction.CANNOT_START_COMPLETED_AUCTION);
			assertThat(a.isCompleted()).isTrue();
		}
	}
	
	@Test
	public void cannotEndInactiveAuction() {
		Auction a = createLegitAuction();

		try {
			a.end();
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualToIgnoringCase(Auction.AUCTION_INACTIVE);
		}
	}
	
	@Test
	public void hiddenBidRemovedAfterEnd() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid b = createLegitBid(500);
		a.bid(b);
		
		a.end();
		assertThat(a.getHiddenBid()).isNull();
	}
	
	@Test
	public void sendTwoEmailsOnSuccessfulAuction() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid b = createLegitBid(500);
		a.bid(b);
		
		a.end();
		assertThat(mailService).hasSize(2);
	}
	
	@Test
	public void sendOneEmailOnFailAuction() {
		Auction a = createLegitAuction();
		a.saveAndStart();

		a.end();
		assertThat(mailService).hasSize(1);
	}
	
	@Test
	public void sellerEmailOnSuccessfulAuction() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid b = createLegitBid(500);
		a.bid(b);
		
		a.end();
		Email sellerEmail = mailService.findByRecipient(a.getSeller().getEmail());
		assertThat(sellerEmail).isNotNull().isInstanceOf(SellerSuccessEmail.class);
	}
	
	@Test
	public void buyerEmailOnSuccessfulAuction() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid b = createLegitBid(500);
		a.bid(b);
		
		a.end();
		Email buyerEmail = mailService.findByRecipient(a.getLastBid().getBidder().getEmail());
		assertThat(buyerEmail).isNotNull().isInstanceOf(BuyerSuccessEmail.class);
	}
	
	@Test
	public void sellerEmailOnFailAuction() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		a.end();
		Email sellerEmail = mailService.findByRecipient(a.getSeller().getEmail());
		assertThat(sellerEmail).isNotNull().isInstanceOf(SellerFailEmail.class);
	}
}
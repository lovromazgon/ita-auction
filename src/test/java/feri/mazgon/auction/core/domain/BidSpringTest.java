package feri.mazgon.auction.core.domain;

import static feri.mazgon.auction.util.test.TestUtil.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import feri.mazgon.auction.util.Util;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/auction-context.xml", "/auction-hibernate.xml"})
public class BidSpringTest {
	@Test
	public void bidIsHighestBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		Bid bid = createLegitBid(a.getCurrentPrice() + 100);
		
		assertThat(a.bid(bid)).isTrue();
		assertThat(a.getLastBid()).isEqualTo(bid);
		assertThat(bid.isExecuted()).isTrue();
	}
	
	@Test
	public void bidIsNotHighest() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid bid1 = createLegitBid(a.getCurrentPrice() + 400);
		Bid bid2 = createLegitBid(a.getCurrentPrice() + 300);
		
		a.bid(bid1);
		
		assertThat(a.bid(bid2)).isFalse();
		assertThat(a.getLastBid()).isNotEqualTo(bid2);
	}
	
	@Test
	public void currentPriceAfterBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		Bid bid = createLegitBid(a.getCurrentPrice() + 200);
		long previousPrice = a.getCurrentPrice();
		
		a.bid(bid);
		assertThat(a.getCurrentPrice()).isEqualTo(previousPrice + Auction.MIN_PRICE_RISE);
	}
	
	@Test
	public void setHiddenBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		long bidAmount = a.getCurrentPrice() + 200;
		Bid bid = createLegitBid(bidAmount);
		
		a.bid(bid);
		assertThat(a.getHiddenBid()).isNotNull();
		assertThat(a.getHiddenBid().getAmount()).isEqualTo(bidAmount);
		assertThat(a.getHiddenBid().isExecuted()).isFalse();
	}
	
	@Test
	public void executeHiddenBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		Bid bid1 = createLegitBid(a.getCurrentPrice() + 400);
		Bid bid2 = createLegitBid(a.getCurrentPrice() + 300);
		
		a.bid(bid1);
		
		Bid hiddenBid = a.getHiddenBid();
		a.bid(bid2);
		
		assertThat(hiddenBid.isExecuted()).isTrue();
	}
	
	@Test
	public void executeBidEqualToHiddenBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		long bidAmount = a.getCurrentPrice() + 300;
		Bid bid1 = createLegitBid(bidAmount);
		Bid bid2 = createLegitBid(bidAmount);
		
		a.bid(bid1);
		
		Bid hiddenBid = a.getHiddenBid();
		
		a.bid(bid2);
		
		assertThat(hiddenBid.isExecuted()).isTrue();
		assertThat(a.getHiddenBid()).isNull();
		assertThat(a.getLastBid().getBidder()).isEqualTo(bid1.getBidder());
	}
	
	@Test
	public void nullBidder() {
		Auction a = createLegitAuction();
		a.saveAndStart();

		long previousPrice = a.getCurrentPrice();
		Bid bid = new Bid(a.getCurrentPrice() + 100, null);
		
		try {
			a.bid(bid);
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Bid.BIDDER_NULL);
			assertThat(bid.isExecuted()).isFalse();
			assertThat(bid.getId()).isEqualTo(0l);
			assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		}
	}
	
	@Test
	public void bidderSameAsSeller() {
		Auction a = createLegitAuction();
		a.saveAndStart();

		long previousPrice = a.getCurrentPrice();
		Bid bid = new Bid(a.getCurrentPrice() + 100, a.getSeller());
		
		try {
			a.bid(bid);
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Bid.BIDDER_IS_SELLER);
			assertThat(bid.isExecuted()).isFalse();
			assertThat(bid.getId()).isEqualTo(0l);
			assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		}
	}
	
	@Test
	public void executeBidOnInactiveAuction() {
		Auction a = createLegitAuction();
		User user = createLegitUser(false);
		user.save();
		
		long previousPrice = a.getCurrentPrice();
		Bid bid = new Bid(a.getCurrentPrice() + 100, user);
		
		try {
			a.bid(bid);
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Auction.AUCTION_INACTIVE);
			assertThat(bid.isExecuted()).isFalse();
			assertThat(bid.getId()).isEqualTo(0l);
			assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		}
	}
	
	@Test
	public void executeNullBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();

		long previousPrice = a.getCurrentPrice();
		Bid bid = null;
		
		try {
			a.bid(bid);
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Auction.BID_NULL);
			assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		}
	}
	
	@Test
	public void executeBidAmountTooLow() {
		Auction a = createLegitAuction();
		User user = createLegitUser(false);
		a.saveAndStart();
		user.save();
		
		long previousPrice = a.getCurrentPrice();
		Bid bid = new Bid(a.getCurrentPrice() + 99, user);
		
		try {
			a.bid(bid);
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(String.format(Auction.BID_AMOUNT_TOO_LOW, Util.formatMoney(a.getCurrentPrice() + Auction.MIN_PRICE_RISE), Util.formatMoney(bid.getAmount())));
			assertThat(bid.isExecuted()).isFalse();
			assertThat(bid.getId()).isEqualTo(0l);
			assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		}
	}
	
	@Test
	public void executeSameBidTwice() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid bid = createLegitBid(a.getCurrentPrice() + 500);
		
		a.bid(bid);
		
		long previousPrice = a.getCurrentPrice();
		try {
			a.bid(bid);
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Bid.CANNOT_EXECUTE_BID_TWICE);
			assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		}
	}
	
	@Test
	public void raiseUnexistingHiddenBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		long bid1Amount = a.getCurrentPrice() + 100;
		long bid2Amount = a.getCurrentPrice() + 140;
		Bid bid1 = createLegitBid(bid1Amount);
		Bid bid2 = new Bid(bid1);
		bid2.setAmount(bid2Amount);
		
		a.bid(bid1);
		long previousPrice = a.getCurrentPrice();
		a.bid(bid2);
		
		assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		assertThat(a.getHiddenBid().getAmount()).isEqualTo(bid2Amount);
	}
	
	@Test
	public void raiseExistingHiddenBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		long bid1Amount = a.getCurrentPrice() + 200;
		long bid2Amount = a.getCurrentPrice() + 240;
		Bid bid1 = createLegitBid(bid1Amount);
		Bid bid2 = new Bid(bid1);
		bid2.setAmount(bid2Amount);
		
		a.bid(bid1);
		long previousPrice = a.getCurrentPrice();
		a.bid(bid2);
		
		assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
		assertThat(a.getHiddenBid().getAmount()).isEqualTo(bid2Amount);
	}
	
	@Test
	public void lowerHiddenBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		long bid1Amount = a.getCurrentPrice() + 400;
		long bid2Amount = a.getCurrentPrice() + 200;
		Bid bid1 = createLegitBid(bid1Amount);
		Bid bid2 = new Bid(bid1);
		bid2.setAmount(bid2Amount);
		
		a.bid(bid1);
	
		long previousPrice = a.getCurrentPrice();
		
		try {
			a.bid(bid2);
			fail(NO_EXCEPTION);
		}
		catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(String.format(Auction.BID_AMOUNT_TOO_LOW, Util.formatMoney(bid1Amount + 1), Util.formatMoney(bid2Amount)));
			assertThat(a.getCurrentPrice()).isEqualTo(previousPrice);
			assertThat(a.getHiddenBid().getAmount()).isEqualTo(bid1Amount);
			assertThat(bid2.getId()).isEqualTo(0l);
		}
	}
}

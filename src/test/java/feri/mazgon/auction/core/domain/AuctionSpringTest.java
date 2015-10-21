package feri.mazgon.auction.core.domain;

import static org.junit.Assert.fail;
import static org.fest.assertions.Assertions.*;
import static feri.mazgon.auction.util.test.TestUtil.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/auction-context.xml", "/auction-hibernate.xml"})
public class AuctionSpringTest {
	@Test
	public void startLegitAuction() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		assertThat(a.isActive()).isTrue();
	}
	
	@Test
	public void startSameAuctionTwice() {
		Auction a = createLegitAuction();
		
		a.saveAndStart();
		try {
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Auction.CANNOT_START_AUCTION_TWICE);
		}
	}
	
	@Test
	public void illegalDuration() {
		Auction a = createLegitAuction();
		
		try {
			a.setDaysToEnd(8);
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(String.format(Auction.ILLEGAL_DURATION, Auction.MIN_DURATION_DAYS, Auction.MAX_DURATION_DAYS, a.getDaysToEnd()));
		}
		
		try {
			a.setDaysToEnd(0);
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(String.format(Auction.ILLEGAL_DURATION, Auction.MIN_DURATION_DAYS, Auction.MAX_DURATION_DAYS, a.getDaysToEnd()));
		}
	}
	
	@Test
	public void illegalStartingPrice() {
		Auction a = createLegitAuction();
		
		try {
			a.setStartingPrice(-1);
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(String.format(Auction.ILLEGAL_STARTING_PRICE, a.getStartingPrice()));
		}
	}
	
	@Test
	public void nonPayingUserCreatesTooManyAuctions() {
		User u = createLegitUser(false);
		u.save();
		
		int i = 0;
		try {
			for (; i < 10; i++) {
				Auction a = createLegitAuction();
				a.setSeller(u);
				a.saveAndStart();
			}
			fail(NO_EXCEPTION);
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Auction.USER_HAS_MAX_AUCTIONS);
			assertThat(i).isEqualTo(5);
		}
	}
	
	@Test
	public void nullUserForAuction() {
		Auction a = createLegitAuction();
		
		try {
			a.setSeller(null);
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Auction.USER_NULL);
		}
	}
	
	@Test
	public void nullTitleForAuction() {
		Auction a = createLegitAuction();
		
		try {
			a.setTitle(null);
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Auction.TITLE_NULL);
		}
	}
	
	@Test
	public void tooShortTitleForAuction() {
		Auction a = createLegitAuction();
		
		try {
			a.setTitle("1234");
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(String.format(Auction.TOO_SHORT_TITLE, Auction.MIN_TITLE_LENGTH));
		}
	}
	
	@Test
	public void nullAuctionCategoryForAuction() {
		Auction a = createLegitAuction();
		
		try {
			a.setAuctionCategory(null);
			a.saveAndStart();
			fail(NO_EXCEPTION);
		} catch(IllegalStateException e) {
			assertThat(e.getMessage()).isEqualTo(Auction.AUCTION_CATEGORY_NULL);
		}
	}
}

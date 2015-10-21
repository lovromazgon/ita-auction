package feri.mazgon.auction.util.test;

import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.AuctionCategory;
import feri.mazgon.auction.core.domain.Bid;
import feri.mazgon.auction.core.domain.User;

public abstract class TestUtil {
	public static final String NO_EXCEPTION = "Test ni vrgel izjeme.";
	private static int userCount = 0;
	
	public static Auction createLegitAuction() {
		AuctionCategory ac = new AuctionCategory().find(1l);
		User u = new User().find(1l);
		Auction a = new Auction("Dražba TEST", "Opis dražbe", u, 0, 0, 5, null, ac);
		return a;
	}
	
	public static User createLegitUser(boolean paying) {
		userCount++;
		return new User("janez.novak" + userCount + "@gmail.com", "12345", "Janez", "Novak", paying);
	}
	
	public static Bid createLegitBid(long amount) {
		User u = createLegitUser(false);
		u.save();
		Bid b = new Bid(amount, u);
		return b;
	}
	
	public static Bid bidOnAuction(Auction a) {
		return bidOnAuction(a, a.getCurrentPrice() + Auction.MIN_PRICE_RISE);
	}
	
	public static Bid bidOnAuction(Auction a, long amount) {
		Bid b = createLegitBid(amount);
		a.bid(b);
		return b;
	}
}

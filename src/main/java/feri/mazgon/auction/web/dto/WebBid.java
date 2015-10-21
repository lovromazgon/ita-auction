package feri.mazgon.auction.web.dto;

import java.util.Date;

import feri.mazgon.auction.util.Util;
import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.Bid;
import feri.mazgon.auction.core.domain.User;
import feri.mazgon.auction.core.repository.MyRepository;

public class WebBid {
	private Bid bid;
	
	public WebBid() {
		this(new Bid());
	}
	public WebBid(Bid bid) {
		this.bid = bid;
	}

	// ---------------------------
	//      GETTERS & SETTERS
	// ---------------------------
	public Bid getBid() {
		return bid;
	}
	
	// ---------------------------
	//     ADDITIONAL METHODS
	// ---------------------------
	public String getTimeFormatted() {
		return Util.DATE_FORMAT.format(new Date(getTime()));
	}
	public String getAmountFormatted() {
		return Util.formatMoney(getAmount());
	}
	
	// ---------------------------
	//      DELEGATED METHODS
	// ---------------------------
	public long getId() {
		return bid.getId();
	}
	public void setId(long id) {
		bid.setId(id);
	}
	public MyRepository<Bid> getRepository() {
		return bid.getRepository();
	}
	public long getTime() {
		return bid.getTime();
	}
	public long getAmount() {
		return bid.getAmount();
	}
	public Auction getAuction() {
		return bid.getAuction();
	}
	public User getBidder() {
		return bid.getBidder();
	}
	public boolean isExecuted() {
		return bid.isExecuted();
	}
	public void setTime(long time) {
		bid.setTime(time);
	}
	public void setAmount(long amount) {
		bid.setAmount(amount);
	}
	public void setAuction(Auction auction) {
		bid.setAuction(auction);
	}
	public void setBidder(User bidder) {
		bid.setBidder(bidder);
	}
	public void setExecuted(boolean executed) {
		bid.setExecuted(executed);
	}
	public boolean isHigherThan(Bid b) {
		return bid.isHigherThan(b);
	}
}

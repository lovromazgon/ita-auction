package feri.mazgon.auction.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
public class Bid extends DBEntity<Bid> implements Comparable<Bid> {
	@Column
	private long time;
	@Column
	private long amount;
	@ManyToOne
	@JoinColumn(name = "auction_id", nullable = false)
	private Auction auction;
	@ManyToOne
	@JoinColumn(name = "bidder_id", nullable = false)
	private User bidder;
	@Column
	private boolean executed;

	// ---------------------------
	//        CONSTRUCTORS
	// ---------------------------
	public Bid() {
		this(0, null);
	}
	public Bid(long amount, User bidder) {
		this.amount = amount;
		this.bidder = bidder;
		
		auction = null;
		time = 0;
		executed = false;
	}
	public Bid(Bid existingBid) {
		this.amount = existingBid.amount;
		this.auction = existingBid.auction;
		this.bidder = existingBid.bidder;
		
		time = 0;
		executed = false;
	}
	
	// ---------------------------
	//           GETTERS
	// ---------------------------
	public long getTime() {
		return time;
	}
	public long getAmount() {
		return amount;
	}
	public Auction getAuction() {
		return auction;
	}
	public User getBidder() {
		return bidder;
	}
	public boolean isExecuted() {
		return executed;
	}
	
	// ---------------------------
	//           SETTERS
	// ---------------------------
	public void setTime(long time) {
		this.time = time;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public void setAuction(Auction auction) {
		this.auction = auction;
	}
	public void setBidder(User bidder) {
		this.bidder = bidder;
	}
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}
	
	// ---------------------------
	//           METHODS
	// ---------------------------
	void execute() {
		validateBeforeExecute();
		time = System.currentTimeMillis();
		executed = true;
		save();
	}

	void save() {
		repository.save(this);
	}
	
	public void validateBeforeExecute() {
		if (executed)
			throw new IllegalStateException(CANNOT_EXECUTE_BID_TWICE);
		if (bidder == null)
			throw new IllegalStateException(BIDDER_NULL);
		if (bidder.getId() == auction.getSeller().getId())
			throw new IllegalStateException(BIDDER_IS_SELLER);
		if (auction == null)
			throw new IllegalStateException(AUCTION_NULL);
	}
	
	public boolean isHigherThan(Bid b) {
		return amount > b.getAmount();
	}
	
	@Override
	public int compareTo(Bid o) {
		int result = Long.compare(time, o.getTime());
		if (result == 0)
			result = Long.compare(amount, o.getAmount());
		return result;
	}
	
	// ---------------------------
	//     EXCEPTION MESSAGES
	// ---------------------------
	public static final String CANNOT_EXECUTE_BID_TWICE = "Ta bid je že izveden - bid se lahko izvede le enkrat!";
	public static final String BIDDER_NULL = "Bidder ni določen (null)!";
	public static final String AUCTION_NULL = "Dražba ni določena (null)!";
	public static final String BIDDER_IS_SELLER = "Prodajalec ne more oddati ponudbe za svoj izdelek!";
}
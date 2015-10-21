package feri.mazgon.auction.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.TaskScheduler;

import feri.mazgon.auction.core.mail.BuyerFailEmail;
import feri.mazgon.auction.core.mail.BuyerSuccessEmail;
import feri.mazgon.auction.core.mail.SellerFailEmail;
import feri.mazgon.auction.core.mail.SellerSuccessEmail;
import feri.mazgon.auction.util.Util;

@Entity
@Configurable
public class Auction extends DBEntity<Auction> {
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private long startingPrice;
	@Column
	private long minPrice;
	@Column(nullable = false)
	private long currentPrice;
	@Column(nullable = false)
	private long startTime;
	@Column(nullable = false)
	private boolean completed;
	@ManyToOne
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;
	@OneToMany(mappedBy = "auction", fetch = FetchType.EAGER)
	private List<Bid> bidHistory;
	
	@Column
	private String description;
	@Column(nullable = false)
	private long endTime;
	@Column(length = 100000)
	@Lob
	private byte[] picture;
	@ManyToOne
	@JoinColumn(name = "auction_category_id", nullable = false)
	private AuctionCategory auctionCategory;
	@OneToOne
	@JoinColumn(name = "hidden_bid_id")
	private Bid hiddenBid;
	
	@Transient
	private int daysToEnd;
	
	@Transient
	@Autowired
	protected TaskScheduler taskScheduler;
	
	// ---------------------------
	//        CONSTRUCTORS
	// ---------------------------
	public Auction() {
		this(null, null, null, 0, 0, 0, null, null);
	}
	public Auction(String title, String description, User seller, long startingPrice, long minPrice, int daysToEnd, byte[] picture, AuctionCategory auctionCategory) {
		bidHistory = new ArrayList<Bid>();
		this.completed = false;
		this.title = title;
		this.description = description;
		this.seller = seller;
		this.startingPrice = startingPrice;
		this.picture = picture;
		this.auctionCategory = auctionCategory;
		this.daysToEnd = daysToEnd;
		this.minPrice = minPrice;
	}

	// ---------------------------
	//           GETTERS
	// ---------------------------
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public long getStartingPrice() {
		return startingPrice;
	}
	public long getCurrentPrice() {
		return currentPrice;
	}
	public long getEndTime() {
		return endTime;
	}
	public byte[] getPicture() {
		return picture;
	}
	public AuctionCategory getAuctionCategory() {
		return auctionCategory;
	}
	public User getSeller() {
		return seller;
	}
	public int getDaysToEnd() {
		return daysToEnd;
	}
	public long getStartTime() {
		return startTime;
	}
	public Bid getHiddenBid() {
		return hiddenBid;
	}
	public boolean isCompleted() {
		return completed;
	}
	public long getMinPrice() {
		return minPrice;
	}
	
	// ---------------------------
	//           SETTERS
	// ---------------------------
	public void setDescription(String description) {
		this.description = description;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public void setPicture(byte[] picture) {
		this.picture = picture;
	}
	public void setAuctionCategory(AuctionCategory auctionCategory) {
		this.auctionCategory = auctionCategory;
	}
	public void setSeller(User seller) {
		this.seller = seller;
	}
	public void setDaysToEnd(int daysToEnd) {
		this.daysToEnd = daysToEnd;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setStartingPrice(long startingPrice) {
		this.startingPrice = startingPrice;
	}
	public void setHiddenBid(Bid hiddenBid) {
		this.hiddenBid = hiddenBid;
	}
	public void setMinPrice(long minPrice) {
		if (minPrice > 0)
			this.minPrice = minPrice;
		else
			this.minPrice = 0;
	}
	
	// ---------------------------
	//          CONSTANTS
	// ---------------------------
	public static final int MIN_DURATION_DAYS = 1;
	public static final int MAX_DURATION_DAYS = 7;
	public static final int MIN_TITLE_LENGTH = 5;
	public static final long MIN_PRICE_RISE = 100;
	
	// ---------------------------
	//           METHODS
	// ---------------------------
	public void saveAndStart() throws IllegalStateException {
		if (completed)
			throw new IllegalStateException(CANNOT_START_COMPLETED_AUCTION);
		if (isActive())
			throw new IllegalStateException(CANNOT_START_AUCTION_TWICE);
		if (endTime == 0 && (daysToEnd < MIN_DURATION_DAYS || daysToEnd > MAX_DURATION_DAYS))
			throw new IllegalStateException(String.format(ILLEGAL_DURATION, MIN_DURATION_DAYS, MAX_DURATION_DAYS, daysToEnd));
		if (startingPrice < 0)
			throw new IllegalStateException(String.format(ILLEGAL_STARTING_PRICE, startingPrice));
		if (seller == null)
			throw new IllegalStateException(USER_NULL);
		if (!seller.canCreateAuction())
			throw new IllegalStateException(USER_HAS_MAX_AUCTIONS);
		if (title == null)
			throw new IllegalStateException(TITLE_NULL);
		if (title.length() < MIN_TITLE_LENGTH)
			throw new IllegalStateException(String.format(TOO_SHORT_TITLE, MIN_TITLE_LENGTH));
		if (auctionCategory == null)
			throw new IllegalStateException(AUCTION_CATEGORY_NULL);
		
		startTime = System.currentTimeMillis();
		if (endTime == 0)
			endTime = startTime + (daysToEnd * 24 * 60 * 60 * 1000);
		repository.save(this);
		currentPrice = startingPrice;
		
		taskScheduler.schedule(new EndAuctionTask(id), new Date(endTime));
	}
	
	public boolean isActive() {
		boolean active = false;
		if (id > 0) {
			active = getEndTime() > System.currentTimeMillis();
		}
		
		return active;
	}
	
	public boolean bid(Bid bid) {
		if (!isActive())
			throw new IllegalStateException(AUCTION_INACTIVE);
		if (bid == null)
			throw new IllegalStateException(BID_NULL);
		
		try {
			bid.setAuction(this);
			bid.validateBeforeExecute();
		} catch (IllegalStateException e) {
			bid.setAuction(null);
			throw e;
		}
		
		BidStrategy strategy = selectBidStrategy(bid);
		Bid highestBid = strategy.executeBidStrategy(bid);
		
		repository.save(this);
		
		return bid == highestBid;
	}
	
	public List<Bid> getBidHistory() {
		refreshBidHistory();
		
		List<Bid> bidHistoryCopy = new ArrayList<Bid>(bidHistory);
		if (hiddenBid != null)
			bidHistoryCopy.remove(hiddenBid);
		
		return bidHistoryCopy;
	}
	
	public Bid getLastBid() {
		if (getBidHistory().size() == 0)
			return null;
		
		refreshBidHistory();
		return bidHistory.get(0);
	}
	
	public void end() {
		if (!isActive())
			throw new IllegalStateException(AUCTION_INACTIVE);
		
		completed = true;
		endTime = System.currentTimeMillis();
		
		Bid hiddenBidTemp = hiddenBid;
		hiddenBid = null;
		
		repository.save(this);
		if (hiddenBidTemp != null)
			hiddenBidTemp.remove(hiddenBidTemp);
		
		try {
			if (getLastBid() != null && isMinPriceReached()) {
				new SellerSuccessEmail(this).send();
				new BuyerSuccessEmail(this).send();
			}
			else {
				new SellerFailEmail(this).send();
				if (getLastBid() != null)
					new BuyerFailEmail(this).send();
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isMinPriceReached() {
		return currentPrice >= minPrice;
	}
	
	private void execute(Bid bid) {
		currentPrice = bid.getAmount();
		bid.execute();
		bidHistory.add(0, bid);
	}
	
	private void refreshBidHistory() {
		Collections.sort(bidHistory);
		Collections.reverse(bidHistory);
	}
	
	private BidStrategy selectBidStrategy(Bid bid) {
		BidStrategy bidStrategy = null;
		Bid lastBid = getLastBid();
		
		if (lastBid != null && lastBid.getBidder().isSameAs(bid.getBidder()))
			bidStrategy = new RaiseOwnBidStrategy();
		else if (bid.getAmount() < currentPrice + MIN_PRICE_RISE)
			throw new IllegalStateException(String.format(BID_AMOUNT_TOO_LOW, Util.formatMoney(currentPrice + MIN_PRICE_RISE), Util.formatMoney(bid.getAmount())));
		else if (isMinPriceReached())
			bidStrategy = new MinPriceReachedStrategy();
		else if (!isMinPriceReached())
			bidStrategy = new MinPriceNotReachedStrategy();
		
		return bidStrategy;
	}
	
	// ---------------------------
	//     EXCEPTION MESSAGES
	// ---------------------------
	public static final String CANNOT_START_COMPLETED_AUCTION = "Ta dražba je že končana in ne more biti ponovno zagnana!";
	public static final String CANNOT_START_AUCTION_TWICE = "Ta dražba že obstaja - dražba se lahko zažene le enkrat!";
	public static final String ILLEGAL_DURATION = "Trajanje dražbe mora biti med %s in %s dnevi! Podana dražba traja %s dni.";
	public static final String ILLEGAL_STARTING_PRICE = "Začetna cena ne sme biti negativna! Podana dražba ima začetno ceno %s.";
	public static final String USER_NULL = "Lastnik dražbe ni določen (null)!";
	public static final String TITLE_NULL = "Naslov dražbe ni določen (null)!";
	public static final String AUCTION_CATEGORY_NULL = "Kategorija dražbe ni določena (null)!";
	public static final String USER_HAS_MAX_AUCTIONS = "Uporabnik že ima maksimalno število aktivnih dražb!";
	public static final String TOO_SHORT_TITLE = "Naslov dražbe mora vsebovati vsaj %s znakov!";
	
	public static final String AUCTION_INACTIVE = "Dražba ni aktivna!";
	public static final String BID_NULL = "Ponudba ni določena (null)!";
	public static final String BID_AMOUNT_TOO_LOW = "Ponudba je prenizka! Pričakovana ponudba je vsaj %s €, prejeta ponudba je enaka %s €.";
	
	// ---------------------------
	//        BID STRATEGY
	// ---------------------------	
	private static interface BidStrategy {
		public Bid executeBidStrategy(Bid bid);
	}
	
	private class RaiseOwnBidStrategy implements BidStrategy {
		@Override
		public Bid executeBidStrategy(Bid bid) {
			long minAmount = currentPrice + 1;
			if (hiddenBid != null)
				minAmount = hiddenBid.getAmount() + 1;
			
			if (bid.getAmount() < minAmount)
				throw new IllegalStateException(String.format(BID_AMOUNT_TOO_LOW, Util.formatMoney(minAmount), Util.formatMoney(bid.getAmount())));
			
			if (isMinPriceReached()) {
				if (hiddenBid == null)
					hiddenBid = bid;
				
				hiddenBid.setAmount(bid.getAmount());
				hiddenBid.save();
			}
			else {
				if (bid.getAmount() > minPrice) {
					hiddenBid = new Bid(bid);
					hiddenBid.save();
					bid.setAmount(minPrice);
				}
				execute(bid);
			}
			
			return bid;
		}
	}
	
	private class MinPriceReachedStrategy implements BidStrategy {
		@Override
		public Bid executeBidStrategy(Bid bid) {
			Bid highestBid = bid;
			
			if (hiddenBid != null) {
				Bid lowerBid = hiddenBid;
				
				if (!bid.isHigherThan(hiddenBid)) {
					lowerBid = bid;
					highestBid = hiddenBid;
				}
				
				execute(lowerBid);
				hiddenBid = null;
			}
			
			if (highestBid.getAmount() > currentPrice + MIN_PRICE_RISE) {
				hiddenBid = new Bid(highestBid);
				hiddenBid.save();
				highestBid.setAmount(currentPrice + MIN_PRICE_RISE);
			}
			
			execute(highestBid);
			return highestBid;
		}
	}
	
	private class MinPriceNotReachedStrategy implements BidStrategy {
		@Override
		public Bid executeBidStrategy(Bid bid) {
			if (bid.getAmount() > minPrice) {
				hiddenBid = new Bid(bid);
				hiddenBid.save();
				
				if (bid.getAmount() > currentPrice + MIN_PRICE_RISE && currentPrice + MIN_PRICE_RISE > minPrice)
					bid.setAmount(currentPrice + MIN_PRICE_RISE);
				else
					bid.setAmount(minPrice);
			}
			
			execute(bid);
			return bid;
		}
	}
}

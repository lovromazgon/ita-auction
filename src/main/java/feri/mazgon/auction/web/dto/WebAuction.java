package feri.mazgon.auction.web.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.AuctionCategory;
import feri.mazgon.auction.core.domain.Bid;
import feri.mazgon.auction.core.domain.User;
import feri.mazgon.auction.core.repository.MyRepository;
import feri.mazgon.auction.util.Util;

public class WebAuction {
	private Auction auction;
	
	private String startingPriceString;
	private String minPriceString;
	private MultipartFile fileData;
	private long categoryId;
	
	public WebAuction() {
		this(new Auction());
	}
	public WebAuction(Auction auction) {
		this.auction = auction;
		startingPriceString = Util.formatMoney(auction.getStartingPrice());
		minPriceString = Util.formatMoney(auction.getMinPrice());
	}
	
	// ---------------------------
	//      GETTERS & SETTERS
	// ---------------------------

	public MultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(MultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getStartingPriceString() {
		return startingPriceString;
	}
	public void setStartingPriceString(String startingPriceString) {
		this.startingPriceString = startingPriceString;
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public String getMinPriceString() {
		return minPriceString;
	}
	public void setMinPriceString(String minPriceString) {
		this.minPriceString = minPriceString;
	}
	public Auction getAuction() {
		return auction;
	}
	
	// ---------------------------
	//     ADDITIONAL METHODS
	// ---------------------------
	public String getSelfUrl() {
		return Util.APP_ROOT + "/auction/" + getId();
	}
	public String getPictureUrl() {
		return getSelfUrl() + "/picture";
	}
	public String getStartTimeFormatted() {
		return Util.DATE_FORMAT.format(new Date(getStartTime()));
	}
	public String getEndTimeFormatted() {
		return Util.DATE_FORMAT.format(new Date(getEndTime()));
	}
	public String getStartingPriceFormatted() {
		return Util.formatMoney(getStartingPrice());
	}
	public String getCurrentPriceFormatted() {
		return Util.formatMoney(getCurrentPrice());
	}
	public String getTimeToEndString() {
		long endTime = getEndTime();
		if (System.currentTimeMillis() > getEndTime())
			return "Dražba je zakljuèena";
		
		long diff = endTime - System.currentTimeMillis();
		
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
		StringBuilder sb = new StringBuilder();
		if (diffDays > 0) {
			sb.append(diffDays);
			sb.append(" ");
			sb.append(Util.formatDan(diffDays));
			sb.append(" ");
		}
		if (diffDays > 0 || diffHours > 0) {
			sb.append(diffHours);
			sb.append(" ");
			sb.append(Util.formatUra(diffHours));
			sb.append(" ");
		}
		sb.append(diffMinutes);
		sb.append(" ");
		sb.append(Util.formatMinuta(diffMinutes));
		
		return sb.toString();
	}
	
	public void validate() throws IOException {
		if (fileData.getSize() < 1)
			throw new IllegalStateException("Niste naložili slike!");
		auction.setPicture(fileData.getBytes());
		
		Long startingPrice = Util.stringToLong(startingPriceString);
		if (startingPrice == null)
			throw new IllegalStateException("Vnesli ste neveljavno zaèetno ceno!");
		auction.setStartingPrice(startingPrice);
		
		Long minPrice = Util.stringToLong(minPriceString);
		if (minPrice == null)
			throw new IllegalStateException("Vnesli ste neveljavno minimalno ceno!");
		auction.setMinPrice(minPrice);
	}
	
	// ---------------------------
	//      DELEGATED METHODS
	// ---------------------------
	public long getId() {
		return auction.getId();
	}
	public MyRepository<Auction> getRepository() {
		return auction.getRepository();
	}
	public String getTitle() {
		return auction.getTitle();
	}
	public String getDescription() {
		return auction.getDescription();
	}
	public long getStartingPrice() {
		return auction.getStartingPrice();
	}
	public long getCurrentPrice() {
		return auction.getCurrentPrice();
	}
	public long getEndTime() {
		return auction.getEndTime();
	}
	public byte[] getPicture() {
		return auction.getPicture();
	}
	public AuctionCategory getAuctionCategory() {
		return auction.getAuctionCategory();
	}
	public User getSeller() {
		return auction.getSeller();
	}
	public int getDaysToEnd() {
		return auction.getDaysToEnd();
	}
	public long getStartTime() {
		return auction.getStartTime();
	}
	public WebBid getHiddenBid() {
		if (auction.getHiddenBid() == null)
			return null;
		return new WebBid(auction.getHiddenBid());
	}
	public long getMinPrice() {
		return auction.getMinPrice();
	}
	public List<WebBid> getBidHistory() {
		List<WebBid> bids = new ArrayList<WebBid>();
		for (Bid b : auction.getBidHistory()) {
			bids.add(new WebBid(b));
		}
		return bids;
	}
	public Bid getLastBid() {
		return auction.getLastBid();
	}
	public boolean isCompleted() {
		return auction.isCompleted();
	}
	public boolean isActive() {
		return auction.isActive();
	}
	public boolean isMinPriceReached() {
		return auction.isMinPriceReached();
	}
	
	public void setId(long id) {
		auction.setId(id);
	}
	public void setDescription(String description) {
		auction.setDescription(description);
	}
	public void setEndTime(long endTime) {
		auction.setEndTime(endTime);
	}
	public void setPicture(byte[] picture) {
		auction.setPicture(picture);
	}
	public void setAuctionCategory(AuctionCategory auctionCategory) {
		auction.setAuctionCategory(auctionCategory);
	}
	public void setSeller(User seller) {
		auction.setSeller(seller);
	}
	public void setDaysToEnd(int daysToEnd) {
		auction.setDaysToEnd(daysToEnd);
	}
	public void setStartTime(long startTime) {
		auction.setStartTime(startTime);
	}
	public void setTitle(String title) {
		auction.setTitle(title);
	}
	public void setStartingPrice(long startingPrice) {
		auction.setStartingPrice(startingPrice);
	}
	public void setHiddenBid(Bid hiddenBid) {
		auction.setHiddenBid(hiddenBid);
	}
	public void setMinPrice(long minPrice) {
		auction.setMinPrice(minPrice);
	}
}
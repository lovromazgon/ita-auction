package feri.mazgon.auction.core.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import feri.mazgon.auction.core.AuctionService;

@Configurable
public class EndAuctionTask implements Runnable {
	private long id;
	@Autowired
	private AuctionService auctionService;
	
	public EndAuctionTask(long id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		Auction a = auctionService.getAuction(id);
		auctionService.endAuction(a);
	}
}

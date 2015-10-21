package feri.mazgon.auction.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.search.Search;

import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.AuctionCategory;
import feri.mazgon.auction.core.domain.Bid;
import feri.mazgon.auction.core.domain.User;
import feri.mazgon.auction.core.repository.*;

@Service
@Transactional
public class AuctionService {
	@Autowired
	private AuctionCategoryRepository auctionCategoryRepository;
	@Autowired
	private AuctionRepository auctionRepository;
	@Autowired
	private UserRepository userRepository;

	public Auction getAuction(long id) {
		return auctionRepository.find(id);
	}

	public List<Auction> getAuctions(AuctionCategory auctionCategory) {
		Search search = new Search(Auction.class);
		
		if (auctionCategory != null)
			search.addFilterEqual("auctionCategory.id", auctionCategory.getId());
		
		search.addFilterGreaterThan("endTime", System.currentTimeMillis());
		search.addSort("endTime", false);
		
		return auctionRepository.search(search);
	}

	public List<AuctionCategory> getAuctionCategories() {
		return auctionCategoryRepository.findAll();
	}
	
	public AuctionCategory getAuctionCategory(long id) {
		return auctionCategoryRepository.find(id);
	}
	
	public void addAuction(Auction a) {
		a.saveAndStart();
	}
	
	public User getUser(String email) {
		Search search = new Search(User.class);
		
		search.addFilterEqual("email", email);
		
		return userRepository.searchUnique(search);
	}
	
	public void endAuction(Auction a) {
		a.end();
	}
	
	public synchronized Bid bid(User bidder, Auction auction, long amount) {
		Bid bid = new Bid(amount, bidder);
		auction.bid(bid);
		return bid;
	}
}

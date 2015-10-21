package feri.mazgon.auction.core.domain;

import static feri.mazgon.auction.util.test.TestUtil.*;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/auction-context.xml", "/auction-hibernate.xml"})
public class BidHistorySpringTest {
	@Test
	public void getHistoryWithoutHiddenBids() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		List<Bid> bids = new ArrayList<Bid>();
		for (int i = 0; i<10; i++) {
			bids.add(bidOnAuction(a));
		}

		List<Bid> bidHistory = a.getBidHistory();
		assertThat(bidHistory).hasSize(10);
		assertThat(bidHistory).contains(bids.toArray());
	}
	
	@Test
	public void getHistoryWithHiddenBids() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		List<Bid> bids = new ArrayList<Bid>();
		for (int i = 0; i<10; i++) {
			if (a.getHiddenBid() != null) {
				bids.add(a.getHiddenBid());
				bids.add(bidOnAuction(a, a.getHiddenBid().getAmount() + 200));
			}
			else
				bidOnAuction(a, a.getCurrentPrice() + 200);
			
			Bid bid2 = new Bid(a.getHiddenBid());
			bid2.setAmount(a.getHiddenBid().getAmount() + 50);
			a.bid(bid2);
		}

		List<Bid> bidHistory = a.getBidHistory();
		
		assertThat(bidHistory).hasSize(19);
		assertThat(bidHistory).contains(bids.toArray());
	}
	
	@Test
	public void historySequenceWithHiddenBids() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		for (int i = 0; i<10; i++) {
			if (a.getHiddenBid() != null)
				bidOnAuction(a, a.getHiddenBid().getAmount() + 200);
			else
				bidOnAuction(a, a.getCurrentPrice() + 200);
			
			Bid bid2 = new Bid(a.getHiddenBid());
			bid2.setAmount(a.getHiddenBid().getAmount() + 50);
			a.bid(bid2);
		}
		
		long previousAmount = Long.MAX_VALUE;
		long previousTime = Long.MAX_VALUE;
		for (Bid bid : a.getBidHistory()) {
			assertThat(bid.getAmount()).isLessThanOrEqualTo(previousAmount);
			assertThat(bid.getTime()).isLessThanOrEqualTo(previousTime);
			previousAmount = bid.getAmount();
			previousTime = bid.getTime();
		}
	}
	
	@Test
	public void historySequenceWithoutHiddenBids() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		for (int i = 0; i<10; i++) {
			bidOnAuction(a);
		}

		long previousAmount = Long.MAX_VALUE;
		long previousTime = Long.MAX_VALUE;
		for (Bid bid : a.getBidHistory()) {
			assertThat(bid.getAmount()).isLessThan(previousAmount);
			assertThat(bid.getTime()).isLessThan(previousTime);
			previousAmount = bid.getAmount();
			previousTime = bid.getTime();
		}
	}
	
	@Test
	public void hiddenBidExcludedFromBidHistory() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid b = bidOnAuction(a, 200);
		Bid hidden = a.getHiddenBid();
		
		List<Bid> bidHistory = a.getBidHistory();
		assertThat(bidHistory).contains(b);
		assertThat(bidHistory).excludes(hidden);
	}
	
	@Test
	public void hiddenBidInsertToBidHistory() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid b1 = bidOnAuction(a, 200);
		Bid hidden = a.getHiddenBid();
		
		Bid b2 = bidOnAuction(a, 400);
		
		List<Bid> bidHistory = a.getBidHistory();
		assertThat(bidHistory).contains(b1);
		assertThat(bidHistory).contains(hidden);
		assertThat(bidHistory).contains(b2);
	}
	
	@Test
	public void hiddenBidRaiseNotInBidHistory() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		Bid bid1 = createLegitBid(200);
		Bid bid2 = new Bid(bid1);
		bid2.setAmount(240);
		
		a.bid(bid1);
		a.bid(bid2);
		
		assertThat(a.getBidHistory()).hasSize(1);
	}
	
	@Test
	public void firstBidInBidHistoryIsLastBid() {
		Auction a = createLegitAuction();
		a.saveAndStart();
		
		List<Bid> bids = new ArrayList<Bid>();
		for (int i = 0; i<10; i++) {
			bids.add(bidOnAuction(a));
		}

		List<Bid> bidHistory = a.getBidHistory();
		assertThat(bidHistory.get(0)).isEqualTo(a.getLastBid());
	}
}
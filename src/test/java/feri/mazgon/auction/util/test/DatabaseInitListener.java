package feri.mazgon.auction.util.test;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.AuctionCategory;
import feri.mazgon.auction.core.domain.User;

@Component
@Transactional
public class DatabaseInitListener implements ApplicationListener<ContextRefreshedEvent> {
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		User[] users = new User[] {
				new User("janez.novak@gmail.com", "12345", "Janez", "Novak", false),
				new User("micka.standeker@gmail.com", "12345", "Micka",	"Štandeker", true) };
		AuctionCategory[] categories = new AuctionCategory[] {
				new AuctionCategory("Dom"), new AuctionCategory("Tehnika") };
		Auction[] auctions = new Auction[] {
				new Auction("Dražba TEST", "Opis dražbe", users[0], 0, 0, 5, null, categories[0])};
		
		for (User u : users)
			u.save();
		for (AuctionCategory ac : categories)
			ac.save();
		for (Auction a : auctions)
			a.saveAndStart();
	}
}
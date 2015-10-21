package feri.mazgon.auction.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.AuctionCategory;
import feri.mazgon.auction.core.domain.User;
import feri.mazgon.auction.util.Util;

@Transactional
@Service
public class InitService implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(InitService.class);
	@Value("${db.init}")
	private Boolean DB_INIT;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("---- INIT ----");
		logger.info("DB - " + DB_INIT);
		
		if (DB_INIT)
			databaseInit();
		startDatabaseAuctions();
	}
	
	private void startDatabaseAuctions() {
		for (Auction a : new Auction().findAll()) {
			try {
				a.saveAndStart();
			} catch (IllegalStateException e) {}
		}
	}
	
	private void databaseInit() {
		AuctionCategory ac = new AuctionCategory();
		User u = new User();
		Auction a = new Auction();
		
		List<AuctionCategory> categories = ac.findAll();
		List<User> users = u.findAll();
		List<Auction> auctions = a.findAll();
		
		if (categories.size() < 1) {
			logger.info("Create auction categories");
			
			categories.add(new AuctionCategory("Tehnologija"));
			categories.add(new AuctionCategory("Knjige"));
			categories.add(new AuctionCategory("Oblaèila in obutev"));
			
			for (AuctionCategory i : categories) {
				i.save();
			}
		}
		if (users.size() < 1) {
			logger.info("Create users");
			
			users.add(new User("lovro.mazgon@gmail.com", "admin", "Lovro", "Mažgon", true));
			users.add(new User("lovro.mazgon@student.um.si", "admin", "Student", "UM", false));

			for (User i : users) {
				i.save();
			}
		}
		if (auctions.size() < 1) {
			logger.info("Create auctions");
			
			String[] picturePaths = {"/WEB-INF/initPictures/iphone4s.png", "/WEB-INF/initPictures/jobs.jpg", "/WEB-INF/initPictures/armani.jpeg"};
			
			auctions.add(new Auction("Apple iPhone 4S", "Apple iPhone 4S v zelo dobrem stanju, star dobro leto.", users.get(0), 12000, 0, 5, Util.readPicture(picturePaths[0]), categories.get(0)));
			auctions.add(new Auction("Walter Isaacson: Steve Jobs", "Knjiga je kot nova. Ima mehke platnice, kupljena 3.7.2013.", users.get(0), 700, 0, 5, Util.readPicture(picturePaths[1]), categories.get(1)));
			auctions.add(new Auction("Usnjena jakna (èrna) - Emporio Armani NOVA", "Jakna ni bila nikoli nošena, velikost je M, moška usnjena jakna. Je zelo kvalitetne izdelave, original Emporio Armani.", users.get(1), 5000, 0, 5, Util.readPicture(picturePaths[2]), categories.get(2)));
			
			for (Auction i : auctions) {
				i.saveAndStart();
			}
		}
	}
}

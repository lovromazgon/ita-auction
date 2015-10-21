package feri.mazgon.auction.core.mail;

import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.core.domain.User;
import feri.mazgon.auction.util.Util;

public class BuyerSuccessEmail extends Email {
	public static final String EMAIL_TEMPLATE_PATH = "/WEB-INF/mailTemplates/endAuctionSuccess.html";
	public static final String EMAIL_SUBJECT = "Zmagali ste na dražbi za %auction_title%!";
	public static final String USER_INFO_TEXT = "PODATKI O PRODAJALCU";
	
	public BuyerSuccessEmail(Auction a) {
		super(a);
	}
	
	@Override
	public String getSubject() {
		return EMAIL_SUBJECT.replaceAll("%auction_title%", auction.getTitle());
	}
	
	@Override
	public String getRecipientAddress() {
		return auction.getLastBid().getBidder().getEmail();
	}
	
	@Override
	public String getEmailBody() {
		String mailContent = Util.readFile(EMAIL_TEMPLATE_PATH);
		
		User user = auction.getSeller();
		
		mailContent = mailContent.replaceAll("%auction_title%", auction.getTitle());
		mailContent = mailContent.replaceAll("%auction_currentprice%", Util.formatMoney(auction.getCurrentPrice()));
		mailContent = mailContent.replaceAll("%user_infotext%", USER_INFO_TEXT);
		mailContent = mailContent.replaceAll("%user_firstname%", user.getFirstName());
		mailContent = mailContent.replaceAll("%user_lastname%", user.getLastName());
		mailContent = mailContent.replaceAll("%user_email%", user.getEmail());
		
		return mailContent;
	}
}

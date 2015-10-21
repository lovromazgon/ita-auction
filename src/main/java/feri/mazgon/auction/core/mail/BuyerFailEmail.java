package feri.mazgon.auction.core.mail;

import feri.mazgon.auction.core.domain.Auction;
import feri.mazgon.auction.util.Util;

public class BuyerFailEmail extends Email {
	public static final String EMAIL_TEMPLATE_PATH = "/WEB-INF/mailTemplates/endAuctionFail.html";
	public static final String EMAIL_SUBJECT = "Dražba za %auction_title% se je žal zaključila neuspešno :(";
	public static final String MIN_PRICE_NOT_REACHED = "Minimalna cena %min_price% € ni bila dosežena.";
	
	public BuyerFailEmail(Auction a) {
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
		
		mailContent = mailContent.replaceAll("%auction_title%", auction.getTitle());
		mailContent = mailContent.replaceAll("%auction_currentprice%", Util.formatMoney(auction.getCurrentPrice()));
		mailContent = mailContent.replaceAll("%fail_reason%", MIN_PRICE_NOT_REACHED.replaceAll("%min_price%", Util.formatMoney(auction.getMinPrice())));
		
		return mailContent;
	}
}

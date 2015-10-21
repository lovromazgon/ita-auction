package feri.mazgon.auction.core.mail;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import feri.mazgon.auction.core.domain.Auction;

@Configurable
public abstract class Email {
	@Autowired
	private MailService mailService;
	protected Auction auction;
	
	public Email(Auction a) {
		this.auction = a;
	}
	
	public Auction getAuction() {
		return auction;
	}
	
	public final void send() throws MessagingException {
		mailService.send(this);
	}
	
	public abstract String getSubject();
	public abstract String getRecipientAddress();
	public abstract String getEmailBody();
}

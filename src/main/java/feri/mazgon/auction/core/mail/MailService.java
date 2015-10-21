package feri.mazgon.auction.core.mail;

import javax.mail.MessagingException;

public interface MailService {
	public void send(Email email) throws MessagingException;
}

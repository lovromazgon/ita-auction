package feri.mazgon.auction.core.mail;

import java.util.ArrayList;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class MailServiceStub extends ArrayList<Email> implements MailService {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void send(Email email) {
		add(email);
	}
	
	public Email findByRecipient(String email) {
		Email result = null;
		for (Email e : this) {
			if (e.getRecipientAddress().equals(email)) {
				result = e;
				break;
			}
		}
		return result;
	}
}

package feri.mazgon.auction.core.mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.stereotype.Component;

@Component
public class MailServiceImpl implements MailService {
	public static String GMAIL_ADDRESS = "ferikeed@gmail.com";
	public static String GMAIL_PASSWORD = "password";

	@Override
	public void send(Email email) throws MessagingException {
		String host = "smtp.gmail.com";
		String port = "465";
		String starttls = "true";
		String auth = "true";
		boolean debug = true;
		String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
		String fallback = "false";

		Properties props = new Properties();

		props.put("mail.smtp.user", GMAIL_ADDRESS);
		props.put("mail.smtp.host", host);

		if (!"".equals(port))
			props.put("mail.smtp.port", port);
		if (!"".equals(starttls))
			props.put("mail.smtp.starttls.enable", starttls);
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.debug", Boolean.toString(debug));
		
		if (!"".equals(port))
			props.put("mail.smtp.socketFactory.port", port);
		if (!"".equals(socketFactoryClass))
			props.put("mail.smtp.socketFactory.class", socketFactoryClass);
		if (!"".equals(fallback))
			props.put("mail.smtp.socketFactory.fallback", fallback);
		
		Session mailSession = Session.getDefaultInstance(props, null);
		mailSession.setDebug(debug);
		Transport transport = mailSession.getTransport("smtp");

		MimeMessage mimeMessage = new MimeMessage(mailSession);
		mimeMessage.setSubject(email.getSubject(), "UTF-8");
		mimeMessage.setFrom(new InternetAddress(GMAIL_ADDRESS));
		mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getRecipientAddress()));

		MimeMultipart multipart = new MimeMultipart("related");

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(email.getSubject(), "text/html; charset=UTF-8");

		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();
		DataSource fds = new ByteArrayDataSource(email.getAuction().getPicture(), "image/jpeg");
		messageBodyPart.setDataHandler(new DataHandler(fds));
		messageBodyPart.setHeader("Content-ID", "<image>");

		multipart.addBodyPart(messageBodyPart);

		mimeMessage.setContent(multipart);
		transport.connect(host, GMAIL_ADDRESS, GMAIL_PASSWORD);
		transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
		transport.close();
	}
}

package be.kpoint.pictochat.util.mailer;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.content.Context;

import be.kpoint.pictochat.app.R;

public class Mailer
{
	private String host;
	private String password;
	private String from;

	public Mailer(Context context) {
		this(
			"smtp.gmail.com",
			context.getResources().getString(R.string.dev_pwd),
			context.getResources().getString(R.string.dev_mail)
		);
	}
	public Mailer(String host, String password, String from) {
		this.host = host;
		this.password = password;
		this.from = from;
	}

	public boolean send(String subject, String body, String toAddress, List<File> attachments) {
		Properties properties = System.getProperties();
	    properties.put("mail.smtp.host", this.host);
	    properties.put("mail.smtps.auth", true);
	    properties.put("mail.smtp.starttls.enable", true);
	    Session session = Session.getInstance(properties, null);

	    try {
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(this.from));
		    message.setRecipients(Message.RecipientType.TO, toAddress);
		    message.setSubject(subject);

		    BodyPart messageBodyPart = new MimeBodyPart();
		    messageBodyPart.setText(body);

		    Multipart multipart = new MimeMultipart();
		    multipart.addBodyPart(messageBodyPart);

		    for (File file : attachments) {
			    BodyPart attachmentPart = new MimeBodyPart();
			    DataSource source = new FileDataSource(file);
			    attachmentPart.setDataHandler(new DataHandler(source));
			    multipart.addBodyPart(attachmentPart);
		    }

		    message.setContent(multipart);

		    try{
		        Transport transport = session.getTransport("smtps");
		        transport.connect(this.host, this.from, this.password);
		        transport.sendMessage(message, message.getAllRecipients());

		        transport.close();

		        return true;
		    } catch (SendFailedException sfe){
		        System.out.println(sfe);
		    }
	    }
	    catch (MessagingException me) {

	    }

	    return false;
	}
}

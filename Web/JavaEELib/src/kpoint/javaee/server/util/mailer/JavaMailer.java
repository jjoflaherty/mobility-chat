/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.util.mailer;

import java.io.UnsupportedEncodingException;
import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

/**
 *
 * @author Steven
 */
public class JavaMailer implements IJavaMailer
{
    private static final Logger LOG = Logger.getLogger(JavaMailer.class);


    private String from;
    private String fromName;


    @Resource(mappedName="java:/Mail")
    private Session session;


    public JavaMailer(String from, String fromName) {
        this.from = from;
        this.fromName = fromName;
    }

    public void send(String to, String subject, String text) throws AddressException, MessagingException {
        InternetAddress fromAddress;
        if (this.fromName != null)
            try {
                fromAddress = new InternetAddress(this.from, this.fromName);
            } catch (UnsupportedEncodingException ex) {
                LOG.error(null, ex);
                fromAddress = new InternetAddress(this.from);
            }
        else
            fromAddress = new InternetAddress(this.from);

        MimeMessage message = new MimeMessage(this.session);
        message.setFrom(fromAddress);
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(text, "utf-8", "html");

        Transport.send(message);
    }
}

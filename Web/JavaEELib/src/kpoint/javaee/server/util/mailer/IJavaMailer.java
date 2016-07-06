/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.util.mailer;

import javax.ejb.Local;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 *
 * @author Steven
 */
@Local
public interface IJavaMailer
{
    public void send(String to, String subject, String text)
    throws AddressException, MessagingException;
}

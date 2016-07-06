/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.util.mailer;

import javax.ejb.Stateless;
import kpoint.javaee.server.util.mailer.JavaMailer;

/**
 *
 * @author Steven
 */
@Stateless
public class PictoChatMailer extends JavaMailer
{
    public PictoChatMailer() {
        super("noreply.ablechat@thomasmore.be", "AbleChat Mailer");
    }
}

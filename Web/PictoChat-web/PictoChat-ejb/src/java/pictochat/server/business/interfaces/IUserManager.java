/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.business.interfaces;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public interface IUserManager
{
    public User register(String firstName, String lastName, String email, String password, Avatar avatar, String mailSubject, IGeneratesMailBody generatesMailBody)
    throws InvalidParameterException, OperationFailedException, StorageException, AddressException, MessagingException;

    public User findUserByEmail(String email)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public void sendMail(User user, String mailSubject, String mailBody)
    throws AddressException, MessagingException;
}

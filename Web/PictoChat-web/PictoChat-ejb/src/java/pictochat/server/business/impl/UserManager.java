/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.business.impl;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.util.mailer.IJavaMailer;
import org.apache.log4j.Logger;
import pictochat.server.business.interfaces.IGeneratesMailBody;
import pictochat.server.business.local.IUserManagerLocal;
import pictochat.server.business.remote.IUserManagerRemote;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
@Stateless
public class UserManager implements IUserManagerRemote, IUserManagerLocal
{
    private static final Logger LOG = Logger.getLogger(UserManager.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);


    @Resource
    private SessionContext ctx;

    //<editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private IUserDAOLocal userDAO;

    @EJB
    private IJavaMailer mailer;
    //</editor-fold>


    public User register(String firstName, String lastName, String email, String password, Avatar avatar, String mailSubject, IGeneratesMailBody generatesMailBody) throws InvalidParameterException, OperationFailedException, StorageException, AddressException, MessagingException {
        User user = userDAO.create(firstName, lastName, email, password, avatar, null, true);

        String mailBody = generatesMailBody.generateMailBody(user);
        if (mailBody != null) {
            sendMail(user, mailSubject, mailBody);
            return user;
        }

        return null;
    }
    public void sendMail(User user, String mailSubject, String mailBody) throws AddressException, MessagingException {
        try {
            this.mailer.send(user.getEmail(), mailSubject, mailBody);
        } catch (AddressException ex) {
            LOG.error(null, ex);
            ctx.setRollbackOnly();
            throw ex;
        } catch (MessagingException ex) {
            LOG.error(null, ex);
            ctx.setRollbackOnly();
            throw ex;
        }
    }

    public User findUserByEmail(String email) throws InvalidParameterException, EntityNotFoundException, StorageException {
        return userDAO.findUserByEmail(email);
    }
}


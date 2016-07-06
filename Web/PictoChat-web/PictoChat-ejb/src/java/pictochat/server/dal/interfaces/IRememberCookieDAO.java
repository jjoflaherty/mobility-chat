/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.interfaces;

import pictochat.server.persistence.RememberCookie;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import pictochat.server.persistence.User;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.persistence.ISimpleEntityBeanManager;

/**
 *
 * @author Steven
 */
public interface IRememberCookieDAO extends ISimpleEntityBeanManager<RememberCookie>
{
    public RememberCookie create(String uuid, User user)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public RememberCookie findByUUID(String uuid)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public void deleteForUser(User user)
    throws InvalidParameterException, OperationFailedException, StorageException;
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.AbstractSimpleEntityBeanManager;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import pictochat.server.dal.local.IRememberCookieDAOLocal;
import pictochat.server.dal.remote.IRememberCookieDAORemote;
import pictochat.server.persistence.RememberCookie;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
@Stateless
public class RememberCookieDAO extends AbstractSimpleEntityBeanManager<RememberCookie> implements IRememberCookieDAOLocal, IRememberCookieDAORemote
{
    private static final Logger LOG = Logger.getLogger(RememberCookieDAO.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    @PersistenceContext(unitName = "PictoChat-ejbPU")
    private EntityManager persistence;

    @Override
    protected EntityManager getEntityManager() {
        return persistence;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected MethodLogger getMethodLogger() {
        return METHOD_LOG;
    }


    public void update(RememberCookie cookie) throws InvalidParameterException, EntityNotFoundException, StorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public RememberCookie findByUUID(String uuid) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = RememberCookie.NQ_FIND_REMEMBERCOOKIE_BY_UUID;
        String object = "cookie";

        ParameterValidation.stringNotNullNotEmpty(uuid, method, "uuid", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("uuid", uuid);
            RememberCookie result = (RememberCookie)query.getSingleResult();
            if(LOG.isDebugEnabled()){
                String message = String.format("%s found : id = %s",
                        object, result.getId());
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find %s for %s, the entity does not exist",
                object, uuid);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, uuid);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find %s for %s, multiple entities exist",
                object, uuid);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, uuid);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find %s for %s, the persistency context was closed",
                object, uuid);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, uuid);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find %s for %s, %s does not exist",
                object, uuid, method);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, uuid);
            throw new StorageException(method, message);
        }
    }

    public void deleteForUser(User user) throws InvalidParameterException, OperationFailedException, StorageException {
        String method = RememberCookie.NQ_DELETE_REMEMBERCOOKIE_FOR_USER;
        String object = "";

        ParameterValidation.objectNotNull(user, method, "user", LOG);

        try {
            Query query = persistence.createNamedQuery(method);
            query.setParameter("user", user.getId());
            query.executeUpdate();
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to delete %s, the persistency context was closed",
                object);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to delete %s",
                object);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to delete %s, the provided object is not an entity",
                object);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to delete %s",
                object);
            throw new OperationFailedException(method, message);
        }
    }
    public RememberCookie create(String uuid, User user) throws InvalidParameterException, OperationFailedException, StorageException {
        String method = "create";
        String object = "rememberCookie";

        ParameterValidation.objectNotNull(uuid, method, "uuid", LOG);
        ParameterValidation.objectNotNull(user, method, "user", LOG);

        RememberCookie cookie = new RememberCookie();
        cookie.setUUID(uuid);
        cookie.setUser(user);

        try {
            persistence.persist(cookie);
            return cookie;
        }
        catch(EntityExistsException ex){
            String details = String.format(
                "Failed to create %s, the entity already exists",
                object);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to create %s",
                object);
            throw new OperationFailedException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to create %s, the persistency context was closed",
                object);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create %s",
                object);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to create %s, the provided object is not an entity",
                object);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create %s",
                object);
            throw new OperationFailedException(method, message);
        }
    }
}

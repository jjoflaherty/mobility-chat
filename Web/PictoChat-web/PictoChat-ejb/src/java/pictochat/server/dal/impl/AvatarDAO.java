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
import pictochat.server.dal.local.IAvatarDAOLocal;
import pictochat.server.dal.remote.IAvatarDAORemote;
import pictochat.server.persistence.Avatar;

/**
 *
 * @author Steven
 */
@Stateless
public class AvatarDAO extends AbstractSimpleEntityBeanManager<Avatar> implements IAvatarDAOLocal, IAvatarDAORemote
{
    private static final Logger LOG = Logger.getLogger(AvatarDAO.class);
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


    public Avatar findById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Avatar.NQ_FIND_AVATAR_BY_ID;
        String object = "avatar";

        ParameterValidation.objectNotNullAndPositive(id, method, "id", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("avatar", id);
            Avatar result = (Avatar)query.getSingleResult();
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
                object, id);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, id);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find %s for %s, multiple entities exist",
                object, id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, id);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find %s for %s, the persistency context was closed",
                object, id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, id);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find %s for %s, %s does not exist",
                object, id, method);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find %s for %s",
                object, id);
            throw new StorageException(method, message);
        }
    }

    public Avatar create(byte[] data, String mime) throws InvalidParameterException, OperationFailedException, StorageException {
        String method = "create";
        String object = "propertyThumbnail";

        ParameterValidation.objectNotNull(data, method, "data", LOG);
        ParameterValidation.stringNotNullNotEmpty(mime, method, "mime", LOG);

        Avatar avatar = new Avatar();
        avatar.setData(data);
        avatar.setMime(mime);

        try {
            persistence.persist(avatar);
            return avatar;
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
    public void update(Avatar bean) throws InvalidParameterException, EntityNotFoundException, StorageException {
        ParameterValidation.objectNotNull(bean, "update", "bean", LOG);
        ParameterValidation.objectNotNull(bean.getId(), "update", "bean.id", LOG);
        ParameterValidation.validatesTrue(bean instanceof Avatar, "update", "bean is not a Avatar", LOG);

        try {
            Avatar persistedBean = persistence.find(Avatar.class, bean.getId());

            persistence.merge(persistedBean);
            if (LOG.isDebugEnabled()) {
                String message = String.format("Avatar %s updated : id = %s", persistedBean.getId(), persistedBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }
        catch (IllegalStateException ex)
        {
            String details = String.format(
                "Failed to update avatar 'id = %s', the persistency context was closed",
                bean.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update avatar 'id = %s'",
                bean.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update avatar 'id = %s', the bean is not an entity or invalid PK reference",
                bean.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update avatar 'id = %s'",
                bean.getId());
            throw new StorageException("update", message);
        }
    }
}

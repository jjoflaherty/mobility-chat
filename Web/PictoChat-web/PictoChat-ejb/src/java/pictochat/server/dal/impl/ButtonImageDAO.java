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
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.AbstractSimpleEntityBeanManager;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import org.apache.log4j.Logger;
import pictochat.server.dal.local.IButtonImageDAOLocal;
import pictochat.server.dal.remote.IButtonImageDAORemote;
import pictochat.server.persistence.ButtonImage;

/**
 *
 * @author Steven
 */
@Stateless
public class ButtonImageDAO extends AbstractSimpleEntityBeanManager<ButtonImage> implements IButtonImageDAOLocal, IButtonImageDAORemote
{
    private static final Logger LOG = Logger.getLogger(ButtonImageDAO.class);
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


    public ButtonImage findById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = ButtonImage.NQ_FIND_BUTTONIMAGE_BY_ID;
        String object = "buttonImage";

        ParameterValidation.objectNotNullAndPositive(id, method, "id", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("image", id);
            ButtonImage result = (ButtonImage)query.getSingleResult();
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
    
    public ButtonImage create(byte[] data, String mime) throws InvalidParameterException, OperationFailedException, StorageException {
        String method = "create";
        String object = "buttonImage";

        ParameterValidation.objectNotNull(data, method, "data", LOG);
        ParameterValidation.stringNotNullNotEmpty(mime, method, "mime", LOG);

        ButtonImage buttonImage = new ButtonImage();
        buttonImage.setData(data);
        buttonImage.setMime(mime);

        try {
            persistence.persist(buttonImage);
            return buttonImage;
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
    public void update(ButtonImage bean) throws InvalidParameterException, EntityNotFoundException, StorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

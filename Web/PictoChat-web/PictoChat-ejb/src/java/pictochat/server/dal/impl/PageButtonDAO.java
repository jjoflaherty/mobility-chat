/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.AbstractSimpleEntityBeanManager;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import org.apache.log4j.Logger;
import pictochat.server.dal.local.IPageButtonDAOLocal;
import pictochat.server.dal.remote.IPageButtonDAORemote;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.PageButton;

/**
 *
 * @author Steven
 */
@Stateless
public class PageButtonDAO extends AbstractSimpleEntityBeanManager<PageButton> implements IPageButtonDAOLocal, IPageButtonDAORemote
{
    private static final Logger LOG = Logger.getLogger(PageButtonDAO.class);
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


    public PageButton findPageButtonById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = PageButton.NQ_FIND_PAGEBUTTON_BY_ID;
        ParameterValidation.objectNotNullAndPositive(id, method, "id", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("button", id);
            PageButton result = (PageButton)query.getSingleResult();
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find answerSet for %s, the entity does not exist",
                id);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find answerSet for %s",
                id);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find answerSet for %s, multiple entities exist",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find answerSet for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find answerSet for %s, the persistency context was closed",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find answerSet for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find answerSet for %s, %s does not exist",
                id, method);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find answerSet for %s",
                id);
            throw new StorageException(method, message);
        }
    }
    public List<PageButton> findButtonsForClient(Client client) throws StorageException, InvalidParameterException {
        String method = PageButton.NQ_FIND_BUTTONS_FOR_CLIENT;
        ParameterValidation.objectNotNull(client, method, "client", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("client", client.getId());

            @SuppressWarnings("unchecked")
            List<PageButton> result = query.getResultList();

            if(LOG.isDebugEnabled()){
                String message = String.format("%s users found", result.size());
                METHOD_LOG.debug(method, message);
            }
            return result;
        }catch(IllegalStateException ex){
            String details = "Failed to find users, the persistency context was closed";
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find users";
            throw new StorageException(method, message);
        }catch(IllegalArgumentException ex){
            String details = String.format("Failed to find users, %s does not exist", method);
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find users";
            throw new StorageException(method, message);
        }
    }

    public void update(PageButton button) throws InvalidParameterException, EntityNotFoundException, StorageException {
        ParameterValidation.objectNotNull(button, "update", "bean", LOG);
        ParameterValidation.objectNotNull(button.getId(), "update", "bean.id", LOG);

        try{
            PageButton theBean = persistence.find(PageButton.class, button.getId());
            theBean.setCell(button.getCell());

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("Button %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update button 'id = %s', the persistency context was closed",
                button.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update button 'id = %s'",
                button.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update button 'id = %s', the bean is not an entity or invalid PK reference",
                button.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update button 'id = %s'",
                button.getId());
            throw new StorageException("update", message);
        }
    }
}

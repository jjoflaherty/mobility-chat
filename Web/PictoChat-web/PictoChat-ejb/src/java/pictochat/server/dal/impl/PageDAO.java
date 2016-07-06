/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.impl;

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
import pictochat.server.dal.local.IPageDAOLocal;
import pictochat.server.dal.remote.IPageDAORemote;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
@Stateless
public class PageDAO extends AbstractSimpleEntityBeanManager<Page> implements IPageDAOLocal, IPageDAORemote
{
    private static final Logger LOG = Logger.getLogger(PageDAO.class);
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


    public Page findPageById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Page.NQ_FIND_PAGE_BY_ID;
        ParameterValidation.objectNotNullAndPositive(id, method, "id", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("page", id);
            Page result = (Page)query.getSingleResult();
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

    public void update(Page page) throws InvalidParameterException, EntityNotFoundException, StorageException {
        ParameterValidation.objectNotNull(page, "update", "bean", LOG);
        ParameterValidation.objectNotNull(page.getId(), "update", "bean.id", LOG);

        try {
            Page persistedBean = persistence.find(Page.class, page.getId());
            persistedBean.setName(page.getName());
            persistedBean.setRows(page.getRows());
            persistedBean.setColumns(page.getColumns());
            persistedBean.setButtons(page.getButtons());

            persistence.merge(persistedBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("Page %s updated : id = %s",
                                               persistedBean.getId(),
                                               persistedBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update page 'id = %s', the persistency context was closed",
                page.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update page 'id = %s'",
                page.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update page 'id = %s', the bean is not an entity or invalid PK reference",
                page.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update page 'id = %s'",
                page.getId());
            throw new StorageException("update", message);
        }
    }
}

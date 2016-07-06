/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.persistence;

import javax.persistence.EntityManager;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import org.apache.log4j.Logger;

/**
 *
 * @author Steven
 */
public abstract class AbstractSimpleEntityBeanManager<T extends SimpleEntityBean> implements ISimpleEntityBeanManager<T>
{
    public void destroy(T bean)
    throws EntityNotFoundException, InvalidParameterException, StorageException
    {
        ParameterValidation.objectNotNull(bean, "destroy", "bean", getLogger());
        ParameterValidation.objectNotNull(bean.getId(), "destroy", "bean.id", getLogger());

        try
        {
            T theParam = bean;
            T theBean = (T)getEntityManager().find(bean.getClass(), theParam.getId());
            if (theBean == null)
            {
                getMethodLogger().warn("destroy", "the specified bean does not exist");
                throw new EntityNotFoundException("destroy", "the specified bean does not exist");
            }
            getEntityManager().remove(theBean);
        }
        catch (RuntimeException ex)
        {
            getMethodLogger().fatal("destroy", "the request could not be processed, reason = " + ex.getMessage());
            throw new StorageException("destroy", "the request could not be processed", ex);
        }
    }

    public T findById(Long id, Class _class)
    throws EntityNotFoundException, InvalidParameterException, StorageException
    {
        ParameterValidation.objectNotNull(id, "findById", "id", getLogger());

        try
        {
            T theBean = (T)getEntityManager().find(_class, id);
            if (theBean == null)
            {
              getMethodLogger().warn("findById", "the specified bean does not exist : id = " + id.longValue());
              throw new EntityNotFoundException("findById", "the specified bean does not exist : id = " + id.longValue());
            }
            return theBean;
        }
        catch (RuntimeException ex)
        {
            getMethodLogger().fatal("findById", "the request could not be processed, reason = " + ex.getMessage());
            throw new StorageException("findById", "the request could not be processed", ex);
        }
    }


    protected abstract EntityManager getEntityManager();
    protected abstract Logger getLogger();
    protected abstract MethodLogger getMethodLogger();
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.persistence;

import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;

/**
 *
 * @author Steven
 */
public interface ISimpleEntityBeanManager<T extends SimpleEntityBean>
{
    public abstract void update(T bean)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public abstract void destroy(T bean)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public abstract T findById(Long paramLong, Class<T> paramClass)
    throws InvalidParameterException, EntityNotFoundException, StorageException;
}

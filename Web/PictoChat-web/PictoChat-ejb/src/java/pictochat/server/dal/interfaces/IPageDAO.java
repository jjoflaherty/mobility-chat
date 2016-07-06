/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.interfaces;

import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.ISimpleEntityBeanManager;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
public interface IPageDAO extends ISimpleEntityBeanManager<Page>
{
    public Page findPageById(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;
}

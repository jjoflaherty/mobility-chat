/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.interfaces;

import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.ISimpleEntityBeanManager;
import pictochat.server.persistence.Avatar;

/**
 *
 * @author Steven
 */
public interface IAvatarDAO extends ISimpleEntityBeanManager<Avatar>
{
    public Avatar create(byte[] data, String mime)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public Avatar findById(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;
}

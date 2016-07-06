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
import pictochat.server.persistence.ButtonImage;

/**
 *
 * @author Steven
 */
public interface IButtonImageDAO extends ISimpleEntityBeanManager<ButtonImage>
{
    public ButtonImage create(byte[] data, String mime)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public ButtonImage findById(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;
}

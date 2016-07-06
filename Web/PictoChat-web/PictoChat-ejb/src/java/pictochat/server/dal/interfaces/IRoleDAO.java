/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.interfaces;

import java.util.List;
import pictochat.server.persistence.Role;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.ISimpleEntityBeanManager;

/**
 *
 * @author Steven
 */
public interface IRoleDAO extends ISimpleEntityBeanManager<Role>
{
    public Role create(String name)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public Role findRoleById(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public Role findRoleByName(String name)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public List<Role> findRolesByNames(List<String> names)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public List<Role> findAllRoles()
    throws StorageException;
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.interfaces;

import java.util.List;
import java.util.Set;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.ISimpleEntityBeanManager;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.Role;
import pictochat.server.persistence.User;


/**
 *
 * @author Steven
 */
public interface IUserDAO extends ISimpleEntityBeanManager<User>
{
    public User create(String firstName, String lastName, String email, String password, Avatar avatar, Role globalRole, Boolean complete)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public User findUserByEmail(String email)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public User findUserById(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public List<User> findAllUsersWithGlobalRole(Set<String> roles)
    throws StorageException, InvalidParameterException;

    public void update(User user)
    throws InvalidParameterException, StorageException;

    public void updatePassword(User user)
    throws InvalidParameterException, StorageException;
}

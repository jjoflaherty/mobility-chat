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
import pictochat.server.persistence.Client;

/**
 *
 * @author Steven
 */
public interface IClientDAO extends ISimpleEntityBeanManager<Client>
{
    public Client create(String firstName, String lastName, String shortCode, String longCode, Avatar avatar)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public Client findClientByNameAndShortCode(String firstName, String lastName, String shortCode)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public Client findClientByLongCode(String longCode)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public Client findClientByIdWithPages(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public Client findClientByIdWithRelations(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public Client findClientByIdWithFriends(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public Client findClientByIdWithFriendOf(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public Client findClientByIdWithContacts(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public void updateRelations(Client client)
    throws InvalidParameterException, StorageException;

    public void updatePages(Client client)
    throws InvalidParameterException, StorageException;

    public void updateFriends(Client client)
    throws InvalidParameterException, StorageException;

    public void update(Client client)
    throws InvalidParameterException, StorageException;
}

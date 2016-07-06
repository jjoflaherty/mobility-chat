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
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public interface IRelationDAO extends ISimpleEntityBeanManager<Relation>
{
    public Relation create(Client client, User user, Boolean active, Boolean accepted)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public Boolean doesRelationExistBetween(Client client, User user)
    throws InvalidParameterException, OperationFailedException, StorageException;

    public Relation findRelationById(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public void update(Relation relation)
    throws InvalidParameterException, StorageException;
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.business.interfaces;

import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public interface IClientManager
{
    public Client create(String firstName, String lastName, Avatar avatar, User firstAdmin)
    throws InvalidParameterException, kpoint.javaee.server.core.exceptions.OperationFailedException, StorageException;
}

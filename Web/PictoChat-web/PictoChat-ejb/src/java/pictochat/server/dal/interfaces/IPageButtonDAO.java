/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.interfaces;

import java.util.List;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.ISimpleEntityBeanManager;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.PageButton;

/**
 *
 * @author Steven
 */
public interface IPageButtonDAO extends ISimpleEntityBeanManager<PageButton>
{
    public PageButton findPageButtonById(Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    public List<PageButton> findButtonsForClient(Client client)
    throws InvalidParameterException, StorageException;
}

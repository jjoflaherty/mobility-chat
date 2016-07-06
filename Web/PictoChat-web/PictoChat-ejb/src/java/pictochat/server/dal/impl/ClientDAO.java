/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.AbstractSimpleEntityBeanManager;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import org.apache.log4j.Logger;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.dal.remote.IClientDAORemote;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
@Stateless
public class ClientDAO extends AbstractSimpleEntityBeanManager<Client> implements IClientDAOLocal, IClientDAORemote
{
    private static final Logger LOG = Logger.getLogger(ClientDAO.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    @PersistenceContext(unitName = "PictoChat-ejbPU")
    private EntityManager persistence;

    @Override
    protected EntityManager getEntityManager() {
        return persistence;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected MethodLogger getMethodLogger() {
        return METHOD_LOG;
    }


    public Client create(String firstName, String lastName, String shortCode, String longCode, Avatar avatar) throws InvalidParameterException, OperationFailedException, StorageException {
        String method = "create";
        ParameterValidation.stringNotNullNotEmpty(firstName, method, "firstName", LOG);
        ParameterValidation.stringNotNullNotEmpty(lastName, method, "lastName", LOG);
        ParameterValidation.stringNotNullNotEmpty(shortCode, method, "shortCode", LOG);
        ParameterValidation.stringNotNullNotEmpty(longCode, method, "longCode", LOG);

        Page page = new Page();
        page.setIsStartPage(true);
        page.setName("Home");

        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setShortCode(shortCode);
        client.setLongCode(longCode);
        client.setAvatar(avatar);
        client.getPages().add(page);

        try {
            persistence.persist(client);
            return client;
        }
        catch(EntityExistsException ex){
            String details = String.format(
                "Failed to create record %s %s, the entity already exists",
                firstName, lastName);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to create record %s %s",
                firstName, lastName);
            throw new OperationFailedException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to create record %s %s, the persistency context was closed",
                firstName, lastName);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create record %s %s",
                firstName, lastName);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to create record %s %s, the provided object is not an entity",
                firstName, lastName);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create record %s %s",
                firstName, lastName);
            throw new OperationFailedException(method, message);
        }
        catch(TransactionRequiredException ex){
            String details = String.format(
                "Failed to create record %s %s, a transaction is required, but does not exist",
                firstName, lastName);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create record %s %s",
                firstName, lastName);
            throw new StorageException(method, message);
        }
    }

    public void updatePages(Client client) throws InvalidParameterException, StorageException {
        ParameterValidation.objectNotNull(client, "update", "client", LOG);
        ParameterValidation.objectNotNull(client.getId(), "update", "client.id", LOG);

        try{
            Client theBean = persistence.find(Client.class, client.getId());
            theBean.setPages(client.getPages());

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("Record %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update record 'id = %s', the persistency context was closed",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update record 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update record 'id = %s', the bean is not an entity or invalid PK reference",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update record 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }
    }
    public void updateFriends(Client client) throws InvalidParameterException, StorageException {
        ParameterValidation.objectNotNull(client, "update", "client", LOG);
        ParameterValidation.objectNotNull(client.getId(), "update", "client.id", LOG);

        try{
            Client theBean = persistence.find(Client.class, client.getId());
            theBean.setFriends(client.getFriends());
            theBean.setFriendOf(client.getFriendOf());

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("Record %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update record 'id = %s', the persistency context was closed",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update record 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update record 'id = %s', the bean is not an entity or invalid PK reference",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update record 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }
    }
    public void updateRelations(Client client) throws InvalidParameterException, StorageException {
        ParameterValidation.objectNotNull(client, "update", "client", LOG);
        ParameterValidation.objectNotNull(client.getId(), "update", "client.id", LOG);

        try{
            Client theBean = persistence.find(Client.class, client.getId());
            theBean.setRelations(client.getRelations());

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("Record %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update record 'id = %s', the persistency context was closed",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update record 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update record 'id = %s', the bean is not an entity or invalid PK reference",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update record 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }
    }
    public void update(Client client) throws InvalidParameterException, StorageException {
        ParameterValidation.objectNotNull(client, "update", "client", LOG);
        ParameterValidation.objectNotNull(client.getId(), "update", "client.id", LOG);

        try{
            Client theBean = persistence.find(Client.class, client.getId());
            theBean.setFirstName(client.getFirstName());
            theBean.setLastName(client.getLastName());
            theBean.setAvatar(client.getAvatar());

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("Client %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update client 'id = %s', the persistency context was closed",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update client 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update client 'id = %s', the bean is not an entity or invalid PK reference",
                client.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update client 'id = %s'",
                client.getId());
            throw new StorageException("update", message);
        }
    }

    public Client findClientByIdWithPages(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        return findClientByIdWith(id, Client.NQ_FIND_CLIENT_BY_ID_WITH_PAGES);
    }
    public Client findClientByIdWithRelations(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        return findClientByIdWith(id, Client.NQ_FIND_CLIENT_BY_ID_WITH_RELATIONS);
    }
    public Client findClientByIdWithFriends(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        return findClientByIdWith(id, Client.NQ_FIND_CLIENT_BY_ID_WITH_FRIENDS);
    }
    public Client findClientByIdWithFriendOf(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        return findClientByIdWith(id, Client.NQ_FIND_CLIENT_BY_ID_WITH_FRIENDOF);
    }
    public Client findClientByIdWithContacts(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        return findClientByIdWith(id, Client.NQ_FIND_CLIENT_BY_ID_WITH_CONTACTS);
    }
    private Client findClientByIdWith(Long id, String method) throws InvalidParameterException, EntityNotFoundException, StorageException {
        ParameterValidation.objectNotNullAndPositive(id, method, "id", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("client", id);

            @SuppressWarnings("unchecked")
            Client result = (Client)query.getSingleResult();

            if(LOG.isDebugEnabled()){
                String message = String.format("client found for %s", id);
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find client for %s, the entity does not exist",
                id);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                id);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find client for %s, multiple entities exist",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find client for %s, the persistency context was closed",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find client for %s, %s does not exist",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                id);
            throw new StorageException(method, message);
        }
    }

    public Client findClientByNameAndShortCode(String firstName, String lastName, String shortCode) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Client.NQ_FIND_CLIENT_BY_NAME_AND_SHORTCODE;
        ParameterValidation.stringNotNullNotEmpty(firstName, method, "firstName", LOG);
        ParameterValidation.stringNotNullNotEmpty(lastName, method, "lastName", LOG);
        ParameterValidation.stringNotNullNotEmpty(shortCode, method, "shortCode", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);
            query.setParameter("shortCode", shortCode);

            @SuppressWarnings("unchecked")
            Client result = (Client)query.getSingleResult();

            if(LOG.isDebugEnabled()){
                String message = String.format("client found for %s %s", firstName, lastName);
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find client for %s %s, the entity does not exist",
                firstName, lastName);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find client for %s %s",
                firstName, lastName);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find client for %s %s, multiple entities exist",
                firstName, lastName);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s %s",
                firstName, lastName);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find client %s %s, the persistency context was closed",
                firstName, lastName);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s %s",
                firstName, lastName);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find client for %s %s",
                firstName, lastName);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s %s",
                firstName, lastName);
            throw new StorageException(method, message);
        }
    }
    public Client findClientByLongCode(String longCode) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Client.NQ_FIND_CLIENT_BY_LONGCODE;
        ParameterValidation.stringNotNullNotEmpty(longCode, method, "longCode", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("longCode", longCode);

            @SuppressWarnings("unchecked")
            Client result = (Client)query.getSingleResult();

            if(LOG.isDebugEnabled()){
                String message = String.format("client found for %s", longCode);
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find client for %s, the entity does not exist",
                longCode);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                longCode);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find client for %s, multiple entities exist",
                longCode);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                longCode);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find client %s, the persistency context was closed",
                longCode);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                longCode);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find client for %s",
                longCode);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find client for %s",
                longCode);
            throw new StorageException(method, message);
        }
    }
}

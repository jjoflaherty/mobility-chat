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
import pictochat.server.dal.local.IRelationDAOLocal;
import pictochat.server.dal.remote.IRelationDAORemote;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
@Stateless
public class RelationDAO extends AbstractSimpleEntityBeanManager<Relation> implements IRelationDAOLocal, IRelationDAORemote
{
    private static final Logger LOG = Logger.getLogger(RelationDAO.class);
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


    public Relation create(Client client, User user, Boolean active, Boolean accepted) throws InvalidParameterException, OperationFailedException, StorageException {
        String method = "create";
        ParameterValidation.objectNotNull(user, method, "user", LOG);
        ParameterValidation.objectNotNull(client, method, "client", LOG);
        ParameterValidation.objectNotNull(active, method, "active", LOG);
        ParameterValidation.objectNotNull(accepted, method, "accepted", LOG);

        Relation relation = new Relation();
        relation.setClient(client);
        relation.setUser(user);
        relation.setActive(active);
        relation.setAccepted(accepted);

        try {
            persistence.persist(relation);
            return relation;
        }
        catch(EntityExistsException ex){
            String details = String.format(
                "Failed to create relation between %s and %s, the entity already exists",
                user.getFullName(), client.getFullName());
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to create relation between %s and %s",
                user.getFullName(), client.getFullName());
            throw new OperationFailedException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to create relation between %s and %s, the persistency context was closed",
                user.getFullName(), client.getFullName());
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create relation between %s and %s",
                user.getFullName(), client.getFullName());
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to create relation between %s and %s, the provided object is not an entity",
                user.getFullName(), client.getFullName());
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create relation between %s and %s",
                user.getFullName(), client.getFullName());
            throw new OperationFailedException(method, message);
        }
        catch(TransactionRequiredException ex){
            String details = String.format(
                "Failed to create relation between %s and %s, a transaction is required, but does not exist",
                user.getFullName(), client.getFullName());
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create relation between %s and %s",
                user.getFullName(), client.getFullName());
            throw new StorageException(method, message);
        }
    }

    public void update(Relation relation) throws InvalidParameterException, StorageException {
        ParameterValidation.objectNotNull(relation, "update", "relation", LOG);
        ParameterValidation.objectNotNull(relation.getId(), "update", "relation.id", LOG);

        try{
            Relation theBean = persistence.find(Relation.class, relation.getId());
            theBean.setAccepted(relation.getAccepted());
            theBean.setActive(relation.getActive());
            theBean.setBlocked(relation.getBlocked());

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("Relation %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update relation 'id = %s', the persistency context was closed",
                relation.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update relation 'id = %s'",
                relation.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update relation 'id = %s', the bean is not an entity or invalid PK reference",
                relation.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update relation 'id = %s'",
                relation.getId());
            throw new StorageException("update", message);
        }
    }

    public Relation findRelationById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Relation.NQ_FIND_RELATION_BY_ID;
        ParameterValidation.objectNotNullAndPositive(id, method, "id", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("relation", id);

            @SuppressWarnings("unchecked")
            Relation result = (Relation)query.getSingleResult();

            if(LOG.isDebugEnabled()){
                String message = String.format("relation found for %s", id);
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find relation for %s, the entity does not exist",
                id);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find relation for %s",
                id);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find relation for %s, multiple entities exist",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find relation for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find relation for %s, the persistency context was closed",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find relation for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find relation for %s, %s does not exist",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find relation for %s",
                id);
            throw new StorageException(method, message);
        }
    }
    public Boolean doesRelationExistBetween(Client client, User user) throws InvalidParameterException, StorageException {
        String method = Relation.NQ_COUNT_RELATIONS_BY_CLIENT_AND_USER;
        ParameterValidation.objectNotNull(client, method, "client", LOG);
        ParameterValidation.objectNotNull(user, method, "user", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("client", client.getId());
            query.setParameter("user", user.getId());

            @SuppressWarnings("unchecked")
            int count = ((Number)query.getSingleResult()).intValue();

            if(LOG.isDebugEnabled()){
                String message = String.format("%s relations found for %s - %s", count, client.getFullName(), user.getFullName());
                METHOD_LOG.debug(method, message);
            }
            return count > 0;
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find relation for %s - %s, the persistency context was closed",
                client.getFullName(), user.getFullName());
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find relation for %s - %s",
                client.getFullName(), user.getFullName());
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find relation for %s - %s, %s or %s does not exist",
                client.getFullName(), user.getFullName(), "client", "user");
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find relation for %s - %s",
                client.getFullName(), user.getFullName());
            throw new StorageException(method, message);
        }
    }
}

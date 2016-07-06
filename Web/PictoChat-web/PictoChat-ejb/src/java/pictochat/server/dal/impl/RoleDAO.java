/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.AbstractSimpleEntityBeanManager;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import pictochat.server.dal.local.IRoleDAOLocal;
import pictochat.server.dal.remote.IRoleDAORemote;
import pictochat.server.persistence.Role;

/**
 *
 * @author Steven
 */
@Stateless
public class RoleDAO extends AbstractSimpleEntityBeanManager<Role> implements IRoleDAOLocal, IRoleDAORemote
{
    private static final Logger LOG = Logger.getLogger(RoleDAO.class);
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


    public Role create(String string) throws InvalidParameterException, OperationFailedException, StorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void update(Role role) throws InvalidParameterException, EntityNotFoundException, StorageException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public Role findRoleById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Role.NQ_FIND_ROLE_BY_ID;
        ParameterValidation.objectNotNullAndPositive(id, method, "id", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("id", id);
            Role result = (Role)query.getSingleResult();
            if(LOG.isDebugEnabled()){
                String message = String.format("%s found : id = %s",
                        result.getName(), result.getId());
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find role for %s, the entity does not exist",
                id);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                id);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find role for %s, multiple entities exist",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find role for %s, the persistency context was closed",
                id);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                id);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find role for %s, %s does not exist",
                id, method);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                id);
            throw new StorageException(method, message);
        }
    }
    public Role findRoleByName(String name) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Role.NQ_FIND_ROLE_BY_NAME;
        ParameterValidation.stringNotNullNotEmpty(name, method, "name", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("name", name);
            Role result = (Role)query.getSingleResult();
            if(LOG.isDebugEnabled()){
                String message = String.format("%s found : id = %s",
                        result.getName(), result.getId());
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(NoResultException ex){
            String details = String.format(
                "Failed to find role for %s, the entity does not exist",
                name);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                name);
            throw new EntityNotFoundException(method, message);
        }
        catch(NonUniqueResultException ex){
            String details = String.format(
                "Failed to find role for %s, multiple entities exist",
                name);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                name);
            throw new StorageException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find role for %s, the persistency context was closed",
                name);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                name);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find role for %s, %s does not exist",
                name, method);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find role for %s",
                name);
            throw new StorageException(method, message);
        }
    }
    public List<Role> findRolesByNames(List<String> names) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String method = Role.NQ_FIND_ROLES_BY_NAMES;
        ParameterValidation.objectNotNull(names, method, "names", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("names", names);
            List<Role> result = query.getResultList();

            if(LOG.isDebugEnabled()){
                String message = String.format("%s roles found", result.size());
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(IllegalStateException ex){
            String details = "Failed to find roles, the persistency context was closed";
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find roles";
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format("Failed to find roles, %s does not exist", method);
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find roles";
            throw new StorageException(method, message);
        }
    }
    public List<Role> findAllRoles() throws StorageException {
        String method = Role.NQ_FIND_ALL;
        try{
            Query query = persistence.createNamedQuery(method);

            @SuppressWarnings("unchecked")
            List<Role> result = query.getResultList();

            if(LOG.isDebugEnabled()){
                String message = String.format("%s roles found", result.size());
                METHOD_LOG.debug(method, message);
            }
            return result;
        }catch(IllegalStateException ex){
            String details = "Failed to find roles, the persistency context was closed";
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find roles";
            throw new StorageException(method, message);
        }catch(IllegalArgumentException ex){
            String details = String.format("Failed to find roles, %s does not exist", method);
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find roles";
            throw new StorageException(method, message);
        }
    }
}

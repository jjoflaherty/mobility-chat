/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.dal.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.persistence.AbstractSimpleEntityBeanManager;
import kpoint.javaee.server.core.persistence.QueryHelper;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.dal.remote.IUserDAORemote;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.Role;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
@Stateless
public class UserDAO extends AbstractSimpleEntityBeanManager<User> implements IUserDAOLocal, IUserDAORemote
{
    private static final Logger LOG = Logger.getLogger(UserDAO.class);
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


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User create(String firstName, String lastName, String email, String password, Avatar avatar, Role globalRole, Boolean complete) throws InvalidParameterException, OperationFailedException, StorageException {
        String method = "create";
        ParameterValidation.objectNotNull(firstName, method, "firstName", LOG);
        ParameterValidation.objectNotNull(lastName, method, "lastName", LOG);
        ParameterValidation.stringNotNullNotEmpty(email, method, "email", LOG);
        ParameterValidation.stringNotNullNotEmpty(password, method, "password", LOG);

        String hash = DigestUtils.sha256Hex(password);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(hash);
        user.setAvatar(avatar);
        user.setGlobalRole(globalRole);
        user.setComplete(complete);

        try {
            persistence.persist(user);
            persistence.flush();
            return user;
        }
        catch(EntityExistsException ex){
            String details = String.format(
                "Failed to create user %s, the entity already exists",
                email);
            METHOD_LOG.warn(method, details, ex);
            String message = String.format(
                "Failed to create user %s",
                email);
            throw new OperationFailedException(method, message);
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to create user %s, the persistency context was closed",
                email);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create user %s",
                email);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to create user %s, the provided object is not an entity",
                email);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create user %s",
                email);
            throw new OperationFailedException(method, message);
        }
        catch(TransactionRequiredException ex){
            String details = String.format(
                "Failed to create user %s, a transaction is required, but does not exist",
                email);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to create user %s",
                email);
            throw new StorageException(method, message);
        }
    }
    public void update(User user) throws InvalidParameterException, StorageException {
        ParameterValidation.objectNotNull(user, "update", "bean", LOG);
        ParameterValidation.objectNotNull(user.getId(), "update", "bean.id", LOG);

        try{
            User theBean = persistence.find(User.class, user.getId());
            theBean.setFirstName(user.getFirstName());
            theBean.setLastName(user.getLastName());
            theBean.setComplete(user.getComplete());
            theBean.setVerified(user.getVerified());
            theBean.setAvatar(user.getAvatar());

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("User %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update user 'id = %s', the persistency context was closed",
                user.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update user 'id = %s'",
                user.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update user 'id = %s', the bean is not an entity or invalid PK reference",
                user.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update user 'id = %s'",
                user.getId());
            throw new StorageException("update", message);
        }
    }
    public void updatePassword(User user) throws InvalidParameterException, StorageException {
        ParameterValidation.objectNotNull(user, "update", "bean", LOG);
        ParameterValidation.objectNotNull(user.getId(), "update", "bean.id", LOG);

        String hash = DigestUtils.sha256Hex(user.getPassword());

        try{
            User theBean = persistence.find(User.class, user.getId());
            theBean.setPassword(hash);

            persistence.merge(theBean);
            if(LOG.isDebugEnabled()){
                String message = String.format("User %s updated : id = %s",
                                               theBean.getId(),
                                               theBean.getId());
                METHOD_LOG.debug("update", message);
            }
        }catch(IllegalStateException ex){
            String details = String.format(
                "Failed to update user 'id = %s', the persistency context was closed",
                user.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update user 'id = %s'",
                user.getId());
            throw new StorageException("update", message);
        }catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to update user 'id = %s', the bean is not an entity or invalid PK reference",
                user.getId());
            METHOD_LOG.fatal("update", details, ex);
            String message = String.format(
                "Failed to update user 'id = %s'",
                user.getId());
            throw new StorageException("update", message);
        }
    }

    public User findUserByEmail(String email) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String queryName = User.NQ_FIND_USER_BY_EMAIL;
        ParameterValidation.stringNotNullNotEmpty(email, queryName, "email", LOG);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);

        QueryHelper<User> qh = new QueryHelper<User>(User.class) {
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
        };
        return qh.findSingleResult(queryName, params);
    }
    public User findUserById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        String queryName = User.NQ_FIND_USER_BY_ID;
        ParameterValidation.objectNotNullAndPositive(id, queryName, "id", LOG);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);

        QueryHelper<User> qh = new QueryHelper<User>(User.class) {
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
        };
        return qh.findSingleResult(queryName, params);
    }
    public List<User> findAllUsersWithGlobalRole(Set<String> roles) throws StorageException, InvalidParameterException {
        String method = User.NQ_FIND_USERS_WITH_GLOBAL_ROLE;
        ParameterValidation.objectNotNull(roles, method, "roles", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("roles", roles);

            @SuppressWarnings("unchecked")
            List<User> result = query.getResultList();

            if(LOG.isDebugEnabled()){
                String message = String.format("%s users found", result.size());
                METHOD_LOG.debug(method, message);
            }
            return result;
        }catch(IllegalStateException ex){
            String details = "Failed to find users, the persistency context was closed";
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find users";
            throw new StorageException(method, message);
        }catch(IllegalArgumentException ex){
            String details = String.format("Failed to find users, %s does not exist", method);
            METHOD_LOG.fatal(method, details, ex);
            String message = "Failed to find users";
            throw new StorageException(method, message);
        }
    }
}

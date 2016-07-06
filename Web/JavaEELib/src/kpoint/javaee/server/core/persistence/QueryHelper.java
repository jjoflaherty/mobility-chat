/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.persistence;

import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;

/**
 *
 * @author Steven
 */
public abstract class QueryHelper<T extends SimpleEntityBean>
{
    private final Class<T> clazz;
    private final String beanName;

    public QueryHelper(Class<T> clazz) {
        this.clazz = clazz;
        this.beanName = clazz.getSimpleName();
    }

    public T findSingleResult(String queryName, HashMap<String, Object> parameters) throws EntityNotFoundException, StorageException {
        try {
            Query query = getEntityManager().createNamedQuery(queryName);
            StringBuilder sb = new StringBuilder("(");

            for (String key : parameters.keySet()) {
                Object value = parameters.get(key);

                query.setParameter(key, value);
                sb.append(value).append(", ");
            }

            @SuppressWarnings("unchecked")
            T result = (T)query.getSingleResult();

            String params = sb.substring(0, sb.length() - 2) + ")";
            if (getLogger().isDebugEnabled()){
                String message = String.format("%s found for parameters ", beanName, params);
                getMethodLogger().debug(queryName, message);
            }

            return result;
        }
        catch (NoResultException ex) {
            String details = String.format("Failed to find %s for %s, the entity does not exist", beanName, parameters);
            getMethodLogger().warn(queryName, details, ex);
            String message = String.format("Failed to find %s for %s", beanName, parameters);
            throw new EntityNotFoundException(queryName, message);
        }
        catch (NonUniqueResultException ex) {
            String details = String.format("Failed to find %s for %s, multiple entities exist", beanName, parameters);
            getMethodLogger().fatal(queryName, details, ex);
            String message = String.format("Failed to find %s for %s", beanName, parameters);
            throw new StorageException(queryName, message);
        }
        catch (IllegalStateException ex) {
            String details = String.format("Failed to find %s for %s, the persistency context was closed", beanName, parameters);
            getMethodLogger().fatal(queryName, details, ex);
            String message = String.format("Failed to find %s for %s", beanName, parameters);
            throw new StorageException(queryName, message);
        }
        catch (IllegalArgumentException ex) {
            String details = String.format("Failed to find %s for %s, %s does not exist", beanName, parameters);
            getMethodLogger().fatal(queryName, details, ex);
            String message = String.format("Failed to find %s for %s", beanName, parameters);
            throw new StorageException(queryName, message);
        }
    }


    protected abstract EntityManager getEntityManager();
    protected abstract Logger getLogger();
    protected abstract MethodLogger getMethodLogger();
}

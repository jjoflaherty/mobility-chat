/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kpoint.javaee.server.core.util;

import java.util.List;
import java.util.Set;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
public class Assert
{
    public static String compareEntities(SimpleEntityBean bean, SimpleEntityBean other) {
        return String.format("Entity (%1$s) is %3$s to entity (%2$s)", bean.toString(), other.toString(), bean.equals(other) ? "equal" : "not equal");
    }

    public static <T> String collectionContains(T bean, List<T> collection) {
        return String.format("List (%2$s) entity (%1$s)", bean.toString(), collection.contains(bean) ? "contains" : "does not contain");
    }
    public static <T> String collectionContains(T bean, Set<T> collection) {
        return String.format("Set (%2$s) entity (%1$s)", bean.toString(), collection.contains(bean) ? "contains" : "does not contain");
    }

    public static <T> String removeAndCompareSize(T bean, List<T> collection) {
        int size = collection.size();

        collection.remove(bean);

        return String.format("List went from %1$s to %2$s items", size, collection.size());
    }
    public static <T> String removeAndCompareSize(T bean, Set<T> collection) {
        int size = collection.size();

        collection.remove(bean);

        return String.format("Set went from %1$s to %2$s items", size, collection.size());
    }
}

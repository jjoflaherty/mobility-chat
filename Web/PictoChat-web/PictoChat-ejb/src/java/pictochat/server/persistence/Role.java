/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
@Entity
@Table(name = "roles")
@NamedQueries({
    @NamedQuery(name = Role.NQ_FIND_ROLE_BY_ID,
                query="SELECT r FROM Role r " +
                      "WHERE r.id = :id"),
    @NamedQuery(name = Role.NQ_FIND_ROLE_BY_NAME,
                query="SELECT r FROM Role r " +
                      "WHERE r.name = :name"),
    @NamedQuery(name = Role.NQ_FIND_ROLES_BY_NAMES,
                query="SELECT r FROM Role r " +
                      "WHERE r.name IN (:names)"),
    @NamedQuery(name = Role.NQ_FIND_ALL,
                query="SELECT r FROM Role r"),
})
public class Role extends SimpleEntityBean
{
    public static final String NQ_FIND_ALL = "findAllRoles";
    public static final String NQ_FIND_ROLE_BY_ID = "findRoleById";
    public static final String NQ_FIND_ROLE_BY_NAME = "findRoleByName";
    public static final String NQ_FIND_ROLES_BY_NAMES = "findRolesByNames";

    public static final String ADMIN = "admin";
    public static final String DEVELOPER = "developer";
    public static final String SUPERADMIN = "superAdmin";


    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "globalOnly", nullable = false)
    private Boolean globalOnly;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGlobalOnly() {
        return globalOnly;
    }
    public void setGlobalOnly(Boolean globalOnly) {
        this.globalOnly = globalOnly;
    }


    @Override
    public SimpleEntityBean clone() {
        Role clone = new Role();

        clone.setName(getName());
        clone.setGlobalOnly(getGlobalOnly());

        clone.setId(getId());
        return clone;
    }
}

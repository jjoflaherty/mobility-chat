/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
@Entity
@Table(
    name = "relations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "clientId"})
)
@NamedQueries({
    @NamedQuery(name = Relation.NQ_FIND_RELATION_BY_ID,
                query="SELECT r FROM Relation r " +
                      "WHERE r.id = :relation"),
    @NamedQuery(name = Relation.NQ_COUNT_RELATIONS_BY_CLIENT_AND_USER,
                query="SELECT COUNT(r) FROM Relation r " +
                      "WHERE r.user.id = :user " +
                      "AND r.client.id = :client")
})
public class Relation extends SimpleEntityBean
{
    public static final String NQ_FIND_RELATION_BY_ID = "findRelationById";
    public static final String NQ_COUNT_RELATIONS_BY_CLIENT_AND_USER = "countRelationsByClientAndUser";


    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "accepted", nullable = false)
    private Boolean accepted;

    @Column(name = "blocked", nullable = false)
    private Boolean blocked = false;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "clientId", nullable = false)
    private Client client;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "assignedRoles",
        joinColumns = {@JoinColumn(name = "relationId")},
        inverseJoinColumns = {@JoinColumn(name = "roleId")}
    )
    private Set<Role> roles;


    @PreRemove
    private void preRemove() {
        setUser(null);
        setClient(null);
    }


    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getAccepted() {
        return accepted;
    }
    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Boolean getBlocked() {
        return blocked;
    }
    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    //<editor-fold defaultstate="collapsed" desc="Convenience functions">
    public Boolean isAdmin() {
        for (Role role : this.getRoles())
            if (Role.ADMIN.equals(role.getName()))
                return true;

        return false;
    }
    //</editor-fold>


    @Override
    public SimpleEntityBean clone() {
        Relation clone = new Relation();

        clone.setActive(getActive());
        clone.setAccepted(getAccepted());
        clone.setBlocked(getBlocked());
        clone.setUser(getUser());
        clone.setClient(getClient());
        clone.setRoles(getRoles());

        clone.setId(getId());
        return clone;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
@Entity
@Table(
    name = "clients",
    uniqueConstraints = @UniqueConstraint(columnNames = {"firstName", "lastName", "shortCode"})
)
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = Client.NQ_FIND_CLIENT_BY_NAME_AND_SHORTCODE,
                query="SELECT c FROM Client c " +
                      "WHERE c.firstName = :firstName " +
                      "AND c.lastName = :lastName " +
                      "AND c.shortCode = :shortCode"),
    @NamedQuery(name = Client.NQ_FIND_CLIENT_BY_LONGCODE,
                query="SELECT c FROM Client c " +
                      "WHERE c.longCode = :longCode"),

    @NamedQuery(name = Client.NQ_FIND_CLIENT_BY_ID_WITH_PAGES,
                query="SELECT c FROM Client c " +
                      "LEFT JOIN FETCH c.pages p " +
                      "WHERE c.id = :client"),
    @NamedQuery(name = Client.NQ_FIND_CLIENT_BY_ID_WITH_RELATIONS,
                query="SELECT c FROM Client c " +
                      "LEFT JOIN FETCH c.relations r " +
                      "WHERE c.id = :client"),
    @NamedQuery(name = Client.NQ_FIND_CLIENT_BY_ID_WITH_CONTACTS,
                query="SELECT c FROM Client c " +
                      "LEFT JOIN FETCH c.contacts m " +
                      "WHERE c.id = :client"),
    @NamedQuery(name = Client.NQ_FIND_CLIENT_BY_ID_WITH_FRIENDS,
                query="SELECT c FROM Client c " +
                      "LEFT JOIN FETCH c.friends f " +
                      "WHERE c.id = :client"),
    @NamedQuery(name = Client.NQ_FIND_CLIENT_BY_ID_WITH_FRIENDOF,
                query="SELECT c FROM Client c " +
                      "LEFT JOIN FETCH c.friendOf f " +
                      "WHERE c.id = :client")
})
//</editor-fold>
public class Client extends SimpleEntityBean
{
    public static final String NQ_FIND_CLIENT_BY_NAME_AND_SHORTCODE = "findClientByNameAndShortCode";
    public static final String NQ_FIND_CLIENT_BY_LONGCODE = "findClientByLongCode";
    public static final String NQ_FIND_CLIENT_BY_ID_WITH_PAGES = "findClientByIdWithPages";
    public static final String NQ_FIND_CLIENT_BY_ID_WITH_RELATIONS = "findClientByIdWithRelations";
    public static final String NQ_FIND_CLIENT_BY_ID_WITH_CONTACTS = "findClientByIdWithContacts";
    public static final String NQ_FIND_CLIENT_BY_ID_WITH_FRIENDS = "findClientByIdWithFriends";
    public static final String NQ_FIND_CLIENT_BY_ID_WITH_FRIENDOF = "findClientByIdWithFriendOf";


    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "shortCode", nullable = false)
    private String shortCode;

    @Column(name = "longCode", nullable = false, unique = true)
    private String longCode;

    @ManyToOne
    @JoinColumn(name = "avatarId", nullable = true)
    private Avatar avatar;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("isStartPage ASC, name ASC")
    @JoinColumn(name = "clientId", nullable = false)
    private List<Page> pages = new ArrayList<Page>();

    @OneToMany(mappedBy = "client", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Relation> relations;

    @ManyToMany
    @JoinTable(name = "assignedContacts",
        joinColumns = {@JoinColumn(name = "clientId")},
        inverseJoinColumns = {@JoinColumn(name = "contactId")}
    )
    private Set<Contact> contacts;

    @ManyToMany
    @JoinTable(name = "friends",
        joinColumns = {@JoinColumn(name = "clientId")},
        inverseJoinColumns = {@JoinColumn(name = "otherClientId")}
    )
    private Set<Client> friends;

    @ManyToMany(mappedBy = "friends")
    private Set<Client> friendOf;


    public String getFirstName() {
        return this.firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getShortCode() {
        return shortCode;
    }
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getLongCode() {
        return longCode;
    }
    public void setLongCode(String longCode) {
        this.longCode = longCode;
    }

    public Avatar getAvatar() {
        return avatar;
    }
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public List<Page> getPages() {
        return pages;
    }
    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public Set<Relation> getRelations() {
        return relations;
    }
    public void setRelations(Set<Relation> relations) {
        this.relations = relations;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }
    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    public Set<Client> getFriends() {
        return friends;
    }
    public void setFriends(Set<Client> friends) {
        this.friends = friends;
    }

    public Set<Client> getFriendOf() {
        return friendOf;
    }
    public void setFriendOf(Set<Client> friendOf) {
        this.friendOf = friendOf;
    }


    //<editor-fold defaultstate="collapsed" desc="Convenience functions">
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
    //</editor-fold>


    @Override
    public SimpleEntityBean clone() {
        Client clone = new Client();

        clone.setFirstName(getFirstName());
        clone.setLastName(getLastName());
        clone.setShortCode(getShortCode());
        clone.setLongCode(getLongCode());
        clone.setAvatar(getAvatar());
        clone.setPages(getPages());
        clone.setRelations(getRelations());
        clone.setContacts(getContacts());
        clone.setFriends(getFriends());
        clone.setFriendOf(getFriendOf());

        clone.setId(getId());
        return clone;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Steven
 */
@Entity
@Table(name = "users")
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = User.NQ_FIND_USER_BY_ID,
                query="SELECT u FROM User u " +
                      "WHERE u.id = :id"),
    @NamedQuery(name = User.NQ_FIND_USER_BY_EMAIL,
                query="SELECT u FROM User u " +
                      "WHERE u.email = :email"),
    @NamedQuery(name = User.NQ_FIND_USERS_WITH_GLOBAL_ROLE,
                query="SELECT u FROM User u " +
                      "INNER JOIN u.globalRole g " +
                      "WHERE g.name IN (:roles)")
})
//</editor-fold>
public class User extends SimpleEntityBean
{
    public static final String NQ_FIND_USER_BY_ID = "findUserById";
    public static final String NQ_FIND_USER_BY_EMAIL = "findUserByEmail";
    public static final String NQ_FIND_USERS_WITH_GLOBAL_ROLE = "findUsersWithGlobalRole";


    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "complete", nullable = false)
    private Boolean complete = false;

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = true)
    private Role globalRole;

    @ManyToOne
    @JoinColumn(name = "avatarId", nullable = true)
    private Avatar avatar;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<Relation> relations;


    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getVerified() {
        return verified;
    }
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getComplete() {
        return complete;
    }
    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Role getGlobalRole() {
        return globalRole;
    }
    public void setGlobalRole(Role role) {
        this.globalRole = role;
    }

    public Avatar getAvatar() {
        return avatar;
    }
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Set<Relation> getRelations() {
        return this.relations;
    }
    public void setRelations(Set<Relation> relations) {
        this.relations = relations;
    }


    //<editor-fold defaultstate="collapsed" desc="Convenience functions">
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
    public Boolean passwordMatches(String password) {
        String hash = DigestUtils.sha256Hex(password);
        return this.getPassword().equals(hash);
    }
    //</editor-fold>


    @Override
    public SimpleEntityBean clone() {
        User clone = new User();

        clone.setFirstName(getFirstName());
        clone.setLastName(getLastName());
        clone.setEmail(getEmail());
        clone.setGlobalRole(getGlobalRole());
        clone.setAvatar(getAvatar());
        clone.setRelations(getRelations());
        clone.setPassword(getPassword());
        clone.setComplete(getComplete());
        clone.setVerified(getVerified());

        clone.setId(getId());
        return clone;
    }
}

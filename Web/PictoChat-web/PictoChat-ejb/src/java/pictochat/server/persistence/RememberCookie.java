/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
@Entity
@Table(name = "cookies")
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = RememberCookie.NQ_FIND_REMEMBERCOOKIE_BY_UUID,
                query="SELECT c FROM RememberCookie c " +
                      "WHERE c.uuid = :uuid"),
    @NamedQuery(name = RememberCookie.NQ_DELETE_REMEMBERCOOKIE_FOR_USER,
                query="DELETE FROM RememberCookie c " +
                      "WHERE c.user.id = :user")
})
//</editor-fold>
public class RememberCookie extends SimpleEntityBean
{
    public static final String NQ_FIND_REMEMBERCOOKIE_BY_UUID = "findCookieByUUID";
    public static final String NQ_DELETE_REMEMBERCOOKIE_FOR_USER = "deleteCookieForUser";


    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;


    public String getUUID() {
        return uuid;
    }
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public SimpleEntityBean clone() {
        RememberCookie clone = new RememberCookie();

        clone.setUUID(getUUID());
        clone.setUser(getUser());

        clone.setId(getId());
        return clone;
    }
}

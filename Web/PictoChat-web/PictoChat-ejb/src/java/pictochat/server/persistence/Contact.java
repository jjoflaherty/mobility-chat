/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
@Entity
@Table(name = "contacts")
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = Contact.NQ_FIND_CONTACT_BY_PHONENUMBER,
                query="SELECT c FROM Contact c " +
                      "WHERE c.phoneNumber = :phoneNumber")
})
//</editor-fold>
public class Contact extends SimpleEntityBean
{
    public static final String NQ_FIND_CONTACT_BY_PHONENUMBER = "findContactByPhoneNumber";


    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "phoneNumber", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "complete", nullable = false)
    private Boolean complete = false;

    @ManyToMany(mappedBy = "contacts")
    private Set<Client> clients;


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

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Set<Client> getClients() {
        return clients;
    }
    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }


    //<editor-fold defaultstate="collapsed" desc="Convenience functions">
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
    //</editor-fold>


    @Override
    public SimpleEntityBean clone() {
        Contact clone = new Contact();

        clone.setFirstName(getFirstName());
        clone.setLastName(getLastName());
        clone.setPhoneNumber(getPhoneNumber());
        clone.setPassword(getPassword());
        clone.setComplete(getComplete());
        clone.setVerified(getVerified());
        clone.setClients(getClients());

        clone.setId(getId());
        return clone;
    }
}

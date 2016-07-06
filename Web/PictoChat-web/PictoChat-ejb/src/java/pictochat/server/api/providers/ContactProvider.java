package pictochat.server.api.providers;


import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Steven
 */
@XmlRootElement(name = "contact")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ContactProvider
{
    @XmlElement(required = true)
    private Long id;

    @XmlElement(required = true)
    private String firstName;

    @XmlElement(required = true)
    private String lastName;

    @XmlElement(required = true)
    private List<ClientProvider> clients;


    public ContactProvider() {}
    public ContactProvider(Long id, String firstName, String lastName) {
        this(id, firstName, lastName, null);
    }
    public ContactProvider(Long id, String firstName, String lastName, List<ClientProvider> clients) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.clients = clients;
    }


    public Long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public List<ClientProvider> getClients() {
        return clients;
    }
}

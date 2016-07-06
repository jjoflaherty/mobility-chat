package pictochat.server.api.providers;


import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Steven
 */
@XmlRootElement(name = "coach")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CoachProvider
{
    @XmlElement(required = true)
    private Long id;

    @XmlElement(required = true)
    private String firstName;

    @XmlElement(required = true)
    private String lastName;

    @XmlElement(required = true)
    private String imageUrl;

    @XmlElement(required = true)
    private List<ClientProvider> clients;


    public CoachProvider() {}
    public CoachProvider(Long id, String firstName, String lastName, String imageUrl) {
        this(id, firstName, lastName, imageUrl, null);
    }
    public CoachProvider(Long id, String firstName, String lastName, String imageUrl, List<ClientProvider> clients) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
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
    public String getImageUrl() {
        return imageUrl;
    }
    public List<ClientProvider> getClients() {
        return clients;
    }
}

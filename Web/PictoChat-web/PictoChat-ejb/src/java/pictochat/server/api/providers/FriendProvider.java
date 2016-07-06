package pictochat.server.api.providers;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Steven
 */
@XmlRootElement(name = "friend")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FriendProvider
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
    private Boolean host;


    public FriendProvider() {}
    public FriendProvider(Long id, String firstName, String lastName, String imageUrl, Boolean host) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.host = host;
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
    public Boolean getHost() {
        return host;
    }
}

package pictochat.server.api.providers;


import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Steven
 */
@XmlRootElement(name = "client")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClientProvider
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
    private String code;

    @XmlElement(required = true)
    private PageProvider startPage;

    @XmlElement(required = true)
    private List<PageProvider> pages;

    @XmlElement(required = true)
    private List<CoachProvider> coaches;

    @XmlElement(required = true)
    private List<ContactProvider> contacts;

    @XmlElement(required = true)
    private List<FriendProvider> friends;

    @XmlElement(required = true)
    private List<PageButtonProvider> buttons;


    public ClientProvider() {}
    public ClientProvider(Long id, String firstName, String lastName, String imageUrl, String code, List<CoachProvider> coaches, List<PageButtonProvider> buttons) {
        this(id, firstName, lastName, imageUrl, code, coaches, null, null, null, null);

        this.buttons = buttons;
    }
    public ClientProvider(Long id, String firstName, String lastName, String imageUrl, String code, List<CoachProvider> coaches, List<ContactProvider> contacts, List<FriendProvider> friends, List<PageProvider> pages, PageProvider startPage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.code = code;
        this.coaches = coaches;
        this.contacts = contacts;
        this.friends = friends;
        this.pages = pages;
        this.startPage = startPage;
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
    public String getCode() {
        return code;
    }
    public List<CoachProvider> getCoaches() {
        return coaches;
    }
    public List<ContactProvider> getContacts() {
        return contacts;
    }
    public List<FriendProvider> getFriends() {
        return friends;
    }
    public PageProvider getStartPage() {
        return startPage;
    }
    public List<PageProvider> getPages() {
        return pages;
    }
    public List<PageButtonProvider> getButtons() {
        return buttons;
    }
}

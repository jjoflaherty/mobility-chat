package pictochat.server.api.providers;


import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Steven
 */
@XmlRootElement(name = "page")
public class PageProvider
{
    @XmlElement(required = true)
    private Long id;

    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private Integer rows;

    @XmlElement(required = true)
    private Integer columns;

    @XmlElement(required = true)
    private List<PageButtonProvider> buttons;


    public PageProvider() {}
    public PageProvider(Long id, String name, Integer rows, Integer columns, List<PageButtonProvider> buttons) {
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.buttons = buttons;
    }


    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Integer getRows() {
        return rows;
    }
    public Integer getColumns() {
        return columns;
    }
    public List<PageButtonProvider> getButtons() {
        return buttons;
    }
}

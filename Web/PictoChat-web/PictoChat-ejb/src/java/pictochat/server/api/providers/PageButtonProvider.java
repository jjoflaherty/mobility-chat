package pictochat.server.api.providers;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import pictochat.server.persistence.enums.Action;
import pictochat.server.persistence.enums.Icon;

/**
 *
 * @author Steven
 */
@XmlRootElement(name = "button")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PageButtonProvider
{
    @XmlElement(required = true)
    private Long id;

    @XmlElement(required = true)
    private Integer cell;

    @XmlElement(required = true)
    private String color;

    @XmlElement(required = true)
    private Action action;

    @XmlElement(required = false)
    private String text;

    @XmlElement(required = false)
    private Long targetPageId;

    @XmlElement(required = true)
    private Icon icon;

    @XmlElement(required = true)
    private String url;


    public PageButtonProvider() {}
    public PageButtonProvider(String text, String url) {
        this(null, null, null, null, text, null, null, url);
    }
    public PageButtonProvider(Long id, Integer cell, String color, Action action, String text, Long targetPageId, Icon icon, String url) {
        this.id = id;
        this.cell = cell;
        this.color = color;
        this.action = action;
        this.text = text;
        this.targetPageId = targetPageId;
        this.icon = icon;
        this.url = url;
    }


    public Long getId() {
        return id;
    }
    public Integer getCell() {
        return cell;
    }
    public String getColor() {
        return color;
    }
    public Action getAction() {
        return action;
    }
    public String getText() {
        return text;
    }
    public Long getTargetPageId() {
        return targetPageId;
    }
    public Icon getIcon() {
        return icon;
    }
    public String getUrl() {
        return url;
    }
}

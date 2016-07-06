/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;
import pictochat.server.Settings;
import pictochat.server.persistence.enums.Action;
import pictochat.server.persistence.enums.Icon;
import static pictochat.server.persistence.enums.Icon.Beta;
import static pictochat.server.persistence.enums.Icon.Sclera;
import static pictochat.server.persistence.enums.Icon.Upload;

/**
 *
 * @author Steven
 */
@Entity
@Table(
    name = "buttons",
    uniqueConstraints = @UniqueConstraint(columnNames = {"pageId", "cell"})
)
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = PageButton.NQ_FIND_PAGEBUTTON_BY_ID,
                query="SELECT b FROM PageButton b " +
                      "WHERE b.id = :button"),
    @NamedQuery(name = PageButton.NQ_FIND_BUTTONS_FOR_CLIENT,
                query="SELECT b FROM Client c " +
                      "LEFT JOIN c.pages p " +
                      "LEFT JOIN p.buttons b " +
                      "WHERE c.id = :client " +
                      "AND b.icon = 'Upload'")
})
//</editor-fold>
public class PageButton extends SimpleEntityBean
{
    public static final String NQ_FIND_PAGEBUTTON_BY_ID = "findPageButtonById";
    public static final String NQ_FIND_BUTTONS_FOR_CLIENT = "findButtonsForClient";


    @Column(name = "cell", nullable = false)
    private Integer cell;

    @Column(name = "color", nullable = true)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "icon", nullable = false)
    private Icon icon;

    @Column(name = "url", nullable = true)
    private String url;

    @ManyToOne
    @JoinColumn(name = "imageId", nullable = true)
    private ButtonImage image;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;

    @Column(name = "text", nullable = true)
    private String text;

    @ManyToOne
    @JoinColumn(name = "targetPageId", nullable = true)
    private Page targetPage;


    protected PageButton() {
        //Required by Java EE spec for Entity Beans
    }


    public Integer getCell() {
        return cell;
    }
    public void setCell(Integer cell) {
        this.cell = cell;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public Icon getIcon() {
        return icon;
    }
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public ButtonImage getImage() {
        return image;
    }
    public void setImage(ButtonImage image) {
        this.image = image;
    }

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Page getTargetPage() {
        return targetPage;
    }
    public void setTargetPage(Page targetPage) {
        this.targetPage = targetPage;
    }


    public static PageButton buildDefault(Integer cell) {
        PageButton button = new PageButton();

        button.setId(null);
        button.setCell(cell);
        button.action = Action.Text;
        button.icon = Icon.Beta;
        button.color = "#ffffff";

        return button;
    }


    //<editor-fold defaultstate="collapsed" desc="Convenience functions">
    public String getFullUrl() {
        String url = null;

        switch (this.getIcon()) {
            case Beta:
                url =  Settings.BETA_URL + this.getUrl();
                break;
            case Sclera:
                url = Settings.SCLERA_URL + this.getUrl();
                break;
            case Upload:
                url = Settings.APP_URL + this.getImage().getImageUrl();
                break;
        }

        return url;
    }
    //</editor-fold>


    @Override
    public SimpleEntityBean clone() {
        PageButton clone = new PageButton();

        clone.setCell(getCell());
        clone.setColor(getColor());
        clone.setIcon(getIcon());
        clone.setUrl(getUrl());
        clone.setImage(getImage());
        clone.setAction(getAction());
        clone.setText(getText());
        clone.setTargetPage(getTargetPage());

        clone.setId(getId());
        return clone;
    }
}

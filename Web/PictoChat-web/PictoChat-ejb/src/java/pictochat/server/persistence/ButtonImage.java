/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;
import pictochat.server.Settings;

/**
 *
 * @author Steven
 */
@Entity
@Table(name = "images")
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = ButtonImage.NQ_FIND_BUTTONIMAGE_BY_ID,
                query="SELECT i FROM ButtonImage i " +
                      "WHERE i.id = :image")
})
//</editor-fold>
public class ButtonImage extends SimpleEntityBean
{
    public static final String NQ_FIND_BUTTONIMAGE_BY_ID = "findButtonImageById";


    @Column(name = "data", length = Settings.MAX_IMAGE_DATA, nullable = false)
    private byte[] data;

    @Column(name = "mime", nullable = false)
    private String mime;


    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }

    public String getMime() {
        return mime;
    }
    public void setMime(String mime) {
        this.mime = mime;
    }


    //<editor-fold defaultstate="collapsed" desc="Convenience functions">
    public String getImageUrl() {
        return "/images?id=" + this.getId();
    }
    public String getBackgroundImageStyle() {
        return "background-image: url(" + Settings.APP_PATH + this.getImageUrl() + ");";
    }
    //</editor-fold>


    @Override
    public SimpleEntityBean clone() {
        ButtonImage clone = new ButtonImage();

        clone.setData(getData());
        clone.setMime(getMime());

        clone.setId(getId());
        return clone;
    }
}

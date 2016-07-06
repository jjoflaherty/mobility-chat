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
import pictochat.server.Settings;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
@Entity
@Table(name = "avatars")
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = Avatar.NQ_FIND_AVATAR_BY_ID,
                query="SELECT a FROM Avatar a " +
                      "WHERE a.id = :avatar")
})
//</editor-fold>
public class Avatar extends SimpleEntityBean
{
    public static final String NQ_FIND_AVATAR_BY_ID = "findAvatarById";


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
        return "/avatar?id=" + this.getId();
    }
    public String getFullImageUrl() {
        return Settings.APP_URL + getImageUrl();
    }
    public String getBackgroundImageStyle() {
        return "background-image: url(" + Settings.APP_PATH + this.getImageUrl() + ");";
    }
    //</editor-fold>


    @Override
    public SimpleEntityBean clone() {
        Avatar clone = new Avatar();

        clone.setData(getData());
        clone.setMime(getMime());

        clone.setId(getId());
        return clone;
    }
}

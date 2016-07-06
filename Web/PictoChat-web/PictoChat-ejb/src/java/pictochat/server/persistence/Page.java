/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.persistence;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import kpoint.javaee.server.core.persistence.SimpleEntityBean;

/**
 *
 * @author Steven
 */
@Entity
@Table(name = "pages")
//<editor-fold defaultstate="collapsed" desc="NamedQueries">
@NamedQueries({
    @NamedQuery(name = Page.NQ_FIND_PAGE_BY_ID,
                query="SELECT p FROM Page p " +
                      "WHERE p.id = :page")
})
//</editor-fold>
public class Page extends SimpleEntityBean
{
    public static final String NQ_FIND_PAGE_BY_ID = "findPageById";


    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "rows", nullable = false)
    private Integer rows = 3;

    @Column(name = "columns", nullable = false)
    private Integer columns = 3;

    @Column(name = "startPage", nullable = false)
    private Boolean isStartPage = false;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("cell ASC")
    @JoinColumn(name = "pageId", nullable = false)
    private List<PageButton> buttons;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getRows() {
        return rows;
    }
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }
    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Boolean getIsStartPage() {
        return isStartPage;
    }
    public void setIsStartPage(Boolean isStartPage) {
        this.isStartPage = isStartPage;
    }

    public List<PageButton> getButtons() {
        return buttons;
    }
    public void setButtons(List<PageButton> buttons) {
        this.buttons = buttons;
    }


    @Override
    public SimpleEntityBean clone() {
        Page clone = new Page();

        clone.setName(getName());
        clone.setRows(getRows());
        clone.setColumns(getColumns());
        clone.setIsStartPage(getIsStartPage());
        clone.setButtons(getButtons());

        clone.setId(getId());
        return clone;
    }
}

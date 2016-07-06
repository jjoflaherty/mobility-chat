package pictochat.client.web.handler.page;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import org.apache.log4j.Logger;
import pictochat.client.web.handler.page.fdto.PageButtonFDTO;
import pictochat.client.web.handler.page.fdto.PageButtonRowFDTO;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.server.dal.local.IPageDAOLocal;
import pictochat.server.persistence.Page;
import pictochat.server.persistence.PageButton;
import pictochat.server.persistence.enums.Icon;

/**
 *
 * @author Steven
 */
public class PreviewPageHandler
{
    private static final Logger LOG = Logger.getLogger(PreviewPageHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;

    private static final String BETA_URL = "http://webservices.ccl.kuleuven.be/picto/beta/";
    private static final String SCLERA_URL = "http://webservices.ccl.kuleuven.be/picto/sclera/";


    private Page activePage;
    private Long pageId;
    private Integer rows;
    private Integer columns;

    public Page getActivePage() {
        return activePage;
    }
    public void setActivePage(Page activePage) {
        this.activePage = activePage;

        this.rows = activePage.getRows();
        this.columns = activePage.getColumns();
    }

    public Long getPageId() {
        return pageId;
    }
    public void setPageId(Long pageId) {
        if (pageId != null) {
            this.pageId = pageId;
            this.load(pageId);
        }
    }


    public String getPreviewStyle() {
        switch (this.columns) {
            case 12:
                return "col-sm-12";
            case 6:
                return "col-sm-8";
            default:
                return "col-sm-4";
        }
    }
    public List<PageButtonRowFDTO> getFindAllButtonRowsForCurrentPage() {
        List<PageButtonRowFDTO> rows = new ArrayList<PageButtonRowFDTO>();

        if (this.activePage != null) {
            Map<Integer, PageButton> map = new HashMap<Integer, PageButton>();
            for (PageButton b : this.activePage.getButtons()) {
                if (!map.containsKey(b.getCell()))
                    map.put(b.getCell(), b);
            }

            Integer index = 0;
            Integer cell = 0;
            List<PageButtonFDTO> buttons = new ArrayList<PageButtonFDTO>();
            for (int i = 1; i <= this.rows * this.columns; i++) {
                PageButton b = map.get(i);
                PageButtonFDTO button;

                if (map.containsKey(i)) {
                    String url = null;

                    Icon icon = b.getIcon();
                    if (icon != null) {
                        switch (icon) {
                            case Beta:
                                url = "background-image: url(" + BETA_URL + b.getUrl() + ");";
                                break;
                            case Sclera:
                                url = "background-image: url(" + SCLERA_URL + b.getUrl() + ");";
                                break;
                            case Upload:
                                url = b.getImage().getBackgroundImageStyle();
                                break;
                        }
                    }

                    button = new PageButtonFDTO(b.getId(), cell++, b.getColor(), url, this.getColumnStyleForColumnCount(this.columns));
                }
                else
                    button = new PageButtonFDTO(null, cell++, "#ffffff", null, this.getColumnStyleForColumnCount(this.columns));

                buttons.add(button);
                if (buttons.size() == this.columns) {
                    rows.add(new PageButtonRowFDTO(index++, buttons));
                    buttons = new ArrayList<PageButtonFDTO>();
                }
            }

            if (!buttons.isEmpty())
                rows.add(new PageButtonRowFDTO(index++, buttons));
        }

        return rows;
    }

    private String getColumnStyleForColumnCount(int columns) {
        return "col-sm-" + (12 / columns);
    }


    /*public String show() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Long pageId = Long.parseLong(ctx.getExternalContext().getRequestParameterMap().get("page"));
        if (this.pageDAO != null && pageId != null)
            return load(pageId);

        return null;
    }
    public void close() {
        this.setActivePage(null);
    }*/

    private String load(Long pageId) {
        try {
            this.setActivePage(this.pageDAO.findPageById(pageId));
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
    }



    @PostConstruct
    public void postConstruct() {
        this.navigatingHandler = new AbstractNavigatingHandler(){
            @Override
            public Logger getLogger() {
                return LOG;
            }

            @Override
            public MethodLogger getMethodLogger() {
                return METHOD_LOG;
            }
        };
        this.navigatingHandler.postConstruct();
    }





    //DAO Objects
    private IPageDAOLocal pageDAO;

    public IPageDAOLocal getPageDAO() {
        return pageDAO;
    }
    public void setPageDAO(IPageDAOLocal pageDAO) {
        this.pageDAO = pageDAO;
    }
}
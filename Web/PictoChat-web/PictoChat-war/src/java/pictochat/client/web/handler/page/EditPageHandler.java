package pictochat.client.web.handler.page;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.log4j.Logger;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.server.dal.local.IPageDAOLocal;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
public class EditPageHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(EditPageHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;


    private Page page;

    public Page getPage() {
        return page;
    }
    public void setPage(Page page) {
        this.page = page;
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


    public String save() {
        if (getPageDAO() != null) {
            try {
                this.getPageDAO().update(this.page);

                return this.navigatingHandler.getNavigateTo().getSecure().getPage().getAdminAction();
            } catch(StorageException ex) {
                LOG.error(null, ex);
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            } catch (EntityNotFoundException ex) {
                LOG.error(null, ex);
            }
        }

        return null;
    }


    public String show() {
        LOG.debug("show");
        FacesContext ctx = FacesContext.getCurrentInstance();
        Long pageId = Long.parseLong(ctx.getExternalContext().getRequestParameterMap().get("page"));
        if (this.pageDAO != null && pageId != null)
            return load(pageId);

        return null;
    }
    private String load(Long pageId) {
        try {
            LOG.debug("load " + pageId);
            this.setPage(this.pageDAO.findPageById(pageId));
            return this.navigatingHandler.getNavigateTo().getSecure().getPage().getEditAction();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
    }


    public List<SelectItem> getRowCounts() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        selectItems.add(new SelectItem(4, "4"));
        selectItems.add(new SelectItem(3, "3"));
        selectItems.add(new SelectItem(2, "2"));
        selectItems.add(new SelectItem(1, "1"));

        return selectItems;
    }
    public List<SelectItem> getColumnCounts() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        selectItems.add(new SelectItem(3, "3"));
        selectItems.add(new SelectItem(6, "6"));
        selectItems.add(new SelectItem(12, "12"));

        return selectItems;
    }


    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getNameMessages());
        list.add(this.getRowsMessages());
        list.add(this.getColumnsMessages());

        return list;
    }

    public FormInputField getNameMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.EDIT_PAGE);
        return this.getFormInputField(resourceBundle.getString("name"), "page_form:name");
    }
    public FormInputField getRowsMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.EDIT_PAGE);
        return this.getFormInputField(resourceBundle.getString("rows"), "page_form:rows");
    }
    public FormInputField getColumnsMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.EDIT_PAGE);
        return this.getFormInputField(resourceBundle.getString("columns"), "page_form:columns");
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
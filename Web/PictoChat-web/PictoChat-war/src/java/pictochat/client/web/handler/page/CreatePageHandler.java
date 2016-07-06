package pictochat.client.web.handler.page;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.log4j.Logger;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
public class CreatePageHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(CreatePageHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);


    private Page page = new Page();
    private Client activeClient;

    public Page getPage() {
        return page;
    }
    public void setPage(Page page) {
        this.page = page;
    }

    public Client getActiveClient() {
        return activeClient;
    }
    public void setActiveClient(Client activeClient) {
        this.activeClient = activeClient;
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


    public String createPage() {
        if (getClientDAO() != null) {
            try {
                this.activeClient.getPages().add(this.page);
                this.getClientDAO().updatePages(this.activeClient);

                return "adminPage";
            } catch(StorageException ex) {
                LOG.error(null, ex);
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            }
        }

        return null;
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
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.CREATE_PAGE);
        return this.getFormInputField(resourceBundle.getString("name"), "page_form:name");
    }
    public FormInputField getRowsMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.CREATE_PAGE);
        return this.getFormInputField(resourceBundle.getString("rows"), "page_form:rows");
    }
    public FormInputField getColumnsMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.CREATE_PAGE);
        return this.getFormInputField(resourceBundle.getString("columns"), "page_form:columns");
    }





    //DAO Objects
    private IClientDAOLocal clientDAO;

    public IClientDAOLocal getClientDAO() {
        return clientDAO;
    }
    public void setClientDAO(IClientDAOLocal clientDAO) {
        this.clientDAO = clientDAO;
    }
}
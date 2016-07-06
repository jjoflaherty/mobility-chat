package pictochat.client.web.handler.page;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
public class DeletePageHandler
{
    private static final Logger LOG = Logger.getLogger(DeletePageHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);


    private Client activeClient;
    private Long pageId;

    public Client getActiveClient() {
        return activeClient;
    }
    public void setActiveClient(Client activeClient) {
        this.activeClient = activeClient;
    }

    public Long getPageId() {
        return pageId;
    }
    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }


    public void delete() {
        if (this.getClientDAO() != null && this.pageId != null) {
            try {
                Client withPages = clientDAO.findClientByIdWithPages(this.activeClient.getId());
                for (Page page : withPages.getPages())
                    if (pageId.equals(page.getId()))
                        withPages.getPages().remove(page);
                this.getClientDAO().updatePages(withPages);
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            } catch (EntityNotFoundException ex) {
                LOG.error(null, ex);
            } catch (StorageException ex) {
                LOG.error(null, ex);
            }
        }
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

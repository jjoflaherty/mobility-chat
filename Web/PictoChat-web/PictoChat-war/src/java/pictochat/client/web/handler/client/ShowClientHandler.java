package pictochat.client.web.handler.client;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import org.apache.log4j.Logger;
import pictochat.client.web.handler.fdto.ClientPageRowFDTO;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Page;

/**
 *
 * @author Steven
 */
public class ShowClientHandler
{
    private static final Logger LOG = Logger.getLogger(ShowClientHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;

    private static final int COLUMNS = 4;


    private Client activeClient;
    private Long clientId;

    public Client getActiveClient() {
        LOG.debug("getActiveClient");
        return activeClient;
    }
    public void setActiveClient(Client activeClient) {
        LOG.debug("setActiveClient");
        this.activeClient = activeClient;
    }

    public Long getClientId() {
        LOG.debug("getClientId");
        return clientId;
    }
    public void setClientId(Long clientId) {
        if (clientId != null) {
            LOG.debug("setClientId");
            this.clientId = clientId;
            this.load(clientId);
        }
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


    public List<ClientPageRowFDTO> getFindAllPageRowsForCurrentClient() {
        List<ClientPageRowFDTO> rows = new ArrayList<ClientPageRowFDTO>();

        if (this.activeClient != null) {
            List<Page> pages = new ArrayList<Page>();

            Integer index = 0;
            try {
                Client withPages = clientDAO.findClientByIdWithPages(this.activeClient.getId());
                SortedSet<Page> sortedPages = new TreeSet<Page>(new Comparator<Page>(){
                    public int compare(Page p1, Page p2) {
                        if (p1.getIsStartPage())
                            return -1;
                        else if (p2.getIsStartPage())
                            return 1;

                        return p1.getName().compareTo(p2.getName());
                    }
                });
                sortedPages.addAll(withPages.getPages());

                for (Page page : sortedPages) {
                    pages.add(page);

                    if (pages.size() >= COLUMNS) {
                        rows.add(new ClientPageRowFDTO(index++, pages));
                        pages = new ArrayList<Page>();
                    }
                }
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            } catch (EntityNotFoundException ex) {
                LOG.error(null, ex);
            } catch (StorageException ex) {
                LOG.error(null, ex);
            }

            if (!pages.isEmpty())
                rows.add(new ClientPageRowFDTO(index++, pages));
        }

        return rows;
    }

    public void detail() {
        this.show();
    }
    public String show() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Long clientId = Long.parseLong(ctx.getExternalContext().getRequestParameterMap().get("client"));
        if (this.clientDAO != null && clientId != null)
            return load(clientId);

        return null;
    }
    public void close() {
        this.setActiveClient(null);
    }

    private String load(Long clientId) {
        METHOD_LOG.debug("load", "client " + clientId);

        try {
            this.setActiveClient(this.clientDAO.findClientByIdWithPages(clientId));
            return this.navigatingHandler.getNavigateTo().getSecure().getPage().getAdminAction();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
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

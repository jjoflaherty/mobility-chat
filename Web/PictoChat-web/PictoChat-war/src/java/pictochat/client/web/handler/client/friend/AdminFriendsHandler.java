package pictochat.client.web.handler.client.friend;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import pictochat.client.web.handler.AbstractCompoundEntityHandler;
import pictochat.client.web.handler.fdto.ClientFDTO;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class AdminFriendsHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(AdminFriendsHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;
    private FriendsHandler friendsHandler = new FriendsHandler();
    private ClientsHandler clientsHandler = new ClientsHandler();

    private Client client;
    private User activeUser;
    private Long newFriendId;

    private Set<Client> updatedFriendOf = new HashSet<Client>();

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = null;

        try {
            Client withFriends = clientDAO.findClientByIdWithFriends(client.getId());
            client.setFriends(withFriends.getFriends());

            Client withFriendOf = clientDAO.findClientByIdWithFriendOf(client.getId());
            client.setFriendOf(withFriendOf.getFriendOf());

            this.client = client;
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        this.clientsHandler.clearModels();
        this.friendsHandler.clearModels();
    }

    public User getActiveUser() {
        return activeUser;
    }
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public Long getNewFriendId() {
        return newFriendId;
    }
    public void setNewFriendId(Long newFriendId) {
        this.newFriendId = newFriendId;
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


    public String show() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Long clientId = Long.parseLong(ctx.getExternalContext().getRequestParameterMap().get("client"));
        if (this.clientDAO != null && clientId != null)
            return load(clientId);

        return null;
    }
    private String load(Long clientId) {
        try {
            this.setClient(this.clientDAO.findClientByIdWithFriends(clientId));
            return this.navigatingHandler.getNavigateTo().getSecure().getClient().getFriends().getAdminAction();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
    }


    public void removeFriend() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Long clientId = Long.parseLong(ctx.getExternalContext().getRequestParameterMap().get("client"));
        if (clientId != null) {
            Client client = this.client;

            for (Client c : client.getFriends())
                if (c.getId().equals(clientId))
                    client.getFriends().remove(c);

            for (Client c : client.getFriendOf())
                if (c.getId().equals(clientId)) {
                    client.getFriendOf().remove(c);
                    updatedFriendOf.add(c);
                }

            clientsHandler.clearModels();
            friendsHandler.clearModels();
        }
    }
    public void addFriend() {
        try {
            Client friend = clientDAO.findClientByIdWithFriendOf(newFriendId);
            client.getFriends().add(friend);

            clientsHandler.clearModels();
            friendsHandler.clearModels();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        }
    }

    public String save() {
        try {
            //Remove all updated friendOf connections.
            //If they were added again, they will show up as friend connections instead.
            for (Client c : updatedFriendOf) {
                try {
                    Client withFriends = clientDAO.findClientByIdWithFriends(c.getId());
                    withFriends.getFriends().remove(this.client);
                    clientDAO.updateFriends(withFriends);
                } catch (EntityNotFoundException ex) {
                    LOG.error(null, ex);
                } catch (InvalidParameterException ex) {
                    LOG.error(null, ex);
                } catch (StorageException ex) {
                    LOG.error(null, ex);
                }
            }

            clientDAO.updateFriends(this.client);
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return navigatingHandler.getNavigateTo().getSecure().getDashboardAction();
    }



    public List<ClientFDTO> getFindAllFriendRowsForCurrentClient() {
        return this.friendsHandler.getData();
    }
    public List<SelectItem> getNewFriends() {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();

        List<ClientFDTO> friends = this.friendsHandler.getData();
        List<ClientFDTO> clients = this.clientsHandler.getData();

        for (ClientFDTO c : clients) {
            if (!c.getClient().equals(this.client) && !friends.contains(c))
                selectItems.add(new SelectItem(c.getId(), c.getName()));
        }

        return selectItems;
    }


    private class FriendsHandler extends AbstractCompoundEntityHandler<Client, ClientFDTO, Long> {
        public List<ClientFDTO> getFindAllFriendRowsForCurrentClient() {
            if (!this.isInit()) {
                this.clearModels();

                Client client = AdminFriendsHandler.this.client;
                List<Client> friends = new ArrayList<Client>();

                for (Client c : client.getFriends())
                    friends.add(c);

                for (Client c : client.getFriendOf())
                    friends.add(c);

                this.addModels(friends);
            }

            return this.getFormModels();
        }
        public void setFindAllFriendRowsForCurrentClient(List<ClientFDTO> data) {}

        @Override
        public ClientFDTO convertToFormModel(Client model) {
            Long id = convertDataModelToKey(model);
            return new ClientFDTO(id, id, model);
        }
        @Override
        public Long convertDataModelToKey(Client model) {
            return model.getId();
        }

        @Override
        public List<ClientFDTO> getData() {
            return this.getFindAllFriendRowsForCurrentClient();
        }
        @Override
        public void setData(List<ClientFDTO> data) {
            this.setFindAllFriendRowsForCurrentClient(data);
        }
    }
    private class ClientsHandler extends AbstractCompoundEntityHandler<Client, ClientFDTO, Long> {
        public List<ClientFDTO> getFindAllClientRowsForCurrentUser() {
            if (!this.isInit()) {
                this.clearModels();

                User user = AdminFriendsHandler.this.activeUser;

                List<Client> clients = new ArrayList<Client>();

                if (user != null) {
                    for (Relation relation : user.getRelations()) {
                        Client client = relation.getClient();
                        clients.add(client);
                    }
                }

                this.addModels(clients);
            }

            return this.getFormModels();
        }
        public void setFindAllClientRowsForCurrentUser(List<ClientFDTO> data) {}

        @Override
        public ClientFDTO convertToFormModel(Client model) {
            Long id = convertDataModelToKey(model);
            return new ClientFDTO(id, id, model);
        }
        @Override
        public Long convertDataModelToKey(Client model) {
            return model.getId();
        }

        @Override
        public List<ClientFDTO> getData() {
            return this.getFindAllClientRowsForCurrentUser();
        }
        @Override
        public void setData(List<ClientFDTO> data) {
            this.setFindAllClientRowsForCurrentUser(data);
        }
    }


    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getNewFriendMessages());

        return list;
    }

    public FormInputField getNewFriendMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.ADMIN_FRIENDS);
        return this.getFormInputField(resourceBundle.getString("name"), "clients_form:client");
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
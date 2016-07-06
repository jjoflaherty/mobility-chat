package pictochat.client.web.handler.client.relation;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
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
import pictochat.client.web.handler.SessionHandler;
import pictochat.client.web.handler.container.RelationRole;
import pictochat.client.web.handler.fdto.RelationFDTO;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractActionFormHandler;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.dal.local.IRelationDAOLocal;
import pictochat.server.dal.local.IRoleDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.Role;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class AdminClientRelationsHandler extends AbstractActionFormHandler
{
    private static final Logger LOG = Logger.getLogger(AdminClientRelationsHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;
    private RelationsHandler relationsHandler = new RelationsHandler();

    private List<Role> roles;

    private User activeUser;
    private Client client;

    public User getActiveUser() {
        return activeUser;
    }
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
        this.relationsHandler.clearModels();
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
        METHOD_LOG.debug("load", "client " + clientId);

        try {
            this.setClient(this.clientDAO.findClientByIdWithRelations(clientId));
            return this.navigatingHandler.getNavigateTo().getSecure().getClient().getCoaches().getAdminAction();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
    }
    private void loadRoles() {
        LOG.debug("loadRoles");

        if (getRoleDAO() != null && this.roles == null) {
            try {
                this.roles = new ArrayList<Role>();
                List<Role> roles = roleDAO.findAllRoles();
                for (Role r : roles) {
                    if (r.getGlobalOnly() == false)
                        this.roles.add(r);
                }
            } catch (StorageException ex) {
                LOG.fatal(null, ex);
            }
        }
    }
    public void refresh() {
        load(client.getId());
    }

    public void save() {
        LOG.debug("save");

        this.relationsHandler.save();
    }


    @Override
    public List<FormInputField> getAllMessages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public List<RoleColumnModel> getColumns() {
        List<RoleColumnModel> columns = new ArrayList<RoleColumnModel>();

        this.loadRoles();
        for (Role role : this.roles)
            columns.add(new RoleColumnModel(role.getName(), role.getName()));

        return columns;
    }
    public List<RelationFDTO> getFindAcceptedRelationsForCurrentClient() {
        List<RelationFDTO> list = new ArrayList<RelationFDTO>();

        for (RelationFDTO relation : this.relationsHandler.getData())
            if (relation.getAccepted())
                list.add(relation);

        return list;
    }
    public List<RelationFDTO> getFindOpenInvitationsForCurrentClient() {
        List<RelationFDTO> list = new ArrayList<RelationFDTO>();

        for (RelationFDTO relation : this.relationsHandler.getData())
            if (!relation.getAccepted())
                list.add(relation);

        return list;
    }

    private class RelationsHandler extends AbstractCompoundEntityHandler<Relation, RelationFDTO, Long> {
        private Relation activeRelation;

        public List<RelationFDTO> getFindAllRelationsForCurrentClient() {
            loadRoles();

            if (!this.isInit()) {
                this.clearModels();
                this.activeRelation = null;

                Client client = AdminClientRelationsHandler.this.client;
                if (client != null) {
                    for (Relation relation : client.getRelations()) {
                        if (relation.getUser().equals(AdminClientRelationsHandler.this.activeUser))
                            this.activeRelation = relation;

                        this.addModel(relation);
                    }
                }

                this.finish();
            }

            return this.getFormModels();
        }
        public void setFindAllRelationsForCurrentClient(List<RelationFDTO> data) {}

        public void save() {
            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.ADMIN_COACHES);

            try {
                int counter = 0;
                StringBuilder sb = new StringBuilder(resourceBundle.getString("error_not_saved")).append("<ul>");

                for (Iterator<RelationFDTO> it = this.getFormModels().iterator(); it.hasNext(); ) {
                    RelationFDTO edit = it.next();
                    Relation original = this.getDataModelFromFormModel(edit);

                    try {
                        if (edit.getCanRemove() && edit.getRemove()) {
                            METHOD_LOG.debug("save", String.format("destroy relation %s", original.getId()));
                            relationDAO.destroy(original);
                        }
                        else {
                            Boolean updateRelation = false;

                            if (edit.getCanEditActive() && !original.getActive().equals(edit.getActive())) {
                                original.setActive(edit.getActive());
                                updateRelation = true;
                            }
                            if (edit.getCanEditBlocked() && !original.getBlocked().equals(edit.getBlocked())) {
                                original.setBlocked(edit.getBlocked());
                                updateRelation = true;
                            }

                            if (updateRelation) {
                                METHOD_LOG.debug("save", String.format("update relation %s", original.getId()));
                                relationDAO.update(original);
                            }
                        }

                        counter++;
                    } catch (InvalidParameterException ex) {
                        LOG.error(null, ex);
                        sb.append("<li>").append(String.format(resourceBundle.getString("error_not_saved"), original.getUser().getFullName())).append("</li>");
                    } catch (EntityNotFoundException ex) {
                        LOG.error(null, ex);
                        sb.append("<li>").append(String.format(resourceBundle.getString("error_not_found"), original.getUser().getFullName())).append("</li>");
                    }
                }
                sb.append("</ul>");

                if (counter == this.getModelCount())
                    addSuccessMessage(resourceBundle.getString("success"), resourceBundle.getString("success_saved"));
                else
                    addWarningMessage(resourceBundle.getString("warning"), sb.toString());
            } catch (StorageException ex) {
                LOG.error(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_try_later"));
            }

            refresh();
            relationsHandler.clearModels();
            sessionHandler.refresh();
        }

        @Override
        public RelationFDTO convertToFormModel(Relation model) {
            Long id = convertDataModelToKey(model);

            Boolean isAdmin = (this.activeRelation != null && this.activeRelation.isAdmin());
            Boolean isSelf = model.getUser().equals(activeUser);

            List<RelationRole> roles = new ArrayList<RelationRole>();
            for (Role role : AdminClientRelationsHandler.this.roles)
                roles.add(new RelationRole(role, model.getRoles().contains(role), !isAdmin));

            return new RelationFDTO(id, id, model, roles, isAdmin || isSelf, isAdmin, (isAdmin && !isSelf) || (!isAdmin && isSelf));
        }
        @Override
        public Long convertDataModelToKey(Relation model) {
            return model.getId();
        }

        @Override
        public List<RelationFDTO> getData() {
            return this.getFindAllRelationsForCurrentClient();
        }
        @Override
        public void setData(List<RelationFDTO> data) {
            this.setFindAllRelationsForCurrentClient(data);
        }
    }





    //Dependant beans
    private SessionHandler sessionHandler;

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }
    public void setSessionHandler(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }


    //DAO Objects
    private IRoleDAOLocal roleDAO;
    private IClientDAOLocal clientDAO;
    private IRelationDAOLocal relationDAO;

    public IRoleDAOLocal getRoleDAO() {
        return roleDAO;
    }
    public void setRoleDAO(IRoleDAOLocal roleDAO) {
        this.roleDAO = roleDAO;
    }

    public IClientDAOLocal getClientDAO() {
        return clientDAO;
    }
    public void setClientDAO(IClientDAOLocal clientDAO) {
        this.clientDAO = clientDAO;
    }

    public IRelationDAOLocal getRelationDAO() {
        return relationDAO;
    }
    public void setRelationDAO(IRelationDAOLocal relationDAO) {
        this.relationDAO = relationDAO;
    }
}

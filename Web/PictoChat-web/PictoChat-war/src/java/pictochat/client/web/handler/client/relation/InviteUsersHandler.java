package pictochat.client.web.handler.client.relation;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.util.mailer.IJavaMailer;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.log4j.Logger;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.handler.fdto.EmailFDTO;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.client.web.templates.TemplateResource;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.dal.local.IRelationDAOLocal;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;
import pictochat.server.util.RandomString;

/**
 *
 * @author Steven
 */
public class InviteUsersHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(InviteUsersHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;

    private String localUrl;
    private List<EmailFDTO> emails = new ArrayList<EmailFDTO>();

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

        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = ctx.getExternalContext();

        String url = ectx.getRequestContextPath();

        HttpServletRequest request = (HttpServletRequest)ectx.getRequest();
        String server = request.getServerName();
        int port = request.getServerPort();

        this.localUrl = server;
        if (port != 80)
            this.localUrl += ":" + port;
        this.localUrl += url;
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

            this.emails = new ArrayList<EmailFDTO>();
            this.emails.add(new EmailFDTO(""));

            return this.navigatingHandler.getNavigateTo().getSecure().getClient().getCoaches().getInviteAction();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
    }


    public String inviteUsers() {
        if (getUserDAO() != null) {
            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.INVITE_COACHES);

            try {
                String sender = this.activeUser.getFullName();

                for (EmailFDTO emailFDTO : this.emails) {
                    String email = emailFDTO.getEmail();
                    if (!emailFDTO.getReadOnly() && email != null && !email.isEmpty()) {
                        User user = null;

                        try {
                            user = userDAO.findUserByEmail(email);
                        } catch (EntityNotFoundException ex) {
                            RandomString rs = new RandomString(8);
                            String password = rs.nextString();

                            try {
                                user = userDAO.create("", "", email, password, null, null, false);
                                user.setPassword(password);
                            } catch (OperationFailedException ex1) {
                                LOG.error(null, ex1);
                                emailFDTO.addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_new_account"));
                            }
                        }

                        if (user != null) {
                            try {
                                if (relationDAO.doesRelationExistBetween(client, user)) {
                                    emailFDTO.addInfoMessage(resourceBundle.getString("info"), resourceBundle.getString("info_existing_relation"));
                                    emailFDTO.setReadOnly(true);
                                }
                                else {
                                    Relation relation = relationDAO.create(client, user, true, false);
                                    sendMail(user, relation, sender, client.getFullName());

                                    emailFDTO.addSuccessMessage(resourceBundle.getString("success"), resourceBundle.getString("success_invite_sent"));
                                    emailFDTO.setReadOnly(true);
                                }
                            } catch (OperationFailedException ex) {
                                LOG.error(null, ex);
                                emailFDTO.addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_add_relation"));
                            } catch (AddressException ex) {
                                LOG.error(null, ex);
                                emailFDTO.addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_invalid_address"));
                            }
                        }
                    }
                }
            } catch (InvalidParameterException ex) {
                LOG.fatal(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_try_later"));
            } catch (StorageException ex) {
                LOG.fatal(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_try_later"));
            } catch (MessagingException ex) {
                LOG.fatal(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_mail_not_sent"));
            } catch (TemplateException ex) {
                LOG.fatal(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_construct"));
            } catch (TemplateNotFoundException ex) {
                LOG.fatal(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_template_not_found"));
            } catch (MalformedTemplateNameException ex) {
                LOG.fatal(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_malformed_template_name"));
            } catch (IOException ex) {
                LOG.fatal(null, ex);
                addErrorMessage(resourceBundle.getString("error"), resourceBundle.getString("error_try_later"));
            }
        }

        adminClientRelationsHandler.refresh();

        return null;
    }
    public void addEmail() {
        this.emails.add(new EmailFDTO(""));
    }


    private void sendMail(User user, Relation relation, String sender, String client)
    throws TemplateNotFoundException, TemplateException, MalformedTemplateNameException, AddressException, MessagingException, IOException {
        LOG.debug("Sending mail to " + user.getEmail());

        String text;
        if (user.getComplete()) {
            text = TemplateResource.getInvitationMailHtml(
                user.getFullName(),
                sender, client,
                "http://" + this.localUrl + "/invite/activate?id=" + relation.getId(),
                new Locale("nl"));
        }
        else {
            text = TemplateResource.getInvitationNewMailHtml(
                user.getFullName(),
                sender, client,
                user.getEmail(), user.getPassword(),
                "http://" + this.localUrl + "/invite/activate?id=" + relation.getId(),
                new Locale("nl"));
        }

        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.INVITE_COACHES);
        this.mailer.send(user.getEmail(), resourceBundle.getString("mail_subject"), text);
    }


    public List<EmailFDTO> getEmails() {
        return this.emails;
    }


    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        return list;
    }





    //Dependant beans
    private AdminClientRelationsHandler adminClientRelationsHandler;

    public AdminClientRelationsHandler getAdminClientRelationsHandler() {
        return adminClientRelationsHandler;
    }
    public void setAdminClientRelationsHandler(AdminClientRelationsHandler adminClientRelationsHandler) {
        this.adminClientRelationsHandler = adminClientRelationsHandler;
    }


    //DAO Objects
    private IJavaMailer mailer;
    private IUserDAOLocal userDAO;
    private IClientDAOLocal clientDAO;
    private IRelationDAOLocal relationDAO;

    public IJavaMailer getMailer() {
        return mailer;
    }
    public void setMailer(IJavaMailer mailer) {
        this.mailer = mailer;
    }

    public IUserDAOLocal getUserDAO() {
        return userDAO;
    }
    public void setUserDAO(IUserDAOLocal userDAO) {
        this.userDAO = userDAO;
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
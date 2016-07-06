package pictochat.client.web.handler.client;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.util.image.ImageUploadResizer;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import org.apache.log4j.Logger;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import pictochat.client.web.handler.SessionHandler;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import kpoint.javaee.web.handler.forms.FormInputField;
import pictochat.server.business.local.IClientManagerLocal;
import pictochat.server.dal.local.IAvatarDAOLocal;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class CreateClientHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(CreateClientHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;
    private ImageUploadResizer avatarUploadResizer = new ImageUploadResizer(300, 300, new String[]{"jpeg", "jpg", "png", "gif"});

    private Client client = new Client();
    private Avatar avatar;

    private User activeUser;

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public Avatar getClientAvatar() {
        if (this.avatar != null && this.avatar.getId() != null)
            return this.avatar;
        else
            return null;
    }

    public User getActiveUser() {
        return activeUser;
    }
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
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


    public String resetAndLoad() {
        this.client = new Client();
        this.avatar = null;

        return this.navigatingHandler.getNavigateTo().getSecure().getClient().getCreateAction();
    }
    public String createClient() {
        if (getClientManager() != null) {
            try {
                Client client = getClientManager().create(this.client.getFirstName(), this.client.getLastName(), this.avatar, this.activeUser);
                sessionHandler.refresh();

                return this.navigatingHandler.getNavigateTo().getSecure().getDashboardAction();
            } catch(StorageException ex) {
                String message = "Het subject kon niet worden aangemaakt";
                String detail = "Create subject failed";
                //showError("Subject creation failed", message, detail, ex);
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            } catch (OperationFailedException ex) {
                LOG.error(null, ex);
            }
        }else{
            String message = "Er is een probleem met de back-end, contacteer de beheerder";
            String detail = "A record DAO was not configured or loaded";
            //showError("Records lookup failed", message, detail);
        }

        return null;
    }
    public void uploadListener(UploadEvent event) throws Exception {
        if (getAvatarDAO() != null) {
            UploadItem item = event.getUploadItem();

            byte[] imageData = avatarUploadResizer.resize(item);
            if (imageData != null) {
                try {
                    if (this.avatar != null) {
                        try { this.avatarDAO.destroy(this.avatar); }
                        catch (Exception ex) {}
                    }

                    this.avatar = this.avatarDAO.create(imageData, item.getContentType());
                } catch (InvalidParameterException ex) {
                    LOG.error(null, ex);
                } catch (StorageException ex) {
                    LOG.error(null, ex);
                } catch (OperationFailedException ex) {
                    LOG.error(null, ex);
                }
            }
        }
    }


    //Validation
    public void validateUsername(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String uniqueReference = (String)value;

        /*try {
            Record s = studentDAO.findStudentByUniqueReference(uniqueReference);
            if (s != null)
                throw new ValidatorException(new FacesMessage("Dit studentnummer is reeds in gebruik"));
        } catch (InvalidParameterException ex) {
            java.util.logging.Logger.getLogger(RegisterHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new ValidatorException(new FacesMessage("Er trad een fout op. Gelieve het later opnieuw te proberen."));
        } catch (EntityDoesNotExistException ex) {
            //This is supposed to happen
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(RegisterHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new ValidatorException(new FacesMessage("Er trad een fout op. Gelieve het later opnieuw te proberen."));
        }*/
    }

    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getFirstNameMessages());
        list.add(this.getLastNameMessages());
        list.add(this.getOrganisationMessages());
        list.add(this.getAvatarMessages());

        return list;
    }

    public FormInputField getFirstNameMessages() {
        return this.getFormInputField("Voornaam", "client_form:firstName");
    }
    public FormInputField getLastNameMessages() {
        return this.getFormInputField("Achternaam", "client_form:lastName");
    }
    public FormInputField getOrganisationMessages() {
        return this.getFormInputField("Organisation", "client_form:organisation");
    }
    public FormInputField getAvatarMessages() {
        return this.getFormInputField("Image", "client_form:avatar");
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
    private IAvatarDAOLocal avatarDAO;
    private IClientManagerLocal clientManager;

    public IAvatarDAOLocal getAvatarDAO() {
        return avatarDAO;
    }
    public void setAvatarDAO(IAvatarDAOLocal avatarDAO) {
        this.avatarDAO = avatarDAO;
    }

    public IClientManagerLocal getClientManager() {
        return clientManager;
    }
    public void setClientManager(IClientManagerLocal clientManager) {
        this.clientManager = clientManager;
    }
}
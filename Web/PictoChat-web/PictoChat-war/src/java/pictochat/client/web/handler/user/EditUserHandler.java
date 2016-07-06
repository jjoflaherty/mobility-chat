package pictochat.client.web.handler.user;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.util.image.ImageUploadResizer;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.log4j.Logger;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.handler.SessionHandler;
import pictochat.client.web.handler.client.*;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.server.dal.local.IAvatarDAOLocal;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class EditUserHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(EditClientHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;
    private ImageUploadResizer avatarUploadResizer = new ImageUploadResizer(200, 200, new String[]{"jpeg", "jpg", "png", "gif"});

    private User user;
    private Avatar avatar;

    private User activeUser;

    private Long userIdFromSession;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
        this.avatar = user.getAvatar();
    }

    public Avatar getUserAvatar() {
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

    public Long getUserIdFromSession() {
        return userIdFromSession;
    }
    public void setUserIdFromSession(Long userIdFromSession) {
        this.userIdFromSession = userIdFromSession;
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

        if (userIdFromSession != null)
            load(userIdFromSession);
    }


    public String show() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Long userId = Long.parseLong(ctx.getExternalContext().getRequestParameterMap().get("user"));

        if (this.userDAO != null && userId != null)
            return load(userId);

        return null;
    }
    public String load(Long userId) {
        try {
            LOG.warn("userDAO is " + (this.userDAO != null ? "not" : "") + " null");

            this.setUser(this.userDAO.findUserById(userId));
            return this.navigatingHandler.getNavigateTo().getSecure().getUser().getEditAction();
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return null;
    }


    public String save() {
        if (getUserDAO() != null) {
            try {
                if (this.avatar != null)
                    this.user.setAvatar(this.avatar);

                if (this.user.getAvatar() == null) {
                    ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
                    FacesContext.getCurrentInstance().addMessage("user_form:avatar", new FacesMessage(resourceBundle.getString("image_required")));
                    return null;
                }

                this.user.setComplete(true);
                getUserDAO().update(this.user);

                this.sessionHandler.refresh();

                return this.navigatingHandler.getNavigateTo().getSecure().getDashboardAction();
            } catch(StorageException ex) {
                LOG.error(null, ex);
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            }
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


    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getFirstNameMessages());
        list.add(this.getLastNameMessages());
        list.add(this.getEmailMessages());
        list.add(this.getAvatarMessages());

        return list;
    }

    public FormInputField getFirstNameMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("firstName"), "user_form:firstName");
    }
    public FormInputField getLastNameMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("lastName"), "user_form:lastName");
    }
    public FormInputField getEmailMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("email"), "user_form:email");
    }
    public FormInputField getAvatarMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("image"), "user_form:avatar");
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
    private IUserDAOLocal userDAO;
    private IAvatarDAOLocal avatarDAO;

    public IUserDAOLocal getUserDAO() {
        return userDAO;
    }
    public void setUserDAO(IUserDAOLocal userDAO) {
        this.userDAO = userDAO;
    }

    public IAvatarDAOLocal getAvatarDAO() {
        return avatarDAO;
    }
    public void setAvatarDAO(IAvatarDAOLocal avatarDAO) {
        this.avatarDAO = avatarDAO;
    }
}
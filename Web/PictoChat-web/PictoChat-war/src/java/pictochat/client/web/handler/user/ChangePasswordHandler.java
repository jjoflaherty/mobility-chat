package pictochat.client.web.handler.user;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class ChangePasswordHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(ChangePasswordHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;

    private User loggedInUser;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public User getLoggedInUser() {
        return this.loggedInUser;
    }
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public String getOldPassword() {
        return "";
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return "";
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return "";
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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


    public void save() {
        if (getUserDAO() != null && this.loggedInUser != null) {
            try {
                this.loggedInUser.setPassword(this.newPassword);
                this.userDAO.updatePassword(this.loggedInUser);
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
            } catch (StorageException ex) {
                LOG.error(null, ex);
            }
        }
    }


    //Navigation
    public String show() {
        return this.navigatingHandler.getNavigateTo().getSecure().getUser().getPasswordAction();
    }

    //Validation
    public void validateOldPassword(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = (String)value;
        String hash = DigestUtils.sha256Hex(password);
        if (!this.loggedInUser.getPassword().equals(hash)) {
            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.PASSWORD);
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("wrong_password"), ""));
        }
    }
    public void validateNewPassword(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = (String)value;
        /* Don't check password strength for the sake of user friendliness... Not my call...
        if (!password.matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{10,20})"))
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, this.resourceBundle.getString("weak_password"), ""));
         */
    }
    public void validatePasswordMatch(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIInput passwordField = (UIInput)context.getViewRoot().findComponent("password_form:newPassword");
        if (passwordField == null)
            throw new IllegalArgumentException(String.format("Unable to find component."));

        String password = (String)passwordField.getValue();
        String confirmPassword = (String)value;
        if (!confirmPassword.equals(password)) {
            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.ENUM_ACTION);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("password_mismatch"), "");
            throw new ValidatorException(message);
        }
    }


    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getPasswordMessages());
        list.add(this.getNewPasswordMessages());
        list.add(this.getRepeatPasswordMessages());

        return list;
    }

    public FormInputField getPasswordMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.PASSWORD);
        return this.getFormInputField(resourceBundle.getString("oldPassword"), "password_form:oldPassword");
    }
    public FormInputField getNewPasswordMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.PASSWORD);
        return this.getFormInputField(resourceBundle.getString("newPassword"), "password_form:newPassword");
    }
    public FormInputField getRepeatPasswordMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.PASSWORD);
        return this.getFormInputField(resourceBundle.getString("repeatPassword"), "password_form:confirmPassword");
    }





    //DAO Objects
    private IUserDAOLocal userDAO;

    public IUserDAOLocal getUserDAO() {
        return userDAO;
    }
    public void setUserDAO(IUserDAOLocal userDAO) {
        this.userDAO = userDAO;
    }
}

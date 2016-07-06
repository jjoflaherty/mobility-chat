package pictochat.client.web.handler.user;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.util.image.ImageUploadResizer;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.log4j.Logger;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.handler.SessionHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.client.web.templates.TemplateResource;
import pictochat.server.business.interfaces.IGeneratesMailBody;
import pictochat.server.business.local.IUserManagerLocal;
import pictochat.server.dal.local.IAvatarDAOLocal;
import pictochat.server.dal.local.IRoleDAOLocal;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class RegisterHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(RegisterHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private final static String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{10,20})";
    private final static Pattern PASSWORD_COMPILED_PATTERN = Pattern.compile(PASSWORD_PATTERN);
    private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final static Pattern EMAIL_COMPILED_PATTERN = Pattern.compile(EMAIL_PATTERN);

    private ImageUploadResizer avatarUploadResizer = new ImageUploadResizer(200, 200, new String[]{"jpeg", "jpg", "png", "gif"});

    private String localUrl;
    private User user;
    private Avatar avatar;
    private Long roleId;


    public User getUser() {
        if (this.user == null)
            this.user = new User();

        return this.user;
    }

    public Avatar getAvatar() {
        if (this.avatar != null && this.avatar.getId() != null)
            return this.avatar;
        else
            return null;
    }

    public Long getRoleId() {
        return this.roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }


    //Actions
    public String register() {
        if (getUserManager() != null && this.user != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);

            try {
                if (this.avatar == null) {
                    ctx.addMessage("register_form:avatar", new FacesMessage(resourceBundle.getString("image_required")));
                    return null;
                }

                User u = userManager.register(
                    this.user.getFirstName(), this.user.getLastName(), this.user.getEmail(), this.user.getPassword(), this.avatar,
                    resourceBundle.getString("mail_subject"), new IGeneratesMailBody() {
                        public String generateMailBody(User user) {
                            return RegisterHandler.this.generateMailBody(user);
                        }
                    }
                );
                if (u != null) {
                    u.setPassword(this.user.getPassword());
                    this.user = u;

                    return this.sessionHandler.login(u);
                }
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
                ctx.addMessage("register_form:email", new FacesMessage(resourceBundle.getString("error_try_later")));
            } catch (StorageException ex) {
                LOG.error(null, ex);
                ctx.addMessage("register_form:email", new FacesMessage(resourceBundle.getString("error_try_later")));
            } catch (OperationFailedException ex) {
                LOG.error(null, ex);
                ctx.addMessage("register_form:email", new FacesMessage(resourceBundle.getString("error_try_later")));
            } catch (AddressException ex) {
                LOG.error(null, ex);
                ctx.addMessage("register_form:email", new FacesMessage(resourceBundle.getString("invalid_email")));
            } catch (MessagingException ex) {
                LOG.error(null, ex);
                ctx.addMessage("register_form:email", new FacesMessage(resourceBundle.getString("error_mail_not_sent")));
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


    @PostConstruct
    private void postConstruct() {
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


    public void sendMail(ActionEvent actionEvent) throws AddressException, MessagingException {
        String body = generateMailBody(this.user);
        if (body != null) {
            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
            userManager.sendMail(this.user, resourceBundle.getString("mail_subject"), body);
        }
    }
    private String generateMailBody(User user) {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);

        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Locale locale = ctx.getViewRoot().getLocale();

            return TemplateResource.getVerificationMailHtml(
                user.getFullName(),
                "http://" + this.localUrl + "/account/activate?id=" + user.getId(),
                locale
            );
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

        return null;
    }


    //Validation
    public void validateEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = (String)value;

        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);

        try {
            Matcher matcher = EMAIL_COMPILED_PATTERN.matcher(email);
            if (!matcher.matches())
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("invalid_email"), ""));

            User u = userManager.findUserByEmail(email);
            if (u != null)
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("username_in_use"), ""));
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
            throw new ValidatorException(new FacesMessage(resourceBundle.getString("error_try_later")));
        } catch (EntityNotFoundException ex) {
            //This is supposed to happen
        } catch (StorageException ex) {
            LOG.error(null, ex);
            throw new ValidatorException(new FacesMessage(resourceBundle.getString("error_try_later")));
        }
    }
    public void validatePassword(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = (String)value;
        /* Don't check password strength for the sake of user friendliness... Not my call...
        Matcher matcher = PASSWORD_COMPILED_PATTERN.matcher(password);
        if (!matcher.matches())
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, this.resourceBundle.getString("weak_password"), ""));
        */
    }
    public void validatePasswordMatch(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIInput passwordField = (UIInput)context.getViewRoot().findComponent("register_form:password");
        if (passwordField == null)
            throw new IllegalArgumentException(String.format("Unable to find component."));

        String password = (String)passwordField.getValue();
        String confirmPassword = (String)value;
        if (!confirmPassword.equals(password)) {
            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, resourceBundle.getString("password_mismatch"), "");
            throw new ValidatorException(message);
        }
    }


    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getEmailMessages());
        list.add(this.getFirstNameMessages());
        list.add(this.getLastNameMessages());
        list.add(this.getPasswordMessages());
        list.add(this.getRepeatPasswordMessages());
        list.add(this.getAvatarMessages());

        return list;
    }

    public FormInputField getFirstNameMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("firstName"), "register_form:firstName");
    }
    public FormInputField getLastNameMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("lastName"), "register_form:lastName");
    }
    public FormInputField getEmailMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("email"), "register_form:email");
    }
    public FormInputField getPasswordMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("password"), "register_form:password");
    }
    public FormInputField getRepeatPasswordMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("repeatPassword"), "register_form:password2");
    }
    public FormInputField getAvatarMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.REGISTER);
        return this.getFormInputField(resourceBundle.getString("image"), "register_form:avatar");
    }





    //Dependant Beans
    private SessionHandler sessionHandler;

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }
    public void setSessionHandler(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }


    //DAO Objects
    private IUserManagerLocal userManager;
    private IAvatarDAOLocal avatarDAO;
    private IRoleDAOLocal roleDAO;

    public IUserManagerLocal getUserManager() {
        return userManager;
    }
    public void setUserManager(IUserManagerLocal userManager) {
        this.userManager = userManager;
    }

    public IAvatarDAOLocal getAvatarDAO() {
        return avatarDAO;
    }
    public void setAvatarDAO(IAvatarDAOLocal avatarDAO) {
        this.avatarDAO = avatarDAO;
    }

    public IRoleDAOLocal getRoleDAO() {
        return roleDAO;
    }
    public void setRoleDAO(IRoleDAOLocal roleDAO) {
        this.roleDAO = roleDAO;
    }
}

package pictochat.client.web.handler;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.web.handler.forms.FormInputField;
import kpoint.javaee.web.navigation.AbstractNavigatingHandler;
import kpoint.javaee.web.resources.ResourceBundleWrapper;
import org.apache.log4j.Logger;
import pictochat.client.web.bundles.Bundles;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.client.web.orlando.forms.AbstractFormHandler;
import pictochat.server.Settings;
import pictochat.server.dal.local.IRememberCookieDAOLocal;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.RememberCookie;
import pictochat.server.persistence.Role;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class SessionHandler extends AbstractFormHandler
{
    private static final Logger LOG = Logger.getLogger(SessionHandler.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private AbstractNavigatingHandler<PageNavigationHandler> navigatingHandler;

    private User user;
    private User loggedInUser;
    private Boolean remember = true;


    public User getUser() {
        if (this.user == null)
            this.user = new User();

        return this.user;
    }
    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    public Boolean getRemember() {
        return remember;
    }
    public void setRemember(Boolean remember) {
        this.remember = remember;
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
        Map<String, Object> cookies = ectx.getRequestCookieMap();
        Cookie cookie = (Cookie)cookies.get("remember");
        if (cookie != null) {
            String uuid = cookie.getValue();
            if (this.loginByUUID(uuid)) {
                try {
                    String url = this.navigatingHandler.getNavigateTo().getSecure().getDashboard();
                    ctx.getExternalContext().redirect(url);
                } catch (IOException ex) {
                    LOG.error(null, ex);
                }
            }
        }
    }

    public String login() {
        return this.login(this.user);
    }
    public String login(User user) {
        this.loggedInUser = null;

        if (getUserDAO() != null && user != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();

            ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.LOGIN);

            try {
                User u = userDAO.findUserByEmail(user.getEmail());
                if (u.passwordMatches(user.getPassword())) {
                    return processLogin(u);
                }
                else {
                    ctx.addMessage("login_form:errors", new FacesMessage(resourceBundle.getString("wrong_password")));
                }
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
                ctx.addMessage("login_form:errors", new FacesMessage(resourceBundle.getString("error_try_later")));
            } catch (EntityNotFoundException ex) {
                ctx.addMessage("login_form:errors", new FacesMessage(resourceBundle.getString("wrong_password")));
            } catch (StorageException ex) {
                LOG.error(null, ex);
                ctx.addMessage("login_form:errors", new FacesMessage(resourceBundle.getString("error_try_later")));
            }
        }

        return null;
    }
    public void blindLogin(User u) {
        try {
            processLogin(u);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        }
    }
    private String processLogin(User u) throws StorageException, InvalidParameterException, EntityNotFoundException {
        this.loggedInUser = u;

        //userDAO.refreshLastLogin(u);

        if (this.remember) {
            String uuid = UUID.randomUUID().toString();
            try {
                rememberCookieDAO.deleteForUser(u);
                rememberCookieDAO.create(uuid, u);
            } catch (OperationFailedException ex) {
                LOG.error(null, ex);
            }

            Cookie cookie = new Cookie("remember", uuid);
            cookie.setPath("/");
            cookie.setMaxAge(Settings.MAX_COOKIE_AGE);

            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ExternalContext ectx = ctx.getExternalContext();
                HttpServletResponse res = (HttpServletResponse)ectx.getResponse();
                res.addCookie(cookie);
            }
        }

        return this.navigatingHandler.getNavigateTo().getSecure().getDashboardAction();
    }
    private Boolean loginByUUID(String uuid) {
        try {
            RememberCookie cookie = rememberCookieDAO.findByUUID(uuid);
            this.loggedInUser = cookie.getUser();
            return true;
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            //Do nothing
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }

        return false;
    }
    public String logout() {
        this.loggedInUser = null;

        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext context = ctx.getExternalContext();

        Cookie cookie = new Cookie("remember", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        ExternalContext ectx = ctx.getExternalContext();
        HttpServletResponse res = (HttpServletResponse)ectx.getResponse();
        res.addCookie(cookie);

        HttpSession session = (HttpSession)context.getSession(false);
        session.invalidate();

        return this.showLogin();
    }

    public boolean isLoggedIn() {
        return (this.loggedInUser != null);
    }

    public void refresh() {
        refresh(this.loggedInUser.getId());
    }
    public void refresh(Long id) {
        try {
            this.loggedInUser = userDAO.findUserById(id);
        } catch (InvalidParameterException ex) {
            LOG.error(null, ex);
        } catch (EntityNotFoundException ex) {
            LOG.error(null, ex);
        } catch (StorageException ex) {
            LOG.error(null, ex);
        }
    }


    public Role getGlobalRole() {
        if (this.isLoggedIn())
            return this.loggedInUser.getGlobalRole();

        return null;
    }
    public String getLoginInfo() {
        if (this.isLoggedIn()) {
            Role role = this.loggedInUser.getGlobalRole();
            if (role != null) {
                ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.ROLE);
                return this.loggedInUser.getFullName() + " (" + resourceBundle.getString(role.getName()) + ")";
            }
            else
                return this.loggedInUser.getFullName();
        }

        return "";
    }


    //Form Messages
    @Override
    public List<FormInputField> getAllMessages() {
        List<FormInputField> list = new ArrayList<FormInputField>();

        list.add(this.getEmailMessages());
        list.add(this.getPasswordMessages());
        list.add(this.getErrorMessages());

        return list;
    }

    public FormInputField getEmailMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.LOGIN);
        return this.getFormInputField(resourceBundle.getString("email"), "login_form:email");
    }
    public FormInputField getPasswordMessages() {
        ResourceBundleWrapper<Bundles> resourceBundle = new ResourceBundleWrapper<Bundles>(Bundles.LOGIN);
        return this.getFormInputField(resourceBundle.getString("password"), "login_form:password");
    }
    public FormInputField getErrorMessages() {
        return this.getFormInputField("", "login_form:errors");
    }


    public String showLogin() {
        return "showLogin";
    }





    //DAO Objects
    private IUserDAOLocal userDAO;
    private IRememberCookieDAOLocal rememberCookieDAO;

    public IUserDAOLocal getUserDAO() {
        return userDAO;
    }
    public void setUserDAO(IUserDAOLocal userDAO) {
        this.userDAO = userDAO;
    }

    public IRememberCookieDAOLocal getRememberCookieDAO() {
        return rememberCookieDAO;
    }
    public void setRememberCookieDAO(IRememberCookieDAOLocal rememberCookieDAO) {
        this.rememberCookieDAO = rememberCookieDAO;
    }
}

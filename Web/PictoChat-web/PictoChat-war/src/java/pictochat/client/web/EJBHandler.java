/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import kpoint.javaee.server.util.mailer.IJavaMailer;
import org.apache.log4j.Logger;
import pictochat.server.business.local.IClientManagerLocal;
import pictochat.server.business.local.IUserManagerLocal;
import pictochat.server.dal.local.IAvatarDAOLocal;
import pictochat.server.dal.local.IButtonImageDAOLocal;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.dal.local.IPageButtonDAOLocal;
import pictochat.server.dal.local.IPageDAOLocal;
import pictochat.server.dal.local.IRelationDAOLocal;
import pictochat.server.dal.local.IRememberCookieDAOLocal;
import pictochat.server.dal.local.IRoleDAOLocal;
import pictochat.server.dal.local.IUserDAOLocal;

/**
 *
 * @author Steven
 */
public class EJBHandler
{
    private static final Logger LOG = Logger.getLogger(EJBHandler.class);

    public static final String CONTEXT_NAME = "/PictoChat";

    private IRoleDAOLocal roleDAO;
    private IUserDAOLocal userDAO;
    private IAvatarDAOLocal avatarDAO;
    private IButtonImageDAOLocal buttonImageDAO;
    private IClientDAOLocal clientDAO;
    private IRelationDAOLocal relationDAO;
    private IPageDAOLocal pageDAO;
    private IPageButtonDAOLocal pageButtonDAO;
    private IRememberCookieDAOLocal rememberCookieDAO;

    private IUserManagerLocal userManager;
    private IClientManagerLocal clientManager;

    private IJavaMailer mailer;


    public EJBHandler() throws InstantiationException{
        String item = "";

        try{
            Context ctx = new InitialContext();
            item = "RoleDAO"; setRoleDAO((IRoleDAOLocal)ctx.lookup(CONTEXT_NAME + "/RoleDAO/local"));
            item = "UserDAO"; setUserDAO((IUserDAOLocal)ctx.lookup(CONTEXT_NAME + "/UserDAO/local"));
            item = "AvatarDAO"; setAvatarDAO((IAvatarDAOLocal)ctx.lookup(CONTEXT_NAME + "/AvatarDAO/local"));
            item = "ButtonImageDAO"; setButtonImageDAO((IButtonImageDAOLocal)ctx.lookup(CONTEXT_NAME + "/ButtonImageDAO/local"));
            item = "ClientDAO"; setClientDAO((IClientDAOLocal)ctx.lookup(CONTEXT_NAME + "/ClientDAO/local"));
            item = "RelationDAO"; setRelationDAO((IRelationDAOLocal)ctx.lookup(CONTEXT_NAME + "/RelationDAO/local"));
            item = "PageDAO"; setPageDAO((IPageDAOLocal)ctx.lookup(CONTEXT_NAME + "/PageDAO/local"));
            item = "PageButtonDAO"; setPageButtonDAO((IPageButtonDAOLocal)ctx.lookup(CONTEXT_NAME + "/PageButtonDAO/local"));
            item = "RememberCookieDAO"; setRememberCookieDAO((IRememberCookieDAOLocal)ctx.lookup(CONTEXT_NAME + "/RememberCookieDAO/local"));

            item = "UserManager"; setUserManager((IUserManagerLocal)ctx.lookup(CONTEXT_NAME + "/UserManager/local"));
            item = "ClientManager"; setClientManager((IClientManagerLocal)ctx.lookup(CONTEXT_NAME + "/ClientManager/local"));

            item = "Mailer"; setMailer((IJavaMailer)ctx.lookup(CONTEXT_NAME + "/PictoChatMailer/local"));

            if(LOG.isDebugEnabled()){
                LOG.debug("All EJB references were successfully loaded.");
            }
        }catch(NamingException ex){
            LOG.error("EJBHandler initiation : The EJB-Handler could not be initialized : " + ex.getMessage() + " - " + item);
            throw new InstantiationException("The EJB-Handler could not be initialized : " + ex.getMessage() + " - " + item);
        }
    }

    public String getAppName() {
        return "AbleChat";
    }


    public final IRoleDAOLocal getRoleDAO() {
        return roleDAO;
    }
    public final void setRoleDAO(IRoleDAOLocal roleDAO) {
        this.roleDAO = roleDAO;
    }

    public final IUserDAOLocal getUserDAO() {
        return userDAO;
    }
    public final void setUserDAO(IUserDAOLocal userDAO) {
        this.userDAO = userDAO;
    }

    public final IAvatarDAOLocal getAvatarDAO() {
        return avatarDAO;
    }
    public final void setAvatarDAO(IAvatarDAOLocal avatarDAO) {
        this.avatarDAO = avatarDAO;
    }

    public final IButtonImageDAOLocal getButtonImageDAO() {
        return buttonImageDAO;
    }
    public final void setButtonImageDAO(IButtonImageDAOLocal buttonImageDAO) {
        this.buttonImageDAO = buttonImageDAO;
    }

    public final IClientDAOLocal getClientDAO() {
        return clientDAO;
    }
    public final void setClientDAO(IClientDAOLocal clientDAO) {
        this.clientDAO = clientDAO;
    }

    public final IRelationDAOLocal getRelationDAO() {
        return relationDAO;
    }
    public final void setRelationDAO(IRelationDAOLocal relationDAO) {
        this.relationDAO = relationDAO;
    }

    public final IPageDAOLocal getPageDAO() {
        return pageDAO;
    }
    public final void setPageDAO(IPageDAOLocal pageDAO) {
        this.pageDAO = pageDAO;
    }

    public final IPageButtonDAOLocal getPageButtonDAO() {
        return pageButtonDAO;
    }
    public final void setPageButtonDAO(IPageButtonDAOLocal pageButtonDAO) {
        this.pageButtonDAO = pageButtonDAO;
    }

    public final IRememberCookieDAOLocal getRememberCookieDAO() {
        return rememberCookieDAO;
    }
    public final void setRememberCookieDAO(IRememberCookieDAOLocal rememberCookieDAO) {
        this.rememberCookieDAO = rememberCookieDAO;
    }


    public final IUserManagerLocal getUserManager() {
        return userManager;
    }
    public final void setUserManager(IUserManagerLocal userManager) {
        this.userManager = userManager;
    }

    public final IClientManagerLocal getClientManager() {
        return clientManager;
    }
    public final void setClientManager(IClientManagerLocal clientManager) {
        this.clientManager = clientManager;
    }


    public final IJavaMailer getMailer() {
        return mailer;
    }
    public final void setMailer(IJavaMailer mailer) {
        this.mailer = mailer;
    }
}


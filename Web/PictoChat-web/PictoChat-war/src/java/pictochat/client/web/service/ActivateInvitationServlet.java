/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.client.web.service;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;
import pictochat.client.web.handler.SessionHandler;
import pictochat.client.web.handler.user.EditUserHandler;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.server.dal.local.IRelationDAOLocal;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class ActivateInvitationServlet extends HttpServlet
{
    private static final Logger LOG = Logger.getLogger(ActivateInvitationServlet.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);


    //<editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private IUserDAOLocal userDAO;

    @EJB
    private IRelationDAOLocal relationDAO;
    //</editor-fold>


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String id = req.getParameter("id");
            Long relationId = Long.parseLong(id);

            try {
                Relation relation = relationDAO.findRelationById(relationId);
                relation.setAccepted(true);
                relationDAO.update(relation);

                User user = relation.getUser();
                if (!user.getVerified()) {
                    user.setVerified(true);
                    userDAO.update(user);
                }

                HttpSession session = req.getSession();
                SessionHandler sessionHandler = (SessionHandler)session.getAttribute("sessionHandler");
                if (sessionHandler != null) {
                    LOG.debug("Refreshing session: user " + user.getId());
                    sessionHandler.refresh(user.getId());
                }
                EditUserHandler editUserHandler = (EditUserHandler)session.getAttribute("editUserHandler");
                if (editUserHandler != null) {
                    LOG.debug("Reloading bean: user " + user.getId());
                    editUserHandler.load(user.getId());
                }
                else
                    session.setAttribute("userId", user.getId());

                if (!user.getComplete())
                    resp.sendRedirect(new PageNavigationHandler().getSecure().getUser().getEdit());
                else
                    resp.sendRedirect(new PageNavigationHandler().getSecure().getDashboard());
            } catch (InvalidParameterException ex) {
                LOG.error(null, ex);
                throw ex;
            } catch (EntityNotFoundException ex) {
                LOG.error(null, ex);
                throw ex;
            } catch (StorageException ex) {
                LOG.error(null, ex);
                throw ex;
            }

            resp.getOutputStream().close();
        } catch (Exception e) {
            LOG.error(e);

            resp.getWriter().write(e.toString());
            resp.getWriter().close();
        }
    }
}

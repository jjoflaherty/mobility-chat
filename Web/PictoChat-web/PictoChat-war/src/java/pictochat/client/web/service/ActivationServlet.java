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
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import org.apache.log4j.Logger;
import pictochat.client.web.navigation.PageNavigationHandler;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
public class ActivationServlet extends HttpServlet
{
    private static final Logger LOG = Logger.getLogger(ActivationServlet.class);


    //<editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private IUserDAOLocal userDAO;
    //</editor-fold>


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String id = req.getParameter("id");
            Long userId = Long.parseLong(id);

            if (userDAO != null) {
                try {
                    User user = userDAO.findUserById(userId);
                    if (!user.getVerified()) {
                        user.setVerified(true);
                        userDAO.update(user);

                        resp.sendRedirect(new PageNavigationHandler().getRegister().getActivationSuccess());
                    }
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
            }

            resp.getOutputStream().close();
        } catch (Exception e) {
            LOG.error(e);

            resp.getWriter().write(e.toString());
            resp.getWriter().close();
        }
    }
}

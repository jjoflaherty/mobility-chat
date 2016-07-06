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
import pictochat.client.web.handler.client.EditClientHandler;
import pictochat.server.dal.local.IButtonImageDAOLocal;
import pictochat.server.persistence.ButtonImage;

/**
 *
 * @author Steven Solberg
 */
public class ButtonImageServlet extends HttpServlet
{
    private static final Logger LOG = Logger.getLogger(EditClientHandler.class);

    
    //<editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private IButtonImageDAOLocal imageDAO;
    //</editor-fold>


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));

            if (imageDAO != null) {
                try {
                    ButtonImage image = imageDAO.findById(id);
                    byte[] imageBytes = image.getData();
                    resp.getOutputStream().write(imageBytes);
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
        } catch (Exception e) {
            resp.getWriter().write(e.getMessage());
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.api.managers.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;
import pictochat.server.api.managers.interfaces.ICoachRest;
import pictochat.server.api.providers.ClientProvider;
import pictochat.server.api.providers.CoachProvider;
import pictochat.server.api.providers.PageButtonProvider;
import pictochat.server.dal.impl.*;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.dal.local.IPageButtonDAOLocal;
import pictochat.server.dal.local.IUserDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.PageButton;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;

/**
 *
 * @author Steven
 */
@Stateless
public class CoachRest implements ICoachRest
{
    private static final Logger LOG = Logger.getLogger(ClientDAO.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);


    //<editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private IUserDAOLocal userDAO;

    @EJB
    private IClientDAOLocal clientDAO;

    @EJB
    private IPageButtonDAOLocal pageButtonDAO;
    //</editor-fold>


    public CoachProvider findCoachProviderByEmail(String email, String password) throws InvalidParameterException, EntityNotFoundException, StorageException {
        User user = userDAO.findUserByEmail(email);
        if (!user.getPassword().equals(password))
            return null;

        return findCoachProvider(user);
    }
    public CoachProvider findCoachProviderById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        User user = userDAO.findUserById(id);

        return findCoachProvider(user);
    }
    private CoachProvider findCoachProvider(User user) {
        List<ClientProvider> clients = new ArrayList<ClientProvider>();

        if (user.getComplete() && user.getVerified()) {
            for (Relation re : user.getRelations()) {
                LOG.debug("Acc: " + re.getAccepted() + " Act: " + re.getActive() + " Bl: " + re.getBlocked());
                if (re.getAccepted() && re.getActive() && !re.getBlocked()) {
                    try {
                        Client client = re.getClient();

                        List<CoachProvider> coaches = new ArrayList<CoachProvider>();
                        Client withRelations = clientDAO.findClientByIdWithRelations(client.getId());
                        for (Relation r : withRelations.getRelations()) {
                            LOG.debug("Acc: " + r.getAccepted() + " Act: " + r.getActive() + " Bl: " + r.getBlocked());
                            if (r.getAccepted() && r.getActive() && !r.getBlocked()) {
                                User u = r.getUser();
                                if (u.getComplete() && u.getVerified() && u.getAvatar() != null)
                                    coaches.add(new CoachProvider(u.getId(), u.getFirstName(), u.getLastName(), u.getAvatar().getFullImageUrl()));
                            }
                        }

                        List<PageButton> buttons = pageButtonDAO.findButtonsForClient(client);
                        List<PageButtonProvider> buttonProviders = new ArrayList<PageButtonProvider>();
                        for (PageButton button : buttons)
                            buttonProviders.add(new PageButtonProvider(button.getText(), button.getFullUrl()));

                        clients.add(new ClientProvider(client.getId(), client.getFirstName(), client.getLastName(), client.getAvatar().getFullImageUrl(), client.getLongCode(), coaches, buttonProviders));
                    } catch (InvalidParameterException ex) {
                        LOG.error(null, ex);
                    } catch (EntityNotFoundException ex) {
                        LOG.error(null, ex);
                    } catch (StorageException ex) {
                        LOG.error(null, ex);
                    }
                }
            }
        }

        return new CoachProvider(user.getId(), user.getFirstName(), user.getLastName(), user.getAvatar().getFullImageUrl(), clients);
    }
}
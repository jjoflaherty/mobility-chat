/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.api.managers.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;
import pictochat.server.api.consumers.AddFriendObject;
import pictochat.server.api.managers.interfaces.IClientRest;
import pictochat.server.api.providers.ClientProvider;
import pictochat.server.api.providers.CoachProvider;
import pictochat.server.api.providers.ContactProvider;
import pictochat.server.api.providers.FriendProvider;
import pictochat.server.api.providers.PageButtonProvider;
import pictochat.server.api.providers.PageProvider;
import pictochat.server.dal.impl.*;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Contact;
import pictochat.server.persistence.Page;
import pictochat.server.persistence.PageButton;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;
import pictochat.server.persistence.comparators.RelationComparator;
import pictochat.server.persistence.enums.Action;

/**
 *
 * @author Steven
 */
@Stateless
public class ClientRest implements IClientRest
{
    private static final Logger LOG = Logger.getLogger(ClientDAO.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);


    //<editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private IClientDAOLocal clientDAO;
    //</editor-fold>


    public ClientProvider findClientProviderById(Long id) throws InvalidParameterException, EntityNotFoundException, StorageException {
        PageProvider startPage = null;
        List<PageProvider> pages = new ArrayList<PageProvider>();
        Client withPages = clientDAO.findClientByIdWithPages(id);
        Client client = withPages;
        for (Page p : withPages.getPages()) {
            List<PageButtonProvider> buttons = new ArrayList<PageButtonProvider>();

            for (PageButton b : p.getButtons()) {
                try {
                    buttons.add(new PageButtonProvider(
                        b.getId(),
                        b.getCell(), b.getColor(), b.getAction(), b.getText(),
                        Action.NavigateAndText.equals(b.getAction()) || Action.Navigate.equals(b.getAction()) ? b.getTargetPage().getId() : null,
                        b.getIcon(), b.getFullUrl()
                    ));
                }
                catch (Exception e) {
                    LOG.warn("Error parsing PageButton: " + b.getId(), e);
                }
            }

            pages.add(new PageProvider(p.getId(), p.getName(), p.getRows(), p.getColumns(), buttons));

            if (p.getIsStartPage() && startPage == null)
                startPage = new PageProvider(p.getId(), p.getName(), p.getRows(), p.getColumns(), null);
        }

        List<CoachProvider> coaches = new ArrayList<CoachProvider>();
        Client withRelations = clientDAO.findClientByIdWithRelations(id);

        SortedSet<Relation> sortedRelations = new TreeSet<Relation>(new RelationComparator());
        sortedRelations.addAll(withRelations.getRelations());
        for (Relation r : sortedRelations) {
            LOG.debug("Acc: " + r.getAccepted() + " Act: " + r.getActive() + " Bl: " + r.getBlocked());
            if (r.getAccepted() && r.getActive() && !r.getBlocked()) {
                User u = r.getUser();
                if (u.getComplete() && u.getVerified() && u.getAvatar() != null)
                    coaches.add(new CoachProvider(u.getId(), u.getFirstName(), u.getLastName(), u.getAvatar().getFullImageUrl()));
            }
        }

        List<ContactProvider> contacts = new ArrayList<ContactProvider>();
        Client withContacts = clientDAO.findClientByIdWithContacts(id);
        for (Contact c : withContacts.getContacts())
            contacts.add(new ContactProvider(c.getId(), c.getFirstName(), c.getLastName()));

        List<FriendProvider> friends = new ArrayList<FriendProvider>();
        Client withFriends = clientDAO.findClientByIdWithFriends(id);
        for (Client c : withFriends.getFriends())
            friends.add(new FriendProvider(c.getId(), c.getFirstName(), c.getLastName(), c.getAvatar().getFullImageUrl(), true));
        Client withFriendOf = clientDAO.findClientByIdWithFriendOf(id);
        for (Client c : withFriendOf.getFriendOf())
            friends.add(new FriendProvider(c.getId(), c.getFirstName(), c.getLastName(), c.getAvatar().getFullImageUrl(), false));

        return new ClientProvider(
            client.getId(), client.getFirstName(), client.getLastName(), client.getAvatar().getFullImageUrl(), client.getLongCode(),
            coaches, contacts, friends, pages, startPage
        );
    }

    public void addFriendForClient(AddFriendObject addFriendObject) throws InvalidParameterException, EntityNotFoundException, StorageException {
        Client client = clientDAO.findClientByIdWithFriends(addFriendObject.getId());
        if (!client.getLongCode().equals(addFriendObject.getClientCode()))
            return;

        Client friend = clientDAO.findClientByLongCode(addFriendObject.getFriendCode());
        client.getFriends().add(friend);
        clientDAO.updateFriends(client);
    }
}

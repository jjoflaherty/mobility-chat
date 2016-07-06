/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.api.managers.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import kpoint.javaee.server.core.util.ParameterValidation;
import org.apache.log4j.Logger;
import pictochat.server.api.managers.interfaces.IContactRest;
import pictochat.server.api.providers.ClientProvider;
import pictochat.server.api.providers.ContactProvider;
import pictochat.server.dal.impl.*;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Contact;

/**
 *
 * @author Steven
 */
@Stateless
public class ContactRest implements IContactRest
{
    private static final Logger LOG = Logger.getLogger(ClientDAO.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    @PersistenceContext(unitName = "PictoChat-ejbPU")
    private EntityManager persistence;


    public Contact findContactByPhoneNumberWithClients(String phoneNumber) throws InvalidParameterException, EntityNotFoundException, StorageException {
        return findContactByPhoneNumberWith(phoneNumber, Contact.NQ_FIND_CONTACT_BY_PHONENUMBER);
    }
    private Contact findContactByPhoneNumberWith(String phoneNumber, String method) throws InvalidParameterException, EntityNotFoundException, StorageException {
        ParameterValidation.stringNotNullNotEmpty(phoneNumber, method, "phoneNumber", LOG);

        try{
            Query query = persistence.createNamedQuery(method);
            query.setParameter("phoneNumber", phoneNumber);

            @SuppressWarnings("unchecked")
            Contact result = (Contact)query.getSingleResult();

            if(LOG.isDebugEnabled()){
                String message = String.format("contact found for %s", phoneNumber);
                METHOD_LOG.debug(method, message);
            }
            return result;
        }
        catch(IllegalStateException ex){
            String details = String.format(
                "Failed to find contact for %s, the persistency context was closed",
                phoneNumber);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find contact for %s",
                phoneNumber);
            throw new StorageException(method, message);
        }
        catch(IllegalArgumentException ex){
            String details = String.format(
                "Failed to find contact for %s, %s does not exist",
                phoneNumber);
            METHOD_LOG.fatal(method, details, ex);
            String message = String.format(
                "Failed to find contact for %s",
                phoneNumber);
            throw new StorageException(method, message);
        }
    }

    public ContactProvider findContactProviderByPhoneNumber(String phoneNumber, String password) throws InvalidParameterException, EntityNotFoundException, StorageException {
        Contact contact = this.findContactByPhoneNumberWithClients(phoneNumber);
        if (!contact.getPassword().equals(password))
            return null;

        List<ClientProvider> clients = new ArrayList<ClientProvider>();
        for (Client c : contact.getClients())
            clients.add(new ClientProvider(c.getId(), c.getFirstName(), c.getLastName(), c.getAvatar().getFullImageUrl(), c.getLongCode(), null, null));

        return new ContactProvider(contact.getId(), contact.getFirstName(), contact.getLastName(), clients);
    }
}

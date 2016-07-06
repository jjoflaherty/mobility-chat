/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.business.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.OperationFailedException;
import kpoint.javaee.server.core.exceptions.StorageException;
import kpoint.javaee.server.core.util.MethodLogger;
import org.apache.log4j.Logger;
import pictochat.server.business.local.IClientManagerLocal;
import pictochat.server.business.remote.IClientManagerRemote;
import pictochat.server.dal.local.IClientDAOLocal;
import pictochat.server.dal.local.IRelationDAOLocal;
import pictochat.server.persistence.Avatar;
import pictochat.server.persistence.Client;
import pictochat.server.persistence.Relation;
import pictochat.server.persistence.User;
import pictochat.server.util.RandomString;

/**
 *
 * @author Steven
 */
@Stateless
public class ClientManager implements IClientManagerRemote, IClientManagerLocal
{
    private static final Logger LOG = Logger.getLogger(ClientManager.class);
    private static final MethodLogger METHOD_LOG = new MethodLogger(LOG);

    private static final int ATTEMPTS = 10;


    //<editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private IClientDAOLocal clientDAO;

    @EJB
    private IRelationDAOLocal relationDAO;
    //</editor-fold>


    public Client create(String firstName, String lastName, Avatar avatar, User firstAdmin)
    throws InvalidParameterException, OperationFailedException, StorageException {
        int i;
        Client client;
        String shortCode, longCode;

        RandomString shortRs = new RandomString(12);
        RandomString longRs = new RandomString(24);

        i = 0;
        do {
            i++;
            shortCode = shortRs.nextString();

            try {
                client = clientDAO.findClientByNameAndShortCode(firstName, lastName, shortCode);
            } catch (EntityNotFoundException ex) {
                client = null;
                LOG.debug(ex);
            }
        }
        while (client != null && i < ATTEMPTS);

        if (client != null)
            throw new OperationFailedException("Failed to create client, failed to create unique shortcode");

        i = 0;
        do {
            i++;
            longCode = longRs.nextString();

            try {
                client = clientDAO.findClientByLongCode(longCode);
            } catch (EntityNotFoundException ex) {
                client = null;
                LOG.debug(ex);
            }
        }
        while (client != null && i < ATTEMPTS);

        if (client != null)
            throw new OperationFailedException("Failed to create client, failed to create unique longCode");

        client = clientDAO.create(firstName, lastName, shortCode, longCode, avatar);
        Relation relation = relationDAO.create(client, firstAdmin, true, true);

        return client;
    }
}


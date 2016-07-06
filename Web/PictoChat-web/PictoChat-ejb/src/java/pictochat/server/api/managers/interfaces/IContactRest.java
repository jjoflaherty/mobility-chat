/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.api.managers.interfaces;

import javax.ejb.Remote;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import pictochat.server.api.providers.ContactProvider;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;

/**
 *
 * @author Steven
 */
@Remote
@Path("/contact")
public interface IContactRest
{
    @GET
    @Path("/login")
    @Produces("application/json")
    public ContactProvider findContactProviderByPhoneNumber(@QueryParam("phone") String phoneNumber, @QueryParam("pw") String password)
    throws InvalidParameterException, EntityNotFoundException, StorageException;
}

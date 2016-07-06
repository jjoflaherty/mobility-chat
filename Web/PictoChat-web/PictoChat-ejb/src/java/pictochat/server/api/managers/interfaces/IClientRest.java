/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pictochat.server.api.managers.interfaces;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import pictochat.server.api.consumers.AddFriendObject;
import pictochat.server.api.providers.ClientProvider;

/**
 *
 * @author Steven
 */
@Remote
@Path("/client")
public interface IClientRest
{
    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    public ClientProvider findClientProviderById(@QueryParam("id") Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    @POST
    @Path("/friend")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addFriendForClient(AddFriendObject addFriendObject)
    throws InvalidParameterException, EntityNotFoundException, StorageException;
}

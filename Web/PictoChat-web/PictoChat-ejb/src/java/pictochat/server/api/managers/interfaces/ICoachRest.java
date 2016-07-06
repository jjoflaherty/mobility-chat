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
import javax.ws.rs.core.MediaType;
import kpoint.javaee.server.core.exceptions.EntityNotFoundException;
import kpoint.javaee.server.core.exceptions.InvalidParameterException;
import kpoint.javaee.server.core.exceptions.StorageException;
import pictochat.server.api.providers.CoachProvider;

/**
 *
 * @author Steven
 */
@Remote
@Path("/coach")
public interface ICoachRest
{
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public CoachProvider findCoachProviderByEmail(@QueryParam("email") String email, @QueryParam("pw") String password)
    throws InvalidParameterException, EntityNotFoundException, StorageException;

    @GET
    @Path("/read")
    @Produces(MediaType.APPLICATION_JSON)
    public CoachProvider findCoachProviderById(@QueryParam("id") Long id)
    throws InvalidParameterException, EntityNotFoundException, StorageException;
}

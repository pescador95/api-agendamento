package app.core.resources;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.cert.Extension;
import java.util.Set;

@Path("/extensions/restClient")
@RegisterRestClient(configKey = "extensions-api")
@Produces(MediaType.APPLICATION_JSON)
public interface RestClient {
    @GET
    @Path("/stream/{stream}")
    Set<Extension> getByStream(@PathParam String stream, @QueryParam("id") String id);
}

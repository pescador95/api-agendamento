package app.core.resources;

import app.core.controller.AuthController;
import app.core.model.Auth;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/auth")
public class AuthResources {

    @Inject
    AuthController authController;
    Auth auth;

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response auth(Auth data) {
        auth = authController.login(data);
        return Response.ok(auth).status(200).build();
    }

    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response refreshToken(Auth data, @Context SecurityContext context) {
        auth = authController.refreshToken(data, context);
        return Response.ok(auth).status(200).build();
    }
}
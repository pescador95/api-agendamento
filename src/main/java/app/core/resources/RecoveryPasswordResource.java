package app.core.resources;

import app.core.controller.RecoveryPasswordController;
import app.core.model.Responses;
import app.core.model.Usuario;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/recoverPassword")

public class RecoveryPasswordResource {
    @Inject
    RecoveryPasswordController controller;

    Responses responses;
    private String login;

    @POST
    @Path("{email}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    public Response sendMail(@PathParam("email") String email) {
        try {
            return controller.sendEmail(email);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível localizar um cadastro com o email informado.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response update(Usuario pUsuario, @Context @NotNull SecurityContext context,
            @QueryParam("password") String password) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.updatePassword(login, password);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível atualizar a senha.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}
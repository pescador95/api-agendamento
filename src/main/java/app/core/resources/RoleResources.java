package app.core.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import app.core.controller.UsuarioController;
import app.core.model.Role;

@Path("/role")
public class RoleResources {

    @Inject
    UsuarioController controller;
    Role role;
    List<Role> roles;
    private long count;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response getById(@PathParam("id") Long pId) {
        role = Role.findById(pId);
        return Response.ok(role).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        count = Role.count();
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response list(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgFilter") @DefaultValue("") String strgFilter,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {

        roles = Role.listAll();
        return Response.ok(roles).status(Response.Status.ACCEPTED).build();
    }
}

package app.core.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import app.core.controller.OrganizacaoController;
import app.core.model.Responses;
import app.core.model.Organizacao;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@Path("/organizacao")
public class OrganizacaoResources {

    @Inject
    OrganizacaoController controller;
    Organizacao organizacao;
    Responses responses;
    private String login;
    private String query;
    private long count;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })

    public Response getById(@PathParam("id") Long pId) {
        organizacao = Organizacao.findById(pId);
        return Response.ok(organizacao).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgFilter") @DefaultValue("") String strgFilter) {

        query = "isAtivo = " + ativo + " " + strgFilter;
        count = Organizacao.count(query);
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
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        query = "isAtivo = " + ativo + " " + strgFilter + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Organizacao> organizacao;
        organizacao = Organizacao.find(query);
        return Response.ok(organizacao.page(Page.of(pageIndex, pageSize)).list()).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response add(Organizacao pOrganizacao, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.addOrganizacao(pOrganizacao, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível cadastrar a Organização.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })

    public Response update(Organizacao pOrganizacao, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.updateOrganizacao(pOrganizacao, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível atualizar a Organização.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response deleteList(List<Long> pListIdOrganizacao, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.deleteOrganizacao(pListIdOrganizacao, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            if (pListIdOrganizacao.size() <= 1) {
                responses.messages.add("Não foi possível excluir a Organização.");
            } else {
                responses.messages.add("Não foi possível excluir as Organizações.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response reactivateList(List<Long> pListIdOrganizacao, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.reactivateOrganizacao(pListIdOrganizacao, login);
        } catch (Exception e) {
            if (pListIdOrganizacao.size() <= 1) {
                responses = new Responses();
                responses.status = 400;
                responses.messages.add("Não foi possível reativar a Organização.");
            } else {
                responses = new Responses();
                responses.status = 400;
                responses.messages.add("Não foi possível reativar as Organizações.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}

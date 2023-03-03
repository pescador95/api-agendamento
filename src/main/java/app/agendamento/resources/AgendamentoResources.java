package app.agendamento.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import app.core.model.Role;
import app.core.model.Usuario;
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

import app.agendamento.controller.AgendamentoController;
import app.core.model.Responses;
import app.agendamento.model.Agendamento;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@Path("/agendamento")
public class AgendamentoResources {

    @Inject
    AgendamentoController controller;
    Agendamento agendamento;

    Responses responses;
    Role role;
    private Usuario usuario;
    private Usuario usuarioAuth;
    private String login;
    private String query;
    private long count;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })

    public Response getById(@PathParam("id") Long pId) {
        agendamento = Agendamento.findById(pId);
        return Response.ok(agendamento).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgFilter") @DefaultValue("") String strgFilter) {
        query = "isAtivo = " + ativo + " " + strgFilter;
        count = Agendamento.count(query);
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
        login = context.getUserPrincipal().getName();
        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        query = "isAtivo = " + ativo + " " + strgFilter + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Agendamento> agendamento;
        agendamento = Agendamento.find(query);
        return Response.ok(agendamento.page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (usuarioAuth.organizacoes.contains(c.organizaoAgendamento))).collect(Collectors.toList()))
                .status(Response.Status.ACCEPTED).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response add(Agendamento pAgendamento, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.addAgendamento(pAgendamento, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível cadastrar a Agendamento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })

    public Response update(Agendamento pAgendamento, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.updateAgendamento(pAgendamento, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível atualizar o Agendamento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response deleteList(List<Long> pListIdAgendamento, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.deleteAgendamento(pListIdAgendamento, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            if (pListIdAgendamento.size() <= 1) {
                responses.messages.add("Não foi possível excluir o Agendamento.");
            } else {
                responses.messages.add("Não foi possível excluir os Organizações.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response reactivateList(List<Long> pListIdAgendamento, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.reactivateAgendamento(pListIdAgendamento, login);
        } catch (Exception e) {
            if (pListIdAgendamento.size() <= 1) {
                responses = new Responses();
                responses.status = 400;
                responses.messages.add("Não foi possível reativar o Agendamento.");
            } else {
                responses = new Responses();
                responses.status = 400;
                responses.messages.add("Não foi possível reativar os Organizações.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}

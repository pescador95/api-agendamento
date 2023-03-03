package app.agendamento.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

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

import app.agendamento.controller.PessoaController;
import app.core.model.Responses;
import app.agendamento.model.Pessoa;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@Path("/pessoa")
public class PessoaResources {

    @Inject
    PessoaController controller;
    Pessoa pessoa;
    Responses responses;
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
        pessoa = Pessoa.findById(pId);
        return Response.ok(pessoa).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })

    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgFilter") @DefaultValue("") String strgFilter, @Context @NotNull SecurityContext context) {

        query = "isAtivo = " + ativo + " " + strgFilter;
        count = Pessoa.count(query);
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
        query = "isAtivo = " + ativo + " " + strgFilter + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Pessoa> pessoa;
        pessoa = Pessoa.find(query);
        return Response.ok(pessoa.page(Page.of(pageIndex, pageSize)).list().stream().filter(c -> (c.isAtivo = ativo))
                .collect(Collectors.toList())).status(Response.Status.ACCEPTED).build();

    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response add(Pessoa pPessoa, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.addPessoa(pPessoa, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível cadastrar a Pessoa.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response update(Pessoa pPessoa, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.updatePessoa(pPessoa, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível atualizar a Pessoa.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response deleteList(List<Long> pListpessoa, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.deletePessoa(pListpessoa, login);
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            if (pListpessoa.size() <= 1) {
                responses.messages.add("Não foi possível excluir a Pessoa.");
            } else {
                responses.messages.add("Não foi possível excluir as Pessoas.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({ "usuario", "dev", "admin" })
    public Response reactivateList(List<Long> pListpessoa, @Context @NotNull SecurityContext context) {
        try {
            login = context.getUserPrincipal().getName();
            return controller.reactivatePessoa(pListpessoa, login);
        } catch (Exception e) {
            if (pListpessoa.size() <= 1) {
                responses = new Responses();
                responses.status = 400;
                responses.messages.add("Não foi possível reativar a Pessoa.");
            } else {
                responses = new Responses();
                responses.status = 400;
                responses.messages.add("Não foi possível reativar as Pessoas.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}

package app.core.controller;

import app.core.model.Organizacao;
import app.core.model.Responses;
import app.core.model.Usuario;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.*;

@ApplicationScoped
@Transactional
public class OrganizacaoController {

    private Organizacao organizacao = new Organizacao();

    private Responses responses;

    private Usuario usuarioAuth;

    public Response addOrganizacao(@NotNull Organizacao pOrganizacao, @NotNull String login) throws ParseException {

        responses = new Responses();
        responses.messages = new ArrayList<>();

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();

        if (pOrganizacao.cnpj != null) {
            organizacao = Organizacao.find("cnpj = ?1 and isAtivo = true", pOrganizacao.cnpj).firstResult();
        }

        if (organizacao == null) {
            organizacao = new Organizacao();

            if (pOrganizacao.nome != null) {
                organizacao.nome = pOrganizacao.nome;
            } else {
                responses.status = 400;
                responses.messages.add("Por favor, informe o Nome da Organização corretamente!");
            }
            if (pOrganizacao.cnpj != null) {
                organizacao.cnpj = pOrganizacao.cnpj;
            } else {
                responses.status = 400;
                responses.messages.add("Por favor, preencha o CNPJ corretamente!");
            }
            if (pOrganizacao.telefone != null) {
                organizacao.telefone = pOrganizacao.telefone;
            } else {
                responses.status = 400;
                responses.messages.add("Por favor, preencha o Telefone corretamente!");
            }
            if (pOrganizacao.endereco != null) {
                organizacao.endereco = pOrganizacao.endereco;
            } else {
                responses.status = 400;
                responses.messages.add("Por favor, preencha o Endereço corretamente!");
            }
            if (pOrganizacao.email != null) {
                organizacao.email = pOrganizacao.email;
            } else {
                responses.status = 400;
                responses.messages.add("Por favor, preencha o Email corretamente!");
            }
            if (responses.messages.isEmpty()) {
                organizacao.usuario = usuarioAuth;
                organizacao.usuarioAcao = usuarioAuth;
                organizacao.isAtivo = Boolean.TRUE;
                organizacao.dataAcao = new Date();
                organizacao.persist();

                responses.status = 201;
                responses.data = organizacao;
                responses.messages.add("Organização cadastrada com sucesso!");

            } else {
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } else {
            responses.status = 400;
            responses.data = organizacao;
            responses.messages.add("Organização já cadastrada!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateOrganizacao(@NotNull Organizacao pOrganizacao, @NotNull String login) {

        usuarioAuth = Usuario.find("login = ?1", login).firstResult();

        responses = new Responses();
        responses.messages = new ArrayList<>();

        try {

            if (pOrganizacao.id != null) {
                organizacao = Organizacao.findById(pOrganizacao.id);
            }
            if (pOrganizacao.id == null && pOrganizacao.nome == null && pOrganizacao.cnpj == null) {
                throw new BadRequestException("Informe os dados para atualizar o cadastro da Organização.");
            } else {
                if (pOrganizacao.nome != null) {
                    organizacao.nome = pOrganizacao.nome;
                }
                if (pOrganizacao.cnpj != null) {
                    organizacao.cnpj = pOrganizacao.cnpj;
                }
                if (pOrganizacao.telefone != null) {
                    organizacao.telefone = pOrganizacao.telefone;
                }
                if (pOrganizacao.endereco != null) {
                    organizacao.endereco = pOrganizacao.endereco;
                }
                if (pOrganizacao.email != null) {
                    organizacao.email = pOrganizacao.email;
                }
                organizacao.usuarioAcao = usuarioAuth;
                organizacao.dataAcao = new Date();
                organizacao.persist();

                responses.status = 200;
                responses.data = organizacao;
                responses.messages.add("Cadastro de Organização atualizado com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            responses.data = organizacao;
            responses.messages.add("Não foi possível atualizar o cadastro da Organização.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteOrganizacao(@NotNull List<Long> pListIdOrganizacao, String login) {

        List<Organizacao> organizacoes;
        List<Organizacao> organizacoesAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();

        organizacoes = Organizacao.list("id in ?1 and isAtivo = true", pListIdOrganizacao);
        int count = organizacoes.size();

        try {

            if (organizacoes.isEmpty()) {
                responses.status = 400;
                responses.messages.add("Organizações não localizadas ou já excluídas.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            organizacoes.forEach((organizacao) -> {

                organizacao.usuarioAcao = usuarioAuth;
                organizacao.isAtivo = Boolean.FALSE;
                organizacao.dataAcao = new Date();
                organizacao.systemDateDeleted = new Date();
                organizacao.persist();
                organizacoesAux.add(organizacao);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = organizacao;
                responses.messages.add("Organização excluída com sucesso!");
            } else {
                responses.datas = Collections.singletonList(organizacoesAux);
                responses.messages.add(count + " Organizações excluídas com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = organizacao;
                responses.messages.add("Organização não localizada ou já excluída.");
            } else {
                responses.datas = Collections.singletonList(organizacoes);
                responses.messages.add("Organizações não localizadas ou já excluídas.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response reactivateOrganizacao(@NotNull List<Long> pListIdOrganizacao, String login) {

        List<Organizacao> organizacoes;
        List<Organizacao> organizacoesAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        Usuario usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        organizacoes = Organizacao.list("id in ?1 and isAtivo = false", pListIdOrganizacao);
        int count = organizacoes.size();

        try {

            if (organizacoes.isEmpty()) {
                responses.status = 400;
                responses.messages.add("Organizações não localizadas ou já reativadas.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            organizacoes.forEach((organizacao) -> {

                organizacao.usuarioAcao = usuarioAuth;
                organizacao.isAtivo = Boolean.TRUE;
                organizacao.dataAcao = new Date();
                organizacao.systemDateDeleted = new Date();
                organizacao.persist();
                organizacoesAux.add(organizacao);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = organizacao;
                responses.messages.add("Organização reativada com sucesso!");
            } else {
                responses.datas = Collections.singletonList(organizacoesAux);
                responses.messages.add(count + " Organizações reativadas com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = organizacao;
                responses.messages.add("Organização não localizada ou já reativada.");
            } else {
                responses.datas = Collections.singletonList(organizacoes);
                responses.messages.add("Organizações não localizadas ou já reativadas.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}

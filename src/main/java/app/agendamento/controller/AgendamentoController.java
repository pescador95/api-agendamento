package app.agendamento.controller;

import app.agendamento.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import app.core.model.Organizacao;
import app.core.model.Responses;
import app.core.model.Usuario;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.*;

@ApplicationScoped
@Transactional
public class AgendamentoController {

    private Agendamento agendamento = new Agendamento();
    private Responses responses;
    private Usuario usuarioAuth;
    private Usuario profissional;
    private Pessoa pessoa;
    private Organizacao organizacao = new Organizacao();

    public Response addAgendamento(@NotNull Agendamento pAgendamento, @NotNull String login) {

        responses = new Responses();
        responses.messages = new ArrayList<>();
        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        agendamento = Agendamento.find("pessoaAgendamento = ?1 and dataConsulta = ?2 and isAtivo = true",
                pAgendamento.pessoaAgendamento, pAgendamento.dataAgenda).firstResult();

        if (pAgendamento.pessoaAgendamento != null && pAgendamento.pessoaAgendamento.id != null) {
            profissional = Usuario.findById(pAgendamento.pessoaAgendamento.id);

        }

        if (pAgendamento.pessoaAgendamento != null && pAgendamento.pessoaAgendamento.id != null) {
            pessoa = Pessoa.findById(pAgendamento.pessoaAgendamento.id);

        }


        if (pAgendamento.organizaoAgendamento != null && pAgendamento.organizaoAgendamento.id != null) {
            organizacao = Organizacao.findById(pAgendamento.organizaoAgendamento.id);
        }

        if (agendamento == null) {
            agendamento = new Agendamento();

            if (pAgendamento.dataAgenda != null) {
                agendamento.dataAgenda = pAgendamento.dataAgenda;
            } else {
                responses.messages.add("Por favor, informe a Data da Consulta corretamente!");
            }
            if (profissional != null) {
                agendamento.profissionalAgendamento = profissional;
                agendamento.nomeProfissional = profissional.pessoa.nome;
            } else {
                agendamento.profissionalAgendamento = usuarioAuth;
                agendamento.nomeProfissional = usuarioAuth.pessoa.nome;
            }
            if (pessoa != null) {
                agendamento.pessoaAgendamento = pessoa;

            }

            if (organizacao != null) {
                agendamento.organizaoAgendamento = organizacao;
            } else {
                responses.messages.add("Por favor, selecione o Local do Atendimento corretamente!");
            }
            if (responses.messages.isEmpty()) {
                agendamento.statusConsulta = "AGENDADA";
                agendamento.usuario = usuarioAuth;
                agendamento.usuarioAcao = usuarioAuth;
                agendamento.isAtivo = Boolean.TRUE;
                agendamento.dataAcao = new Date();
                agendamento.persist();

                responses.status = 201;
                responses.data = agendamento;
                responses.messages.add("Agendamento cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } else {
            responses.status = 400;
            responses.data = agendamento;
            responses.messages.add("Agendamento já realizado!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateAgendamento(@NotNull Agendamento pAgendamento, @NotNull String login) {

        Usuario usuarioAuth = Usuario.find("login = ?1", login).firstResult();

        responses = new Responses();
        responses.messages = new ArrayList<>();
        organizacao = new Organizacao();

        if (pAgendamento.id != null) {
            agendamento = Agendamento.find("id = ?1 and isAtivo = true", pAgendamento.id).firstResult();
        }

        if (pAgendamento.profissionalAgendamento != null && pAgendamento.profissionalAgendamento.id != null) {
            profissional = Usuario.findById(pAgendamento.profissionalAgendamento.id);
        }

        if (pAgendamento.pessoaAgendamento != null && pAgendamento.pessoaAgendamento.id != null) {
            pessoa = Pessoa.findById(pAgendamento.pessoaAgendamento.id);
        }


        if (pAgendamento.organizaoAgendamento != null && pAgendamento.organizaoAgendamento.id != null) {
            organizacao = Organizacao.findById(pAgendamento.organizaoAgendamento.id);
        }

        try {

            if (pAgendamento.id == null && pAgendamento.dataAgenda == null
                    && pAgendamento.profissionalAgendamento == null
                    && pAgendamento.pessoaAgendamento == null && pAgendamento.organizaoAgendamento == null) {
                throw new BadRequestException("Informe os dados para remarcar o Agendamento.");
            } else {
                if (pAgendamento.dataAgenda != null) {
                    agendamento.dataAgenda = pAgendamento.dataAgenda;
                }
                if (pAgendamento.profissionalAgendamento != null) {
                    agendamento.profissionalAgendamento = pAgendamento.profissionalAgendamento;
                }
                if (pAgendamento.pessoaAgendamento != null) {
                    agendamento.pessoaAgendamento = pAgendamento.pessoaAgendamento;
                }
                if (pAgendamento.organizaoAgendamento != null) {
                    agendamento.organizaoAgendamento = pAgendamento.organizaoAgendamento;
                }
                if (profissional != null) {
                    agendamento.profissionalAgendamento = profissional;
                    agendamento.nomeProfissional = profissional.pessoa.nome;
                }
                if (organizacao != null) {
                    agendamento.organizaoAgendamento = organizacao;
                }
                agendamento.nomePessoa = pessoa.nome;
                agendamento.statusConsulta = "REMARCADA";
                agendamento.usuarioAcao = usuarioAuth;
                agendamento.dataAcao = new Date();
                agendamento.persist();

                responses.status = 200;
                responses.data = agendamento;
                responses.messages.add("Agendamento remarcado com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            responses.data = agendamento;
            responses.messages.add("Não foi possível remarcar o Agendamento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteAgendamento(@NotNull List<Long> pListIdAgendamento, String login) {

        List<Agendamento> agendamentos;
        List<Agendamento> agendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        agendamentos = Agendamento.list("id in ?1 and isAtivo = true", pListIdAgendamento);
        int count = agendamentos.size();

        try {

            if (agendamentos.isEmpty()) {
                responses.status = 400;
                responses.messages.add("Agendamentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            agendamentos.forEach((agendamento) -> {
                agendamento.statusConsulta = "CANCELADO";
                agendamento.usuarioAcao = usuarioAuth;
                agendamento.isAtivo = Boolean.FALSE;
                agendamento.dataAcao = new Date();
                agendamento.systemDateDeleted = new Date();
                agendamento.persist();
                agendamentosAux.add(agendamento);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = agendamento;
                responses.messages.add("Agendamento excluído com sucesso!");
            } else {
                responses.datas = Collections.singletonList(agendamentosAux);
                responses.messages.add(count + " Agendamentos excluídos com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = agendamento;
                responses.messages.add("Agendamento não localizada ou já excluído.");
            } else {
                responses.datas = Collections.singletonList(agendamentos);
                responses.messages.add("Agendamentos não localizadas ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response reactivateAgendamento(@NotNull List<Long> pListIdAgendamento, String login) {

        List<Agendamento> agendamentos;
        List<Agendamento> agendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        Usuario usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        agendamentos = Agendamento.list("id in ?1 and isAtivo = false", pListIdAgendamento);
        int count = agendamentos.size();

        try {

            if (agendamentos.isEmpty()) {
                responses.status = 400;
                responses.messages.add("Agendamentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            agendamentos.forEach((agendamento) -> {
                agendamento.statusConsulta = "A REMARCAR";
                agendamento.usuarioAcao = usuarioAuth;
                agendamento.isAtivo = Boolean.TRUE;
                agendamento.dataAcao = new Date();
                agendamento.systemDateDeleted = new Date();
                agendamento.persist();
                agendamentosAux.add(agendamento);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = agendamento;
                responses.messages.add("Agendamento reativado com sucesso!");
            } else {
                responses.datas = Collections.singletonList(agendamentosAux);
                responses.messages.add(count + " Agendamentos reativados com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = agendamento;
                responses.messages.add("Agendamento não localizado ou já reativado.");
            } else {
                responses.datas = Collections.singletonList(agendamentos);
                responses.messages.add("Agendamentos não localizados ou já reativados.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}

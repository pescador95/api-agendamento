package app.agendamento.controller;

import app.agendamento.model.Pessoa;
import app.core.model.Responses;
import app.core.model.Usuario;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.*;

@ApplicationScoped
@Transactional
public class PessoaController {

    private Pessoa pessoa = new Pessoa();

    private Responses responses;

    private Usuario usuarioAuth;

    public Response addPessoa(@NotNull Pessoa pPessoa, String login) {

        responses = new Responses();
        responses.messages = new ArrayList<>();
        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();

        if (pPessoa.cpf != 0L) {
            pessoa = Pessoa.find("cpf = ?1 and isAtivo = true", pPessoa.cpf).firstResult();
        }

        if (pessoa == null) {
            pessoa = new Pessoa();

            if (pPessoa.nome != null) {
                pessoa.nome = pPessoa.nome;
            } else {
                responses.messages.add("Por favor, preencha o nome da Pessoa corretamente!");
            }

            if (pPessoa.genero != null) {
                pessoa.genero = pPessoa.genero;
            } else {
                responses.messages.add("Por favor, preencha o gênero da Pessoa corretamente!");
            }

            if (pPessoa.dataNascimento != null) {
                pessoa.dataNascimento = pPessoa.dataNascimento;
            } else {
                responses.messages.add("Por favor, preencha a data de nascimento da Pessoa corretamente!");
            }

            if (pPessoa.telefone != null) {
                pessoa.telefone = pPessoa.telefone;
            } else {
                responses.messages.add("Por favor, preencha o telefone de contato da Pessoa corretamente!");
            }

            if (pPessoa.telefone2 != null) {
                pessoa.telefone2 = pPessoa.telefone2;
            } else {
                responses.messages.add("Por favor, preencha o telefone secundário da Pessoa corretamente!");
            }

            if (pPessoa.email != null) {
                pessoa.email = pPessoa.email;
            } else {
                responses.messages.add("Por favor, preencha o Email da Pessoa corretamente!");
            }

            if (pPessoa.endereco != null) {
                pessoa.endereco = pPessoa.endereco;
            } else {
                responses.messages.add("Por favor, preencha o Endereço da Pessoa corretamente!");
            }

            if (responses.messages.isEmpty()) {
                pessoa.cpf = pPessoa.cpf;
                pessoa.usuario = usuarioAuth;
                pessoa.usuarioAcao = usuarioAuth;
                pessoa.isAtivo = Boolean.TRUE;
                pessoa.dataAcao = new Date();
                pessoa.persist();

                responses.status = 201;
                responses.data = pessoa;
                responses.messages.add("Pessoa cadastrada com sucesso!");

            } else {
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } else {
            responses.status = 400;
            responses.data = pessoa;
            responses.messages.add("Pessoa já cadastrada!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updatePessoa(@NotNull Pessoa pPessoa, String login) throws BadRequestException {

        usuarioAuth = Usuario.find("login = ?1", login).firstResult();

        responses = new Responses();
        responses.messages = new ArrayList<>();

        try {

            if (pPessoa != null && pPessoa.id != null) {
                pessoa = Pessoa.findById(pPessoa.id);
            }

            if (pessoa == null) {
                responses.messages.add("A pessoa qual você deseja alterar os dados não foi localizada!");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            } else {
                if (pPessoa.nome != null) {
                    if (!pessoa.nome.equals(pPessoa.nome)) {
                        pessoa.nome = pPessoa.nome;
                    }
                }
                if (pPessoa.dataNascimento != null) {
                    if (!pessoa.genero.equals(pPessoa.genero)) {
                        pessoa.genero = pPessoa.genero;
                    }
                }

                if (pPessoa.cpf != 0L) {
                    if (pessoa.cpf != pPessoa.cpf) {
                        pessoa.cpf = pPessoa.cpf;
                    }
                }

                if (pPessoa.dataNascimento != null) {
                    if (!pessoa.dataNascimento.equals(pPessoa.dataNascimento)) {
                        pessoa.dataNascimento = pPessoa.dataNascimento;
                    }
                }
                if (pPessoa.telefone != null) {
                    if (pessoa.telefone != null && !pessoa.telefone.equals(pPessoa.telefone)) {
                        pessoa.telefone = pPessoa.telefone;
                    }
                }
                if (pPessoa.telefone != null) {
                    if (!pessoa.telefone.equals(pPessoa.telefone)) {
                        pessoa.telefone = pPessoa.telefone;
                    }
                }
                if (pPessoa.telefone != null) {
                    if (!pessoa.telefone2.equals(pPessoa.telefone2)) {
                        pessoa.telefone2 = pPessoa.telefone2;
                    }
                }
                if (pPessoa.email != null) {
                    if (!pessoa.email.equals(pPessoa.email)) {
                        pessoa.email = pPessoa.email;
                    }
                }
                if (pPessoa.endereco != null) {
                    if (!pessoa.endereco.equals(pPessoa.endereco)) {
                        pessoa.endereco = pPessoa.endereco;
                    }
                }

                pessoa.usuarioAcao = usuarioAuth;
                pessoa.dataAcao = new Date();
                pessoa.persist();

                responses.status = 200;
                responses.data = pessoa;
                responses.messages.add("Cadastro de Pessoa atualizada com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            responses.data = pessoa;
            responses.messages.add("Não foi possível atualizar o cadastro Pessoa.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deletePessoa(@NotNull List<Long> pListpessoa, String login) {

        List<Pessoa> pessoas;
        List<Pessoa> pessoasAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        pessoas = Pessoa.list("id in ?1 and isAtivo = true", pListpessoa);
        int count = pessoas.size();

        if (pessoas.isEmpty()) {
            responses.status = 400;
            responses.messages.add("Pessoas não localizadas ou já excuídas.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }

        try {

            pessoas.forEach((pessoa) -> {

                pessoa.usuarioAcao = usuarioAuth;
                pessoa.isAtivo = Boolean.FALSE;
                pessoa.dataAcao = new Date();
                pessoa.systemDateDeleted = new Date();
                pessoa.persist();
                pessoasAux.add(pessoa);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = pessoa;
                responses.messages.add("Pessoa excluída com sucesso!");
            } else {
                responses.datas = Collections.singletonList(pessoasAux);
                responses.messages.add(count + " Pessoas excluídas com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = pessoa;
                responses.messages.add("Pessoa não localizada ou já excluído.");
            } else {
                responses.datas = Collections.singletonList(pessoas);
                responses.messages.add("Pessoas não localizadas ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response reactivatePessoa(@NotNull List<Long> pListpessoa, String login) {

        List<Pessoa> pessoas;
        List<Pessoa> pessoasAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        pessoas = Pessoa.list("id in ?1 and isAtivo = false", pListpessoa);
        int count = pessoas.size();

        if (pessoas.isEmpty()) {
            responses.status = 400;
            responses.messages.add("Pessoas não localizadas ou já reativadas.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }

        try {
            pessoas.forEach((pessoa) -> {

                pessoa.usuarioAcao = usuarioAuth;
                pessoa.isAtivo = Boolean.TRUE;
                pessoa.dataAcao = new Date();
                pessoa.systemDateDeleted = new Date();
                pessoa.persist();
                pessoasAux.add(pessoa);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = pessoa;
                responses.messages.add("Pessoa reativado com sucesso!");
            } else {
                responses.datas = Collections.singletonList(pessoasAux);
                responses.messages.add(count + " Pessoas reativados com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = pessoa;
                responses.messages.add("Pessoa não localizada ou já reativado.");
            } else {
                responses.datas = Collections.singletonList(pessoas);
                responses.messages.add("Pessoas não localizadas ou já reativados.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}

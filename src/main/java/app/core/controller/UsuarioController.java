package app.core.controller;

import app.agendamento.model.*;
import app.core.model.Organizacao;
import app.core.model.Responses;
import app.core.model.Role;
import app.core.model.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.*;

@ApplicationScoped
@Transactional
public class UsuarioController {
    private Usuario usuario;

    private Responses responses;

    private Usuario usuarioAuth;

    private Pessoa pessoa;

    public Response addUser(@NotNull Usuario pUsuario, String login) {

        responses = new Responses();
        responses.messages = new ArrayList<>();
        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();

        if (pUsuario != null && pUsuario.login != null) {
            usuario = Usuario.find("login = ?1 and isAtivo = true", pUsuario.login).firstResult();
        }

        List<Organizacao> organizacoes;

        List<Role> roles;

        List<Long> organizacoesId = new ArrayList<>();

        if (pUsuario.organizacoes != null) {
            pUsuario.organizacoes.forEach(organizacao -> organizacoesId.add(organizacao.id));
        }

        organizacoes = Organizacao.list("id in ?1", organizacoesId);

        if (pUsuario.pessoa != null && pUsuario.pessoa.id != null) {
            pessoa = Pessoa.findById(pUsuario.pessoa.id);
        }

        List<Long> privilegioId = new ArrayList<>();

        if (pUsuario.privilegio != null) {
            pUsuario.privilegio.forEach(privilegio -> privilegioId.add(privilegio.id));
        }

        roles = Role.list("id in ?1", privilegioId);

        if (usuario == null && (!organizacoes.isEmpty()) && (pessoa != null) && !roles.isEmpty()) {
            usuario = new Usuario();

            if (pUsuario.login != null) {
                usuario.login = pUsuario.login;
            } else {
                responses.messages.add("Por favor, preencha o Login do Usuário corretamente!");
            }
            if (pUsuario.password != null) {
                usuario.password = BcryptUtil.bcryptHash(pUsuario.password);
            } else {
                responses.messages.add("Por favor, preencha a senha do Usuário corretamente!");
            }
            if (roles != null) {
                usuario.privilegio = new ArrayList<>();
                usuario.privilegio.addAll(roles);
            } else {
                responses.messages.add("Por favor, preencha a permissão do Usuário corretamente!");
            }
            if (pUsuario.pessoa != null) {
                usuario.pessoa = pUsuario.pessoa;
            } else {
                responses.messages.add("Por favor, preencha a Pessoa corretamente!");
            }
            if (pUsuario.email != null) {
                usuario.email = pUsuario.email;
            } else {
                responses.messages.add("Por favor, preencha a Pessoa corretamente!");
            }
            if (organizacoes != null) {
                usuario.organizacoes = new ArrayList<>();
                usuario.organizacoes.addAll(organizacoes);
            } else {
                responses.messages.add("Por favor, preencha a Organização corretamente!");
            }
            if (responses.messages != null) {
                usuario.usuario = usuarioAuth.pessoa.nome;
                usuario.usuarioAcao = usuarioAuth.pessoa.nome;
                usuario.isAtivo = Boolean.TRUE;
                usuario.dataAcao = new Date();
                usuario.persist();

                responses.status = 201;
                responses.data = usuario;
                responses.messages.add("Usuário cadastrado com sucesso!");

            } else {
                responses.messages.add("Por favor, verifique as informações necessárias!");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } else {
            responses.status = 400;
            responses.data = usuario;
            responses.messages.add("Verifique as informações!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateUser(@NotNull Usuario pUsuario, String login) {

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();

        responses = new Responses();
        responses.messages = new ArrayList<>();
        List<Organizacao> organizacoes;
        List<Long> organizacoesId = new ArrayList<>();
        List<Role> roles;
        List<Long> privilegioId = new ArrayList<>();

        if (pUsuario.privilegio != null) {
            pUsuario.privilegio.forEach(privilegio -> privilegioId.add(privilegio.id));
        }

        if (pUsuario.organizacoes != null) {
            pUsuario.organizacoes.forEach(organizacao -> organizacoesId.add(organizacao.id));
        }
        organizacoes = Organizacao.list("id in ?1", organizacoesId);

        roles = Role.list("id in ?1", privilegioId);

        try {

            if (pUsuario != null && pUsuario.id != null && !organizacoes.isEmpty() && !roles.isEmpty()) {
                usuario = Usuario.findById(pUsuario.id);
            }

            if (pUsuario.login == null && pUsuario.password == null
                    && pUsuario.privilegio == null && pUsuario.organizacoes == null && pUsuario.pessoa == null
                    && pUsuario.email == null && organizacoes.isEmpty()) {
                throw new BadRequestException("Informe os dados para atualizar o Usuário.");

            } else {

                if (pUsuario.login != null) {
                    if (!usuario.login.equals(pUsuario.login)) {
                        usuario.login = pUsuario.login;
                    }
                }
                if (pUsuario.password != null) {
                    if (usuario.password != null && !usuario.password.equals(pUsuario.password)) {
                        usuario.password = BcryptUtil.bcryptHash(pUsuario.password);
                    }
                }
                if (pUsuario.privilegio != null) {
                    if (!Objects.equals(usuario.privilegio, pUsuario.privilegio)) {
                        usuario.privilegio = pUsuario.privilegio;
                    }
                }
                if (pUsuario.pessoa != null) {
                    if (!Objects.equals(usuario.pessoa, pUsuario.pessoa)) {
                        usuario.pessoa = pUsuario.pessoa;
                    }
                }
                if (pUsuario.email != null) {
                    if (!Objects.equals(usuario.email, pUsuario.email)) {
                        usuario.email = pUsuario.email;
                    }
                }
                if (organizacoes != null) {
                    usuario.organizacoes = new ArrayList<>();
                    usuario.organizacoes.addAll(organizacoes);
                }
                if (roles != null) {
                    usuario.privilegio = new ArrayList<>();
                    usuario.privilegio.addAll(roles);
                }
                usuario.usuarioAcao = usuarioAuth.login;
                usuario.dataAcao = new Date();
                usuario.persist();

                responses.status = 200;
                responses.data = usuario;
                responses.messages.add("Usuário atualizado com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            responses.data = usuario;
            responses.messages.add("Não foi possível atualizar o Usuário.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteUser(@NotNull List<Long> pListIdUsuario, String login) {

        List<Usuario> usuarios;
        List<Usuario> usuariosAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        usuarios = Usuario.list("id in ?1 and isAtivo = true", pListIdUsuario);
        int count = usuarios.size();

        try {

            if (usuarios.isEmpty()) {
                responses.status = 400;
                responses.messages.add("Usuários não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            usuarios.forEach((usuario) -> {

                usuario.usuarioAcao = usuarioAuth.usuario;
                usuario.isAtivo = Boolean.FALSE;
                usuario.dataAcao = new Date();
                usuario.systemDateDeleted = new Date();
                usuario.persist();
                usuariosAux.add(usuario);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = usuario;
                responses.messages.add("Usuário excluído com sucesso!");
            } else {
                responses.datas = Collections.singletonList(usuariosAux);
                responses.messages.add(count + " Usuários excluídos com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = usuario;
                responses.messages.add("Usuário não localizado ou já excluído.");
            } else {
                responses.datas = Collections.singletonList(usuarios);
                responses.messages.add("Usuários não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response reactivateUser(@NotNull List<Long> pListIdUsuario, String login) {

        List<Usuario> usuarios;
        List<Usuario> usuariosAux = new ArrayList<>();
        responses = new Responses();
        responses.messages = new ArrayList<>();

        usuarioAuth = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        usuarios = Usuario.list("id in ?1 and isAtivo = false", pListIdUsuario);
        int count = usuarios.size();

        if (usuarios.isEmpty()) {
            responses.status = 400;
            responses.messages.add("Usuários não localizados ou já reativados.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }

        try {

            usuarios.forEach((usuario) -> {

                usuario.usuarioAcao = usuarioAuth.usuario;
                usuario.isAtivo = Boolean.TRUE;
                usuario.dataAcao = new Date();
                usuario.systemDateDeleted = new Date();
                usuario.persist();
                usuariosAux.add(usuario);
            });
            responses.status = 200;
            if (count <= 1) {
                responses.data = usuario;
                responses.messages.add("Usuário reativado com sucesso!");
            } else {
                responses.datas = Collections.singletonList(usuariosAux);
                responses.messages.add(count + " Usuários reativados com sucesso!");
            }
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses.status = 400;
            if (count <= 1) {
                responses.data = usuario;
                responses.messages.add("Usuário não localizado ou já reativado.");
            } else {
                responses.datas = Collections.singletonList(usuarios);
                responses.messages.add("Usuários não localizados ou já reativados.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}

package app.core.controller;


import app.agendamento.model.Pessoa;
import app.core.model.Responses;
import app.core.model.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import net.bytebuddy.utility.RandomString;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;

@ApplicationScoped
@Transactional
public class RecoveryPasswordController {
    @Inject
    Mailer mailer;

    private Responses responses;

    public Response sendEmail(String login) {
        responses = new Responses();
        responses.messages = new ArrayList<>();

        Usuario usuario = Usuario.find("login = ?1 and isAtivo = true", login).firstResult();
        if (usuario.isAtivo) {
            String senha = RandomString.make(12);
            System.out.println(senha);
            usuario.password = BcryptUtil.bcryptHash(senha);
            usuario.persist();
            Pessoa pessoa = Pessoa.findById(usuario.pessoa.id);
            String nome = pessoa.nome;
            mailer.send(Mail.withText(usuario.email, "API Agendamento - Recuperação de Senha", "Olá, " + nome + "!\n"
                    + "Aqui está a sua nova senha para realização do acesso ao sistema de Agendamentos: " + senha));
            responses.status = 200;
            responses.data = usuario;
            responses.messages.add("Enviado uma nova senha para o email informado: " + usuario.email);
        } else {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível localizar um cadastro com o email informado.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(responses).status(Response.Status.ACCEPTED).build();
    }

    public Response updatePassword(String email, String password) {
        responses = new Responses();
        responses.messages = new ArrayList<>();

        try {
            Usuario usuarioAuth = Usuario.find("email = ?1", email).firstResult();

            usuarioAuth.password = BcryptUtil.bcryptHash(password);
            usuarioAuth.usuarioAcao = usuarioAuth.login;
            usuarioAuth.dataAcao = new Date();
            usuarioAuth.persist();

            responses.status = 200;
            responses.data = usuarioAuth;
            responses.messages.add("Senha atualizada com sucesso.");
            return Response.ok(responses).status(Response.Status.ACCEPTED).build();
        } catch (Exception e) {
            responses = new Responses();
            responses.status = 400;
            responses.messages.add("Não foi possível atualizar a senha.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }
}
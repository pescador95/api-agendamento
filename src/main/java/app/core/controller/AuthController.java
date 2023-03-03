package app.core.controller;

import app.core.model.Role;
import app.core.model.Usuario;
import app.core.utils.AuthToken;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import app.core.model.Auth;
import org.apache.sshd.common.config.keys.loader.openssh.kdf.BCrypt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class AuthController {

    @Inject
    AuthToken token;

    @Inject
    JWTParser parser;

    private List<Role> roles;

    private List<String> privilegios = new ArrayList<>();

    public Auth login(Auth data) {

        roles = new ArrayList<>();
        privilegios = new ArrayList<>();

        Usuario usuario = Usuario.find("login", data.login).firstResult();

        if (!(usuario == null && usuario.privilegio.isEmpty())) {
            usuario.privilegio.forEach(c -> privilegios.add(c.privilegio));
        }

        if (usuario == null) {
            throw new BadRequestException("login ou senha incorretas");
        }

        boolean authenticated = BCrypt.checkpw(data.password, usuario.password);

        if (!authenticated) {
            throw new BadRequestException("login ou senha incorretas");
        }

        System.out.print("\n" + "Iniciando login com usuário: " + usuario.login + "..." + "\n" + "\n" + "Bem vindo, "
                + usuario.login + "!"); // todo log
        String accessToken = token.GenerateAccessToken(usuario);
        String refreshToken = token.GenerateRefreshToken(usuario);

        Long ACTOKEN = 0L;
        Long RFTOKEN;

        try {
            ACTOKEN = parser.parse(accessToken).getClaim("exp");
            RFTOKEN = parser.parse(refreshToken).getClaim("exp");
        } catch (ParseException e) {
            throw new BadRequestException("Credenciais incorretas!");
        }

        Auth auth = new Auth();
        auth.login = usuario.login;
        auth.password = BcryptUtil.bcryptHash(usuario.password);
        auth.privilegio = new ArrayList<>();
        auth.roles = new ArrayList<>();
        auth.privilegio.addAll(usuario.privilegio);
        auth.roles.addAll(privilegios);
        auth.accessToken = accessToken;
        auth.refreshToken = refreshToken;
        auth.usuario = usuario;
        auth.expireDateAccessToken = new Date(ACTOKEN * 1000); // milisec -> sec -> Date
        auth.expireDateRefreshToken = new Date(RFTOKEN * 1000); // milisec -> sec -> Date
        return auth;
    }

    public Auth refreshToken(Auth data, SecurityContext context) {
        boolean authenticated = false;
        Usuario usuario = null;
        Date expireDate;
        roles = new ArrayList<>();
        privilegios = new ArrayList<>();
        try {
            String login = parser.parse(data.refreshToken).getClaim("upn");
            long expireDateOldToken = parser.parse(data.refreshToken).getClaim("exp");

            usuario = Usuario.find("login", login).firstResult();

            if (!(usuario == null && usuario.privilegio.isEmpty())) {
                usuario.privilegio.forEach(c -> privilegios.add(c.privilegio));
            }

            expireDate = new Date(expireDateOldToken * 1000); // milsec to sec

            if (expireDate.after(new Date()) && usuario != null) {
                authenticated = true;
            } else {
                throw new BadRequestException("Credenciais incorretas!");
            }
        } catch (ParseException error) {
            System.out.print(error.getMessage()); // todo logger
        }
        if (authenticated) {

            String accessToken = token.GenerateAccessToken(usuario);
            String refreshToken = token.GenerateRefreshToken(usuario);
            System.out.print("Refresh token solicitado pelo usuário: " + usuario.login + "\n"); // todo logger
            Long ACTOKEN;
            Long RFTOKEN;
            try {
                ACTOKEN = parser.parse(accessToken).getClaim("exp");
                RFTOKEN = parser.parse(refreshToken).getClaim("exp");
            } catch (ParseException e) {
                throw new BadRequestException("Credenciais incorretas!");
            }
            Auth auth = new Auth();
            auth.login = usuario.login;
            auth.privilegio = new ArrayList<>();
            auth.roles = new ArrayList<>();
            auth.privilegio.addAll(usuario.privilegio);
            auth.roles.addAll(privilegios);
            auth.usuario = usuario;
            auth.accessToken = accessToken;
            auth.expireDateAccessToken = new Date(ACTOKEN * 1000); // milisec -> sec -> Date
            auth.refreshToken = refreshToken;
            auth.expireDateRefreshToken = new Date(RFTOKEN * 1000); // milisec -> sec -> Date
            return auth;
        } else {
            throw new BadRequestException("Credenciais incorretas");
        }
    }
}

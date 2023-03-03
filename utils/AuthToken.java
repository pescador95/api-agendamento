package app.core.utils;

import app.agendamento.model.Usuario;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class AuthToken {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    public String issuer;

    public String GenerateAccessToken(Usuario pUsuario) {
        String accessToken = Jwt.issuer(this.issuer)
                .upn(pUsuario.login)
                .groups(String.valueOf(pUsuario.privilegio))
                .expiresAt(Instant.now().plus(Duration.ofMinutes(10)))
                .sign();
        return accessToken;
    }

    public String GenerateRefreshToken(Usuario pUsuario) {
        String refreshToken = Jwt.issuer(this.issuer)
                .upn(pUsuario.login)
                .groups(String.valueOf(pUsuario.privilegio))
                .expiresAt(Instant.now().plus(Duration.ofDays(7)))
                .sign();
        return refreshToken;
    }
}

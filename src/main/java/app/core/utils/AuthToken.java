package app.core.utils;

import app.core.model.Usuario;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class AuthToken {

    Set<String> privilegios = new HashSet<>();
    @ConfigProperty(name = "mp.jwt.verify.issuer")

    public String issuer;
    private String accessToken;
    private String refreshToken;

    public String GenerateAccessToken(Usuario pUsuario) {


        if (pUsuario != null && (!pUsuario.privilegio.isEmpty())) {
            pUsuario.privilegio.forEach(c -> privilegios.add(c.privilegio));
        }
        accessToken = Jwt.issuer(this.issuer)
                .upn(pUsuario.login)
                .groups(privilegios)
                .expiresAt(Instant.now().plus(Duration.ofMinutes(10)))
                .sign();
        return accessToken;
    }

    public String GenerateRefreshToken(Usuario pUsuario) {

        if (pUsuario != null && (!pUsuario.privilegio.isEmpty())) {
            pUsuario.privilegio.forEach(c -> privilegios.add(c.privilegio));
        }

        refreshToken = Jwt.issuer(this.issuer)
                .upn(pUsuario.login)
                .groups(privilegios)
                .expiresAt(Instant.now().plus(Duration.ofDays(7)))
                .sign();
        return refreshToken;
    }
}

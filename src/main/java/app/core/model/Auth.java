package app.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class Auth {
    public String login;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public List<Role> privilegio;
    public List<String> roles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Usuario usuario;
    public String accessToken;
    public Date expireDateAccessToken;
    public String refreshToken;
    public Date expireDateRefreshToken;

}

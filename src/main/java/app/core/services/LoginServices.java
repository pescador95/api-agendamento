package app.core.services;

import app.core.model.Usuario;
import app.core.utils.AuthToken;

import javax.inject.Inject;

public class LoginServices {

    @Inject
    AuthToken authToken;
    Usuario usuario;

    public void login() {

        authToken.GenerateAccessToken(usuario);
        {

        }
    }
}

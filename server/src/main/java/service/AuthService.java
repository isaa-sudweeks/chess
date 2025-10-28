package service;

import dataaccess.AuthDAO;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private AuthDAO authDAO;

    public AuthService(final AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthService() {
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String addAuthData(final String userName) {
        final String authToken = AuthService.generateToken();
        this.authDAO.addAuth(new AuthData(authToken, userName));
        return authToken;
    }

    public AuthData removeAuthData(final String authToken) {
        return this.authDAO.removeAuth(authToken);
    }

    public AuthData getAuth(final String authToken) {
        return this.authDAO.getAuth(authToken);
    }

    public void clear() {
        this.authDAO.clear();
    }
}

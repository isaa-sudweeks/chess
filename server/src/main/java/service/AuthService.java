package service;

import dataaccess.MemoryAuthDAO;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public AuthService(final MemoryAuthDAO authDAO) {
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

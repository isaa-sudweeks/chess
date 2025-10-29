package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.SQLException;
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

    public String addAuthData(final String userName) throws SQLException, DataAccessException {
        final String authToken = AuthService.generateToken();
        this.authDAO.addAuth(new AuthData(authToken, userName));
        return authToken;
    }

    public AuthData removeAuthData(final String authToken) throws SQLException, DataAccessException {
        return this.authDAO.removeAuth(authToken);
    }

    public AuthData getAuth(final String authToken) throws SQLException, DataAccessException {
        return this.authDAO.getAuth(authToken);
    }

    public void clear() throws SQLException, DataAccessException {
        this.authDAO.clear();
    }
}

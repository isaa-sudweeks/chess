package service;

import model.AuthData;
import dataaccess.MemoryAuthDAO;
import java.util.UUID;

public class AuthService {
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public void addAuthData(AuthData authData){
        if (authDAO.getAuth(authData.authToken()) == null){
            authDAO.addAuth(authData);
        }
    }
}

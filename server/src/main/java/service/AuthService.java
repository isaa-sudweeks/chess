package service;

import model.AuthData;
import dataaccess.MemoryAuthDAO;
import java.util.UUID;

public class AuthService {
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public AuthService(MemoryAuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public AuthService(){}

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public String addAuthData(String userName){
        String authToken = generateToken();
        authDAO.addAuth(new AuthData(authToken, userName));
        return authToken;
    }
    public AuthData removeAuthData(String authToken) {
        return authDAO.removeAuth(authToken);
    }
    public AuthData getAuth(String authToken){
        return authDAO.getAuth(authToken);
    }

    public void clear(){
        authDAO.clear();
    }
}

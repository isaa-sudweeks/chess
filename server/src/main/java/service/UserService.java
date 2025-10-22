package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.AuthService;

import java.util.Objects;

public class UserService {
    private MemoryUserDAO dataAccess = new MemoryUserDAO();
    private AuthService authService = new AuthService();
    public RegisterLoginResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (dataAccess.getUser(registerRequest.userName()) == null){
            dataAccess.addUser(new UserData(registerRequest.userName(), registerRequest.password(),registerRequest.email()));
            String authToken = authService.addAuthData(registerRequest.userName());
            return new RegisterLoginResult(registerRequest.userName(), authToken);
        }
        else {
            throw new DataAccessException("User already taken");
        }
    }
    public RegisterLoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = dataAccess.getUser(loginRequest.userName());
        if ((dataAccess.getUser(loginRequest.userName())!= null) && (Objects.equals(userData.password(), loginRequest.password()))){
            String authToken = authService.addAuthData(loginRequest.userName());
            return new RegisterLoginResult(loginRequest.userName(), authToken);
        }
        else {
            throw new DataAccessException("User not found or password doesn't match");
        }
    }
    public void logout(String authToken){
        authService.removeAuthData(authToken);
    }
}

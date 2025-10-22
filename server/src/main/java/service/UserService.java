package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;
import java.util.Objects;

public class UserService {
    private MemoryUserDAO dataAccess = new MemoryUserDAO();
    private AuthService authService = new AuthService();

    public UserService(MemoryUserDAO memoryUserDAO){
        this.dataAccess = memoryUserDAO;
    }

    public UserService(){}

    public UserService(AuthService authService, MemoryUserDAO memoryUserDAO){
        this.authService = authService;
        this.dataAccess = memoryUserDAO;
    }

    public RegisterLoginResult register(RegisterRequest registerRequest) throws DataAccessException {

        if (dataAccess.getUser(registerRequest.username()) == null){
            dataAccess.addUser(new UserData(registerRequest.username(), registerRequest.password(),registerRequest.email()));
            String authToken = authService.addAuthData(registerRequest.username());
            return new RegisterLoginResult(registerRequest.username(), authToken);
        }
        else {
            throw new DataAccessException("User already taken");
        }
    }
    public RegisterLoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = dataAccess.getUser(loginRequest.username());
        if ((dataAccess.getUser(loginRequest.username())!= null) && (Objects.equals(userData.password(), loginRequest.password()))){
            String authToken = authService.addAuthData(loginRequest.username());
            return new RegisterLoginResult(loginRequest.username(), authToken);
        }
        else {
            throw new DataAccessException("User not found or password doesn't match");
        }
    }
    public Object logout(String authToken) throws DataAccessException {
        if (authService.removeAuthData(authToken) == null){
            throw new DataAccessException("User is not logged in");
        }
        return null;
    }
}

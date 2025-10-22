package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.AuthService;
public class UserService {
    private MemoryUserDAO dataAccess = new MemoryUserDAO();
    private AuthService authService = new AuthService();
    public RegisterLoginResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (dataAccess.getUser(registerRequest.userName()) == null){
            dataAccess.addUser(new UserData(registerRequest.userName(), registerRequest.password(),registerRequest.email()));
            String authToken = AuthService.generateToken();
            authService.addAuthData(new AuthData(authToken , registerRequest.userName()));
            return new RegisterLoginResult(registerRequest.userName(), authToken);
        }
        else {
            throw new DataAccessException("User already taken");
        }
    }
    public RegisterLoginResult login(LoginRequest loginRequest){
        return null;
    }
    public void logout(){

    }
}

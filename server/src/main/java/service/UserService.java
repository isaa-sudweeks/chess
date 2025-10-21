package service;
import dataaccess.MemoryUserDAO;
import model.UserData;
import service.AuthService;
public class UserService {
    private MemoryUserDAO dataAccess = new MemoryUserDAO();
    public RegisterLoginResult register(RegisterRequest registerRequest){
        if (dataAccess.getUser(registerRequest.userName()) == null){
            dataAccess.addUser(new UserData(registerRequest.userName(), registerRequest.password(),registerRequest.email()));
        //TODO: Start Here
        }
    }
    public RegisterLoginResult login(LoginRequest loginRequest){
        return null;
    }
    public void logout(){

    }
}

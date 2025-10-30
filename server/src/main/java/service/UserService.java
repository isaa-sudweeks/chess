package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {
    private UserDAO dataAccess = new MemoryUserDAO();
    private AuthService authService = new AuthService();

    public UserService(final UserDAO userDAO) {
        dataAccess = userDAO;
    }

    public UserService() {
    }

    public UserService(final AuthService authService, final UserDAO userDAO) {
        this.authService = authService;
        dataAccess = userDAO;
    }

    String hashedPassword(String clearTextPassword) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        return hashedPassword;
    }

    public RegisterLoginResult register(final RegisterRequest registerRequest) throws DataAccessException, SQLException {
        //Check if the password or username are null
        if (null == registerRequest.password() || null == registerRequest.username()) {
            throw new BadRequestException("The user needs a password/username");
        }

        if (null == dataAccess.getUser(registerRequest.username())) {
            this.dataAccess.addUser(new UserData(registerRequest.username(), hashedPassword(registerRequest.password()), registerRequest.email()));
            final String authToken = this.authService.addAuthData(registerRequest.username());
            return new RegisterLoginResult(registerRequest.username(), authToken);
        } else {
            throw new AlreadyTakenException("User already taken");
        }
    }

    private boolean passwordsEqual(UserData userData, LoginRequest loginRequest) {
        return BCrypt.checkpw(loginRequest.password(), userData.password());
    }


    public RegisterLoginResult login(final LoginRequest loginRequest) throws DataAccessException, SQLException {
        final UserData userData = this.dataAccess.getUser(loginRequest.username());

        if (null == loginRequest.password() || null == loginRequest.username()) {
            throw new BadRequestException("Need a username or password to login");
        }

        if ((null != dataAccess.getUser(loginRequest.username()))) {
            if (passwordsEqual(userData, loginRequest)) {
                final String authToken = this.authService.addAuthData(loginRequest.username());
                return new RegisterLoginResult(loginRequest.username(), authToken);
            } else {
                throw new UnauthorizedException("User Password Doesn't match");
            }
        } else {
            throw new UnauthorizedException("User not found or password doesn't match");
        }
    }

    @SuppressWarnings("SameReturnValue")
    public Object logout(final String authToken) throws DataAccessException, SQLException {
        var data = authService.getAuth(authToken);
        if (data == null) {
            throw new UnauthorizedException("User is not logged in");
        } else {
            authService.removeAuthData(authToken);
            return null;
        }

    }

    public void clear() throws SQLException, DataAccessException {
        this.dataAccess.clear();
    }
}

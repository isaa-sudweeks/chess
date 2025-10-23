package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;

import java.util.Objects;

public class UserService {
    private MemoryUserDAO dataAccess = new MemoryUserDAO();
    private AuthService authService = new AuthService();

    public UserService(final MemoryUserDAO memoryUserDAO) {
        dataAccess = memoryUserDAO;
    }

    public UserService() {
    }

    public UserService(final AuthService authService, final MemoryUserDAO memoryUserDAO) {
        this.authService = authService;
        dataAccess = memoryUserDAO;
    }

    public RegisterLoginResult register(final RegisterRequest registerRequest) throws DataAccessException {

        //Check if password or username are null
        if (null == registerRequest.password() || null == registerRequest.username()) {
            throw new BadRequestException("The user needs a password/username");
        }

        if (null == dataAccess.getUser(registerRequest.username())) {
            this.dataAccess.addUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
            final String authToken = this.authService.addAuthData(registerRequest.username());
            return new RegisterLoginResult(registerRequest.username(), authToken);
        } else {
            throw new AlreadyTakenException("User already taken");
        }
    }

    public RegisterLoginResult login(final LoginRequest loginRequest) throws DataAccessException {
        final UserData userData = this.dataAccess.getUser(loginRequest.username());

        if (null == loginRequest.password() || null == loginRequest.username()) {
            throw new BadRequestException("Need a username or password to login");
        }

        if ((null != dataAccess.getUser(loginRequest.username()))) {
            if ((Objects.equals(userData.password(), loginRequest.password()))) {
                final String authToken = this.authService.addAuthData(loginRequest.username());
                return new RegisterLoginResult(loginRequest.username(), authToken);
            } else {
                throw new UnauthorizedException("User Password Doesn't match");
            }
        } else {
            throw new UnauthorizedException("User not found or password doesn't match");
        }
    }

    public Object logout(final String authToken) throws DataAccessException {
        if (null == authService.removeAuthData(authToken)) {
            throw new UnauthorizedException("User is not logged in");
        }
        return null;
    }

    public void clear() {
        this.dataAccess.clear();
    }
}

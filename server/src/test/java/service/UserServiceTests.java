package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @Test
    void testRegisterPass() throws DataAccessException {
        final MemoryUserDAO dataAccess = new MemoryUserDAO();
        final UserService service = new UserService(dataAccess);
        service.register(new RegisterRequest("Isaac", "Sudweeks", "isuds@byu.edu"));
        final UserData data = dataAccess.getUser("Isaac");
        assertEquals(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"), data);
    }

    @Test
    void testRegisterFail() {
        final MemoryUserDAO dataAccess = new MemoryUserDAO();
        dataAccess.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));
        final UserService service = new UserService(dataAccess);

        assertThrows(AlreadyTakenException.class, () ->
                service.register(new RegisterRequest("Isaac", "Sudweeks", "isuds@byu.edu")));
    }

    @Test
    void testLoginPass() throws DataAccessException {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);
        final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        final UserService service = new UserService(authService, memoryUserDAO);

        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));

        final RegisterLoginResult actual = service.login(new LoginRequest("Isaac", "Sudweeks"));
        final AuthData data = memoryAuthDAO.getAuth(actual.authToken());
        assertEquals(data.username(), actual.username());
    }

    @Test
    void testLoginFail() {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);
        final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        final UserService service = new UserService(authService, memoryUserDAO);
        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));
        assertThrows(UnauthorizedException.class, () ->
                service.login(new LoginRequest("Isaac", "Sudweek"))); //Bad password
    }

    @Test
    void testLogoutPass() throws DataAccessException {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);
        final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        final UserService service = new UserService(authService, memoryUserDAO);
        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));
        final RegisterLoginResult result = service.login(new LoginRequest("Isaac", "Sudweeks")); //See if I can figure out how to do this another way

        assertNull(service.logout(result.authToken()));
    }

    @Test
    void testLogoutFail() throws DataAccessException {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);
        final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();

        final UserService service = new UserService(authService, memoryUserDAO);

        //Add a user
        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));

        final RegisterLoginResult result = service.login(new LoginRequest("Isaac", "Sudweeks"));

        service.logout(result.authToken()); //Already Logged Out

        assertThrows(UnauthorizedException.class, () ->
                service.logout(result.authToken()));
    }

    @Test
    void testClear() {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);
        final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();

        final UserService service = new UserService(authService, memoryUserDAO);

        //Add a user
        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));
        //Clear users
        service.clear();

        assertNull(memoryUserDAO.getUser("Isaac"));
    }
}




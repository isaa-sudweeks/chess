package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @BeforeEach
    public void init() {
    }

    @Test
    void testRegisterPass() throws DataAccessException {
        MemoryUserDAO dataAccess = new MemoryUserDAO();
        UserService service = new UserService(dataAccess);
        service.register(new RegisterRequest("Isaac", "Sudweeks", "isuds@byu.edu"));
        UserData data = dataAccess.getUser("Isaac");
        assertEquals(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"), data);
    }

    @Test
    void testRegisterFail() {
        MemoryUserDAO dataAccess = new MemoryUserDAO();
        dataAccess.addUser(new UserData("Isaac","Sudweeks", "isuds@byu.edu"));
        UserService service = new UserService(dataAccess);

        assertThrows(DataAccessException.class, () ->
                service.register(new RegisterRequest("Isaac","Sudweeks", "isuds@byu.edu")));
    }

    @Test
    void testLoginPass() throws DataAccessException {
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(memoryAuthDAO);
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserService service = new UserService(authService, memoryUserDAO);

        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));

        RegisterLoginResult actual = service.login(new LoginRequest("Isaac","Sudweeks"));
        AuthData data = memoryAuthDAO.getAuth(actual.authToken());
        assertEquals(data.username(), actual.userName());
    }

    @Test
    void testLoginFail() {
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(memoryAuthDAO);
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserService service = new UserService(authService, memoryUserDAO);
        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));
        assertThrows(DataAccessException.class, () ->
                service.login(new LoginRequest("Isaac","Sudweek"))); //Bad password
    }
    @Test
    void testLogoutPass() throws DataAccessException {
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(memoryAuthDAO);
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserService service = new UserService(authService, memoryUserDAO);
        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));
        RegisterLoginResult result = service.login(new LoginRequest("Isaac", "Sudweeks")); //See if I can figure out how to do this another way

        assertNull(service.logout(result.authToken()));
    }

    @Test
    void testLogoutFail() throws DataAccessException {
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(memoryAuthDAO);
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        UserService service = new UserService(authService, memoryUserDAO);
        memoryUserDAO.addUser(new UserData("Isaac", "Sudweeks", "isuds@byu.edu"));
        RegisterLoginResult result = service.login(new LoginRequest("Isaac", "Sudweeks"));
        service.logout(result.authToken()); //Already Logged Out
        assertThrows(DataAccessException.class, () ->
                service.logout(result.authToken()));
    }
    }




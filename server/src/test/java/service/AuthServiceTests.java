package service;

import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthServiceTests {
    @Test
    public void TestAddAuthPass(){
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(memoryAuthDAO);

        //Get authToken
        String authToken = authService.addAuthData("Isaac");

        //See if it was added
        AuthData data = memoryAuthDAO.getAuth(authToken);
        assertEquals("Isaac", data.username());
    }

    @Test
    public void TestRemoveAuthData(){
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(memoryAuthDAO);

        //manually add authToken
        memoryAuthDAO.addAuth(new AuthData("Isaac", "Sudweeks"));

        //Remove the data
        authService.removeAuthData("Isaac");

        assertNull(authService.getAuth("Isaac"));
    }

    @Test
    public void TestClear(){
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(memoryAuthDAO);

        //manually add authToken
        memoryAuthDAO.addAuth(new AuthData("Isaac", "Sudweeks"));

        authService.clear();
        assertNull(authService.getAuth("Isaac"));
    }
}

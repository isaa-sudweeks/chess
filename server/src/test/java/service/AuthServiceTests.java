package service;

import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthServiceTests {
    @Test
    public void TestAddAuthPass() {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);

        //Get authToken
        final String authToken = authService.addAuthData("Isaac");

        //See if it was added
        final AuthData data = memoryAuthDAO.getAuth(authToken);
        assertEquals("Isaac", data.username());
    }

    @Test
    public void TestRemoveAuthData() {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);

        //manually add authToken
        memoryAuthDAO.addAuth(new AuthData("Isaac", "Sudweeks"));

        //Remove the data
        authService.removeAuthData("Isaac");

        assertNull(authService.getAuth("Isaac"));
    }

    @Test
    public void TestClear() {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);

        //manually add authToken
        memoryAuthDAO.addAuth(new AuthData("Isaac", "Sudweeks"));

        authService.clear();
        assertNull(authService.getAuth("Isaac"));
    }
}

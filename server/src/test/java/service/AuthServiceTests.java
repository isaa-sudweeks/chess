package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthServiceTests {
    @Test
    public void testAddAuthPass() throws SQLException, DataAccessException {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);

        //Get authToken
        final String authToken = authService.addAuthData("Isaac");

        //See if it was added
        final AuthData data = memoryAuthDAO.getAuth(authToken);
        assertEquals("Isaac", data.username());
    }

    @Test
    public void testRemoveAuthData() throws SQLException, DataAccessException {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);

        //manually add authToken
        memoryAuthDAO.addAuth(new AuthData("Isaac", "Sudweeks"));

        //Remove the data
        authService.removeAuthData("Isaac");

        assertNull(authService.getAuth("Isaac"));
    }

    @Test
    public void testClear() throws SQLException, DataAccessException {
        final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        final AuthService authService = new AuthService(memoryAuthDAO);

        //manually add authToken
        memoryAuthDAO.addAuth(new AuthData("Isaac", "Sudweeks"));

        authService.clear();
        assertNull(authService.getAuth("Isaac"));
    }
}

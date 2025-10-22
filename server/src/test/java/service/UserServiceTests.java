package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testLoginPass(){

    }

}

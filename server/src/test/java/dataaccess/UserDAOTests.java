package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    DatabaseHelper helper = new DatabaseHelper();

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return GSON.fromJson(json, UserData.class);
    }

    @BeforeEach
    public void clearAll() throws SQLException, DataAccessException {
        var statement = "TRUNCATE users";
        helper.executeUpdate(statement);
        statement = "TRUNCATE auths";
        helper.executeUpdate(statement);
        statement = "TRUNCATE games";
        helper.executeUpdate(statement);
    }

    @Test
    public void testClear() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();
        //Add some stuff to the user data base

        //Lets make some user data 
        UserData userData1 = new UserData("isaac", "jay", "sudweeks");
        userDAO.addUser(userData1);

        //Lets make another user 
        UserData userData2 = new UserData("isc", "jy", "sudweeks");
        //Insert it
        userDAO.addUser(userData2);

        //Lets clear it now:
        userDAO.clear();

        //Lets see if we get any data
        assertTrue(listUsers().isEmpty());


    }

    @Test
    public void testAddUserPositive() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();

        //Lets try adding a user
        userDAO.addUser(new UserData("username", "password", "email"));

        //Get a list of all the users
        Map<String, UserData> users = listUsers();
        UserData data = users.get("username");
        assertEquals(new UserData("username", "password", "email"), data);
    }

    @Test
    public void testAddUserNegative() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();

        //Lets make some user data
        userDAO.addUser(new UserData("isaac", "jay", "sudweeks"));

        assertThrows(RuntimeException.class, () ->
                userDAO.addUser(new UserData("isaac", "jay", "sudweeks")));

    }

    @Test
    public void getUserPositive() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();

        //Add a user
        userDAO.addUser(new UserData("username", "password", "email"));

        assertEquals(new UserData("username", "password", "email"), userDAO.getUser("username"));

    }

    @Test
    public void getUserNegative() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();

        //No user is added
        assertNull(userDAO.getUser("username"));
    }

    private Map<String, UserData> listUsers() throws SQLException, DataAccessException {
        Map<String, UserData> result = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement3 = "SELECT username, json FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement3)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UserData data = readUser(rs);
                        result.put(data.username(), data);
                    }
                }
            }
        }
        return result;
    }

}


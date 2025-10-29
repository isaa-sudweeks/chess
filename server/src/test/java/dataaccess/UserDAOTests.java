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

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {
    static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    private void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var json = rs.getString("json");
        return GSON.fromJson(json, UserData.class);
    }

    @BeforeEach
    public void clearAll() throws SQLException, DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
        statement = "TRUNCATE auths";
        executeUpdate(statement);
        statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Test
    public void testClear() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();
        //Add some stuff to the user data base

        //Lets make some user data 
        UserData userData1 = new UserData("isaac", "jay", "sudweeks");
        addUser(userData1);

        //Lets make another user 
        UserData userData2 = new UserData("isc", "jy", "sudweeks");
        //Insert it
        addUser(userData2);

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
        addUser(new UserData("isaac", "jay", "sudweeks"));

        assertThrows(RuntimeException.class, () ->
                userDAO.addUser(new UserData("isaac", "jay", "sudweeks")));

    }

    @Test
    public void getUserPositive() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();

        //Add a user
        addUser(new UserData("username", "password", "email"));

        assertEquals(new UserData("username", "password", "email"), userDAO.getUser("username"));

    }

    @Test
    public void getUserNegative() throws SQLException, DataAccessException {
        UserDAO userDAO = new DBUserDAO();

        //No user is added
        assertNull(userDAO.getUser("username"));
    }


    public void addUser(UserData userData) throws SQLException, DataAccessException {
        var statement = "INSERT INTO users (username, json) VALUES(?,?)";
        String jsonString = new Gson().toJson(userData);
        String username = userData.username();
        executeUpdate(statement, username, jsonString);
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


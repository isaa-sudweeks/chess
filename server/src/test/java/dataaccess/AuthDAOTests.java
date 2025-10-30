package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {
    static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    DatabaseHelper helper = new DatabaseHelper();

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return GSON.fromJson(json, AuthData.class);
    }

    private void addAuth(AuthData authData) throws SQLException, DataAccessException {
        var statement = "INSERT INTO auths (authToken, json) VALUES(?,?)";
        String jsonString = new Gson().toJson(authData);
        String authToken = authData.authToken();
        helper.executeUpdate(statement, authToken, jsonString);
    }


    private Map<String, AuthData> listAuths() throws SQLException, DataAccessException {
        Map<String, AuthData> result = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement3 = "SELECT authToken, json FROM auths";
            try (PreparedStatement ps = conn.prepareStatement(statement3)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        AuthData data = readAuth(rs);
                        result.put(data.authToken(), data);
                    }
                }
            }
        }
        return result;
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
    public void addAuthPositive() throws SQLException, DataAccessException {
        AuthDAO authDAO = new DBAuthDAO();

        //Try adding an auth
        authDAO.addAuth(new AuthData("token", "username"));

        //Get all AuthData
        Map<String, AuthData> data = listAuths();
        assertEquals(new AuthData("token", "username"), data.get("token"));
    }

    @Test
    public void addAuthNegative() throws SQLException, DataAccessException {
        AuthDAO authDAO = new DBAuthDAO();

        //Add auth manually
        addAuth(new AuthData("token", "username"));

        assertThrows(RuntimeException.class, () ->
                authDAO.addAuth(new AuthData("token", "username")));
    }

    @Test
    public void getAuthPositive() throws SQLException, DataAccessException {
        AuthDAO authDAO = new DBAuthDAO();

        //Add auth manually
        addAuth(new AuthData("token", "username"));

        assertEquals(new AuthData("token", "username"), authDAO.getAuth("token"));

    }

    @Test
    public void getAuthNegative() throws SQLException, DataAccessException {
        AuthDAO authDAO = new DBAuthDAO();

        //No Auths are there

        assertNull(authDAO.getAuth("token"));
    }

    @Test
    public void removeAuthPositive() throws SQLException, DataAccessException {
        AuthDAO authDAO = new DBAuthDAO();

        addAuth(new AuthData("token", "username"));

        authDAO.removeAuth("token");
        var data = listAuths();

        assertTrue(data.isEmpty());

    }

    @Test
    public void removeAuthNegative() throws SQLException, DataAccessException {
        AuthDAO authDAO = new DBAuthDAO();

        //Try to remove something that isnt there
        assertThrows(DataAccessException.class, () ->
                authDAO.removeAuth("token"));
    }

    @Test
    public void clearAuthPositive() throws SQLException, DataAccessException {
        AuthDAO authDAO = new DBAuthDAO();

        //add some stuff
        addAuth(new AuthData("token", "username"));
        addAuth(new AuthData("token2", "username2"));

        authDAO.clear();

        var data = listAuths();
        assertTrue(data.isEmpty());


    }
}





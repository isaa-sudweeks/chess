package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBAuthDAO implements AuthDAO {
    private DatabaseHelper helper = new DatabaseHelper();

    public DBAuthDAO() throws SQLException, DataAccessException {
        String[] createStatements = {
                """
                    CREATE TABLE IF NOT EXISTS  auths (
                                  `authToken` varchar(256) NOT NULL,
                                  `json` JSON NOT NULL,
                                  PRIMARY KEY (`authToken`)
                                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """
        };
        helper.configureDatabase(createStatements);
    }

    @Override
    public void addAuth(AuthData authData) throws SQLException, DataAccessException {
        var statement = "INSERT INTO auths (authToken, json) VALUES(?,?)";
        String jsonString = new Gson().toJson(authData);
        String authToken = authData.authToken();
        helper.executeUpdate(statement, authToken, jsonString);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, json FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    @Override
    public AuthData removeAuth(String authToken) throws SQLException, DataAccessException {
        AuthData authData = getAuth(authToken);
        var statement = "DELETE FROM auths WHERE authToken=?";
        int affected = helper.executeUpdate(statement, authToken);
        if (affected == 0) {
            throw new DataAccessException("No authdata affected");
        }
        return authData;
    }

    @Override
    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE auths";
        helper.executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String jsonString = rs.getString("json");
        Gson gson = new Gson();
        return gson.fromJson(jsonString, AuthData.class);
    }
}

package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DBAuthDAO implements AuthDAO {

    public DBAuthDAO() throws SQLException, DataAccessException {
        configureDatabase();
    }

    @Override
    public void addAuth(AuthData authData) throws SQLException, DataAccessException {
        var statement = "INSERT INTO auths (authToken, json) VALUES(?,?)";
        String jsonString = new Gson().toJson(authData);
        String authToken = authData.authToken();
        executeUpdate(statement, authToken, jsonString);
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
        int affected = executeUpdate(statement, authToken);
        if (affected == 0) {
            throw new DataAccessException("No authdata affected");
        }
        return authData;
    }

    @Override
    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String jsonString = rs.getString("json");
        Gson gson = new Gson();
        return gson.fromJson(jsonString, AuthData.class);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
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
                return ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            String[] createStatements = {
                    """
                    CREATE TABLE IF NOT EXISTS  auths (
                                  `authToken` varchar(256) NOT NULL,
                                  `json` JSON NOT NULL,
                                  PRIMARY KEY (`authToken`)
                                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """
            };
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

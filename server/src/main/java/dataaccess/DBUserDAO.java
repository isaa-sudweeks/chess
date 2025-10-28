package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DBUserDAO implements UserDAO {
    public DBUserDAO() throws SQLException, DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String jsonString = rs.getString("json");

        Gson gson = new Gson();
        UserData userData = gson.fromJson(jsonString, UserData.class);
        return userData;
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException, SQLException {
        var statement = "INSERT INTO users (username, json) VALUES(?,?)";
        String jsonString = new Gson().toJson(userData);
        String username = userData.username();
        executeUpdate(statement, username, jsonString);
    }

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

    @Override
    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            String[] createStatements = { //TODO: Write the DB creation string here
                    """
                    CREATE TABLE IF NOT EXISTS  users (
                                  `username` varchar(256) NOT NULL,
                                  `json` JSON NOT NULL,
                                  PRIMARY KEY (`username`)
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


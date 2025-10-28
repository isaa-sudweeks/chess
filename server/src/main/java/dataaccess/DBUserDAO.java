package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUserDAO implements UserDAO {
    public DBUserDAO() throws SQLException, DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    } else {
                        throw new DataAccessException("Username Not Found");
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
    public void addUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            String[] createStatements = { //TODO: Write the DB creation string here
                    """
                    CREATE TABLE IF NOT EXISTS  users (
                                  `username` varchar(256) NOT NULL,
                                  `json` JSON NOT NULL,
                                  PRIMARY KEY (`username`),
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


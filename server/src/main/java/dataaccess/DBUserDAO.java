package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUserDAO implements UserDAO {
    private DatabaseHelper helper = new DatabaseHelper();

    public DBUserDAO() throws SQLException, DataAccessException {
        String[] createStatements = {
                """
                    CREATE TABLE IF NOT EXISTS  users (
                                  `username` varchar(256) NOT NULL,
                                  `json` JSON NOT NULL,
                                  PRIMARY KEY (`username`)
                                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """
        };
        helper.configureDatabase(createStatements);
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
        helper.executeUpdate(statement, username, jsonString);
    }


    @Override
    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE users";
        helper.executeUpdate(statement);
    }
}


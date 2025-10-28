package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DBGameDAO implements GameDAO {

    public DBGameDAO() throws SQLException, DataAccessException {
        configureDatabase();
    }

    @Override
    public Map<Integer, GameData> getGames() throws DataAccessException, SQLException {
        Map<Integer, GameData> result = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData data = readGame(rs);
                        result.put(data.gameID(), data);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void addGame(GameData gameData) throws SQLException, DataAccessException {
        var statement = "INSERT INTO games (id, json) VALUES(?,?)";
        String jsonString = new Gson().toJson(gameData);
        int id = gameData.gameID();
        executeUpdate(statement, id, jsonString);
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public void clear() {

    }

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            String[] createStatements = {
                    """
                    CREATE TABLE IF NOT EXISTS  games (
                                  'id' int NOT NULL AUTO_INCREMENT,
                                  `json` JSON NOT NULL,
                                  PRIMARY KEY (`id`)
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

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var json = rs.getString("json");
        return new Gson().fromJson(json, GameData.class);
    }
}

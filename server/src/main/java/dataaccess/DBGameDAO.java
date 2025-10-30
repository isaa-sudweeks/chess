package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBGameDAO implements GameDAO {
    static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    DatabaseHelper helper = new DatabaseHelper();

    public DBGameDAO() throws SQLException, DataAccessException {
        String[] createString = {"""
                    CREATE TABLE IF NOT EXISTS  games (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `json` JSON NOT NULL,
                                   PRIMARY KEY (`id`)
                                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """};
        helper.configureDatabase(createString);
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
        String jsonString = GSON.toJson(gameData);
        int id = gameData.gameID();
        helper.executeUpdate(statement, id, jsonString);
    }

    @Override
    public void updateGame(GameData gameData) throws SQLException, DataAccessException {
        String statement = "UPDATE games SET json = ? WHERE id = ?";
        helper.executeUpdate(statement, GSON.toJson(gameData), gameData.gameID());
    }

    @Override
    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE games";
        helper.executeUpdate(statement);
    }


    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return GSON.fromJson(json, GameData.class);
    }
}

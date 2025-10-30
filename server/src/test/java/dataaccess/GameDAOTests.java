package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;
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

public class GameDAOTests {
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

    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return GSON.fromJson(json, GameData.class);
    }

    private void addGame(GameData gameData) throws SQLException, DataAccessException {
        var statement = "INSERT INTO games (id, json) VALUES(?,?)";
        String jsonString = GSON.toJson(gameData);
        int id = gameData.gameID();
        executeUpdate(statement, id, jsonString);
    }


    private Map<Integer, GameData> listGames() throws SQLException, DataAccessException {
        Map<Integer, GameData> result = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement3 = "SELECT id, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement3)) {
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
    public void getGamesPositive() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        GameData game1 = new GameData(1, null, null, "game1", new ChessGame());
        GameData game2 = new GameData(2, null, null, "game2", new ChessGame());
        //Add a couple games
        addGame(game1);
        addGame(game2);

        Map<Integer, GameData> expected = new HashMap<>();

        expected.put(1, game1);
        expected.put(2, game2);

        var actual = gameDAO.getGames();

        assertEquals(expected, actual);

    }

    @Test
    public void getGamesNegative() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        var data = listGames();

        assertTrue(data.isEmpty());

    }

    @Test
    public void addGamesPositive() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //add a game
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(game1);
        var games = listGames();

        assertEquals(games.get(1), game1);
    }

    @Test
    public void addGamesNegative() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //Manually add a game
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        addGame(game1);

        assertThrows(RuntimeException.class, () ->
                gameDAO.addGame(game1));
    }

    @Test
    public void updateGamePositive() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //Add a game
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        addGame(game1);

        var gameUpdate = new GameData(game1.gameID(), "Isaac", game1.blackUsername(), game1.gameName(), game1.game());

        gameDAO.updateGame(gameUpdate);

        var games = listGames();

        assertEquals(gameUpdate, games.get(1));

    }

    @Test
    public void updateGameNegative() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //Tries to update a game that isn't there
        var game1 = new GameData(1, null, null, "game1", new ChessGame());

        gameDAO.updateGame(game1);

        var games = listGames();

        assertTrue(listGames().isEmpty());
    }

    @Test
    public void clearGame() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        addGame(game1);

        gameDAO.clear();

        var games = listGames();
        assertTrue(games.isEmpty());
    }
}

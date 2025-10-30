package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    DatabaseHelper helper = new DatabaseHelper();

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
    public void getGamesPositive() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        GameData game1 = new GameData(1, null, null, "game1", new ChessGame());
        GameData game2 = new GameData(2, null, null, "game2", new ChessGame());
        //Add a couple games
        gameDAO.addGame(game1);
        gameDAO.addGame(game2);

        Map<Integer, GameData> expected = new HashMap<>();

        expected.put(1, game1);
        expected.put(2, game2);

        var actual = gameDAO.getGames();

        assertEquals(expected, actual);

    }

    @Test
    public void getGamesNegative() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        var data = gameDAO.getGames();

        assertTrue(data.isEmpty());

    }

    @Test
    public void addGamesPositive() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //add a game
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(game1);
        var games = gameDAO.getGames();

        assertEquals(games.get(1), game1);
    }

    @Test
    public void addGamesNegative() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //Manually add a game
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(game1);

        assertThrows(RuntimeException.class, () ->
                gameDAO.addGame(game1));
    }

    @Test
    public void updateGamePositive() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //Add a game
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(game1);

        var gameUpdate = new GameData(game1.gameID(), "Isaac", game1.blackUsername(), game1.gameName(), game1.game());

        gameDAO.updateGame(gameUpdate);

        var games = gameDAO.getGames();

        assertEquals(gameUpdate, games.get(1));

    }

    @Test
    public void updateGameNegative() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();

        //Tries to update a game that isn't there
        var game1 = new GameData(1, null, null, "game1", new ChessGame());

        gameDAO.updateGame(game1);

        var games = gameDAO.getGames();

        assertTrue(games.isEmpty());
    }

    @Test
    public void clearGame() throws SQLException, DataAccessException {
        GameDAO gameDAO = new DBGameDAO();
        var game1 = new GameData(1, null, null, "game1", new ChessGame());
        gameDAO.addGame(game1);

        gameDAO.clear();

        var games = gameDAO.getGames();
        assertTrue(games.isEmpty());
    }
}

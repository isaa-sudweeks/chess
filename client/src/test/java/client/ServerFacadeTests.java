package client;

import ServerFacade.ServerFacade;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseHelper;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    static DatabaseHelper helper;
    static ServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void init() throws SQLException, DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        helper = new DatabaseHelper();
        helper.clearAll();
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() throws SQLException, DataAccessException {
        helper.clearAll();
        server.stop();
    }

    @AfterEach
    void clear() throws SQLException, DataAccessException {
        helper.clearAll();
    }


    @Test
    public void testRegisterPositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);

    }

    @Test
    public void testRegisterNegative() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(ResponseException.class, () ->
                serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com")));
    }

    @Test
    public void testLoginPositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        var authData2 = serverFacade.login(new LoginRequest("player1", "password"));
        assertNotEquals(authData, authData2);
    }

    @Test
    public void testLoginFail() throws ResponseException {
        assertThrows(ResponseException.class, () ->
                serverFacade.login(new LoginRequest("nousername", "nopassword")));
    }

    @Test
    public void testClear() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        serverFacade.clear();
        assertThrows(ResponseException.class, () ->
                serverFacade.login(new LoginRequest("player1", "password")));

    }

    @Test
    public void logoutTestPositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        serverFacade.logout(authData.authToken());
        assertTrue(true);
    }

    @Test
    public void logoutTestNegative() throws ResponseException {
        assertThrows(ResponseException.class, () ->
                serverFacade.logout("autToken"));
    }

    @Test
    public void createGamesPositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        CreateGameResult gameResult = serverFacade.createGame(new CreateGameRequest(authData.authToken(), "game1"));
        assertTrue(gameResult.gameID() > 0);
    }

    @Test
    public void createGamesNegative() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(ResponseException.class, () ->
                serverFacade.createGame(new CreateGameRequest("badAuthToken", "game1")));
    }

    @Test
    public void listGamesPositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        CreateGameResult gameResult = serverFacade.createGame(new CreateGameRequest(authData.authToken(), "game1"));
        CreateGameResult gameResult2 = serverFacade.createGame(new CreateGameRequest(authData.authToken(), "game2"));
        var list = serverFacade.listGames(authData.authToken());
        List<GameData> games = new ArrayList<>();
        games.add(new GameData(1, null, null, "game1", new ChessGame()));
        games.add(new GameData(2, null, null, "game2", new ChessGame()));
        ListResult expected = new ListResult(games);
        assertEquals(expected, list);
    }

    @Test
    public void listGamesNegatic() throws ResponseException {
        //No games to list
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertEquals(serverFacade.listGames(authData.authToken()), new ListResult(new ArrayList<>()));
    }

    @Test
    public void joinGamePositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        var gameData = serverFacade.createGame(new CreateGameRequest(authData.authToken(), "game1"));

        serverFacade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameData.gameID(), authData.authToken()));
        var game = new GameData(gameData.gameID(), "player1", null, "game1", new ChessGame());
        var games = new ArrayList<GameData>();
        games.add(game);
        ListResult result = new ListResult(games);
        assertEquals(result, serverFacade.listGames(authData.authToken()));
    }

    @Test
    public void joinGameNegative() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        var gameData = serverFacade.createGame(new CreateGameRequest(authData.authToken(), "game1"));
        serverFacade.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameData.gameID(), authData.authToken()));

        //Lets try to join again:
        assertThrows(ResponseException.class, () ->
                serverFacade.joinGame(
                        new JoinGameRequest(ChessGame.TeamColor.WHITE, gameData.gameID(), authData.authToken())));
    }
}

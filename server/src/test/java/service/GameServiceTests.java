package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.CreateGameRequest;
import model.GameData;
import model.JoinGameRequest;
import model.ListResult;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {
    @Test
    public void listGamesPass() throws SQLException, DataAccessException {
        final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        final AuthService authService = new AuthService();
        final GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        final String authToken = authService.addAuthData("Isaac");

        //Make some games
        final GameData gameOne = new GameData(1, null, null, "Game1", new ChessGame());
        final GameData gameTwo = new GameData(2, "Isaac", "Sudweeks", "Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        //Expected List of Games
        final Collection<GameData> expectedValues = memoryGameDAO.getGames().values();
        final List<GameData> expectedList = new ArrayList<>(expectedValues);
        final ListResult expected = new ListResult(expectedList);
        assertEquals(expected, service.listGames(authToken));

    }

    @Test
    public void listGamesFail() throws SQLException, DataAccessException {
        final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        final AuthService authService = new AuthService();
        final GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        final String authToken = authService.addAuthData("Isaac");

        //Make some games
        final GameData gameOne = new GameData(1, null, null, "Game1", new ChessGame());
        final GameData gameTwo = new GameData(2, "Isaac", "Sudweeks", "Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        assertThrows(UnauthorizedException.class, () ->
                service.listGames("Bad Auth Token"));
    }

    @Test
    public void createGamePass() throws SQLException, DataAccessException {
        final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        final AuthService authService = new AuthService();
        final GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        final String authToken = authService.addAuthData("Isaac");

        //Create the game
        service.createGame(new CreateGameRequest(authToken, "Game1"));

        //Get that game
        final Map<Integer, GameData> games = memoryGameDAO.getGames();
        final GameData game = games.get(1);

        //Check if the game has been created and is stored
        assertEquals(new GameData(1, null, null, "Game1", new ChessGame()), game);

    }

    @Test
    public void createGameFail() throws SQLException, DataAccessException {
        final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        final AuthService authService = new AuthService();
        final GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        final String authToken = authService.addAuthData("Isaac");


        assertThrows(UnauthorizedException.class, () ->
                service.createGame(new CreateGameRequest("Bad Auth Token", "Game1")));
    }

    @Test
    public void joinGamePass() throws SQLException, DataAccessException {
        final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        final AuthService authService = new AuthService();
        final GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        final String authToken = authService.addAuthData("Isaac");

        //Make some games
        GameData gameOne = new GameData(1, null, null, "Game1", new ChessGame());
        final GameData gameTwo = new GameData(2, "Isaac", "Sudweeks", "Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        //Join a game
        service.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1, authToken));

        //Change game 1 to be what it should be now
        gameOne = new GameData(gameOne.gameID(), "Isaac", gameOne.blackUsername(), gameOne.gameName(), gameOne.game());

        //Get games from memoryGameDAO
        final Map<Integer, GameData> games = memoryGameDAO.getGames();
        final GameData game = games.get(gameOne.gameID());

        assertEquals(gameOne, game);


    }

    @Test
    public void joinGameFail() throws SQLException, DataAccessException {
        final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        final AuthService authService = new AuthService();
        final GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        final String authToken = authService.addAuthData("Isaac");

        //Make some games
        final GameData gameOne = new GameData(1, null, null, "Game1", new ChessGame());
        final GameData gameTwo = new GameData(2, "Isaac", "Sudweeks", "Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        assertThrows(BadRequestException.class, () ->
                service.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 3, authToken)));
    }

    @Test
    public void testClear() throws SQLException, DataAccessException {
        final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        final AuthService authService = new AuthService();
        final GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        final String authToken = authService.addAuthData("Isaac");

        //Make some games
        final GameData gameOne = new GameData(1, null, null, "Game1", new ChessGame());
        final GameData gameTwo = new GameData(2, "Isaac", "Sudweeks", "Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        service.clear();

        assertEquals(new HashMap<>(), memoryGameDAO.getGames());
    }
}






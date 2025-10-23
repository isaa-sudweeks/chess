package service;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    @Test
    public void ListGamesPass(){
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        AuthService authService = new AuthService();
        GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        String authToken = authService.addAuthData("Isaac");

        //Make some games
        GameData gameOne = new GameData(1,null,null,"Game1",new ChessGame());
        GameData gameTwo = new GameData(2,"Isaac","Sudweeks","Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        //Expected List of Games
        Collection<GameData> expectedValues = memoryGameDAO.getGames().values();
        List<GameData> expectedList = new ArrayList<>(expectedValues);
        ListResult expected = new ListResult(expectedList);
        assertEquals(expected, service.ListGames(authToken));

    }

    @Test
    public void ListGamesFail(){
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        AuthService authService = new AuthService();
        GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        String authToken = authService.addAuthData("Isaac");

        //Make some games
        GameData gameOne = new GameData(1,null,null,"Game1",new ChessGame());
        GameData gameTwo = new GameData(2,"Isaac","Sudweeks","Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        assertThrows(UnauthorizedException.class, () ->
                service.ListGames("Bad Auth Token"));
    }

    @Test
    public void CreateGamePass(){
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        AuthService authService = new AuthService();
        GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        String authToken = authService.addAuthData("Isaac");

        //Create the game
        service.CreateGame(new CreateGameRequest(authToken, "Game1"));

        //Get that game
        Map<Integer,GameData> games = memoryGameDAO.getGames();
        GameData game = games.get(0);

        //Check if the game has been created and is stored
        assertEquals(new GameData(0,null,null,"Game1", new ChessGame()), game);

    }

    @Test
    public void CreateGameFail(){
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        AuthService authService = new AuthService();
        GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        String authToken = authService.addAuthData("Isaac");



        assertThrows(UnauthorizedException.class, () ->
                service.CreateGame(new CreateGameRequest("Bad Auth Token","Game1")));
    }
    
    @Test
    public void JoinGamePass(){
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        AuthService authService = new AuthService();
        GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        String authToken = authService.addAuthData("Isaac");

        //Make some games
        GameData gameOne = new GameData(1,null,null,"Game1",new ChessGame());
        GameData gameTwo = new GameData(2,"Isaac","Sudweeks","Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        //Join a game
        service.JoinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 1, authToken));

        //Change game 1 to be what it should be now
        gameOne = new GameData(gameOne.gameID(),"Isaac", gameOne.blackUsername(),gameOne.gameName(),gameOne.game());

        //Get games from memoryGameDAO
        Map<Integer, GameData> games = memoryGameDAO.getGames();
        GameData game = games.get(gameOne.gameID());

        assertEquals(gameOne, game);


    }

    @Test
    public void JoinGameFail(){
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        AuthService authService = new AuthService();
        GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        String authToken = authService.addAuthData("Isaac");

        //Make some games
        GameData gameOne = new GameData(1,null,null,"Game1",new ChessGame());
        GameData gameTwo = new GameData(2,"Isaac","Sudweeks","Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        assertThrows(BadRequestException.class, () ->
                service.JoinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, 3, authToken)));
    }

    @Test
    public void TestClear(){
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        AuthService authService = new AuthService();
        GameService service = new GameService(memoryGameDAO, authService);

        //Authenticate
        String authToken = authService.addAuthData("Isaac");

        //Make some games
        GameData gameOne = new GameData(1,null,null,"Game1",new ChessGame());
        GameData gameTwo = new GameData(2,"Isaac","Sudweeks","Game2", new ChessGame());

        //Add some games via the memoryGameDAO
        memoryGameDAO.addGame(gameOne);
        memoryGameDAO.addGame(gameTwo);

        service.clear();

        assertEquals(new HashMap<>(),memoryGameDAO.getGames());
    }
    }






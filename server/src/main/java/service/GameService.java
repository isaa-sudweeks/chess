package service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import java.util.List;
import java.util.Map;

public class GameService {
    private MemoryGameDAO dataAccess = new MemoryGameDAO();
    private AuthService authService = new AuthService();

    public GameService(){}
    public GameService(MemoryGameDAO memoryGameDAO){
        this.dataAccess = memoryGameDAO;
    }

    public GameService(MemoryGameDAO memoryGameDAO, AuthService authService){
        this.dataAccess = memoryGameDAO;
        this.authService = authService;
    }

    public ListResult ListGames(String authToken) throws UnauthorizedException {
        //First make sure it is authorized
        if (authService.getAuth(authToken) != null) {
            return new ListResult((List<GameData>)dataAccess.getGames().values());
        }
        else {
            throw new UnauthorizedException("AuthToken was unauthorized");
        }
    }

    public int CreateGame(CreateGameRequest createGameRequest){
        if (authService.getAuth(createGameRequest.authToken()) != null) {
            int gameID = dataAccess.getGames().size();
            dataAccess.addGame(new GameData(gameID, null,null,createGameRequest.gameName(), new ChessGame()));
            return gameID;
        }
        else {
            throw new UnauthorizedException("Auth Token not found");
        }
    }

    public Object JoinGame(JoinGameRequest joinGameRequest) {
        Map<Integer, GameData> games = dataAccess.getGames();
        GameData gameData = games.get(joinGameRequest.gameID());
        if (authService.getAuth(joinGameRequest.authToken()) != null) {
            if (gameData != null) {
                if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE) {
                    gameData = new GameData(
                            gameData.GameID(),
                            authService.getAuth(joinGameRequest.authToken()).username(),
                            null,
                            gameData.gameName(),
                            gameData.game());
                    dataAccess.updateGame(gameData);
                }
                else if (joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK) {
                    gameData = new GameData(
                            gameData.GameID(),
                            null,
                            authService.getAuth(joinGameRequest.authToken()).username(),
                            gameData.gameName(),
                            gameData.game());
                    dataAccess.updateGame(gameData);
                }
                return null;

            } else {
                throw new BadRequestException("There is no game with that ID");
            }
        }
        else {
            throw new UnauthorizedException("AuthToken not found");
        }
    }
}

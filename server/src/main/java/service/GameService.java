package service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;

import java.util.ArrayList;
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
            List<GameData> games = new ArrayList<>(dataAccess.getGames().values());
            return new ListResult(games);
        }
        else {
            throw new UnauthorizedException("AuthToken was unauthorized");
        }
    }

    public int CreateGame(CreateGameRequest createGameRequest){
        if(createGameRequest.gameName() == null){
            throw new BadRequestException("The game name is null");
        }
        if (authService.getAuth(createGameRequest.authToken()) != null) {
            int gameID = dataAccess.getGames().size()+1;
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
        if (joinGameRequest.playerColor() == null){
            throw new BadRequestException("Not a valid player color");
        }
        if (authService.getAuth(joinGameRequest.authToken()) != null) {
            if (gameData != null) {
                if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE) {
                    if (gameData.whiteUsername() == null) {
                        gameData = new GameData(
                                gameData.gameID(),
                                authService.getAuth(joinGameRequest.authToken()).username(),
                                gameData.blackUsername(),
                                gameData.gameName(),
                                gameData.game());
                        dataAccess.updateGame(gameData);
                    }
                    else {
                        throw new AlreadyTakenException("That player is already taken");
                    }
                }
                else if (joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK) {
                    if (gameData.blackUsername() == null) {
                        gameData = new GameData(
                                gameData.gameID(),
                                gameData.whiteUsername(),
                                authService.getAuth(joinGameRequest.authToken()).username(),
                                gameData.gameName(),
                                gameData.game());
                        dataAccess.updateGame(gameData);
                    }
                    else {
                        throw new AlreadyTakenException("That player is already taken");
                    }
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
    public void clear(){
        dataAccess.clear();
    }
}

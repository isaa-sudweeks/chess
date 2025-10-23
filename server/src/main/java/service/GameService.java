package service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameService {
    private MemoryGameDAO dataAccess = new MemoryGameDAO();
    private AuthService authService = new AuthService();

    public GameService() {
    }

    public GameService(final MemoryGameDAO memoryGameDAO) {
        dataAccess = memoryGameDAO;
    }

    public GameService(final MemoryGameDAO memoryGameDAO, final AuthService authService) {
        dataAccess = memoryGameDAO;
        this.authService = authService;
    }

    public ListResult listGames(final String authToken) throws UnauthorizedException {
        //First, make sure it is authorized.
        if (null != authService.getAuth(authToken)) {
            final List<GameData> games = new ArrayList<>(this.dataAccess.getGames().values());
            return new ListResult(games);
        } else {
            throw new UnauthorizedException("AuthToken was unauthorized");
        }
    }

    public int createGame(final CreateGameRequest createGameRequest) {
        if (null == createGameRequest.gameName()) {
            throw new BadRequestException("The game name is null");
        }
        if (null != authService.getAuth(createGameRequest.authToken())) {
            final int gameID = this.dataAccess.getGames().size() + 1;
            this.dataAccess.addGame(new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame()));
            return gameID;
        } else {
            throw new UnauthorizedException("Auth Token not found");
        }
    }

    @SuppressWarnings("SameReturnValue")
    public Object joinGame(final JoinGameRequest joinGameRequest) {

        final Map<Integer, GameData> games = this.dataAccess.getGames();
        GameData gameData = games.get(joinGameRequest.gameID());
        if (null == joinGameRequest.playerColor()) {
            throw new BadRequestException("Not a valid player color");
        }
        if (null != authService.getAuth(joinGameRequest.authToken())) {
            if (null != gameData) {
                if (ChessGame.TeamColor.WHITE == joinGameRequest.playerColor()) {
                    if (null == gameData.whiteUsername()) {
                        gameData = new GameData(
                                gameData.gameID(),
                                this.authService.getAuth(joinGameRequest.authToken()).username(),
                                gameData.blackUsername(),
                                gameData.gameName(),
                                gameData.game());
                        this.dataAccess.updateGame(gameData);
                    } else {
                        throw new AlreadyTakenException("That player is already taken");
                    }
                } else if (ChessGame.TeamColor.BLACK == joinGameRequest.playerColor()) {
                    if (null == gameData.blackUsername()) {
                        gameData = new GameData(
                                gameData.gameID(),
                                gameData.whiteUsername(),
                                this.authService.getAuth(joinGameRequest.authToken()).username(),
                                gameData.gameName(),
                                gameData.game());
                        this.dataAccess.updateGame(gameData);
                    } else {
                        throw new AlreadyTakenException("That player is already taken");
                    }
                }
                return null;

            } else {
                throw new BadRequestException("There is no game with that ID");
            }
        } else {
            throw new UnauthorizedException("AuthToken not found");
        }
    }

    public void clear() {
        this.dataAccess.clear();
    }
}

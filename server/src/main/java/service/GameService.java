package service;

import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import java.util.List;

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

    public String CreateGame(CreateGameRequest createGameRequest){

    }
}

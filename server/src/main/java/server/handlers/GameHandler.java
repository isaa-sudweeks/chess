package server.handlers;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import io.javalin.Javalin;
import service.*;

import io.javalin.http.Context;

import java.util.Map;

public class GameHandler {
    private GameService service = new GameService();
    public void registerRoutes(Javalin app, AuthService authService, MemoryGameDAO memoryGameDAO){
        this.service = new GameService(memoryGameDAO, authService);
        app.get("/game",this::ListGames);
        app.post("/game", this::CreateGame);
        app.put("/game", this::JoinGame);

    }

    public void ListGames(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            ListResult result = service.ListGames(authToken);
            ctx.json(result);
        }
        catch (AlreadyTakenException e) {
            ctx.status(403).json(Map.of("message","Error: already taken"));
        }
        catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message","Error: unauthorized"));
        }
        catch (BadRequestException e) {
            ctx.status(400).json(Map.of("message" , "Error: bad request"));
        }
        catch(Exception e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }

    public void CreateGame(Context ctx){
        CreateGameRequest body = ctx.bodyAsClass(CreateGameRequest.class);
        String authToken = ctx.header("authorization");
        CreateGameRequest request = new CreateGameRequest(authToken, body.gameName());
        try {
            int gameID = service.CreateGame(request);
            ctx.json(Map.of("gameID", gameID));
        }
        catch (AlreadyTakenException e) {
            ctx.status(403).json(Map.of("message","Error: already taken"));
        }
        catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message","Error: unauthorized"));
        }
        catch (BadRequestException e) {
            ctx.status(400).json(Map.of("message" , "Error: bad request"));
        }
        catch(Exception e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }

    public void JoinGame(Context ctx){
        JoinGameRequest body = ctx.bodyAsClass(JoinGameRequest.class);
        String authToken = ctx.header("authorization");
        JoinGameRequest request = new JoinGameRequest(body.playerColor(),body.gameID(),authToken);
        try {
            service.JoinGame(request);
            ctx.json(Map.of());
        }
        catch (AlreadyTakenException e) {
            ctx.status(403).json(Map.of("message","Error: already taken"));
        }
        catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message","Error: unauthorized"));
        }
        catch (BadRequestException e) {
            ctx.status(400).json(Map.of("message" , "Error: bad request"));
        }
        catch(Exception e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }

    }
}

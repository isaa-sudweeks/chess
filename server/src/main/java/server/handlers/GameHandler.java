package server.handlers;

import dataaccess.MemoryGameDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class GameHandler {
    private GameService service = new GameService();

    public void registerRoutes(final Javalin app, final AuthService authService, final MemoryGameDAO memoryGameDAO) {
        service = new GameService(memoryGameDAO, authService);
        app.get("/game", this::ListGames);
        app.post("/game", this::CreateGame);
        app.put("/game", this::JoinGame);

    }

    public void ListGames(final Context ctx) {
        final String authToken = ctx.header("authorization");
        try {
            final ListResult result = this.service.ListGames(authToken);
            ctx.json(result);
        } catch (final AlreadyTakenException e) {
            ctx.status(403).json(Map.of("message", "Error: already taken"));
        } catch (final UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (final BadRequestException e) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } catch (final Exception e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }

    public void CreateGame(final Context ctx) {
        final CreateGameRequest body = ctx.bodyAsClass(CreateGameRequest.class);
        final String authToken = ctx.header("authorization");
        final CreateGameRequest request = new CreateGameRequest(authToken, body.gameName());
        try {
            final int gameID = this.service.CreateGame(request);
            ctx.json(Map.of("gameID", gameID));
        } catch (final AlreadyTakenException e) {
            ctx.status(403).json(Map.of("message", "Error: already taken"));
        } catch (final UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (final BadRequestException e) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } catch (final Exception e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }

    public void JoinGame(final Context ctx) {
        final JoinGameRequest body = ctx.bodyAsClass(JoinGameRequest.class);
        final String authToken = ctx.header("authorization");
        final JoinGameRequest request = new JoinGameRequest(body.playerColor(), body.gameID(), authToken);
        try {
            this.service.JoinGame(request);
            ctx.json(Map.of());
        } catch (final AlreadyTakenException e) {
            ctx.status(403).json(Map.of("message", "Error: already taken"));
        } catch (final UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (final BadRequestException e) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } catch (final Exception e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }

    }
}

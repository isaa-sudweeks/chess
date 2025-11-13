package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.GameDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class GameHandler {
    static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    private GameService service = new GameService();

    public void registerRoutes(final Javalin app, final AuthService authService, final GameDAO gameDAO) {
        service = new GameService(gameDAO, authService);
        app.get("/game", this::listGames);
        app.post("/game", this::createGame);
        app.put("/game", this::joinGame);

    }

    public void listGames(final Context ctx) {
        final String authToken = ctx.header("authorization");
        try {
            final ListResult result = this.service.listGames(authToken);
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

    public void createGame(final Context ctx) {
        final CreateGameRequest body = ctx.bodyAsClass(CreateGameRequest.class);
        final String authToken = ctx.header("authorization");
        final CreateGameRequest request = new CreateGameRequest(authToken, body.gameName());
        try {
            final int gameID = this.service.createGame(request);
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

    public void joinGame(final Context ctx) {
        final JoinGameRequest body = ctx.bodyAsClass(JoinGameRequest.class);
        final String authToken = ctx.header("authorization");
        final JoinGameRequest request = new JoinGameRequest(body.playerColor(), body.gameID(), authToken);
        try {
            this.service.joinGame(request);
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

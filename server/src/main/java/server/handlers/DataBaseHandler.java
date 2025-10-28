package server.handlers;

import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.sql.SQLException;
import java.util.Map;

public class DataBaseHandler {
    private AuthService authService;
    private UserService userService;
    private GameService gameService;

    public void registerRoutes(final Javalin app,
                               final AuthService authService,
                               final MemoryUserDAO memoryUserDAO,
                               final MemoryGameDAO memoryGameDAO) {
        this.authService = authService;
        userService = new UserService(authService, memoryUserDAO);
        gameService = new GameService(memoryGameDAO, authService);
        app.delete("/db", this::clear);
    }

    public void clear(final Context ctx) throws SQLException, DataAccessException {
        this.authService.clear();
        this.userService.clear();
        this.gameService.clear();
        ctx.json(Map.of());
    }
}

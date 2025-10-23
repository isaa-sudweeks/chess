package server.handlers;

import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class DataBaseHandler {
    private AuthService authService;
    private UserService userService;
    private GameService gameService;
    public void registerRoutes(Javalin app, AuthService authService, MemoryUserDAO memoryUserDAO, MemoryGameDAO memoryGameDAO){
        this.authService = authService;
        this.userService = new UserService(authService, memoryUserDAO);
        this.gameService = new GameService(memoryGameDAO, authService);
        app.delete("/db",this::clear);
    }
    public void clear(Context ctx){
        authService.clear();
        userService.clear();
        gameService.clear();
        ctx.json(Map.of());
    }
}

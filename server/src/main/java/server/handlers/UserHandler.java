package server.handlers;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import io.javalin.http.Context;
import io.javalin.Javalin;
import service.*;

import java.util.Map;

public class UserHandler {
    private UserService service = new UserService();
    public void registerRoutes(Javalin app, AuthService authService, MemoryUserDAO memoryUserDAO){
        this.service = new UserService(authService, memoryUserDAO);
        app.post("/user",this::register);
        app.post("/session", this::login);
        app.delete("/session", this::logout);
    }

    public void register(Context ctx) throws DataAccessException {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        try {
            RegisterLoginResult result = service.register(request);
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

    public void login(Context ctx) throws DataAccessException {
        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
        try {
            RegisterLoginResult result = service.login(request);
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

    public void logout(Context ctx) throws DataAccessException {
       String authToken = ctx.header("authorization");
       try {
           service.logout(authToken);
           ctx.json(new Object());
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
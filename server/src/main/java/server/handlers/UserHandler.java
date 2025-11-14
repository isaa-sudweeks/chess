package server.handlers;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.LoginRequest;
import model.RegisterLoginResult;
import model.RegisterRequest;
import service.*;

import java.util.Map;

public class UserHandler {
    private UserService service;

    public void registerRoutes(final Javalin app, final AuthService authService, final UserDAO userDAO) {
        this.service = new UserService(authService, userDAO);
        app.post("/user", this::register);
        app.post("/session", this::login);
        app.delete("/session", this::logout);
    }

    public void register(final Context ctx) {
        final RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        try {
            final RegisterLoginResult result = this.service.register(request);
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

    public void login(final Context ctx) throws DataAccessException {
        final LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
        try {
            final RegisterLoginResult result = this.service.login(request);
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

    public void logout(final Context ctx) throws DataAccessException {
        final String authToken = ctx.header("authorization");
        try {
            this.service.logout(authToken);
            ctx.json(new Object());
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
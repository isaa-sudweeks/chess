package server.handlers;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.Javalin;
import service.*;

public class UserHandler {
    private UserService service = new UserService();
    public void registerRoutes(Javalin app){
        app.post("/user",this::register);
        app.post("/session", this::login);
        app.delete("/session", this::logout);
    }

    public void register(Context ctx) throws DataAccessException {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        RegisterLoginResult result = service.register(request);
        ctx.json(result);
    }

    public void login(Context ctx) throws DataAccessException {
        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
        RegisterLoginResult result = service.login(request);
        ctx.json(result);
    }

    public void logout(Context ctx) throws DataAccessException {
       String authToken = ctx.header("authorization");
        service.logout(authToken);
        ctx.json(new Object());
    }
}
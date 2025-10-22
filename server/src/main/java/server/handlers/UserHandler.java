package server.handlers;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.Javalin;
import service.RegisterLoginResult;
import service.RegisterRequest;
import service.UserService;

public class UserHandler {
    private UserService service = new UserService();
    public void registerRoutes(Javalin app){
        app.post("/user",this::register);
    }

    public void register(Context ctx) throws DataAccessException {
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        RegisterLoginResult result = service.register(request);
        ctx.json(result);
    }
}
package server.handlers;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.Javalin;
import service.RegisterRequest;
import service.UserService;

public class UserHandler {
    private UserService service = new UserService();
    public void registerRoutes(Javalin app){
        app.post("/user",this::register);
    }

    public void register(Context ctx) throws DataAccessException { //TODO: Test this
        RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
        service.register(request);
    }
}

//package server.handlers;
//
//import io.javalin.Javalin;
//import io.javalin.http.Context;
//
//import java.util.Map;
//
//public class UserHandler {
//
//    public void registerRoutes(Javalin app) {
//        app.get("/users/{id}", this::getUser);
//        app.post("/users", this::createUser);
//        app.post("/login", this::loginUser);
//    }

//    private void getUser(Context ctx) {
//        String id = ctx.pathParam("id");
//        ctx.json(Map.of("id", id, "name", "Test User"));
//    }
//
//    private void createUser(Context ctx) {
//        String name = ctx.formParam("name");
//        ctx.status(201).json(Map.of("status", "created", "name", name));
//    }
//
//    private void loginUser(Context ctx) {
//        String username = ctx.formParam("username");
//        String password = ctx.formParam("password");
//        // pretend to check credentials
//        if ("admin".equals(username) && "password".equals(password)) {
//            ctx.status(200).json(Map.of("message", "Login successful"));
//        } else {
//            ctx.status(401).json(Map.of("error", "Invalid credentials"));
//        }
//    }
//}
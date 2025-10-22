package server;
import io.javalin.json.JavalinGson;
import io.javalin.*;
import java.util.UUID;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            //Setup a global JSON mapper so I do not need to GSON all the time
            config.jsonMapper(new JavalinGson());
        });




    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }


}

//package server;
//
//import io.javalin.Javalin;
//import server.handlers.UserHandler;
//import server.handlers.GameHandler;
//
//public class Server {
//
//    private final Javalin javalin;
//
//    public Server() {
//        javalin = Javalin.create(config -> config.staticFiles.add("web"));
//
//        // Register routes from each handler
//        new UserHandler().registerRoutes(javalin);
//        new GameHandler().registerRoutes(javalin);
//    }
//
//    public int run(int desiredPort) {
//        javalin.start(desiredPort);
//        return javalin.port();
//    }
//
//    public void stop() {
//        javalin.stop();
//    }
//}

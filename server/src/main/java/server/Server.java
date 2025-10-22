package server;
import io.javalin.json.JavalinGson;
import io.javalin.*;
import server.handlers.UserHandler;

import java.util.UUID;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            //Setup a global JSON mapper so I do not need to GSON all the time
            config.jsonMapper(new JavalinGson());
        });

        //Handlers
        new UserHandler().registerRoutes(javalin);



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }


}


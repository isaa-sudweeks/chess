package server;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.json.JavalinGson;
import io.javalin.*;
import server.handlers.DataBaseHandler;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import service.AuthService;

import java.util.UUID;

public class Server {

    private final Javalin javalin;
    AuthService authService = new AuthService();
    private MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();


    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            //Setup a global JSON mapper so I do not need to GSON all the time
            config.jsonMapper(new JavalinGson());
        });

        //Handlers
        new UserHandler().registerRoutes(javalin,authService,memoryUserDAO);
        new GameHandler().registerRoutes(javalin,authService,memoryGameDAO);
        new DataBaseHandler().registerRoutes(javalin,authService,memoryUserDAO,memoryGameDAO);



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }


}


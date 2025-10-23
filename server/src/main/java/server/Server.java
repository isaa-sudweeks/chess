package server;

import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import server.handlers.DataBaseHandler;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import service.AuthService;

public class Server {

    private final Javalin javalin;
    AuthService authService = new AuthService();


    public Server() {
        this.javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            //Set up a global JSON mapper, so I do not need to GSON all the time
            config.jsonMapper(new JavalinGson());
        });

        //Handlers
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        new UserHandler().registerRoutes(this.javalin, this.authService, memoryUserDAO);
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
        new GameHandler().registerRoutes(this.javalin, this.authService, memoryGameDAO);
        new DataBaseHandler().registerRoutes(this.javalin, this.authService, memoryUserDAO, memoryGameDAO);


    }

    public int run(final int desiredPort) {
        this.javalin.start(desiredPort);
        return this.javalin.port();
    }

    public void stop() {
        this.javalin.stop();
    }


}


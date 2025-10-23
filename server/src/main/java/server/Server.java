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
    private final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    AuthService authService = new AuthService();


    public Server() {
        this.javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            //Setup a global JSON mapper so I do not need to GSON all the time
            config.jsonMapper(new JavalinGson());
        });

        //Handlers
        new UserHandler().registerRoutes(this.javalin, this.authService, this.memoryUserDAO);
        new GameHandler().registerRoutes(this.javalin, this.authService, this.memoryGameDAO);
        new DataBaseHandler().registerRoutes(this.javalin, this.authService, this.memoryUserDAO, this.memoryGameDAO);


    }

    public int run(final int desiredPort) {
        this.javalin.start(desiredPort);
        return this.javalin.port();
    }

    public void stop() {
        this.javalin.stop();
    }


}


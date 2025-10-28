package server;

import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import server.handlers.DataBaseHandler;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import service.AuthService;

import java.sql.SQLException;

public class Server {

    private final Javalin javalin;
    AuthService authService = new AuthService(new MemoryAuthDAO());


    public Server() {
        this.javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            //Set up a global JSON mapper, so I do not need to GSON all the time
            config.jsonMapper(new JavalinGson());
        });

        //Handlers
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        try {
            UserDAO userDAO = new DBUserDAO();
            new UserHandler().registerRoutes(this.javalin, this.authService, userDAO);
            MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
            new GameHandler().registerRoutes(this.javalin, this.authService, memoryGameDAO);
            new DataBaseHandler().registerRoutes(this.javalin, this.authService, memoryUserDAO, memoryGameDAO);
        } catch (SQLException | DataAccessException e) {
            System.out.println("There was an error on the startup of the server");
        }


    }

    public int run(final int desiredPort) {
        this.javalin.start(desiredPort);
        return this.javalin.port();
    }

    public void stop() {
        this.javalin.stop();
    }


}


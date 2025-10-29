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

    public Server() {
        this.javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            //Set up a global JSON mapper, so I do not need to GSON all the time
            config.jsonMapper(new JavalinGson());
        });

        //Handlers

        try {
            UserDAO userDAO = new DBUserDAO();
            AuthService authService = new AuthService(new DBAuthDAO());
            GameDAO gameDAO = new DBGameDAO();

            new UserHandler().registerRoutes(this.javalin, authService, userDAO);


            new GameHandler().registerRoutes(this.javalin, authService, gameDAO);

            new DataBaseHandler().registerRoutes(this.javalin, authService, userDAO, gameDAO);
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


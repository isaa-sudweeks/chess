package client;

import ServerFacade.ServerFacade;
import dataaccess.DataAccessException;
import dataaccess.DatabaseHelper;
import exception.ResponseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import service.LoginRequest;
import service.RegisterRequest;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    static DatabaseHelper helper = new DatabaseHelper();
    static ServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void init() throws SQLException, DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        helper.clearAll();
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void testRegisterPositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);

    }

    @Test
    public void testRegisterNegative() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(ResponseException.class, () ->
                serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com")));
    }

    @Test
    public void testLoginPositive() throws ResponseException {
        var authData = serverFacade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        var authData2 = serverFacade.login(new LoginRequest("player1", "password"));
        assertNotEquals(authData, authData2);
    }

    @Test
    public void testLoginFail() throws ResponseException {
        assertThrows(ResponseException.class, () ->
                serverFacade.login(new LoginRequest("nousername", "nopassword")));
    }


}

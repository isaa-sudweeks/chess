package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.Javalin;
import io.javalin.websocket.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.ConnectionManager;
import service.AuthService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private AuthService authService;

    public WebSocketHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket Connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT ->
                    connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), wsMessageContext.session, userGameCommand.getColor());
            case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID(), wsMessageContext.session);
        }
    }

    private void leave(String authToken, Integer gameID, Session session) throws IOException {
        try {
            AuthData authData = authService.getAuth(authToken);
            if (authData == null) {
                session.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Not Authorized")));
            } else {
                connections.remove(gameID, session);
                connections.broadcast_game(
                        gameID,
                        new ServerMessage(
                                ServerMessage.ServerMessageType.NOTIFICATION,
                                authData.username() + " has left the game"));
            }
        } catch (IOException | SQLException | DataAccessException e) {
            session.getRemote().sendString(
                    new Gson().toJson(
                            new ServerMessage(
                                    ServerMessage.ServerMessageType.ERROR,
                                    "There was an error" + e.getMessage())));
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket Closed");

    }

    private void connect(String authToken, int gameID, Session session, String color) throws IOException, SQLException, DataAccessException {
        try {
            AuthData authData = authService.getAuth(authToken);
            if (authData == null) {
                session.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Not Authorized")));
            } else {
                connections.add(gameID, session);
                StringBuilder sb = new StringBuilder();
                sb.append(authData.username());
                sb.append(" has joined");
                if (!color.equalsIgnoreCase("")) {
                    sb.append(" playing as ");
                    sb.append(color);
                }
                connections.broadcast_game(gameID, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, sb.toString()));
            }
        } catch (IOException | SQLException | DataAccessException e) {
            session.getRemote().sendString(
                    new Gson().toJson(
                            new ServerMessage(
                                    ServerMessage.ServerMessageType.ERROR,
                                    "There was an error" + e.getMessage())));
        }
    }

    public void registerRoutes(final Javalin app) {
        app.ws("/ws", ws -> {
            ws.onConnect(this);
            ws.onMessage(this);
            ws.onClose(this);
        });
    }
}

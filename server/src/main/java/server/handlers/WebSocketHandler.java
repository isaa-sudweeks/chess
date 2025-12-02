package server.handlers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.ConnectionManager;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

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
                    connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), wsMessageContext.session);
            case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID(), wsMessageContext.session);
        }
    }

    private void leave(String authToken, Integer gameID, Session session) {
        connections.remove(gameID, session);
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket Closed");

    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        connections.add(gameID, session);
        System.out.println("connect was called");
        connections.broadcast_game(gameID, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "message")); //TODO:Change this so that it says which player connected and to which side
    }

    public void registerRoutes(final Javalin app) {
        app.ws("/ws", ws -> {
            ws.onConnect(this);
            ws.onMessage(this);
            ws.onClose(this);
        });
    }
}

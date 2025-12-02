package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
            this.session = webSocketContainer.connectToServer(this, socketURI);

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        session.addMessageHandler(String.class, s -> {
            try {
                ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
                notificationHandler.notify(message);
            } catch (Exception e) {
                System.out.println("CLIENT WS FAILED TO PARSE:");
                e.printStackTrace();
            }
        });
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void joinGame(String authToken, int gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(websocket.commands.UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

}

package server.handlers;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.Javalin;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.ConnectionManager;
import service.AuthService;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    private final ConnectionManager connections = new ConnectionManager();
    private AuthService authService;
    private GameService gameService;

    public WebSocketHandler(AuthService authService, GameDAO gameDAO) {
        this.authService = authService;
        // Pass authService so gameService auth checks don't NPE
        this.gameService = new GameService(gameDAO, authService);
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket Connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        UserGameCommand userGameCommand = GSON.fromJson(wsMessageContext.message(), UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT ->
                    connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), wsMessageContext.session, userGameCommand.getColor());
            case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID(), wsMessageContext.session);
            case MAKE_MOVE ->
                    makeMove(userGameCommand.getAuthToken(), userGameCommand.getGameID(), userGameCommand.getMove(), wsMessageContext.session);
            case RESIGN ->
                    resign(userGameCommand.getAuthToken(), userGameCommand.getGameID(), wsMessageContext.session);
        }
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException {
        try {
            var games = gameService.listGames(authToken);
            GameData gameData = games.games().get(gameID - 1);
            gameService.finishGame(gameData);
            connections.broadcast_game(gameData.gameID(),
                    new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            authService.getAuth(authToken).username() + " resigned the game "));
        } catch (SQLException | DataAccessException e) {
            authorized(session, "There was an error" + e.getMessage());
        }
    }

    private void makeMove(String authToken, Integer gameID, ChessMove move, Session session) throws IOException {
        try {
            AuthData authData = authService.getAuth(authToken);
            if (authData == null) {
                authorized(session, "Not Authorized");
            } else {
                GameData gameData = getGame(gameID, authToken);
                if (gameData.game().getIsDone()) {
                    authorized(session, "You can not make a move on a finished game");
                } else {
                    gameService.updateGame(gameData, move);
                    connections.broadcast_game(gameID, new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData));
                    connections.broadcast_game(gameID,
                            new ServerMessage(
                                    ServerMessage.ServerMessageType.NOTIFICATION,
                                    authData.username() +
                                            " moved" + move.getStartPosition() + " to " + move.getEndPosition()));
                }

                //TODO: Check for check and checkmate
            }

        } catch (SQLException | DataAccessException e) {
            authorized(session, "There was an error" + e.getMessage());
        } catch (InvalidMoveException e) {
            authorized(session, "Move not valid");
        }
    }

    private void authorized(Session session, String Not_Autherized) throws IOException {
        session.getRemote().sendString(new Gson().toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR, Not_Autherized)));
    }

    private void leave(String authToken, Integer gameID, Session session) throws IOException {
        try {
            AuthData authData = authService.getAuth(authToken);
            if (authData == null) {
                authorized(session, "Not Authorized");
            } else {
                connections.remove(gameID, session);
                connections.broadcast_game(
                        gameID,
                        new ServerMessage(
                                ServerMessage.ServerMessageType.NOTIFICATION,
                                authData.username() + " has left the game"));
            }
        } catch (IOException | SQLException | DataAccessException e) {
            authorized(session, "There was an error" + e.getMessage());
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
                authorized(session, "Not Authorized");
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
                var games = gameService.listGames(authToken);
                connections.broadcast_game(gameID, new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, getGame(gameID, authToken)));
            }
        } catch (IOException | SQLException | DataAccessException e) {
            authorized(session, "There was an error" + e.getMessage());
        }
    }

    public GameData getGame(Integer gameID, String authToken) throws SQLException, DataAccessException {
        var games = gameService.listGames(authToken);
        for (var game : games.games()) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public void registerRoutes(final Javalin app) {
        app.ws("/ws", ws -> {
            ws.onConnect(this);
            ws.onMessage(this);
            ws.onClose(this);
        });
    }
}

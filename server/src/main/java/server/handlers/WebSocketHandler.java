package server.handlers;

import chess.ChessGame;
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
            AuthData authData = authService.getAuth(authToken);
            GameData gameData = getGame(gameID, authToken);
            if (gameData.game().getIsDone()) {
                authorized(session, "You can not resign more than once");
            } else if (!(authData.username().equalsIgnoreCase(gameData.blackUsername())) &&
                    !(authData.username().equalsIgnoreCase(gameData.whiteUsername()))) {
                authorized(session, "You can not resign as an observer");
            } else {
                gameService.finishGame(gameData);
                connections.broadcast_game(gameData.gameID(),
                        new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                authService.getAuth(authToken).username() + " resigned the game "));
            }
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
                ChessGame.TeamColor turn = gameData.game().getTeamTurn();
                String testUsername = "";
                if (turn == ChessGame.TeamColor.BLACK) {
                    testUsername = gameData.blackUsername();
                }
                if (turn == ChessGame.TeamColor.WHITE) {
                    testUsername = gameData.whiteUsername();
                }
                if (gameData.game().getIsDone()) {
                    authorized(session, "You can not make a move on a finished game");
                } else if (testUsername == null || !(testUsername.equalsIgnoreCase(authData.username()))) {
                    authorized(session, "It isn't your turn");

                } else {
                    gameService.updateGame(gameData, move);
                    connections.broadcast_game(gameID, new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData));
                    connections.broadcast_all(session, gameID,
                            new ServerMessage(
                                    ServerMessage.ServerMessageType.NOTIFICATION,
                                    authData.username() +
                                            " moved" + move.getStartPosition() + " to " + move.getEndPosition()));
                    ChessGame.TeamColor checkColor = null;
                    String checkUsername = null;
                    if (turn == ChessGame.TeamColor.WHITE) {
                        checkColor = ChessGame.TeamColor.BLACK;
                        checkUsername = gameData.blackUsername();
                    } else if (turn == ChessGame.TeamColor.BLACK) {
                        checkColor = ChessGame.TeamColor.WHITE;
                        checkUsername = gameData.whiteUsername();
                    }
                    if (gameData.game().isInCheckmate(checkColor)) {
                        gameService.finishGame(gameData);
                        connections.broadcast_game(
                                gameID,
                                new ServerMessage(
                                        ServerMessage.ServerMessageType.NOTIFICATION,
                                        checkUsername + " is in checkmate the game is over"));
                    } else if (gameData.game().isInStalemate(checkColor)) {
                        gameService.finishGame(gameData);
                        connections.broadcast_game(
                                gameID,
                                new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                        "The game is in stalemate"));
                    } else if (gameData.game().isInCheck(checkColor)) {
                        connections.broadcast_game(
                                gameID,
                                new ServerMessage(
                                        ServerMessage.ServerMessageType.NOTIFICATION,
                                        checkUsername + " is in check"));
                    }
                }


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

    private void sendOne(Session session, GameData gameData) throws IOException {
        session.getRemote().sendString(GSON.toJson(
                new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData)));
    }

    private void leave(String authToken, Integer gameID, Session session) throws IOException {
        try {
            AuthData authData = authService.getAuth(authToken);
            if (authData == null) {
                authorized(session, "Not Authorized");
            } else {
                connections.remove(gameID, session);
                ChessGame.TeamColor color = null;
                GameData gameData = getGame(gameID, authToken);
                if (authData.username().equalsIgnoreCase(gameData.whiteUsername())) {
                    color = ChessGame.TeamColor.WHITE;
                }
                if (authData.username().equalsIgnoreCase(gameData.blackUsername())) {
                    color = ChessGame.TeamColor.BLACK;
                }
                if (color != null) {
                    gameService.playerLeaves(getGame(gameID, authToken), color);
                }
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
        //System.out.println("Websocket Closed");

    }

    private void connect(String authToken, int gameID, Session session, String color) throws IOException, SQLException, DataAccessException {
        try {
            AuthData authData = authService.getAuth(authToken);
            if (authData == null) {
                authorized(session, "Not Authorized");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(authData.username());
                sb.append(" has joined");
                if (!color.equalsIgnoreCase("")) {
                    sb.append(" playing as ");
                    sb.append(color);
                }
                var gameData = getGame(gameID, authToken);
                if (gameData == null) {
                    authorized(session, "Not a valid gameID");
                } else {
                    connections.broadcast_game(gameID,
                            new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, sb.toString()));
                    connections.add(gameID, session);
                    sendOne(session, getGame(gameID, authToken));
                }
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

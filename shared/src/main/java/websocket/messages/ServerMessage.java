package websocket.messages;

import model.GameData;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    private String message;
    private GameData game;
    private String errorMessage;

    public ServerMessage(ServerMessageType type, String message) {

        this.serverMessageType = type;

        if (type == ServerMessageType.ERROR) {
            this.errorMessage = message;
        } else {
            this.message = message;
        }

    }

    public ServerMessage(ServerMessageType type, GameData game) {
        this.serverMessageType = type;
        this.game = game;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getMessage() {
        return this.message;
    }

    public GameData getGame() {
        return this.game;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }
}

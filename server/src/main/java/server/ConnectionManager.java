package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        var currentList = connections.get(gameID);
        if (currentList != null) {
            currentList.add(session);
            connections.put(gameID, currentList);
        } else {
            List<Session> tempList = new ArrayList<>();
            tempList.add(session);
            connections.put(gameID, tempList);
        }

    }

    public void remove(int gameID, Session session) {
        var currentList = connections.get(gameID);
        currentList.remove(session);
        connections.replace(gameID, currentList);
    }

    public void broadcast_all(Session excludeSession, Integer gameID, ServerMessage message) throws IOException {
        var sessions = connections.get(gameID);
        String msg = GSON.toJson(message);
        for (Session c : sessions) {
            if (c != excludeSession) {
                if (c.isOpen()) {
                    c.getRemote().sendString(msg);
                }
            }
        }

    }

    public void broadcast_game(int gameID, ServerMessage message) throws IOException {
        var sessions = connections.get(gameID);
        // Use a Gson instance that supports complex map keys so ChessPosition keys deserialize on the client
        String msg = GSON.toJson(message);

        if (sessions == null || sessions.isEmpty()) {

            return;
        }
        for (Session c : sessions) {
            if (c.isOpen()) {
                c.getRemote().sendString(msg);
            } else {
            }
        }
    }
}

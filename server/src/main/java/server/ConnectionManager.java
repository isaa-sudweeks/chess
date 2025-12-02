package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
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

    public void broadcast_all(Session excludeSession, ServerMessage message) throws IOException {
        String msg = message.toString();
        var sessions = connections.values();
        for (List<Session> session : sessions) {
            for (Session c : session) {
                if (c.isOpen()) {
                    if (!c.equals(excludeSession)) {
                        c.getRemote().sendString(msg);
                    }
                }
            }
        }

    }

    public void broadcast_game(int gameID, ServerMessage message) throws IOException {
        var sessions = connections.get(gameID);
        String msg = new Gson().toJson(message);

        System.out.println("broadcast_game: gameID=" + gameID);
        System.out.println("broadcast_game: json=" + msg);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("broadcast_game: no sessions for game " + gameID);
            return;
        }

        System.out.println("broadcast_game: sending to " + sessions.size() + " sessions");
        for (Session c : sessions) {
            if (c.isOpen()) {
                System.out.println("broadcast_game: sending to " + c.getRemoteAddress());
                c.getRemote().sendString(msg);
            } else {
                System.out.println("broadcast_game: session closed: " + c.getRemoteAddress());
            }
        }
    }
}
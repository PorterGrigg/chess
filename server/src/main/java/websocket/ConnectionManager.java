package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;
//import webSocketMessages.Notification;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        //check if the gameID already exists
        if (connections.containsKey(gameID)){
            Set<Session> sessions = connections.get(gameID); //get the set connected to the key
            sessions.add(session); // now add the session
        }
        else{
            Set<Session> sessions = new HashSet<>(); // create a new set
            sessions.add(session);
            connections.put(gameID, sessions);
        }
    }

    public void remove(int gameID, Session session) {
        Set<Session> sessions = connections.get(gameID); //get the set connected to the key
        sessions.remove(session); // now remove the session

        if (sessions.isEmpty()){ //if all the players have left the game then remove this connecion
            connections.remove(gameID);
        }
    }

    public void removeAll(int gameID) {
        connections.remove(gameID);
    }

    public void broadcastGame(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        String jsonNotification = new Gson().toJson(notification);
        Set<Session> sessions = connections.get(gameID);
        for (Session sesh : sessions) {
            if (sesh.isOpen()) {
                if (!sesh.equals(excludeSession)) {
                    sesh.getRemote().sendString(jsonNotification);
                }
            }
        }
    }

    public void broadcastUser(Session userSession, ServerMessage message) throws IOException {
        String jsonMessage = new Gson().toJson(message);
        userSession.getRemote().sendString(jsonMessage);
    }
}

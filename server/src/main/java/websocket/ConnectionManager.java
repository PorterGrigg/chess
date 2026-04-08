package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;
//import webSocketMessages.Notification;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcastGame(Session excludeSession, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }


    public void broadcastUser(Session userSession, ServerMessage message) throws IOException {
        String msg = message.toString();
        userSession.getRemote().sendString(msg);
    }
}

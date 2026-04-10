package client.websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(String message); //Json
}

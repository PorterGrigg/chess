package client.websocket;

import websocket.messages.ServerMessage;

import static ui.EscapeSequences.RED;

public class ServerToClientMessageHandler implements ServerMessageHandler{
    @Override
    public void notify(ServerMessage serverMessage) { //handle incomming messages from the server through websocket and send to gameREPL
        switch(serverMessage.getServerMessageType()){
//            case ERROR -> displayError();
//            case LOAD_GAME -> displayLoadedGame();
//            case NOTIFICATION -> displayNotification();
        }
//        System.out.println(RED + serverMessage);
//        printPrompt();
    }
}

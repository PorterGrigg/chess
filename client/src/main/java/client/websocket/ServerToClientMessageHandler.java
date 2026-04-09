package client.websocket;

import client.GameREPL;
import client.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class ServerToClientMessageHandler implements ServerMessageHandler{

    private final GameREPL gameREPL;

    public ServerToClientMessageHandler(GameREPL gameREPL){
        this.gameREPL = gameREPL;
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case ERROR -> displayError((ErrorMessage) serverMessage);
            case LOAD_GAME -> displayLoadedGame((LoadGameMessage) serverMessage);
            case NOTIFICATION -> displayNotification((NotificationMessage) serverMessage);
        }
    }

    public void displayError(ErrorMessage serverMessage) {
        String message = serverMessage.errorMessage;
        System.out.println(RED + message);
        gameREPL.printPrompt();
    }

    public void displayNotification(NotificationMessage serverMessage) {
        String message = serverMessage.message;
        System.out.println(RED + message);
        gameREPL.printPrompt();
    }

    public void displayLoadedGame(LoadGameMessage serverMessage){//don't actually need the game because the
        // client will pull the gae straight from the database
        try {
            gameREPL.redrawBoard();
        }catch(ResponseException ex){
            System.out.println(RED + ex.getMessage());
            gameREPL.printPrompt();
        }
    }
}

package client.websocket;

import client.GameREPL;
import client.ResponseException;
import com.google.gson.Gson;
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
    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        //System.out.println("Receiving message from server");
        switch (serverMessage.getServerMessageType()) {
            case ERROR ->{
                ErrorMessage errorMessage= new Gson().fromJson(message, ErrorMessage.class);
                displayError(errorMessage);
            }
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                displayLoadedGame(loadGameMessage);
            }
            case NOTIFICATION ->  {
                NotificationMessage notificationMessage= new Gson().fromJson(message, NotificationMessage.class);
                displayNotification(notificationMessage);
            }
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
            gameREPL.printPrompt();
        }catch(ResponseException ex){
            System.out.println(RED + ex.getMessage());
            gameREPL.printPrompt();
        }
    }
}

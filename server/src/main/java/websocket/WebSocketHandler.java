package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedUserException;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final WebSocketService webSocketService;

    public WebSocketHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE-> { //make move has a seperate field that we need to extract
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), moveCommand.getMove(), ctx.session);
                }
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx.session);
                case RESIGN-> resign(command.getAuthToken(), command.getGameID(), ctx.session);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        try {
            webSocketService.authorizeUser(authToken);
            String username = webSocketService.getUsername(authToken);

            connections.add(session);

            var userMessage = getUserLoadGameMessage(authToken, gameID);
            connections.broadcastUser(session, userMessage); //send board update to new player

            var notification = getConnectNotifiction(authToken, gameID);
            connections.broadcastGame(session, notification); //send notificaiton that a user has entered to all participants

        }catch(UnauthorizedUserException exception){
            var userErrorMessage = new ErrorMessage("Error: unauthorized user");
            connections.broadcastUser(session, userErrorMessage); //send error to user
        }catch(DataAccessException exception){
            exception.printStackTrace();
            var userErrorMessage = new ErrorMessage("Error: could not access data");
            connections.broadcastUser(session, userErrorMessage); //send error to
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        ChessGame game = null;
        try {
            game = webSocketService.getGame(gameID);
        } catch(DataAccessException exception){
            exception.printStackTrace();
            var userErrorMessage = new ErrorMessage("Error: could not access data");
            connections.broadcastUser(session, userErrorMessage); //send error to
        }

        //makeMove method in ChessGame validates its moves
        try{
            game.makeMove(move);
        }catch (InvalidMoveException exception){
            var userErrorMessage = new ErrorMessage(exception.getMessage());
            connections.broadcastUser(session, userErrorMessage);
        }

        //update game in database
        webSocketService.updateGame(gameID, game);


        if(webSocketService.validMove(game, move)){

        }
        else{
            var userErrorMessage = new ErrorMessage("Error: invlaid move!");
            connections.broadcastUser(session, userErrorMessage); //send error to user
        }

        try {


            webSocketService.authorizeUser(authToken);
            String username = webSocketService.getUsername(authToken);

            connections.add(session);

            var userMessage = getUserLoadGameMessage(authToken, gameID);
            connections.broadcastUser(session, userMessage); //send board update to new player

            var notification = getConnectNotifiction(authToken, gameID);
            connections.broadcastGame(session, notification); //send notificaiton that a user has entered to all participants

        }catch(UnauthorizedUserException exception){
            var userErrorMessage = new ErrorMessage("Error: unauthorized user");
            connections.broadcastUser(session, userErrorMessage); //send error to user
        }catch(DataAccessException exception){
            exception.printStackTrace();
            var userErrorMessage = new ErrorMessage("Error: could not access data");
            connections.broadcastUser(session, userErrorMessage); //send error to
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private LoadGameMessage getUserLoadGameMessage(String authToken, int gameID) throws BadRequestException, DataAccessException{
        GameData game = webSocketService.getGameData(gameID);
        return new LoadGameMessage(game);
    }

    private NotificationMessage getConnectNotifiction(String authToken, int gameID) throws DataAccessException {
        String username = webSocketService.getUsername(authToken);
        NotificationMessage notification = null;

        if (usernameIsInGame(username, gameID)){
            String playerColor = getPlayerColor(username, gameID);
            var message = String.format("%s has joined as %s", username, playerColor);
            notification = new NotificationMessage(message); //don't need to specify the type because it is supered
        }
        else{
            var message = String.format("%s is observing the game", username);
            notification = new NotificationMessage(message);
        }
        return notification;

    }

    private boolean usernameIsInGame(String username, int gameID) throws DataAccessException {
        GameData game = webSocketService.getGameData(gameID);
        String blackUser = game.blackUsername();
        String whiteUser = game.whiteUsername();
        if (username.equals(blackUser) || username.equals(whiteUser)){
            return true;
        }
        else{
            return false;
        }
    }

    private String getPlayerColor(String username, int gameID) throws DataAccessException {
        GameData game = webSocketService.getGameData(gameID);
        String blackUser = game.blackUsername();
        String whiteUser = game.whiteUsername();
        if (username.equals(blackUser)){
            return "Black";
        }
        else if ((username.equals(whiteUser))){
            return "White";
        }
        else{
            return "Invalid Color";
        }
    }

}

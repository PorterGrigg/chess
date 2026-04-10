package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
            System.out.println("Handling Message");
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
            System.out.println("Connecting");
            webSocketService.authorizeUser(authToken);
            String username = webSocketService.getUsername(authToken);
            webSocketService.validateGameID(gameID); //will throw error if ID not found

            connections.add(gameID, session);

            //when enter the gameREPL loop it already does this
//            var userMessage = getLoadGameMessage(authToken, gameID);
//            connections.broadcastUser(session, userMessage); //send board update to new player

            var notification = getConnectNotifiction(authToken, gameID);
            connections.broadcastGame(gameID, session, notification); //send notificaiton that a user has entered to all participants

        }catch(UnauthorizedUserException  | DataAccessException | BadRequestException exception){
            var userErrorMessage = new ErrorMessage(exception.getMessage());
            connections.broadcastUser(session, userErrorMessage);
        }
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        ChessGame game = null;
        System.out.println("Making Move");
        try {
            webSocketService.authorizeUser(authToken);

            game = webSocketService.getGame(gameID);

            //update game in database
            //makeMove method in ChessGame validates its moves

            webSocketService.validateTeamColor(gameID, authToken, move);

            webSocketService.updateGame(gameID, move);


            //send load game message to all clients
            var loadNotification = getLoadGameMessage(authToken, gameID);
            connections.broadcastGame(gameID, null, loadNotification); //don't exclude any session

            //send a notification with move made to all other players
            var moveNotification = getMoveBreakdownNotification(move, authToken, gameID);
            connections.broadcastGame(gameID, session, moveNotification); //exclude the session that made the move


            //send notification if in checkmate
            if (webSocketService.opponentInCheckmate(authToken, gameID)){
                var checkmateNotification = getCheckmateNotification(authToken, gameID);
                connections.broadcastGame(gameID, null, checkmateNotification);
                endGame(authToken, gameID);
            }
            //send notification if in check
            else if (webSocketService.opponentInCheck(authToken, gameID)){
                var checkNotification = getCheckNotification(authToken, gameID);
                connections.broadcastGame(gameID, null, checkNotification);
            }
            //send notification if in stalemate
            else if (webSocketService.opponentInStalemate(authToken, gameID)){
                var stalemateNotification = getStalemateNotification(authToken, gameID);
                connections.broadcastGame(gameID, null, stalemateNotification);
                endGame(authToken, gameID);
            }

        }catch (InvalidMoveException | DataAccessException | BadRequestException | UnauthorizedUserException exception){
            var userErrorMessage = new ErrorMessage(exception.getMessage());
            connections.broadcastUser(session, userErrorMessage);
        }
    }

    private void endGame(String authToken, int gameID) throws IOException, DataAccessException {
        //send end game message to all players
        GameData gameData =  webSocketService.getGameData(gameID);
        String endMessage = String.format("Well done %s and %s and thanks for playing!", gameData.whiteUsername(), gameData.blackUsername());
        var endNotification = new NotificationMessage(endMessage);
        //connections.broadcastGame(gameID, null, endNotification); //the autograder tests don't allow extra messages:(

        //send winner congrats
        String username = webSocketService.getUsername(authToken);
        String winnerMessage = String.format("Congratulations %s you are a chess master!!", username);
        var winnerNotification = new NotificationMessage(winnerMessage);
        //connections.broadcastUser(session, winnerNotification); //the autograder tests don't allow extra messages:(

        //disconnect all players
        connections.removeAll(gameID);
        webSocketService.removeBothPlayers(gameID);
    }

    private void leave(String authToken, int gameID, Session session) throws IOException {
        System.out.println("Leaving");
        try {
            webSocketService.authorizeUser(authToken);

            var notification = getLeaveNotification(authToken, gameID);

            connections.remove(gameID, session);

            //update gameData if it is a player and not an observer
            if (webSocketService.usernameIsInGame(webSocketService.getUsername(authToken), gameID)){
                webSocketService.removePlayer(authToken, gameID);
            }

            connections.broadcastGame(gameID, null, notification); //send notificaiton that a user has left to all participants

        }catch(UnauthorizedUserException  | DataAccessException | BadRequestException exception){
            var userErrorMessage = new ErrorMessage(exception.getMessage());
            connections.broadcastUser(session, userErrorMessage);
        }
    }

    private void resign(String authToken, int gameID, Session session) throws IOException {
        try {
            webSocketService.authorizeUser(authToken);

            var notification = getResignNotification(authToken, gameID);
            connections.broadcastGame(gameID, null, notification); //send notificaiton that a user has left to all participants
            webSocketService.removeBothPlayers(gameID);

        }catch(UnauthorizedUserException  | DataAccessException | BadRequestException exception){
            var userErrorMessage = new ErrorMessage(exception.getMessage());
            connections.broadcastUser(session, userErrorMessage);
        }
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }






    //Helper methods
    private LoadGameMessage getLoadGameMessage(String authToken, int gameID) throws BadRequestException, DataAccessException{
        GameData game = webSocketService.getGameData(gameID);
        return new LoadGameMessage(game);
    }

    private NotificationMessage getConnectNotifiction(String authToken, int gameID) throws DataAccessException {
        String username = webSocketService.getUsername(authToken);
        NotificationMessage notification = null;

        if (webSocketService.usernameIsInGame(username, gameID)){
            String playerColor = getPlayerColorString(username, gameID);
            var message = String.format("%s has joined as %s", username, playerColor);
            notification = new NotificationMessage(message); //don't need to specify the type because it is supered
        }
        else{
            var message = String.format("%s is observing the game", username);
            notification = new NotificationMessage(message);
        }
        return notification;

    }


    private NotificationMessage getMoveBreakdownNotification(ChessMove move, String authToken, int gameID) throws DataAccessException {
        String username = webSocketService.getUsername(authToken);
        NotificationMessage notification;

        String playerColor = getPlayerColorString(username, gameID);

        String startPos = getPositionString(move.getStartPosition());
        String endPos = getPositionString(move.getEndPosition());

        var message = String.format("%s as %s has moved %s to %s", username, playerColor, startPos, endPos);
        notification = new NotificationMessage(message);

        return notification;
    }

    private String getPositionString(ChessPosition pos){
        String posStr = "";

        //need to translate col to letters
        int colNum = pos.getColumn();
        switch(colNum){
            case 1 -> posStr += "A";
            case 2 -> posStr += "B";
            case 3 -> posStr += "C";
            case 4 -> posStr += "D";
            case 5 -> posStr += "E";
            case 6 -> posStr += "F";
            case 7 -> posStr += "G";
            case 8 -> posStr += "H";
        }
        posStr += String.valueOf(pos.getRow());

        return posStr;
    }

    private NotificationMessage getCheckNotification(String authToken, int gameID) throws DataAccessException {
        NotificationMessage notification;
        String username = webSocketService.getUsername(authToken);
        String opponentColor = getOpponentColorString(username, gameID);
        String opponentUsername = getUsernameFromColor(opponentColor, gameID);

        var message = String.format("%s as %s in in Check", opponentUsername, opponentColor);
        notification = new NotificationMessage(message);

        return notification;
    }

    private NotificationMessage getCheckmateNotification(String authToken, int gameID) throws DataAccessException {
        NotificationMessage notification;
        String username = webSocketService.getUsername(authToken);
        String opponentColor = getOpponentColorString(username, gameID);
        String opponentUsername = getUsernameFromColor(opponentColor, gameID);

        var message = String.format("%s as %s in in Checkmate", opponentUsername, opponentColor);
        notification = new NotificationMessage(message);

        return notification;
    }

    private NotificationMessage getStalemateNotification(String authToken, int gameID) throws DataAccessException {
        NotificationMessage notification;
        String username = webSocketService.getUsername(authToken);
        String opponentColor = getOpponentColorString(username, gameID);
        String opponentUsername = getUsernameFromColor(opponentColor, gameID);

        var message = String.format("%s as %s in in Stalemate", opponentUsername, opponentColor);
        notification = new NotificationMessage(message);

        return notification;
    }



    public String getPlayerColorString(String username, int gameID) throws DataAccessException {
        ChessGame.TeamColor playerColorType = webSocketService.getPlayerColor(username, gameID);
        if (playerColorType == ChessGame.TeamColor.BLACK){
            return "Black";
        }
        else if (playerColorType == ChessGame.TeamColor.WHITE){
            return "White";
        }
        else{
            return "Error: Invalid Color";
        }
    }

    public String getOpponentColorString(String username, int gameID) throws DataAccessException {
        ChessGame.TeamColor playerColorType = webSocketService.getPlayerColor(username, gameID);
        if (playerColorType == ChessGame.TeamColor.BLACK){
            return "White";
        }
        else if (playerColorType == ChessGame.TeamColor.WHITE){
            return "Black";
        }
        else{
            return "Error: Invalid Color";
        }
    }

    private NotificationMessage getLeaveNotification(String authToken, int gameID) throws DataAccessException {
        String username = webSocketService.getUsername(authToken);
        NotificationMessage notification = null;

        if (webSocketService.usernameIsInGame(username, gameID)){
            var message = String.format("%s has left the game", username);
            notification = new NotificationMessage(message); //don't need to specify the type because it is supered
        }
        else{
            var message = String.format("%s has stopped observing the game", username);
            notification = new NotificationMessage(message);
        }
        return notification;

    }

    private NotificationMessage getResignNotification(String authToken, int gameID) throws DataAccessException {
        String username = webSocketService.getUsername(authToken);
        NotificationMessage notification = null;

        if (webSocketService.usernameIsInGame(username, gameID)){
            String playerColor = getPlayerColorString(username, gameID);
            var message = String.format("%s has resigned the game as %s", username, playerColor);
            notification = new NotificationMessage(message);
        }
        else{
            throw new BadRequestException("Error: Cannot resign as observer");
        }

        return notification;

    }

    private String getUsernameFromColor(String color, int gameID) throws DataAccessException {
        GameData gameData = webSocketService.getGameData(gameID);

        if (color.equals("White")){
            return gameData.whiteUsername();
        }
        else{
            return gameData.blackUsername();
        }
    }

}

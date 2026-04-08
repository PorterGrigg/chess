package websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedUserException;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;

import java.io.IOException;

public class WebSocketService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }


    private void connect(String authToken, int gameID, Session session) throws IOException {
        try {
            AuthData userAuthData = authorizeUser(authToken);
            String username = userAuthData.username();

            connections.add(session);

            GameData game = gameService.getGame(gameID);
            var userMessage = new LoadGameMessage(game);
            connections.broadcastUser(session, userMessage); //send board update to new player

            var message = String.format("%s is in the shop", visitorName);
            var notification = new Notification(Notification.Type.ARRIVAL, message);
            connections.broadcastGame(session, notification); //send notificaiton that a user has entered to all participants
        }catch(UnauthorizedUserException exception){
            var errorMessage = String.format("%s is in the shop", visitorName);
            var notification = new Notification(Notification.Type.ARRIVAL, message);
            connections.notify(); //send board update to new player
        }catch(DataAccessException exception){

        }
    }

    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private void authorizeUser(String authToken) throws DataAccessException, UnauthorizedUserException{
        AuthData authorization = authDAO.findAuth(authToken);
        //if username does not exist then throw error

        if(authorization == null){
            throw new UnauthorizedUserException("Error: Unauthorized");
        }
    }

    public GameData getGame(int gameID) throws BadRequestException, DataAccessException{
        GameData game = gameDAO.findGame(gameID);
        //check if the game was found
        if (game == null){
            throw new BadRequestException("Error: game not found");
        }
        return game;
    }
}

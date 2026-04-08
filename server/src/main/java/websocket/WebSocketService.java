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
import websocket.messages.NotificationMessage;

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

    public void authorizeUser(String authToken) throws DataAccessException, UnauthorizedUserException{
        AuthData authorization = authDAO.findAuth(authToken);

        if(authorization == null){
            throw new UnauthorizedUserException("Error: Unauthorized");
        }
    }

    public String getUsername(String authToken) throws DataAccessException, UnauthorizedUserException{
        AuthData userAuth = authDAO.findAuth(authToken);

        if(userAuth == null){
            throw new UnauthorizedUserException("Error: Unauthorized");
        }
        return userAuth.username();
    }

    public LoadGameMessage getUserLoadGameMessage(String authToken, int gameID) throws BadRequestException, DataAccessException{

        GameData game = getGame(gameID);
        return new LoadGameMessage(game);
    }

    public NotificationMessage getBroadcastNotificationMessage

    private GameData getGame(int gameID) throws BadRequestException, DataAccessException{
        GameData game = gameDAO.findGame(gameID);
        //check if the game was found
        if (game == null){
            throw new BadRequestException("Error: game not found");
        }
        return game;
    }
}

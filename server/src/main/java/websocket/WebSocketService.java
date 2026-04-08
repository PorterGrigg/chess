package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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

    public GameData getGameData(int gameID) throws BadRequestException, DataAccessException{
        GameData game = gameDAO.findGame(gameID);
        //check if the game was found
        if (game == null){
            throw new BadRequestException("Error: game not found");
        }
        return game;
    }

    public ChessGame getGame(int gameID) throws BadRequestException, DataAccessException{
        GameData gameData = gameDAO.findGame(gameID);
        //check if the game was found
        if (gameData == null){
            throw new BadRequestException("Error: game not found");
        }
        return gameData.game();
    }

    public void updateGame(int gameID, ChessMove move) throws DataAccessException, InvalidMoveException {
        GameData beforeData = getGameData(gameID);
        ChessGame game = getGame(gameID);

        game.makeMove(move);

        //update in database
        gameDAO.updateGameData(gameID, beforeData.whiteUsername(), beforeData.blackUsername(), beforeData.gameName(), game);
    }
}

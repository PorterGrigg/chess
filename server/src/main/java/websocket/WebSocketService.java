package websocket;

import chess.*;
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

    public void validateGameID(int gameID) throws DataAccessException {
        getGameData(gameID);
    }

    public void validateTeamColor(int gameID, String authToken, ChessMove move) throws DataAccessException, InvalidMoveException {
        GameData gameData = getGameData(gameID);
        ChessGame game = getGame(gameID);
        ChessPosition startPos = move.getStartPosition();

        //find user color
        String username = getUsername(authToken);
        ChessGame.TeamColor playerColor = getPlayerColor(username, gameID);

        //find piece color
        ChessPiece piece = game.gameBoard.getPiece(startPos);

        //check if the position is empty!!
        if (piece == null){
            throw new InvalidMoveException("Error: No piece at start position");
        }

        //throw error if don't match
        if (piece.getTeamColor() != playerColor){
            throw new BadRequestException("Error: Wrong piece color");
        }

    }


    public ChessGame.TeamColor getPlayerColor(String username, int gameID) throws DataAccessException, BadRequestException {
        GameData game = getGameData(gameID);
        String blackUser = game.blackUsername();
        String whiteUser = game.whiteUsername();
        if (username.equals(blackUser)){
            return ChessGame.TeamColor.BLACK;
        }
        else if ((username.equals(whiteUser))){
            return ChessGame.TeamColor.WHITE;
        }
        else{
            throw new BadRequestException("Error: invalid color");
        }
    }

    public boolean opponentInCheck(String authToken, int gameID) throws DataAccessException {
        String username = getUsername(authToken);
        ChessGame game = getGame(gameID);

        ChessGame.TeamColor playerColor = getPlayerColor(username, gameID);

        if(playerColor == ChessGame.TeamColor.WHITE){
            return game.isInCheck(ChessGame.TeamColor.BLACK);
        }
        else{
            return game.isInCheck(ChessGame.TeamColor.WHITE);
        }


    }

    public boolean opponentInCheckmate(String authToken, int gameID) throws DataAccessException {
        String username = getUsername(authToken);
        ChessGame game = getGame(gameID);

        ChessGame.TeamColor playerColor = getPlayerColor(username, gameID);

        if(playerColor == ChessGame.TeamColor.WHITE){
            return game.isInCheckmate(ChessGame.TeamColor.BLACK);
        }
        else{
            return game.isInCheckmate(ChessGame.TeamColor.WHITE);
        }
    }

    public boolean opponentInStalemate(String authToken, int gameID) throws DataAccessException {
        String username = getUsername(authToken);
        ChessGame game = getGame(gameID);

        ChessGame.TeamColor playerColor = getPlayerColor(username, gameID);

        if(playerColor == ChessGame.TeamColor.WHITE){
            return game.isInStalemate(ChessGame.TeamColor.BLACK);
        }
        else{
            return game.isInStalemate(ChessGame.TeamColor.WHITE);
        }
    }

    public boolean usernameIsInGame(String username, int gameID) throws DataAccessException {
        GameData game = getGameData(gameID);
        String blackUser = game.blackUsername();
        String whiteUser = game.whiteUsername();
        if (username.equals(blackUser) || username.equals(whiteUser)){
            return true;
        }
        else{
            return false;
        }
    }

    public void removePlayer(String authToken, int gameID) throws DataAccessException {
        String username = getUsername(authToken);
        GameData oldGameData = getGameData(gameID);
        ChessGame.TeamColor playerColor = getPlayerColor(username, gameID);

        String whiteUsername;
        String blackUsername;
        //determine which color to set to null
        if (playerColor == ChessGame.TeamColor.WHITE){
            whiteUsername = null;
            blackUsername = oldGameData.blackUsername();
        }
        else{
            whiteUsername = oldGameData.whiteUsername();
            blackUsername = null;
        }

        gameDAO.updateGameData(gameID, whiteUsername, blackUsername, oldGameData.gameName(), oldGameData.game());
    }

    public void removeBothPlayers(int gameID) throws DataAccessException {

        GameData oldGameData = getGameData(gameID);
        gameDAO.updateGameData(gameID, null, null, oldGameData.gameName(), oldGameData.game());
    }
}

package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import requests.*;
import results.*;

import java.util.ArrayList;


public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private int nextGameID = 0;

    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListResult list(ListRequest request) throws UnauthorizedUserException{
        String authToken = request.authToken();

        //find if  user authorized
        AuthData authorization = authDAO.findAuth(authToken);

        //if username does not exist then throw error
        if(authorization == null){
            throw new UnauthorizedUserException("AuthToken Not Found");
        }

        //read all the games
        ArrayList<GameData> games = gameDAO.readAll();

        //create and return result
        return new ListResult(games);
    }


    public CreateResult create(CreateRequest request) throws UnauthorizedUserException{
        String authToken = request.authToken();
        String gameName = request.gameName();

        //find if  user authorized
        AuthData authorization = authDAO.findAuth(authToken);

        //if username does not exist then throw error
        if(authorization == null){
            throw new UnauthorizedUserException("AuthToken Not Found");
        }

        //create and add a new game
        int gameID = createID();
        gameDAO.create(new GameData(gameID, null, null, gameName, new ChessGame()));

        //create and return result
        return new CreateResult(gameID);
    }

    private int createID(){
        nextGameID++; //increment from last use
        return nextGameID;
    }

    public JoinResult join(JoinRequest request) throws UnauthorizedUserException, AlreadyTakenException{
        String authToken = request.authToken();
        chess.ChessGame.TeamColor playerColor = request.playerColor();
        int gameID = request.gameID();


        //find if  user authorized
        AuthData authorization = authDAO.findAuth(authToken);

        //if username does not exist then throw error
        if(authorization == null){
            throw new UnauthorizedUserException("AuthToken Not Found");
        }

        //check that user is not trying to steal another player's color
        GameData game = gameDAO.findGame(gameID);
        if ((playerColor == ChessGame.TeamColor.BLACK & game.blackUsername() != null) | (playerColor == ChessGame.TeamColor.WHITE & game.whiteUsername() != null)){
            throw new AlreadyTakenException("Error: already taken");
        }

        //update game
        String username = authorization.username();
        gameDAO.updateGame(gameID, playerColor, username);

        //create and return result
        return new JoinResult();
    }

}

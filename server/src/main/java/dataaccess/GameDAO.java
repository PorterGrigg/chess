package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    int create(GameData gameData) throws DataAccessException;
    GameData findGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException;
    ArrayList<GameData> readAll() throws DataAccessException;
    void clear() throws DataAccessException;
}

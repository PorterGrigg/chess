package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    void create(GameData gameData);
    GameData findGame(int gameID);
    void updateGame(int gameID, ChessGame.TeamColor playerColor, String username);
    ArrayList<GameData> readAll();
    void clear();
}

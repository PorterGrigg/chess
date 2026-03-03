package dataAccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    void create(GameData gameData);
    //GameData findGame(String gameName);
    ArrayList<GameData> readAll();
    void clear();
}

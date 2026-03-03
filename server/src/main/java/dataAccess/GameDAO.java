package dataAccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {

    void create(GameData gameData);
    ArrayList<GameData> readAll();
    void clear();
}

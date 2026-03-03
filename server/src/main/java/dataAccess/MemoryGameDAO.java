package dataAccess;


import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    private final ArrayList<GameData> gameStorage = new ArrayList<>();

    @Override
    public void create(GameData gameData){
        gameStorage.add(gameData);
    }

    @Override
    public ArrayList<GameData> readAll(){
        return gameStorage;
    }

    @Override
    public void clear(){
        gameStorage.clear();
    }

}

package dataAccess;


import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{
    private final ArrayList<UserData> userStorage = new ArrayList<>();

    @Override
    public void create(UserData userData){
        userStorage.add(userData);
    }

    @Override
    public ArrayList<UserData> readAll(){
        return userStorage;
    }

    @Override
    public void clear(){
        userStorage.clear();
    }

}

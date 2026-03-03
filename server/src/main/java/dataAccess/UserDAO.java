package dataAccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {

    void create(UserData userData);
    ArrayList<UserData> readAll();
    void clear();
}

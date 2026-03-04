package dataAccess;

import model.AuthData;


import java.util.ArrayList;

public interface AuthDAO {

    void create(AuthData authData);
    AuthData findAuth(String authToken);
    ArrayList<AuthData> readAll();
    void deleteAuth(String authToken);
    void clear();
}

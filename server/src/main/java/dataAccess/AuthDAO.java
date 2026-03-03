package dataAccess;

import model.AuthData;

import java.util.ArrayList;

public interface AuthDAO {

    void create(AuthData authData);
    ArrayList<AuthData> readAll();
    void clear();
}

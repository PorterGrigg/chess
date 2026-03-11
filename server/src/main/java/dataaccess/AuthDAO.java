package dataaccess;

import model.AuthData;


import java.util.ArrayList;

public interface AuthDAO {

    void create(AuthData authData) throws DataAccessException;
    AuthData findAuth(String authToken) throws DataAccessException;
    ArrayList<AuthData> readAll() throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}

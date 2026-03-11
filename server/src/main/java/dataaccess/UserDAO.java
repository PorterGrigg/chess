package dataaccess;

import model.UserData;

import java.util.ArrayList;

public interface UserDAO {

    void create(UserData userData) throws DataAccessException;
    UserData findUser(String username) throws DataAccessException;
    ArrayList<UserData> readAll() throws DataAccessException;
    void clear() throws DataAccessException;
}

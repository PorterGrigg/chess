package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.GameDAO;

public class ClearService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public void clearAll() {
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();
    }
}

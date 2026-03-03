package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public void register(String username, String password, String email) {

    }
}

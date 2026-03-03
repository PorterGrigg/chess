package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import requests.RegisterRequest;
import results.RegisterResult;

import java.util.UUID;


public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) {
        String username = request.username();

        //find if username already exists in userdata
        UserData user = userDAO.findUser(username);

        //if username does exist then throw error
        if(user != null){
            return null; //throw error here
        }

        //else:add the user to user data
        userDAO.create(new UserData(username, request.password(), request.email())); //decided to creat the Data models here because service has full control and info

        //create authData
        String authToken = createAuthToken();
        authDAO.create(new AuthData(authToken, username));

        //create and return register result
        return new RegisterResult(username, authToken);
    }

    private String createAuthToken(){
        return UUID.randomUUID().toString();
    }
}

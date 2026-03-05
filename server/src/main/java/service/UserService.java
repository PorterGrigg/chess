package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import requests.*;
import results.*;

import java.util.UUID;


public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) throws AlreadyTakenException{
        String username = request.username();

        //find if username already exists in userdata
        UserData user = userDAO.findUser(username);

        //if username does exist then throw error
        if(user != null){
            throw new AlreadyTakenException("");
        }

        //else:add the user to user data
        userDAO.create(new UserData(username, request.password(), request.email()));
        //decided to creat the Data models here because service has full control and info

        //create authData
        String authToken = createAuthToken();
        authDAO.create(new AuthData(authToken, username));

        //create and return register result
        return new RegisterResult(username, authToken);
    }

    private String createAuthToken(){
        return UUID.randomUUID().toString();
    }

    public LoginResult login(LoginRequest request) throws UnauthorizedUserException{
        String username = request.username();
        String password = request.password();

        //find if username already exists in userdata
        UserData user = userDAO.findUser(username);

        //if username does not exist then throw error
        if(user == null){
            throw new UnauthorizedUserException("Error: Unauthorized");
        }

        //check that the given password is correct
        if(!user.password().equals(password)){
            throw new UnauthorizedUserException("Error: Unauthorized");
        }

        //create authData
        String authToken = createAuthToken();
        authDAO.create(new AuthData(authToken, username));

        //create and return register result
        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(LogoutRequest request) throws UnauthorizedUserException{
        String authToken = request.authToken();

        //find if  user authorized
        AuthData authorization = authDAO.findAuth(authToken);

        //if username does not exist then throw error
        if(authorization == null){
            throw new UnauthorizedUserException("Error: Unauthorized");
        }

        //remove authData
        authDAO.deleteAuth(authToken);

        //create and return register result
        return new LogoutResult();
    }


}

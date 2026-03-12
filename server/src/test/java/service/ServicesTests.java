package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

import org.junit.jupiter.api.DisplayName; //I was trying to match the provided chesssboard tests
import org.junit.jupiter.api.Test;
import model.AuthData;
import model.GameData;
import model.UserData;

import requests.*;
import results.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServicesTests {

    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;

    private ClearService clearService;
    private UserService userService;
    private GameService gameService;



    @Test
    @DisplayName("Clear Data Positive")
    public void clearAllPositiveTest() throws DataAccessException {
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data toDOA
        authDAO.create(new AuthData("auth1", "porker"));
        userDAO.create(new UserData("porker", "password", "porker@byu.edu"));
        gameDAO.create(new GameData(10, "porker", "piggy", "game1", null));

        clearService = new ClearService(authDAO, userDAO, gameDAO);

        //here i want to see if data exists before clearing
        List<AuthData> authBefore = authDAO.readAll();
        List<UserData> usersBefore = userDAO.readAll();
        List<GameData> gamesBefore = gameDAO.readAll();
        assertFalse(authBefore.isEmpty());
        assertFalse(usersBefore.isEmpty());
        assertFalse(gamesBefore.isEmpty());

        //service function
        clearService.clearAll();

        //assert that the service has cleared all the memory
        assertTrue(authDAO.readAll().isEmpty());
        assertTrue(userDAO.readAll().isEmpty());
        assertTrue(gameDAO.readAll().isEmpty());
    }

    @Test
    @DisplayName("Register Positive")
    public void registerPositiveTest() throws DataAccessException{
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data toDOA
        authDAO.create(new AuthData("auth1", "porker"));
        userDAO.create(new UserData("porker", "password", "porker@byu.edu"));

        userService = new UserService(authDAO, userDAO);

        //create new register request
        RegisterRequest request = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");

        RegisterResult result = userService.register(request);

        assertNotNull(result);

        //assert that the user has been added to userData and also authData and that the fields are correct
        UserData storedUser = userDAO.findUser("Piggy");
        assertNotNull(storedUser);
        assertEquals("Piggy", storedUser.username());
        assertEquals("hotwife1", storedUser.password());
        assertEquals("mrspiggy@byu.edu", storedUser.email());

        AuthData storedAuth = authDAO.findAuth(result.authToken());
        assertNotNull(storedAuth);
        assertEquals("Piggy", storedAuth.username());
        //can't check authtoken because don't know what it will be (random)
    }

    @Test
    @DisplayName("Register Negative")
    public void registerNegativeTest() throws DataAccessException{
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data toDOA
        authDAO.create(new AuthData("auth1", "porker"));
        userDAO.create(new UserData("Porker", "password", "porker@byu.edu"));

        userService = new UserService(authDAO, userDAO);

        //create new register request
        RegisterRequest request = new RegisterRequest("Porker", "password", "porker@byu.edu");

        //RegisterResult result = userService.register(request);

        //assert that the register result failed
        //assertNull(result); //come back and fix later when add in the exceptions
        assertThrows(AlreadyTakenException.class, () -> userService.register(request)); //using lambda function inside of this assertThrows

    }

    @Test
    @DisplayName("Login Negative")
    public void loginNegativeTest() {
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data toDOA
        authDAO.create(new AuthData("auth1", "porker"));
        userDAO.create(new UserData("Kermit", "password", "porker@byu.edu"));

        userService = new UserService(authDAO, userDAO);

        //create new login request
        //create new login request
        LoginRequest request = new LoginRequest("Kermit", "pass");

        //assert throws error
        assertThrows(UnauthorizedUserException.class, () -> userService.login(request));
    }

    @Test
    @DisplayName("Login Positive")
    public void loginPositiveTest() throws DataAccessException{
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data toDOA
        authDAO.create(new AuthData("auth1", "porker"));
        userDAO.create(new UserData("Kermit", "password", "porker@byu.edu"));

        userService = new UserService(authDAO, userDAO);

        //create new login request
        LoginRequest request = new LoginRequest("Kermit", "password");

        LoginResult result = userService.login(request);

        //assert authToken generted
        assertNotNull(result.authToken());

    }

    @Test
    @DisplayName("Logout Positive")
    public void logoutPositiveTest() throws DataAccessException{
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data toDOA
        authDAO.create(new AuthData("auth1", "porker"));
        userDAO.create(new UserData("Kermit", "password", "porker@byu.edu"));

        userService = new UserService(authDAO, userDAO);

        //create new request
        LogoutRequest request = new LogoutRequest("auth1");

        LogoutResult result = userService.logout(request);

        //assert result is given
        LogoutResult expectedResult = new LogoutResult();
        assertEquals(expectedResult, result);
        //assertTrue(expectedResult.equals(result));

    }

    @Test
    @DisplayName("Logout Negative")
    public void logoutNegativeTest() {
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data toDOA
        authDAO.create(new AuthData("auth1", "porker"));
        userDAO.create(new UserData("Kermit", "password", "porker@byu.edu"));

        userService = new UserService(authDAO, userDAO);


        //create new request
        LogoutRequest request = new LogoutRequest("auth2");

        //assert throws error
        assertThrows(UnauthorizedUserException.class, () -> userService.logout(request));
    }

    @Test
    @DisplayName("Create Positive")
    public void createPositiveTest() throws DataAccessException{
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data to DOA
        authDAO.create(new AuthData("auth1", "porker"));
        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));

        gameService = new GameService(authDAO, gameDAO);

        //create new request
        CreateRequest request = new CreateRequest("auth1", "Slither.io");

        gameService.create(request);

        //find the gamdata size
        ArrayList<GameData> games = gameDAO.readAll();
        assertEquals(2, games.size());

    }

    @Test
    @DisplayName("Create Negative")
    public void createNegativeTest() {
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data to DOA
        authDAO.create(new AuthData("auth1", "porker"));
        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));

        gameService = new GameService(authDAO, gameDAO);

        //create new request
        CreateRequest request = new CreateRequest("auth2", "Slither.io"); //invalid Auth

        assertThrows(UnauthorizedUserException.class, ()->gameService.create(request));

        //find the gameData size
        ArrayList<GameData> games = gameDAO.readAll();
        assertEquals(1, games.size());
    }

    @Test
    @DisplayName("Join Positive")
    public void joinPositiveTest() throws DataAccessException{
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data to DOA
        authDAO.create(new AuthData("auth1", "Porker"));
        gameDAO.create(new GameData(123, "PeterParker",null, "DoodleJump", new ChessGame()));

        gameService = new GameService(authDAO, gameDAO);

        //create new request
        JoinRequest request = new JoinRequest("auth1", ChessGame.TeamColor.BLACK, 123);

        gameService.join(request);

        //find the gamdata
        GameData game = gameDAO.findGame(123);
        assertEquals("Porker", game.blackUsername());
    }

    @Test
    @DisplayName("Join Negative")
    public void joinNegativeTest() {
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data to DOA
        authDAO.create(new AuthData("auth1", "porker"));
        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));

        gameService = new GameService(authDAO, gameDAO);

        //create new request
        JoinRequest request = new JoinRequest("auth1", ChessGame.TeamColor.WHITE, 123);

        //JoinResult result = gameService.join(request);

        assertThrows(AlreadyTakenException.class, ()->gameService.join(request));
    }

    @Test
    @DisplayName("List Positive")
    public void listPositiveTest() throws DataAccessException{
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data to DOA
        authDAO.create(new AuthData("auth1", "Porker"));
        gameDAO.create(new GameData(123, "PeterParker",null, "DoodleJump", new ChessGame()));

        gameService = new GameService(authDAO, gameDAO);

        //create new request
        ListRequest request = new ListRequest("auth1");

        ListResult result = gameService.list(request);

        //Assert size is 1
        assertEquals(1, result.games().size());

        gameDAO.create(new GameData(124, "Joker","Nokia", "RegShow", new ChessGame()));
        gameDAO.create(new GameData(125, "JigglyPuff","FlynnRider", "Hearts", new ChessGame()));

        //After adding make sure size increases
        ListResult newResult = gameService.list(request);
        assertEquals(3, newResult.games().size());
    }

    @Test
    @DisplayName("List Negative")
    public void listNegativeTest() {
        //initialize DAO
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        //add data to DOA
        authDAO.create(new AuthData("auth1", "porker"));
        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));

        gameService = new GameService(authDAO, gameDAO);

        //create new request
        ListRequest request = new ListRequest("auth2");

        assertThrows(UnauthorizedUserException.class, ()->gameService.list(request));
    }
}

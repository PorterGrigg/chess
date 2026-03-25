package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import requests.*;
import results.*;
import server.Server;
import service.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    private static int port;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
     void wipeApplication() throws ResponseException {
        serverFacade.clearAll(); //My tests were not passing because the SQL memory was persisting through tests
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }



    @Test
    @DisplayName("Clear Data Positive")
    public void clearAllPositiveTest() throws ResponseException {

        //add data to the databases

        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);
        CreateRequest requestGame = new CreateRequest(resultLogin.authToken(), "Piggy_Game");
        serverFacade.createGame(requestGame);

        //check that there is data present
        ListRequest requestList = new ListRequest(resultLogin.authToken());
        ListResult gameBefore = serverFacade.listGames(requestList);

        assertEquals(1, gameBefore.games().size());

        //clrea all data
        serverFacade.clearAll();

        //aerte that there is no maore data
        assertThrows(ResponseException.class, ()-> serverFacade.listGames(requestList));
        //will throw exception because AuthToken no longer exists in database

        requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        requestAuth = new LoginRequest("Piggy", "hotwife1");
        resultLogin = serverFacade.loginUser(requestAuth);

        //check that there is no game data present
        ListRequest requestListAgain = new ListRequest(resultLogin.authToken());
        ListResult gamesAfter = serverFacade.listGames(requestListAgain);

        assertEquals(0, gamesAfter.games().size());
    }

    @Test
    @DisplayName("RegisterUser Positive")
    public void registerPositiveTest() throws ResponseException{

        //create new register request
        RegisterRequest request = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");

        RegisterResult result = serverFacade.registerUser(request);

        assertNotNull(result);
        assertEquals("Piggy", result.username());
    }

    @Test
    @DisplayName("RegisterUser Negative")
    public void registerNegativeTest() throws ResponseException{

        //create new register request
        RegisterRequest request = new RegisterRequest("Porker", "password", "porker@byu.edu");

        serverFacade.registerUser((request)); //this way the username is already taken
        assertThrows(ResponseException.class, () -> serverFacade.registerUser(request));

    }

    @Test
    @DisplayName("Login Positive")
    public void loginPositiveTest() throws ResponseException{

        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        assertNotNull(resultLogin.authToken());

    }

//
//    @Test
//    @DisplayName("Login Negative")
//    public void loginNegativeTest() {
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        userService = new UserService(authDAO, userDAO);
//
//        //add data toDOA
//        authDAO.create(new AuthData("auth1", "porker"));
//        userDAO.create(new UserData("Kermit", userService.createPasswordHash("password"), "porker@byu.edu"));
//
//
//
//        //create new login request
//        //create new login request
//        LoginRequest request = new LoginRequest("Kermit", "pass");
//
//        //assert throws error
//        assertThrows(UnauthorizedUserException.class, () -> userService.login(request));
//    }
//
//
//    @Test
//    @DisplayName("Logout Positive")
//    public void logoutPositiveTest() throws DataAccessException{
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        userService = new UserService(authDAO, userDAO);
//
//        //add data toDOA
//        authDAO.create(new AuthData("auth1", "porker"));
//        userDAO.create(new UserData("Kermit", userService.createPasswordHash("password"), "porker@byu.edu"));
//
//        //create new request
//        LogoutRequest request = new LogoutRequest("auth1");
//
//        LogoutResult result = userService.logout(request);
//
//        //assert result is given
//        LogoutResult expectedResult = new LogoutResult();
//        assertEquals(expectedResult, result);
//        //assertTrue(expectedResult.equals(result));
//
//    }
//
//    @Test
//    @DisplayName("Logout Negative")
//    public void logoutNegativeTest() {
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        //add data toDOA
//        authDAO.create(new AuthData("auth1", "porker"));
//        userDAO.create(new UserData("Kermit", "password", "porker@byu.edu"));
//
//        userService = new UserService(authDAO, userDAO);
//
//
//        //create new request
//        LogoutRequest request = new LogoutRequest("auth2");
//
//        //assert throws error
//        assertThrows(UnauthorizedUserException.class, () -> userService.logout(request));
//    }
//
//    @Test
//    @DisplayName("Create Positive")
//    public void createPositiveTest() throws DataAccessException{
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        //add data to DOA
//        authDAO.create(new AuthData("auth1", "porker"));
//        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));
//
//        gameService = new GameService(authDAO, gameDAO);
//
//        //create new request
//        CreateRequest request = new CreateRequest("auth1", "Slither.io");
//
//        gameService.create(request);
//
//        //find the gamdata size
//        ArrayList<GameData> games = gameDAO.readAll();
//        assertEquals(2, games.size());
//
//    }
//
//    @Test
//    @DisplayName("Create Negative")
//    public void createNegativeTest() {
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        //add data to DOA
//        authDAO.create(new AuthData("auth1", "porker"));
//        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));
//
//        gameService = new GameService(authDAO, gameDAO);
//
//        //create new request
//        CreateRequest request = new CreateRequest("auth2", "Slither.io"); //invalid Auth
//
//        assertThrows(UnauthorizedUserException.class, ()->gameService.create(request));
//
//        //find the gameData size
//        ArrayList<GameData> games = gameDAO.readAll();
//        assertEquals(1, games.size());
//    }
//
//    @Test
//    @DisplayName("Join Positive")
//    public void joinPositiveTest() throws DataAccessException{
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        //add data to DOA
//        authDAO.create(new AuthData("auth1", "Porker"));
//        gameDAO.create(new GameData(123, "PeterParker",null, "DoodleJump", new ChessGame()));
//
//        gameService = new GameService(authDAO, gameDAO);
//
//        //create new request
//        JoinRequest request = new JoinRequest("auth1", ChessGame.TeamColor.BLACK, 123);
//
//        gameService.join(request);
//
//        //find the gamdata
//        GameData game = gameDAO.findGame(123);
//        assertEquals("Porker", game.blackUsername());
//    }
//
//    @Test
//    @DisplayName("Join Negative")
//    public void joinNegativeTest() {
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        //add data to DOA
//        authDAO.create(new AuthData("auth1", "porker"));
//        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));
//
//        gameService = new GameService(authDAO, gameDAO);
//
//        //create new request
//        JoinRequest request = new JoinRequest("auth1", ChessGame.TeamColor.WHITE, 123);
//
//        //JoinResult result = gameService.join(request);
//
//        assertThrows(AlreadyTakenException.class, ()->gameService.join(request));
//    }
//
//    @Test
//    @DisplayName("List Positive")
//    public void listPositiveTest() throws DataAccessException{
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        //add data to DOA
//        authDAO.create(new AuthData("auth1", "Porker"));
//        gameDAO.create(new GameData(123, "PeterParker",null, "DoodleJump", new ChessGame()));
//
//        gameService = new GameService(authDAO, gameDAO);
//
//        //create new request
//        ListRequest request = new ListRequest("auth1");
//
//        ListResult result = gameService.list(request);
//
//        //Assert size is 1
//        assertEquals(1, result.games().size());
//
//        gameDAO.create(new GameData(124, "Joker","Nokia", "RegShow", new ChessGame()));
//        gameDAO.create(new GameData(125, "JigglyPuff","FlynnRider", "Hearts", new ChessGame()));
//
//        //After adding make sure size increases
//        ListResult newResult = gameService.list(request);
//        assertEquals(3, newResult.games().size());
//    }
//
//    @Test
//    @DisplayName("List Negative")
//    public void listNegativeTest() {
//        //initialize DAO
//        authDAO = new MemoryAuthDAO();
//        userDAO = new MemoryUserDAO();
//        gameDAO = new MemoryGameDAO();
//
//        //add data to DOA
//        authDAO.create(new AuthData("auth1", "porker"));
//        gameDAO.create(new GameData(123, "PeterParker", "Batman", "DoodleJump", new ChessGame()));
//
//        gameService = new GameService(authDAO, gameDAO);
//
//        //create new request
//        ListRequest request = new ListRequest("auth2");
//
//        assertThrows(UnauthorizedUserException.class, ()->gameService.list(request));
//    }

}

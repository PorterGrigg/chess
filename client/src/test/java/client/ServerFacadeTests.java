package client;


import chess.ChessGame;
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


    @Test
    @DisplayName("Login Negative")
    public void loginNegativeTest() throws ResponseException{

        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);


        LoginRequest request = new LoginRequest("Kermit", "hotwife1");

        //assert throws error
        assertThrows(ResponseException.class, () -> serverFacade.loginUser(request));
    }


    @Test
    @DisplayName("Logout Positive")
    public void logoutPositiveTest() throws ResponseException{

        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        LogoutRequest request = new LogoutRequest(resultLogin.authToken());
        LogoutResult result = serverFacade.logoutUser(request);

        //assert result is given
        LogoutResult expectedResult = new LogoutResult();
        assertEquals(expectedResult, result);


    }

    @Test
    @DisplayName("Logout Negative")
    public void logoutNegativeTest() throws ResponseException{
        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        LogoutRequest request = new LogoutRequest("RandoAuth");

        //assert throws error
        assertThrows(ResponseException.class, () -> serverFacade.logoutUser(request));
    }

    @Test
    @DisplayName("Create Positive")
    public void createPositiveTest() throws ResponseException{
        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        //create new request
        CreateRequest request = new CreateRequest(resultLogin.authToken(), "Slither.io");

        serverFacade.createGame(request);

        //find the gamdata size
        ListRequest requestList = new ListRequest(resultLogin.authToken());
        ListResult gamesAfter = serverFacade.listGames(requestList);
        assertEquals(1, gamesAfter.games().size());

    }

    @Test
    @DisplayName("Create Negative")
    public void createNegativeTest() throws ResponseException{
        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        //create new request
        CreateRequest request = new CreateRequest(resultLogin.authToken(), null);
        //game name cannot be null
        assertThrows(ResponseException.class, ()-> serverFacade.createGame(request));

    }

    @Test
    @DisplayName("Join Positive")
    public void joinPositiveTest() throws ResponseException{
        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        //create new game
        CreateRequest requestCreate = new CreateRequest(resultLogin.authToken(), "Slither.io");
        CreateResult createdGame = serverFacade.createGame(requestCreate);

        //create new request
        JoinRequest request = new JoinRequest(resultLogin.authToken(), ChessGame.TeamColor.BLACK, createdGame.gameID());

        JoinResult result = serverFacade.joinGame(request);

        //assert the result is as expected
        JoinResult expectedResult = new JoinResult();
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Join Negative")
    public void joinNegativeTest() throws ResponseException{
        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        //create new game
        CreateRequest requestCreate = new CreateRequest(resultLogin.authToken(), "Slither.io");
        CreateResult createdGame = serverFacade.createGame(requestCreate);

        //create new request
        JoinRequest request = new JoinRequest(resultLogin.authToken(), ChessGame.TeamColor.BLACK, 123);

        //cannot join a game where the ID does not match any
        assertThrows(ResponseException.class, ()-> serverFacade.joinGame(request));
    }

    @Test
    @DisplayName("List Positive")
    public void listPositiveTest() throws ResponseException{
        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        //create new games
        CreateRequest request = new CreateRequest(resultLogin.authToken(), "Slither.io");
        serverFacade.createGame(request);
        request = new CreateRequest(resultLogin.authToken(), "BestGame");
        serverFacade.createGame(request);
        request = new CreateRequest(resultLogin.authToken(), "Potato");
        serverFacade.createGame(request);

        //assert size of list
        ListRequest requestList = new ListRequest(resultLogin.authToken());
        ListResult gamesAfter = serverFacade.listGames(requestList);
        assertEquals(3, gamesAfter.games().size());
    }

    @Test
    @DisplayName("List Negative")
    public void listNegativeTest() throws ResponseException{
        //register and login
        RegisterRequest requestUser = new RegisterRequest("Piggy", "hotwife1", "mrspiggy@byu.edu");
        serverFacade.registerUser(requestUser);
        LoginRequest requestAuth = new LoginRequest("Piggy", "hotwife1");
        LoginResult resultLogin = serverFacade.loginUser(requestAuth);

        //create new games
        CreateRequest request = new CreateRequest(resultLogin.authToken(), "Slither.io");
        serverFacade.createGame(request);
        request = new CreateRequest(resultLogin.authToken(), "BestGame");
        serverFacade.createGame(request);
        request = new CreateRequest(resultLogin.authToken(), "Potato");
        serverFacade.createGame(request);

        //unauthorized user
        ListRequest requestList = new ListRequest("Random_Auth");
        assertThrows(ResponseException.class, ()-> serverFacade.listGames(requestList));
    }

}

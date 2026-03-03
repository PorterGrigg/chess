package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;

import org.junit.jupiter.api.DisplayName; //I was trying to match the provided chesssboard tests
import org.junit.jupiter.api.Test;
import model.AuthData;
import model.GameData;
import model.UserData;

import requests.RegisterRequest;
import results.RegisterResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServicesTests {

    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;

    private ClearService clearService;
    private UserService userService;



    @Test
    @DisplayName("Clear Data Positive")
    public void clearAllPositiveTest() {
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
    public void registerPositiveTest() {
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
    public void registerNegativeTest() {
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
}

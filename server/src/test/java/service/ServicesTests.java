package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;

import org.junit.jupiter.api.DisplayName; //I was trying to match the provided chesssboard tests
import org.junit.jupiter.api.Test;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServicesTests {

    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;

    private ClearService clearService;

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
}

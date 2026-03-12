
package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName; //I was trying to match the provided chesssboard tests
import org.junit.jupiter.api.Test;
import model.AuthData;
import model.GameData;
import model.UserData;

import requests.*;
import results.*;
import service.ClearService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;
    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();

        // clear all tables
        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
        clearService.clearAll();
    }

    //Auth Tests
    @Test
    @DisplayName("AuthDAO Clear Positive")
    public void clearAuthPositiveTest() throws DataAccessException {
        authDAO.create(new AuthData("auth1", "porker"));

        List<AuthData> authBefore = authDAO.readAll();
        assertFalse(authBefore.isEmpty());

        authDAO.clear();

        assertTrue(authDAO.readAll().isEmpty());
    }

    //User Tests
    @Test
    @DisplayName("UserDAO Clear Positive")
    public void clearUserPositiveTest() throws DataAccessException {
        userDAO.create(new UserData("porker", "password", "porker@byu.edu"));

        List<UserData> userBefore = userDAO.readAll();
        assertFalse(userBefore.isEmpty());

        userDAO.clear();

        assertTrue(userDAO.readAll().isEmpty());
    }

    //Game Tests
    @Test
    @DisplayName("GameDAO Clear Positive")
    public void clearGamePositiveTest() throws DataAccessException {
        gameDAO.create(new GameData(10, "porker", "piggy",
                "game1", null));

        List<GameData> gameBefore = gameDAO.readAll();
        assertFalse(gameBefore.isEmpty());

        gameDAO.clear();

        assertTrue(gameDAO.readAll().isEmpty());
    }

}

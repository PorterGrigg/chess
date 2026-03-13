
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
import service.AlreadyTakenException;
import service.ClearService;

import java.sql.Connection;
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

    @Test
    @DisplayName("AuthDAO Create Positive")
    public void createAuthPositiveTest() throws DataAccessException {
        ArrayList<AuthData> beforeList = authDAO.readAll();

        assertTrue(beforeList.isEmpty());

        authDAO.create(new AuthData("auth1", "porker"));

        List<AuthData> afterList = authDAO.readAll();
        assertFalse(afterList.isEmpty());
    }

    @Test
    @DisplayName("AuthDAO Create Negative")
    public void createAuthNegativeTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.create(new AuthData(null, "porker")));
    } //negative because fields cannot be negative

    @Test
    @DisplayName("AuthDAO Find Positive")
    public void findAuthPositiveTest() throws DataAccessException {

        authDAO.create(new AuthData("auth1", "porker"));

        AuthData foundAuth = authDAO.findAuth("auth1");

        assertEquals("porker", foundAuth.username());
    }

    @Test
    @DisplayName("AuthDAO Find Negative")
    public void findAuthNegativeTest() throws DataAccessException {

        authDAO.create(new AuthData("auth1", "porker"));

        AuthData foundAuth = authDAO.findAuth("none");

        assertNull(foundAuth); //returns null if not found
    }

    @Test
    @DisplayName("AuthDAO readAll Positive")
    public void readAllAuthPositiveTest() throws DataAccessException {
        ArrayList<AuthData> beforeList = authDAO.readAll();

        assertTrue(beforeList.isEmpty());

        authDAO.create(new AuthData("auth1", "porker"));

        List<AuthData> afterList = authDAO.readAll();
        assertFalse(afterList.isEmpty());

        authDAO.create(new AuthData("auth2", "peter"));
        authDAO.create(new AuthData("auth3", "kip"));

        List<AuthData> finalList = authDAO.readAll();
        assertEquals(3, finalList.size());
    }

    @Test
    @DisplayName("AuthDAO readAll Negative")
    public void readAllAuthNegativeTest() throws DataAccessException {

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("DROP TABLE AuthData");
            statement.executeUpdate();
        } catch(Exception e){
            throw new DataAccessException("Yeet");
        }

        //negative test will fail because the table has been deleted
        assertThrows(DataAccessException.class, () -> authDAO.readAll());
    }

    @Test
    @DisplayName("AuthDAO delete Positive")
    public void deleteAuthPositiveTest() throws DataAccessException {
        authDAO.create(new AuthData("auth1", "porker"));

        assertNotNull(authDAO.findAuth("auth1"));

        authDAO.deleteAuth("auth1");

        assertNull(authDAO.findAuth("auth1"));
    }

    @Test
    @DisplayName("AuthDAO delete Negative")
    public void deleteAuthNegativeTest() throws DataAccessException {
        authDAO.create(new AuthData("auth1", "porker"));

        assertNotNull(authDAO.findAuth("auth1"));

        //unclear if delete method is supposed to
        assertThrows(DataAccessException.class, () ->authDAO.deleteAuth("auth2"));
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

    @Test
    @DisplayName("UserDAO Create Positive")
    public void createUserPositiveTest() throws DataAccessException {
        ArrayList<UserData> beforeList = userDAO.readAll();

        assertTrue(beforeList.isEmpty());

        userDAO.create(new UserData("porker", "hoboi", "porker@byu.edu"));

        List<UserData> afterList = userDAO.readAll();
        assertFalse(afterList.isEmpty());
    }

    @Test
    @DisplayName("UserDAO Create Negative")
    public void createUserNegativeTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userDAO.create(new UserData(null, "hoboi", "porker@byu.edu")));
    } //negative because fields cannot be null

    @Test
    @DisplayName("UserDAO Find Positive")
    public void findUserPositiveTest() throws DataAccessException {

        userDAO.create(new UserData("porker", "hoboi", "porker@byu.edu"));

        UserData foundUser = userDAO.findUser("porker");

        assertEquals("porker@byu.edu", foundUser.email());
    }

    @Test
    @DisplayName("UserDAO Find Negative")
    public void findUserNegativeTest() throws DataAccessException {

        userDAO.create(new UserData("porker", "hoboi", "porker@byu.edu"));

        UserData foundUser = userDAO.findUser("none");

        assertNull(foundUser); //returns null if not found
    }

    @Test
    @DisplayName("UserDAO readAll Positive")
    public void readAllUserPositiveTest() throws DataAccessException {
        ArrayList<UserData> beforeList = userDAO.readAll();

        assertTrue(beforeList.isEmpty());

        userDAO.create(new UserData("porker", "hoboi", "porker@byu.edu"));

        List<UserData> afterList = userDAO.readAll();
        assertFalse(afterList.isEmpty());

        userDAO.create(new UserData("peter", "hoboi", "porker@byu.edu"));
        userDAO.create(new UserData("parker", "hoboi", "porker@byu.edu"));

        List<UserData> finalList = userDAO.readAll();
        assertEquals(3, finalList.size());
    }

    @Test
    @DisplayName("UserDAO readAll Negative")
    public void readAllUserNegativeTest() throws DataAccessException {

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("DROP TABLE UserData");
            statement.executeUpdate();
        } catch(Exception e){
            throw new DataAccessException("Yeet");
        }

        //negative test will fail because the table has been deleted
        assertThrows(DataAccessException.class, () -> userDAO.readAll());
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

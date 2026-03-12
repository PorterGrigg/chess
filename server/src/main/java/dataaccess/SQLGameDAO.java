package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.sql.*;
import java.util.ArrayList;

public class SQLGameDAO extends BaseSQLDAO implements GameDAO {
    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public int create(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO GameData (whiteUsername, blackUsername, gameName, game) VALUES ( ?, ?, ?, ?)";

        //convert game to json
        String jsonGame = new Gson().toJson(gameData.game());

        int newGameID = executeUpdate(statement, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), jsonGame);
        return newGameID;
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM GameData WHERE gameID =?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return translateResults(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("unable to read data");
        }
        return null;
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {

        GameData currGame = findGame(gameID);
        String whiteUsername;
        String blackUsername;
        if (playerColor == ChessGame.TeamColor.BLACK) {
            whiteUsername = currGame.whiteUsername();
            blackUsername = username;
        } else {
            blackUsername = currGame.blackUsername();
            whiteUsername = username;
        }

        //convert game to json
        String jsonGame = new Gson().toJson(currGame.game());

        String gameName = currGame.gameName();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE GameData " +
                    "SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? " +
                    "WHERE gameID =?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, whiteUsername);
                ps.setString(2, blackUsername);
                ps.setString(3, gameName);
                ps.setString(4, jsonGame);
                ps.setInt(5, gameID);

                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("unable to read data");
        }
    }

    @Override
    public ArrayList<GameData> readAll() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM GameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(translateResults(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("unable to read data");
        }
        return result;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE GameData";
        executeUpdate(statement);
    }

    private GameData translateResults(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var jsonGame = rs.getString("game");
        chess.ChessGame game = new Gson().fromJson(jsonGame, ChessGame.class); //translate it back from json to a chesss game
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }
}

package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.sql.*;
import java.util.ArrayList;

public class SQLGameDAO extends BaseSQLDAO implements GameDAO{
    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public int create(GameData gameData) throws DataAccessException{
        var statement = "INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        int newGameID = executeUpdate(statement, gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        return newGameID;
    }

    @Override
    public GameData findGame(int gameID) throws DataAccessException{
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

//    @Override
//    public void updateGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException{
//        try (Connection conn = DatabaseManager.getConnection()) {
//            var statement = "UPDATE gameID, whiteUsername, blackUsername, gameName, game FROM GameData WHERE gameID =?";
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
//                ps.setInt(1, gameID);
//                try (ResultSet rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return translateResults(rs);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new DataAccessException("unable to read data");
//        }
//        return null;
//    }

    @Override
    public ArrayList<GameData> readAll() throws DataAccessException{
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
    public void clear() throws DataAccessException{
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
//gameID, whiteUsername, blackUsername, gameName, game

//        String json = new Gson().toJson(pet);
//        int id = executeUpdate(statement, pet.name(), pet.type(), json);
//      return new Pet(id, pet.name(), pet.type());
//    }
//    public Pet getPet(int id) throws ResponseException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet WHERE id=?";
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
//                ps.setInt(1, id);
//                try (ResultSet rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return readPet(rs);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return null;
//    }
//
//    public PetList listPets() throws ResponseException {
//        var result = new PetList();
//        try (Connection conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT id, json FROM pet";
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
//                try (ResultSet rs = ps.executeQuery()) {
//                    while (rs.next()) {
//                        result.add(readPet(rs));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to read data: %s", e.getMessage()));
//        }
//        return result;
//    }
//
//    public void deletePet(Integer id) throws ResponseException {
//        var statement = "DELETE FROM pet WHERE id=?";
//        executeUpdate(statement, id);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var statement = "TRUNCATE pet";
//        executeUpdate(statement);
//    }
//
//    private Pet readPet(ResultSet rs) throws SQLException {
//        var id = rs.getInt("id");
//        var json = rs.getString("json");
//        Pet pet = new Gson().fromJson(json, Pet.class);
//        return pet.setId(id);
//    }
//
//    private int executeUpdate(String statement, Object... params) throws ResponseException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                for (int i = 0; i < params.length; i++) {
//                    Object param = params[i];
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
//                    else if (param == null) ps.setNull(i + 1, NULL);
//                }
//                ps.executeUpdate();
//
//                ResultSet rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//
//                return 0;
//            }
//        } catch (SQLException e) {
//            throw new ResponseException(ResponseException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
//        }
//    }
}

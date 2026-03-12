package dataaccess;

import model.*;

import java.sql.*;
import java.util.ArrayList;

public class SQLUserDAO extends BaseSQLDAO implements UserDAO{
    public SQLUserDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void create(UserData userData) throws DataAccessException{
        var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData findUser(String username) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM UserData WHERE username =?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
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
    public ArrayList<UserData> readAll() throws DataAccessException{
        ArrayList<UserData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM UserData";
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
        var statement = "TRUNCATE UserData";
        executeUpdate(statement);
    }

    private UserData translateResults(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }


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

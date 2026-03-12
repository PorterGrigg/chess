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
}

package dataaccess;

import model.*;

import java.sql.*;
import java.util.ArrayList;

public class SQLAuthDAO extends BaseSQLDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void create(AuthData authData) throws DataAccessException{
        var statement = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData findAuth(String authToken) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM AuthData WHERE authToken =?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return translateResults(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to read data");
        }
        return null;
    }

    @Override
    public ArrayList<AuthData> readAll() throws DataAccessException{
        ArrayList<AuthData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM AuthData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(translateResults(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to read data");
        }
        return result;
    }

    @Override
    public void deleteAuth(String authToken) throws  DataAccessException{
        //check that deleting exists
        AuthData foundAuth = findAuth(authToken);
        if (foundAuth == null){
            throw new DataAccessException("Error: Data does not exist");
        }

        var statement = "DELETE FROM AuthData WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException{
        try{
            var statement = "TRUNCATE AuthData";
            executeUpdate(statement);
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to read data");
        }

    }

    private AuthData translateResults(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }
}

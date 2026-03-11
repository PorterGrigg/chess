package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseSQLDAO {
    public void executeUpdate(String statement, String... params) throws DataAccessException {
        //the String... params allows for a variable length of parameters and are stored in object to later iterate
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    String param = params[i];
                    ps.setString(i + 1, param); //will always be strings
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database");
        }
    }

    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        SchemaInitializer.createTables();
    }
}

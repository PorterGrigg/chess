package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class BaseSQLDAO {
    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        //the String... params allows for a variable length of parameters and are stored in object to later iterate
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0; //if no keys are generated then return 0
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to update database");
        }
    }

    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        SchemaInitializer.createTables();
    }
}

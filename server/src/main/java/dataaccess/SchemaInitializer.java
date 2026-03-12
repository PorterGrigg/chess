package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class SchemaInitializer {

    private static final String[] CREATE_AUTH_DATA_STATEMENT = {
            """
        CREATE TABLE IF NOT EXISTS  AuthData (
          `authToken` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL,
          PRIMARY KEY (`authToken`),
          INDEX(username)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    }; //index allows faster queries //Last line specifies the engine we use in
    // SQL, the set of characters, and the way characters are sorted

    private static final String[] CREATE_USER_DATA_STATEMENT = {
            """
        CREATE TABLE IF NOT EXISTS  UserData (
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          PRIMARY KEY (`username`),
          INDEX(password),
          INDEX(email)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    private static final String[] CREATE_GAME_DATA_STATEMENT = {
            """
        CREATE TABLE IF NOT EXISTS  GameData (
          `gameID` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256) NOT NULL,
          `game` TEXT DEFAULT NULL,
          PRIMARY KEY (`gameID`),
          INDEX(whiteUsername),
          INDEX(blackUsername),
          INDEX(gameName)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    public static void createTables() throws DataAccessException{
        try (
                Connection conn = DatabaseManager.getConnection()) {
            for (String statement : CREATE_AUTH_DATA_STATEMENT) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for (String statement : CREATE_USER_DATA_STATEMENT) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for (String statement : CREATE_GAME_DATA_STATEMENT) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (
                SQLException ex) {
            throw new DataAccessException("Unable to configure tables", ex);
        }
    }
}

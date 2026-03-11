package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class SchemaInitializer {

    private static final String[] createAuthDataStatement = {
            """
        CREATE TABLE IF NOT EXISTS  pet (
          `id` int NOT NULL AUTO_INCREMENT,
          `name` varchar(256) NOT NULL,
          `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(type),
          INDEX(name)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    private static final String[] createUserDataStatement = {
            """
        CREATE TABLE IF NOT EXISTS  pet (
          `id` int NOT NULL AUTO_INCREMENT,
          `name` varchar(256) NOT NULL,
          `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(type),
          INDEX(name)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    private static final String[] createGameDataStatement = {
            """
        CREATE TABLE IF NOT EXISTS  pet (
          `id` int NOT NULL AUTO_INCREMENT,
          `name` varchar(256) NOT NULL,
          `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(type),
          INDEX(name)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
    };

    public static void createTables() throws DataAccessException{
        try (
                Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createAuthDataStatement) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for (String statement : createUserDataStatement) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            for (String statement : createGameDataStatement) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (
                SQLException ex) {
            //throw new DataAccessException(DataAccessException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

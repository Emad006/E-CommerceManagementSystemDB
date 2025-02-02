package database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://[server-ip]/[database-name][?useSSL=false&allowPublicKeyRetrieval=true]";
    private static final String USER = "[username]";
    private static final String PASSWORD = "[password]";

    // Private constructor to prevent direct instantiation
    private DatabaseConnection() {}

    // Get a connection (New connection per request, proper cleanup expected in DAO)
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    // Close connection safely (DAO should call this if try-with-resources is not used)
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}

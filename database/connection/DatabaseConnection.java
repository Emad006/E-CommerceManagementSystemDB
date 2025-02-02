package database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://10.100.110.96:3306/ecms?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "xyrophyte";
    private static final String PASSWORD = "publicstaticvoidmain2006";

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
    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

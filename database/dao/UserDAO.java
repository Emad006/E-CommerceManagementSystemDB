package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import core.entities.User;
import core.entities.Admin;
import core.entities.Worker;
import core.entities.Customer;
import core.entities.SuperAdmin;
import database.connection.DatabaseConnection;

public class UserDAO {

    public UserDAO() {
    }

    // Get ID from email
    public int getUserID(String email) {
        String getUserIDQuery = "SELECT USER_ID FROM USERS WHERE EMAIL = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUserIDQuery)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getInt("USER_ID");
                } else {
                    return -1;
                }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Check if user already exist by ID
    public boolean userExists(int id) {
        String userExistsQuery = "SELECT * FROM USERS WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(userExistsQuery)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if user already exists by email
    public boolean userExists(String email) {
        return userExists(getUserID(email));
    }

    // Check if credentials are valid
    public boolean validCredentials(String email, String password) {
        String validCredentialsQuery = "SELECT * FROM USERS WHERE EMAIL = ? AND PASSWORD = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(validCredentialsQuery)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add user. Method overloading cause of two types of User (this one is for Admin)
    // TODO: Validate data in front-end
    public void addUser(String name, String email, String password, String role) {
        // Admin a = new Admin(name, email, password, role);

        String createUserQuery = "INSERT INTO USERS (NAME, EMAIL, PASSWORD, ROLE) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(createUserQuery)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add user. Method overloading cause of two types of User (this one is for Customer & Worker)
    // TODO: Validate data in front-end
    public void addUser(String name, String email, String password, String role, String gender, String contactNo, String address) {
        String createUserQuery = "INSERT INTO USERS (NAME, EMAIL, PASSWORD, ROLE) VALUES (?, ?, ?, ?)";
        String insertUserDetailQuery = "INSERT INTO USER_DETAIL (USER_ID, GENDER, CONTACT_NO, ADDR) VALUES (?, ?, ?, ?)";
        String createCartQuery = "INSERT INTO CART (USER_ID) VALUES (?)";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement userDetailStmt = null;
        PreparedStatement cartStmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into USERS table
            userStmt = conn.prepareStatement(createUserQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, name);
            userStmt.setString(2, email);
            userStmt.setString(3, password);
            userStmt.setString(4, role);
            int userAffectedRows = userStmt.executeUpdate();

            if (userAffectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // Get the auto-generated USER_ID
            generatedKeys = userStmt.getGeneratedKeys();
            int userID;
            if (generatedKeys.next()) {
                userID = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }

            // Insert into USER_DETAIL table
            userDetailStmt = conn.prepareStatement(insertUserDetailQuery);
            userDetailStmt.setInt(1, userID);
            userDetailStmt.setString(2, gender);
            userDetailStmt.setString(3, contactNo);
            userDetailStmt.setString(4, address);
            int userDetailAffectedRows = userDetailStmt.executeUpdate();

            if (userDetailAffectedRows == 0) {
                throw new SQLException("Creating user detail failed, no rows affected.");
            }

            // Insert into CART table if it's a Customer
            if (role.equalsIgnoreCase("Customer")) {
                cartStmt = conn.prepareStatement(createCartQuery);
                cartStmt.setInt(1, userID);
                int cartAffectedRows = cartStmt.executeUpdate();

                if (cartAffectedRows == 0) {
                    throw new SQLException("Creating cart failed, no rows affected.");
                }
            }

            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            e.printStackTrace();

            // Rollback the transaction if any exception occurs
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // Close all resources
            DatabaseConnection.closeQuietly(generatedKeys);
            DatabaseConnection.closeQuietly(cartStmt);
            DatabaseConnection.closeQuietly(userDetailStmt);
            DatabaseConnection.closeQuietly(userStmt);
            DatabaseConnection.closeQuietly(conn);
        }
    }
}

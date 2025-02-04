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
import interfaces.database.dao.IUserDAO;

public class UserDAO implements IUserDAO {

    public UserDAO() {
    }

    // Get ID from email
    public int getUserID(String email) {
        // System.out.println("✅ Executing UserDAO->getUserID() ✅");
        String getUserIDQuery = "SELECT USER_ID FROM USERS WHERE EMAIL = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(getUserIDQuery)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("USER_ID");
                } else {
                    return -1;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Check if user already exist by ID
    public boolean userExists(int id) {
        // System.out.println("✅ Executing UserDAO->userExists(int id) ✅");
        String userExistsQuery = "SELECT * FROM USERS WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(userExistsQuery)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if user already exists by email
    public boolean userExists(String email) {
        // System.out.println("✅ Executing UserDAO->userExists(String email) ✅");
        return userExists(getUserID(email));
    }

    // Check if credentials are valid
    public boolean validCredentials(String email, String password) {
        // System.out.println("✅ Executing UserDAO->validCredentials() ✅");
        String validCredentialsQuery = "SELECT * FROM USERS WHERE EMAIL = ? AND PWD = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(validCredentialsQuery)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add user. Method overloading cause of two types of User (this one is for
    // Admin)
    // TODO: Validate data in front-end
    public void addUser(String name, String email, String password, String role) {
        // System.out.println("✅ Executing UserDAO->addUser() (ADMIN) ✅");
        // Admin a = new Admin(name, email, password, role);

        String createUserQuery = "INSERT INTO USERS (NAME, EMAIL, PWD, ROLE) VALUES (?, ?, ?, ?)";

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

    // Add user. Method overloading cause of two types of User (this one is for
    // Customer & Worker)
    // TODO: Validate data in front-end
    public void addUser(String name, String email, String password, String role, String gender, String contactNo,
            String address) {
        // System.out.println("✅ Executing UserDAO->addUser() (CUSTOMER, WORKER) ✅");
        String createUserQuery = "INSERT INTO USERS (NAME, EMAIL, PWD, ROLE) VALUES (?, ?, ?, ?)";
        String insertUserDetailQuery = "INSERT INTO USER_DETAIL (USER_ID, GENDER, CONTACT_NO, ADDR) VALUES (?, ?, ?, ?)";
        String createCartQuery = "INSERT INTO CARTS (USER_ID) VALUES (?)";

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

    // Delete user by ID
    // TODO: Validate ID in front-end || Just check if the userExists
    public void deleteUser(int id) {
        // System.out.println("✅ Executing UserDAO->deleteUser(int id) ✅");
        String deleteUserQuery = "DELETE FROM USERS WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pr = conn.prepareStatement(deleteUserQuery)) {
            pr.setInt(1, id);
            pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete user by email
    public void deleteUser(String email) {
        // System.out.println("✅ Executing UserDAO->deleteUser(String email) ✅");
        deleteUser(getUserID(email));
    }

    // Search user by ID (return user object)
    // TODO: Catch NullPointerException in front-end and notify user that the user
    // does not exist.
    public User searchUser(int id) {
        // System.out.println("✅ Executing UserDAO->searchUser(int id) ✅");
        String searchUserQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        String searchUserDetailQuery = "SELECT * FROM USER_DETAIL WHERE USER_ID = ?";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement userDetailStmt = null;
        ResultSet userRs = null;
        ResultSet userDetailRs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Search in USERS table
            userStmt = conn.prepareStatement(searchUserQuery);
            userStmt.setInt(1, id);
            userRs = userStmt.executeQuery();

            if (userRs.next()) { // User found
                int userId = userRs.getInt("USER_ID");
                String name = userRs.getString("NAME");
                String email = userRs.getString("EMAIL");
                String password = userRs.getString("PWD");
                String role = userRs.getString("ROLE");

                // If user is SuperAdmin or Admin, return early
                if (role.equalsIgnoreCase("SuperAdmin")) {
                    return new SuperAdmin(userId, name, email, password, role);
                } else if (role.equalsIgnoreCase("Admin")) {
                    return new Admin(userId, name, email, password, role);
                }

                // If user is "Worker" or "Customer", get additional details
                userDetailStmt = conn.prepareStatement(searchUserDetailQuery);
                userDetailStmt.setInt(1, id);
                userDetailRs = userDetailStmt.executeQuery();

                if (userDetailRs.next()) {
                    String gender = userDetailRs.getString("GENDER");
                    String contactNo = userDetailRs.getString("CONTACT_NO");
                    String address = userDetailRs.getString("ADDR");

                    if (role.equalsIgnoreCase("Customer")) {
                        return new Customer(userId, name, email, password, role, gender, contactNo, address);
                    } else if (role.equalsIgnoreCase("Worker")) {
                        return new Worker(userId, name, email, password, role, gender, contactNo, address);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeQuietly(userDetailRs);
            DatabaseConnection.closeQuietly(userRs);
            DatabaseConnection.closeQuietly(userDetailStmt);
            DatabaseConnection.closeQuietly(userStmt);
            DatabaseConnection.closeQuietly(conn);
        }

        return null; // Return null if user not found
    }

    // Search user by email (return user object)
    public User searchUser(String email) {
        // System.out.println("✅ Executing UserDAO->searchUser(String email) ✅");
        return searchUser(getUserID(email));
    }

    // TODO: Catch NullPointerException in front-end and notify user that the user
    // does not exist.
    // Get user role by ID
    public String getUserRole(int id) {
        User u = searchUser(id);

        if (u != null) {
            return u.getRole();
        } else {
            return null;
        }
    }

    // Get user role by email
    public String getUserRole(String email) {
        return getUserRole(getUserID(email));
    }

    // Update user (Customer || Worker)
    public boolean updateUser(int id, String name, String email, String password, String role, String gender,
            String contactNo, String address) {
        // System.out.println("✅ Executing UserDAO->updateUser() (CUSTOMER, WORKER) ✅");
        String updateUserQuery = "UPDATE USERS SET NAME = ?, PWD = ?, ROLE = ? WHERE USER_ID = ?";
        String updateUserDetailQuery = "UPDATE USER_DETAIL SET GENDER = ?, CONTACT_NO = ?, ADDR = ? WHERE USER_ID = ?";

        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement userDetailStmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update USERS table
            userStmt = conn.prepareStatement(updateUserQuery);
            userStmt.setString(1, name);
            userStmt.setString(2, password);
            userStmt.setString(3, role);
            userStmt.setInt(4, id);
            int userAffectedRows = userStmt.executeUpdate();

            if (userAffectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            // Update USER_DETAIL table
            userDetailStmt = conn.prepareStatement(updateUserDetailQuery);
            userDetailStmt.setString(1, gender);
            userDetailStmt.setString(2, contactNo);
            userDetailStmt.setString(3, address);
            userDetailStmt.setInt(4, id);
            int userDetailAffectedRows = userDetailStmt.executeUpdate();

            if (userDetailAffectedRows == 0) {
                throw new SQLException("Updating user detail failed, no rows affected.");
            }

            conn.commit(); // Commit the transaction
            return true;
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

            return false;
        } finally {
            // Close all resources
            DatabaseConnection.closeQuietly(userDetailStmt);
            DatabaseConnection.closeQuietly(userStmt);
            DatabaseConnection.closeQuietly(conn);
        }
    }

    // Update user (Admin)
    public boolean updateUser(int id, String name, String email, String password, String role) {
        // System.out.println("✅ Executing UserDAO->updateUser() (ADMIN) ✅");
        String updateUserQuery = "UPDATE USERS SET NAME = ?, PWD = ?, ROLE = ? WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pr = conn.prepareStatement(updateUserQuery)) {
            pr.setString(1, name);
            pr.setString(2, password);
            pr.setString(3, role);
            pr.setInt(4, id);
            pr.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // // Return 2D array for displaying in a table
    // public String[][] getDataForTable() {
    // String[][] data = new String[userList.size()][6];

    // int i = 0;

    // for (User u : userList) {
    // data[i][0] = u.getName();
    // data[i][1] = u.getEmail();
    // data[i][2] = u.getRole();

    // if (u instanceof Customer) {
    // Customer c = (Customer) u;
    // data[i][3] = c.getGender();
    // data[i][4] = c.getContactNo();
    // data[i][5] = c.getAddress();
    // } else if(u instanceof Worker) {
    // Worker w = (Worker) u;
    // data[i][3] = w.getGender();
    // data[i][4] = w.getContactNo();
    // data[i][5] = w.getAddress();
    // } else {
    // data[i][3] = "N/A";
    // data[i][4] = "N/A";
    // data[i][5] = "N/A";
    // }
    // i++;
    // }
    // return data;
    // }

    // Fixed method to get data for table
    public String[][] getDataForTable() {
        String countRowsQuery = "SELECT COUNT(*) FROM USERS";
        String getDataQuery = "SELECT U.*, IFNULL(UD.GENDER, 'N/A') AS GENDER, IFNULL(UD.CONTACT_NO, 'N/A') AS CONTACT_NO, IFNULL(UD.ADDR, 'N/A') AS ADDR FROM USERS U LEFT JOIN USER_DETAIL UD ON U.USER_ID = UD.USER_ID";

        Connection conn = null;
        PreparedStatement countRowsStmt = null;
        PreparedStatement getDataStmt = null;
        ResultSet countRowsRs = null;
        ResultSet getDataRs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Get the number of rows
            countRowsStmt = conn.prepareStatement(countRowsQuery);
            countRowsRs = countRowsStmt.executeQuery();
            if (countRowsRs.next()) {
                int rowCount = countRowsRs.getInt(1);
                String[][] data = new String[rowCount][6];

                // Get the data
                getDataStmt = conn.prepareStatement(getDataQuery);
                getDataRs = getDataStmt.executeQuery();
                if (getDataRs.next()) {
                    int i = 0;
                    while (getDataRs.next()) {
                        data[i][0] = getDataRs.getString("NAME");
                        data[i][1] = getDataRs.getString("EMAIL");
                        data[i][2] = getDataRs.getString("ROLE");
                        data[i][3] = getDataRs.getString("GENDER");
                        data[i][4] = getDataRs.getString("CONTACT_NO");
                        data[i][5] = getDataRs.getString("ADDR");
                        i++;
                    }
                    return data;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeQuietly(getDataRs);
            DatabaseConnection.closeQuietly(countRowsRs);
            DatabaseConnection.closeQuietly(getDataStmt);
            DatabaseConnection.closeQuietly(countRowsStmt);
            DatabaseConnection.closeQuietly(conn);
        }

        return new String[0][6];
    }
}

package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.connection.DatabaseConnection;

public class CardDAO {
    UserDAO userDAO;

    public CardDAO() {
        userDAO = new UserDAO();
    }

    public void addCard(String customerEmail, String cardNumber, String expiryDate, int securityCode, String nameOnCard, String billingAddress) {
        String addCardQuery = "INSERT INTO CARDS (CARD_NO, EXP_DATE, CCV, CARD_NAME, BILL_ADDR) VALUES (?, ?, ?, ?, ?)";
        String addUserCardRelationQuery = "INSERT INTO USER_CARD (USER_ID, CARD_NO) VALUES (?, ?)";

        // Split expiry date into month and year
        String[] expiryDateParts = expiryDate.split("/");
        String expiryMonth = expiryDateParts[0];
        String expiryYear = "20" + expiryDateParts[1];
        String expiryDateFormatted = expiryYear + "-" + expiryMonth + "-01";

        // Fetch user ID from email
        int userID = userDAO.getUserID(customerEmail);

        Connection conn = null;
        PreparedStatement addCardStmt = null;
        PreparedStatement addUserCardRelationStmt = null;

        // Start transaction
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Add card
            addCardStmt = conn.prepareStatement(addCardQuery);
            addCardStmt.setString(1, cardNumber);
            addCardStmt.setString(2, expiryDateFormatted);
            addCardStmt.setInt(3, securityCode);
            addCardStmt.setString(4, nameOnCard);
            addCardStmt.setString(5, billingAddress);
            addCardStmt.executeUpdate();

            // Add user-card relation
            addUserCardRelationStmt = conn.prepareStatement(addUserCardRelationQuery);
            addUserCardRelationStmt.setInt(1, userID);
            addUserCardRelationStmt.setString(2, cardNumber);
            addUserCardRelationStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            DatabaseConnection.closeQuietly(addUserCardRelationStmt);
            DatabaseConnection.closeQuietly(addCardStmt);
            DatabaseConnection.closeQuietly(conn);
        }
    }
}
package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import core.entities.Card;
import database.connection.DatabaseConnection;

public class CardDAO {
    UserDAO userDAO;

    public CardDAO() {
        userDAO = new UserDAO();
    }

    public void addCard(String customerEmail, String cardNumber, String expiryDate, int securityCode, String nameOnCard,
            String billingAddress) {
        // System.out.println("✅ Executing CardDAO->addCard() ✅");
        String cardExistsQuery = "SELECT CARD_NO FROM CARDS WHERE CARD_NO = ?";
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
        PreparedStatement cardExistsStmt = null;
        PreparedStatement addCardStmt = null;
        PreparedStatement addUserCardRelationStmt = null;
        ResultSet cardExistsRs = null;

        // Start transaction
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            boolean cardExists = false;

            // Check if card already exists
            cardExistsStmt = conn.prepareStatement(cardExistsQuery);
            cardExistsStmt.setString(1, cardNumber);
            cardExistsRs = cardExistsStmt.executeQuery();
            if (cardExistsRs.next()) {
                cardExists = true;
            }

            // Insert card only if it doesn't already exist
            if (!cardExists) {
                addCardStmt = conn.prepareStatement(addCardQuery);
                addCardStmt.setString(1, cardNumber);
                addCardStmt.setString(2, expiryDateFormatted);
                addCardStmt.setInt(3, securityCode);
                addCardStmt.setString(4, nameOnCard);
                addCardStmt.setString(5, billingAddress);
                addCardStmt.executeUpdate();
            }

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
            DatabaseConnection.closeQuietly(cardExistsRs);
            DatabaseConnection.closeQuietly(addUserCardRelationStmt);
            DatabaseConnection.closeQuietly(addCardStmt);
            DatabaseConnection.closeQuietly(cardExistsStmt);
            DatabaseConnection.closeQuietly(conn);
        }
    }

    public void deleteCard(String customerEmail, String cardNumber) {
        // System.out.println("✅ Executing CardDAO->deleteCard() ✅");
        String deleteCardUserQuery = "DELETE FROM USER_CARD WHERE USER_ID = ? AND CARD_NO = ?";
        String countCardUsersQuery = "SELECT COUNT(*) FROM USER_CARD WHERE CARD_NO = ?";
        String deleteCardQuery = "DELETE FROM CARDS WHERE CARD_NO = ?";

        // Fetch user ID from email
        int userID = userDAO.getUserID(customerEmail);

        Connection conn = null;
        PreparedStatement deleteCardUserStmt = null;
        PreparedStatement countCardUsersStmt = null;
        PreparedStatement deleteCardStmt = null;
        ResultSet countCardUsersRs = null;

        // Start transaction
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete user-card relation
            deleteCardUserStmt = conn.prepareStatement(deleteCardUserQuery);
            deleteCardUserStmt.setInt(1, userID);
            deleteCardUserStmt.setString(2, cardNumber);
            deleteCardUserStmt.executeUpdate();

            // Check if card is still in use by other users
            countCardUsersStmt = conn.prepareStatement(countCardUsersQuery);
            countCardUsersStmt.setString(1, cardNumber);
            countCardUsersRs = countCardUsersStmt.executeQuery();

            int count = -1;
            if (countCardUsersRs.next()) {
                count = countCardUsersRs.getInt(1);
            }

            // Delete card if no longer in use
            if (count == 0) {
                deleteCardStmt = conn.prepareStatement(deleteCardQuery);
                deleteCardStmt.setString(1, cardNumber);
                deleteCardStmt.executeUpdate();
            }

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
            DatabaseConnection.closeQuietly(countCardUsersRs);
            DatabaseConnection.closeQuietly(deleteCardStmt);
            DatabaseConnection.closeQuietly(countCardUsersStmt);
            DatabaseConnection.closeQuietly(deleteCardUserStmt);
            DatabaseConnection.closeQuietly(conn);
        }
    }

    public ArrayList<Card> getCardsByCustomerEmail(String email) {
        // System.out.println("✅ Executing CardDAO->getCardsByCustomerEmail() ✅");
        String getCardsQuery = "SELECT * FROM CARDS WHERE CARD_NO IN (SELECT CARD_NO FROM USER_CARD WHERE USER_ID = ?)";
        ArrayList<Card> customerCards = new ArrayList<Card>();

        // Fetch user ID from email
        int userID = userDAO.getUserID(email);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pr = conn.prepareStatement(getCardsQuery)) {
            pr.setInt(1, userID);
            try (ResultSet rs = pr.executeQuery()) {
                while (rs.next()) {

                    // Process date
                    String[] expiryDate = rs.getString("EXP_DATE").split("-");
                    String expiryMonth = expiryDate[1];
                    String expiryYear = expiryDate[0].substring(2);
                    String expiryDateFormatted = expiryMonth + "/" + expiryYear;

                    Card c = new Card(email, rs.getString("CARD_NO"), expiryDateFormatted,
                            rs.getInt("CCV"), rs.getString("CARd_NAME"), rs.getString("BILL_ADDR"));
                    customerCards.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerCards;
    }

    public boolean cardExistsOnUserAccount(String customerEmail, String cardNumber) {
        // System.out.println("✅ Executing CardDAO->cardExistsOnUserAccount() ✅");
        String cardExistsQuery = "SELECT COUNT(*) FROM USER_CARD WHERE USER_ID = ? AND CARD_NO = ?";
        int userID = userDAO.getUserID(customerEmail);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pr = conn.prepareStatement(cardExistsQuery)) {
            pr.setInt(1, userID);
            pr.setString(2, cardNumber);
            try (ResultSet rs = pr.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String[][] getDataForTable(String email) {
        // System.out.println("✅ Executing CardDAO->getDataForTable() ✅");
        String getUserCardsQuery = "SELECT * FROM CARDS WHERE CARD_NO IN (SELECT CARD_NO FROM USER_CARD WHERE USER_ID = ?)";
        ArrayList<Card> customerCards = getCardsByCustomerEmail(email);
        String[][] data = new String[customerCards.size()][5];
        int userID = userDAO.getUserID(email);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pr = conn.prepareStatement(getUserCardsQuery)) {
            pr.setInt(1, userID);
            try (ResultSet rs = pr.executeQuery()) {
                int i = 0;
                while (rs.next()) {
                    data[i][0] = rs.getString("CARD_NO");
                    data[i][1] = rs.getString("EXP_DATE");
                    data[i][2] = Integer.toString(rs.getInt("CCV"));
                    data[i][3] = rs.getString("CARD_NAME");
                    data[i][4] = rs.getString("BILL_ADDR");
                    i++;
                }

                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new String[0][5];
    }
}
package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.connection.DatabaseConnection;

public class CartDAO {
    private UserDAO userDAO;

    public CartDAO() {
        userDAO = new UserDAO();
    }

    public int getCartID(String customerEmail) {
        String fetchCartQuery = "SELECT CARD_ID FROM CARTS WHERE USER_ID = ?";
        int userID = userDAO.getUserID(customerEmail);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(fetchCartQuery)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CARD_ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

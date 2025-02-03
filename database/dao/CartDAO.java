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

    public void addProductToCart(int cartID, int productID, int quantity) {
        String addProductToCartQuery = "INSERT INTO CART_ITEMS (CART_ID, PROD_ID, QUANTITY) VALUES (?, ?, ?)";

        // Check if product already in cart, if so, then update quantity
        int currentQuantity = getProductQuantityInCart(cartID, productID);
        if (currentQuantity > 0) {
            updateQuantity(cartID, productID, currentQuantity + quantity);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(addProductToCartQuery)) {
                    ps.setInt(1, cartID);
                    ps.setInt(2, productID);
                    ps.setInt(3, quantity);
                    ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeProductFromCart(int cartID, int productID) {
        String removeProductFromCartQuery = "DELETE FROM CART_ITEMS WHERE CART_ID = ? AND PROD_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(removeProductFromCartQuery)) {
                    ps.setInt(1, cartID);
                    ps.setInt(2, productID);
                    ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQuantity(int cartID, int productID, int quantity) {
        String updateQuantityQuery = "UPDATE CART_ITEMS SET QUANTITY = ? WHERE CART_ID = ? AND PROD_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(updateQuantityQuery)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, cartID);
                    ps.setInt(3, productID);
                    ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getProductQuantityInCart(int cartID, int productID) {
        String fetchQuantityQuery = "SELECT QUANTITY FROM CART_ITEMS WHERE CART_ID = ? AND PROD_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(fetchQuantityQuery)) {
                    ps.setInt(1, cartID);
                    ps.setInt(2, productID);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("QUANTITY");
                        }
                    }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

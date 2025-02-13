package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import core.entities.Product;
import database.connection.DatabaseConnection;

public class CartDAO {
    private UserDAO userDAO;
    private ProductDAO productDAO;

    public CartDAO() {
        userDAO = new UserDAO();
        productDAO = new ProductDAO();
    }

    public int getCartID(String customerEmail) {
        // System.out.println("✅ Executing CartDAO->getCartID() ✅");
        String fetchCartQuery = "SELECT CART_ID FROM CARTS WHERE USER_ID = ?";
        int userID = userDAO.getUserID(customerEmail);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(fetchCartQuery)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CART_ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addProductToCart(int cartID, int productID, int quantity) {
        // System.out.println("✅ Executing CartDAO->addProductToCart() ✅");
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
        // System.out.println("✅ Executing CartDAO->removeProductFromCart() ✅");
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
        // System.out.println("✅ Executing CartDAO->updateQuantity() ✅");
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
        // System.out.println("✅ Executing CartDAO->getProductQuantityInCart() ✅");
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

    public HashMap<Product, Integer> getCartHashMap(int cartID) {
        // System.out.println("✅ Executing CartDAO->getCartHashMap() ✅");
        String fetchCartQuery = "SELECT PROD_ID, QUANTITY FROM CART_ITEMS WHERE CART_ID = ?";
        HashMap<Product, Integer> customerCart = new HashMap<Product, Integer>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(fetchCartQuery)) {
            ps.setInt(1, cartID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = productDAO.searchProduct(rs.getInt("PROD_ID"));
                    int quantity = rs.getInt("QUANTITY");
                    customerCart.put(p, quantity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerCart;
    }

    public boolean isEmpty(int cartID) {
        // System.out.println("✅ Executing CartDAO->isEmpty() ✅");
        String fetchCartItemNoQuery = "SELECT COUNT(*) FROM CART_ITEMS WHERE CART_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(fetchCartItemNoQuery)) {
            ps.setInt(1, cartID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return true;
    }

    public void clearCart(int cartID) {
        // System.out.println("✅ Executing CartDAO->clearCart() ✅");
        String clearCartQuery = "DELETE FROM CART_ITEMS WHERE CART_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(clearCartQuery)) {
            ps.setInt(1, cartID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getTotal(int cartID) {
        // System.out.println("✅ Executing CartDAO->getTotal() ✅");
        HashMap<Product, Integer> cartItems = getCartHashMap(cartID);
        double total = 0;

        for (Product p : cartItems.keySet()) {
            total += p.getPrice() * cartItems.get(p);
        }

        return total;
    }
}

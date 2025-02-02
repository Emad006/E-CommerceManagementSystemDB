package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.connection.DatabaseConnection;

public class ProductDAO {

    public ProductDAO() {}

    // TODO: Validate ID in front-end
    public boolean productExists(int id) {
        String fetchProductsQuery = "SELECT PROD_ID FROM PRODUCTS WHERE PROD_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pr = conn.prepareStatement(fetchProductsQuery)) {
            pr.setInt(1, id);
            ResultSet rs = pr.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add product with provided image path
    // TODO: Validate data in front-end
    public void addProduct(int id, String name, double price, int stock, String category, String desc, String imagePath) {
        String addProductQuery;

        // Check if image path is null
        if (imagePath == null) { // Avoid inserting image path (will use default image)
            addProductQuery = "INSERT INTO PRODUCTS (PROD_ID, PROD_NAME, PROD_PRICE, PROD_STOCK, PROD_CATEGORY, PROD_DESC) VALUES (?, ?, ?, ?, ?, ?)";
        } else { // Insert image path
            addProductQuery = "INSERT INTO PRODUCTS (PROD_ID, PROD_NAME, PROD_PRICE, PROD_STOCK, PROD_CATEGORY, PROD_DESC, PROD_IMAGE) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pr = conn.prepareStatement(addProductQuery)) {
                pr.setInt(1, id);
                pr.setString(2, name);
                pr.setDouble(3, price);
                pr.setInt(4, stock);
                pr.setString(5, category);
                pr.setString(6, desc);

                // Set image path if not null
                if (imagePath != null) {
                    pr.setString(7, imagePath);
                }

                pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add product with deafault image
    public void addProduct(int id, String name, double price, int stock, String category, String desc) {
        addProduct(id, name, price, stock, category, desc, null);
    }
}

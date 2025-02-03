package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import core.entities.Product;
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

    // Edit product with provided image path
    public void editProduct(int id, String name, double price, int stock, String category, String desc, String imagePath) {
        String editProductQuery;

        // Check if image path is null
        if (imagePath == null) { // Avoid updating image path
            editProductQuery = "UPDATE PRODUCTS SET PROD_NAME = ?, PROD_PRICE = ?, PROD_STOCK = ?, PROD_CATEGORY = ?, PROD_DESC = ? WHERE PROD_ID = ?";
        } else { // Update image path
            editProductQuery = "UPDATE PRODUCTS SET PROD_NAME = ?, PROD_PRICE = ?, PROD_STOCK = ?, PROD_CATEGORY = ?, PROD_DESC = ?, PROD_IMAGE = ? WHERE PROD_ID = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pr = conn.prepareStatement(editProductQuery)) {
                pr.setString(1, name);
                pr.setDouble(2, price);
                pr.setInt(3, stock);
                pr.setString(4, category);
                pr.setString(5, desc);

                // Set image path if not null
                if (imagePath != null) {
                    pr.setString(6, imagePath);
                    pr.setInt(7, id);
                } else {
                    pr.setInt(6, id);
                }

                pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Edit product with default image
    public void editProduct(int id, String name, double price, int stock, String category, String desc) {
        editProduct(id, name, price, stock, category, desc, null);
    }

    // Delete product
    public void deleteProduct(int id) {
        String deleteProductQuery = "DELETE FROM PRODUCTS WHERE PROD_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pr = conn.prepareStatement(deleteProductQuery)) {
                pr.setInt(1, id);
                pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all products
    public ArrayList<Product> getAllProducts() {
        String fetchProductsQuery = "SELECT * FROM PRODUCTS";
        ArrayList<Product> productList = new ArrayList<Product>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pr = conn.prepareStatement(fetchProductsQuery)) {
                ResultSet rs = pr.executeQuery();

                // Create a product object for each row and add to the array list
                while (rs.next()) {
                    Product p = new Product(rs.getInt("PROD_ID"), rs.getString("NAME"), rs.getDouble("PRICE"), rs.getInt("STOCK"), rs.getString("CAT"), rs.getString("DESCRIP"), rs.getString("IMG_PATH"));
                    productList.add(p);
                }

                return productList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all categories
    public String[] getAllCategories() {
        String fetchCategoriesQuery = "SELECT DISTINCT CAT FROM PRODUCTS";
        ArrayList<String> categories = new ArrayList<String>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(fetchCategoriesQuery)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    categories.add(rs.getString("CAT"));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories.toArray(new String[0]);
    }

    // Deduct stock after checkout
    public void deductStock(int id, int quantity) {
        String deductStockQuery = "UPDATE PRODUCTS SET STOCK = STOCK - ? WHERE PROD_ID = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pr = conn.prepareStatement(deductStockQuery)) {
                pr.setInt(1, quantity);
                pr.setInt(2, id);
                pr.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get data for table
    public String[][] getDataForTable() {
        String countQuery = "SELECT COUNT(*) FROM PRODUCTS";
        String fetchProductsQuery = "SELECT * FROM PRODUCTS";

        Connection conn = null;
        PreparedStatement countStmt = null;
        PreparedStatement fetchStmt = null;
        ResultSet countRs = null;
        ResultSet fetchRs = null;

        try {
            conn = DatabaseConnection.getConnection();

            // Get number of rows
            countStmt = conn.prepareStatement(countQuery);
            countRs = countStmt.executeQuery();

            int rowCount;

            if (countRs.next()) {
                rowCount = countRs.getInt(1);
            } else {
                return new String[0][7];
            }

            // Fetch products
            fetchStmt = conn.prepareStatement(fetchProductsQuery);
            fetchRs = fetchStmt.executeQuery();

            int i = 0;
            String[][] data = new String[rowCount][7];

            while (fetchRs.next()) {
                data[i][0] = Integer.toString(fetchRs.getInt("PROD_ID"));
                data[i][1] = fetchRs.getString("NAME");
                data[i][2] = Double.toString(fetchRs.getDouble("PRICE"));
                data[i][3] = Integer.toString(fetchRs.getInt("STOCK"));
                data[i][4] = fetchRs.getString("CAT");
                data[i][5] = fetchRs.getString("DESCRIP");
                data[i][6] = fetchRs.getString("IMG_PATH");
                i++;
            }

            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeQuietly(fetchRs);
            DatabaseConnection.closeQuietly(countRs);
            DatabaseConnection.closeQuietly(fetchStmt);
            DatabaseConnection.closeQuietly(countStmt);
            DatabaseConnection.closeQuietly(conn);
        }

        return new String[0][7];
    }
}

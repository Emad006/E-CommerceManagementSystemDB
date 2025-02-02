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
}

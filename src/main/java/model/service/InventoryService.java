package model.service;

import model.entity.Inventory;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {

    // Add a new inventory item
    public void addInventoryItem(Inventory inventory) throws SQLException {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null.");
        }
        String sql = "INSERT INTO inventory (productCode, productName, quantityInStock, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inventory.getProductCode());
            pstmt.setString(2, inventory.getProductName());
            pstmt.setInt(3, inventory.getQuantityInStock());
            pstmt.setString(4, inventory.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding inventory item: " + e.getMessage());
            throw e;
        }
    }

    // Update an existing inventory item
    public void updateInventoryItem(Inventory inventory) throws SQLException {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null.");
        }
        String sql = "UPDATE inventory SET productName = ?, quantityInStock = ?, status = ? WHERE productCode = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inventory.getProductName());
            pstmt.setInt(2, inventory.getQuantityInStock());
            pstmt.setString(3, inventory.getStatus());
            pstmt.setString(4, inventory.getProductCode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating inventory item: " + e.getMessage());
            throw e;
        }
    }

    // Delete an inventory item
    public void deleteInventoryItem(String productCode) throws SQLException {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty.");
        }
        String sql = "DELETE FROM inventory WHERE productCode = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting inventory item: " + e.getMessage());
            throw e;
        }
    }

    // Get an inventory item by product code
    public Inventory getInventoryItem(String productCode) throws SQLException {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty.");
        }
        String sql = "SELECT * FROM inventory WHERE productCode = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Inventory(
                        rs.getString("productCode"),
                        rs.getString("productName"),
                        rs.getInt("quantityInStock"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching inventory item: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // Get all inventory items
    public List<Inventory> getAllInventoryItems() throws SQLException {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM inventory";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                inventoryList.add(new Inventory(
                        rs.getString("productCode"),
                        rs.getString("productName"),
                        rs.getInt("quantityInStock"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving inventory items: " + e.getMessage());
            throw e;
        }
        return inventoryList;
    }

    // Search inventory items by a given criterion
    public List<Inventory> searchInventory(String criteria, String searchText) throws SQLException {
        String columnName;
        switch (criteria) {
            case "Product Code":
                columnName = "productCode";
                break;
            case "Product Name":
                columnName = "productName";
                break;
            case "Status":
                columnName = "status";
                break;
            default:
                throw new IllegalArgumentException("Invalid search criteria: " + criteria);
        }

        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE " + columnName + " LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                inventoryList.add(new Inventory(
                        rs.getString("productCode"),
                        rs.getString("productName"),
                        rs.getInt("quantityInStock"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching inventory items: " + e.getMessage());
            throw e;
        }
        return inventoryList;
    }

    // Generate a new product code
    public String generateProductCode() throws SQLException {
        String sql = "SELECT MAX(productCode) FROM inventory";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int lastCode = Integer.parseInt(rs.getString(1));
                return String.valueOf(lastCode + 1);
            }
            return "1001";
        } catch (SQLException e) {
            System.err.println("Error generating product code: " + e.getMessage());
            throw e;
        }
    }
}

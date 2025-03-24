package controller;

import model.entity.Inventory;
import model.service.InventoryService;
import view.management.InventoryManagementFrame;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.Map;

public class InventoryController {
    private InventoryService inventoryService;
    private InventoryManagementFrame view;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setView(InventoryManagementFrame view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        if (view == null) return;

        view.getSearchButton().addActionListener(e -> {
            try {
                String searchText = view.getSearchField().getText().trim();
                String criteria = view.getSearchCriteria();

                if (searchText.isEmpty()) {
                    loadAllInventoryItems();
                    return;
                }

                List<Inventory> results = searchInventory(criteria, searchText);
                view.updateTableWithResults(results);
            } catch (SQLException ex) {
                showError("Error searching inventory: " + ex.getMessage());
            }
        });

        view.getAddButton().addActionListener(e -> {
            try {
                Inventory inventory = view.getInventoryFromFields();
                addInventoryItem(inventory);
                view.clearFields();
                loadAllInventoryItems();
                showMessage("Inventory item added successfully!");
            } catch (SQLException ex) {
                showError("Error adding inventory item: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showError("Invalid input for quantity. Please enter a valid number.");
            }
        });

        view.getUpdateButton().addActionListener(e -> {
            try {
                Inventory inventory = view.getInventoryFromFields();
                updateInventoryItem(inventory);
                loadAllInventoryItems();
                showMessage("Inventory item updated successfully!");
            } catch (SQLException ex) {
                showError("Error updating inventory item: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showError("Invalid input for quantity. Please enter a valid number.");
            }
        });

        view.getDeleteButton().addActionListener(e -> {
            try {
                String productCode = view.getSelectedProductCode();
                if (productCode == null) {
                    showMessage("Please select an inventory item to delete!");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Are you sure you want to delete this inventory item?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteInventoryItem(productCode);
                    loadAllInventoryItems();
                    showMessage("Inventory item deleted successfully!");
                }
            } catch (SQLException ex) {
                showError("Error deleting inventory item: " + ex.getMessage());
            }
        });
    }

    public void loadAllInventoryItems() {
        try {
            List<Inventory> inventoryList = getAllInventoryItems();
            view.updateTableWithResults(inventoryList);
        } catch (SQLException e) {
            showError("Error loading inventory items: " + e.getMessage());
        }
    }

    private void showError(String message) {
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message) {
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Service methods
    public void addInventoryItem(Inventory inventory) throws SQLException {
        inventoryService.addInventoryItem(inventory);
    }

    public void updateInventoryItem(Inventory inventory) throws SQLException {
        inventoryService.updateInventoryItem(inventory);
    }

    public void deleteInventoryItem(String productCode) throws SQLException {
        inventoryService.deleteInventoryItem(productCode);
    }

    public List<Inventory> searchInventory(String criteria, String searchText) throws SQLException {
        return inventoryService.searchInventory(criteria, searchText);
    }

    public List<Inventory> getAllInventoryItems() throws SQLException {
        return inventoryService.getAllInventoryItems();
    }

    public Inventory getInventoryItem(String productCode) throws SQLException {
        return inventoryService.getInventoryItem(productCode);
    }

    public String generateProductCode() throws SQLException {
        return inventoryService.generateProductCode();
    }

    public List<Map<String, Object>> getInventoryUsageReport(int year, int month) throws SQLException {
        return inventoryService.getInventoryUsageReport(year, month);
    }    
}

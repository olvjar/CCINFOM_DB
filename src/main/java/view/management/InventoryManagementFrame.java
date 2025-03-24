package view.management;

import model.entity.Inventory;
import controller.InventoryController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class InventoryManagementFrame extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField productCodeField, productNameField, quantityField, statusField, priceField;
    private JTextField searchField;
    private JComboBox<String> searchCriteria;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private InventoryController inventoryController;

    public InventoryManagementFrame(InventoryController controller) {
        this.inventoryController = controller;
        setTitle("Inventory Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        inventoryController.setView(this);
        inventoryController.loadAllInventoryItems();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filterLabel = new JLabel("Search by:");
        searchCriteria = new JComboBox<>(new String[]{
            "Product Code", "Product Name", "Status"
        });
        filterPanel.add(filterLabel);
        filterPanel.add(searchCriteria);

        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        searchButton = new JButton("Search");
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.EAST);

        searchPanel.add(filterPanel);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        searchPanel.add(searchBarPanel);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inventory Details"));

        productCodeField = new JTextField();
        productNameField = new JTextField();
        quantityField = new JTextField();
        statusField = new JTextField();
        priceField = new JTextField();

        inputPanel.add(new JLabel("Product Code:"));
        inputPanel.add(productCodeField);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(productNameField);
        inputPanel.add(new JLabel("Quantity in Stock:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Price (₱):"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusField);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Item");
        deleteButton = new JButton("Delete Item");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Exit button
        JPanel exitPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> dispose());
        exitPanel.add(exitButton);

        // Left content panel
        JPanel leftContentPanel = new JPanel();
        leftContentPanel.setLayout(new BoxLayout(leftContentPanel, BoxLayout.Y_AXIS));
        leftContentPanel.add(searchPanel);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentPanel.add(inputPanel);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentPanel.add(buttonPanel);
        leftContentPanel.add(exitPanel);

        leftPanel.add(leftContentPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"Product Code", "Product Name", "Quantity", "Price (₱)", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);

        // Main panel layout
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // Button listeners
        addButton.addActionListener(e -> addInventoryItem());
        updateButton.addActionListener(e -> updateInventoryItem());
        deleteButton.addActionListener(e -> deleteInventoryItem());
        searchButton.addActionListener(e -> searchInventoryItems());

        // Table selection listener
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = inventoryTable.getSelectedRow();
                if (selectedRow != -1) {
                    productCodeField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    productNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    quantityField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    priceField.setText(tableModel.getValueAt(selectedRow, 3).toString().replace("₱", ""));
                    statusField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        inventoryTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setText(String.format("₱%.2f", value));
                }
                return this;
            }
        });
    }


    // Add Inventory Item
public void addInventoryItem() {
    try {
        Inventory inventory = getInventoryFromFields();
        inventoryController.addInventoryItem(inventory);
        inventoryController.loadAllInventoryItems();
        clearFields();
        JOptionPane.showMessageDialog(this, "Inventory item added successfully!");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error adding inventory item: " + e.getMessage());
    }
}

// Update Inventory Item
public void updateInventoryItem() {
    try {
        Inventory inventory = getInventoryFromFields();
        inventoryController.updateInventoryItem(inventory);
        inventoryController.loadAllInventoryItems();
        clearFields();
        JOptionPane.showMessageDialog(this, "Inventory item updated successfully!");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating inventory item: " + e.getMessage());
    }
}

// Delete Inventory Item
public void deleteInventoryItem() {
    try {
        String productCode = getSelectedProductCode();
        if (productCode == null) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            inventoryController.deleteInventoryItem(productCode);
            inventoryController.loadAllInventoryItems();
            clearFields();
            JOptionPane.showMessageDialog(this, "Inventory item deleted successfully!");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error deleting inventory item: " + e.getMessage());
    }
}

// Search Inventory Items
public void searchInventoryItems() {
    try {
        String criteria = getSearchCriteria();
        String searchText = searchField.getText().trim();
        List<Inventory> results = inventoryController.searchInventory(criteria, searchText);
        updateTableWithResults(results);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching inventory items: " + e.getMessage());
    }
}


    // Getters for controller access
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getSearchButton() { return searchButton; }
    public JTextField getSearchField() { return searchField; }
    public String getSearchCriteria() { return (String) searchCriteria.getSelectedItem(); }

    public Inventory getInventoryFromFields() {
        double price = 0.0;
        try {
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format. Please enter a valid number.");
        }
        
        return new Inventory(
            productCodeField.getText(),
            productNameField.getText(),
            Integer.parseInt(quantityField.getText()),
            price,
            statusField.getText()
        );
    }

    public String getSelectedProductCode() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) tableModel.getValueAt(selectedRow, 0);
    }

    public void clearFields() {
        productCodeField.setText("");
        productNameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        statusField.setText("");
    }

    public void updateTableWithResults(List<Inventory> inventoryList) {
        tableModel.setRowCount(0);
        for (Inventory inventory : inventoryList) {
            Object[] row = {
                inventory.getProductCode(),
                inventory.getProductName(),
                inventory.getQuantityInStock(),
                inventory.getPrice(),
                inventory.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}

package view.management;

import model.entity.Inventory;
import controller.InventoryController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class InventoryManagementFrame extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField productCodeField, productNameField, quantityField, statusField;
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
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inventory Details"));

        productCodeField = new JTextField();
        productNameField = new JTextField();
        quantityField = new JTextField();
        statusField = new JTextField();

        inputPanel.add(new JLabel("Product Code:"));
        inputPanel.add(productCodeField);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(productNameField);
        inputPanel.add(new JLabel("Quantity in Stock:"));
        inputPanel.add(quantityField);
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
        String[] columns = {"Product Code", "Product Name", "Quantity", "Status"};
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
                    statusField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });
    }

    private void addInventoryItem() {
        try {
            Inventory inventory = new Inventory(
                productCodeField.getText(),
                productNameField.getText(),
                Integer.parseInt(quantityField.getText()),
                statusField.getText()
            );
            inventoryController.addInventoryItem(inventory);
            inventoryController.loadAllInventoryItems();
            clearFields();
            JOptionPane.showMessageDialog(this, "Item added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage());
        }
    }

    private void updateInventoryItem() {
        try {
            Inventory inventory = new Inventory(
                productCodeField.getText(),
                productNameField.getText(),
                Integer.parseInt(quantityField.getText()),
                statusField.getText()
            );
            inventoryController.updateInventoryItem(inventory);
            inventoryController.loadAllInventoryItems();
            clearFields();
            JOptionPane.showMessageDialog(this, "Item updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + e.getMessage());
        }
    }

    private void deleteInventoryItem() {
        try {
            String productCode = productCodeField.getText();
            inventoryController.deleteInventoryItem(productCode);
            inventoryController.loadAllInventoryItems();
            clearFields();
            JOptionPane.showMessageDialog(this, "Item deleted successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage());
        }
    }

    private void searchInventoryItems() {
        try {
            String criteria = (String) searchCriteria.getSelectedItem();
            String searchText = searchField.getText().trim();
            List<Inventory> results = inventoryController.searchInventory(criteria, searchText);
            updateTableWithResults(results);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching items: " + e.getMessage());
        }
    }

    private void clearFields() {
        productCodeField.setText("");
        productNameField.setText("");
        quantityField.setText("");
        statusField.setText("");
    }

    private void updateTableWithResults(List<Inventory> inventoryList) {
        tableModel.setRowCount(0);
        for (Inventory inventory : inventoryList) {
            Object[] row = {
                inventory.getProductCode(),
                inventory.getProductName(),
                inventory.getQuantityInStock(),
                inventory.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}

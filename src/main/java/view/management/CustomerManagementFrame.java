package view.management;

import model.entity.Customer;
import controller.CustomerController;
import view.dialog.DeviceManagementDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerManagementFrame extends JFrame {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField customerCodeField, firstNameField, lastNameField, contactNumberField, addressField;
    private JButton addButton, updateButton, deleteButton, viewAppointmentsButton, viewDevicesButton;
    private CustomerController customerController = new CustomerController();

    public CustomerManagementFrame() {
        setTitle("Customer Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
        loadCustomerData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // CREATE:
        // left panel
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(300, getHeight()));

        // search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        
        // Filter dropdown
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filterLabel = new JLabel("Search by:");
        JComboBox<String> searchCriteria = new JComboBox<>(new String[]{
            "Customer Code", "First Name", "Last Name", "Contact Number"
        });
        filterPanel.add(filterLabel);
        filterPanel.add(searchCriteria);
        
        // search bar panel
        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 0));
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.EAST);
        
        searchPanel.add(filterPanel);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacing
        searchPanel.add(searchBarPanel);

        // search functionality
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            String criteria = (String) searchCriteria.getSelectedItem();
            
            if (searchText.isEmpty()) {
                loadCustomerData(); // Reset to show all if search is empty
                return;
            }
            
            try {
                List<Customer> results = customerController.searchCustomers(criteria, searchText);
                updateTableWithResults(results);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error searching customers: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // reset search
        searchField.addActionListener(e -> {
            if (searchField.getText().trim().isEmpty()) {
                loadCustomerData();
            }
        });

        // left content panel
        JPanel leftContentPanel = new JPanel();
        leftContentPanel.setLayout(new BoxLayout(leftContentPanel, BoxLayout.Y_AXIS));
        
        leftContentPanel.add(searchPanel);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing

        // input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        customerCodeField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        contactNumberField = new JTextField();
        addressField = new JTextField();

        inputPanel.add(new JLabel("Customer Code:"));
        inputPanel.add(customerCodeField);
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Contact Number:"));
        inputPanel.add(contactNumberField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);

        // button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Customer");
        updateButton = new JButton("Update Customer");
        deleteButton = new JButton("Delete Customer");
        viewAppointmentsButton = new JButton("View Appointments");
        viewDevicesButton = new JButton("View Devices");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewAppointmentsButton);
        buttonPanel.add(viewDevicesButton);
        
        // exit button
        JPanel exitPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        JButton exitButton = new JButton("Exit");
        exitPanel.add(exitButton);

        leftContentPanel.add(inputPanel);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
        leftContentPanel.add(buttonPanel);
        leftContentPanel.add(exitPanel);

        leftPanel.add(leftContentPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"Customer Code", "First Name", "Last Name", "Contact Number", "Address"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(customerTable);

        // finish main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // action listeners
        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        viewAppointmentsButton.addActionListener(e -> viewAppointments());
        viewDevicesButton.addActionListener(e -> viewDevices());
        exitButton.addActionListener(e -> dispose());

        // table selection listener
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    customerCodeField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    firstNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    lastNameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    contactNumberField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    addressField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        try {
            for (Customer customer : customerController.getAllCustomers()) {
                Object[] row = {
                    customer.getCustomerCode(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getContactNumber(),
                    customer.getAddress()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage());
        }
    }

    private void addCustomer() {
        try {
            Customer customer = new Customer(
                customerCodeField.getText(),
                firstNameField.getText(),
                lastNameField.getText(),
                contactNumberField.getText(),
                addressField.getText()
            );
            
            customerController.addCustomer(customer);
            loadCustomerData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update!");
            return;
        }

        try {
            Customer customer = new Customer(
                customerCodeField.getText(),
                firstNameField.getText(),
                lastNameField.getText(),
                contactNumberField.getText(),
                addressField.getText()
            );
            
            customerController.updateCustomer(customer);
            loadCustomerData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete!");
            return;
        }

        String customerCode = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this customer?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerController.deleteCustomer(customerCode);
                loadCustomerData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage());
            }
        }
    }

    private void viewAppointments() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to view appointments!");
            return;
        }

        String customerCode = (String) tableModel.getValueAt(selectedRow, 0);
        String customerName = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);
        
        JDialog appointmentsDialog = new JDialog(this, "Appointments for: " + customerName, true);
        appointmentsDialog.setSize(800, 400);

        String[] columns = {"Invoice #", "Date & Time", "Service Status", "Payment Status", "Device"};
        DefaultTableModel appointmentsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable appointmentsTable = new JTable(appointmentsModel);

        // Style the status columns to show ENUM values nicely
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String status = value.toString();
                    setForeground(getStatusColor(status));
                }
                return this;
            }
        };
        appointmentsTable.getColumnModel().getColumn(2).setCellRenderer(statusRenderer);

        // Set column widths
        appointmentsTable.getColumnModel().getColumn(0).setPreferredWidth(70);   // Invoice
        appointmentsTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Date
        appointmentsTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Service Status
        appointmentsTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Payment Status
        appointmentsTable.getColumnModel().getColumn(4).setPreferredWidth(250);  // Device

        // Load appointments
        try {
            for (String[] appointment : customerController.getCustomerAppointments(customerCode)) {
                appointmentsModel.addRow(appointment);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }

        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add legend panel at the top
        JPanel legendPanel = createStatusLegendPanel();
        mainPanel.add(legendPanel, BorderLayout.NORTH);

        // Add table in the center
        mainPanel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);

        // Add close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> appointmentsDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        appointmentsDialog.add(mainPanel);
        appointmentsDialog.setLocationRelativeTo(this);
        appointmentsDialog.setVisible(true);
    }

    private JPanel createStatusLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Service Status"));

        String[] statuses = {"Pending", "In Progress", "For Pickup", "Completed", "Cancelled"};
        for (String status : statuses) {
            JLabel label = new JLabel(status);
            label.setForeground(getStatusColor(status));
            legendPanel.add(label);
        }

        return legendPanel;
    }

    private Color getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return new Color(128, 128, 128);  // Gray
            case "IN PROGRESS":
                return new Color(0, 0, 255);      // Blue
            case "FOR PICKUP":
                return new Color(255, 140, 0);    // Orange
            case "COMPLETED":
                return new Color(0, 128, 0);      // Green
            case "CANCELLED":
                return new Color(255, 0, 0);      // Red
            default:
                return Color.BLACK;
        }
    }

    private void viewDevices() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to view devices!");
            return;
        }

        String customerCode = (String) tableModel.getValueAt(selectedRow, 0);
        String customerName = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);
        
        DeviceManagementDialog dialog = new DeviceManagementDialog(this, customerCode, customerName);
        dialog.setVisible(true);
    }

    private void clearFields() {
        customerCodeField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        contactNumberField.setText("");
        addressField.setText("");
    }

    private void updateTableWithResults(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer customer : customers) {
            Object[] row = {
                customer.getCustomerCode(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getContactNumber(),
                customer.getAddress()
            };
            tableModel.addRow(row);
        }
    }
} 
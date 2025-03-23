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
    private JTextField searchField;
    private JComboBox<String> searchCriteria;
    private JButton addButton, updateButton, deleteButton, viewAppointmentsButton, viewDevicesButton, searchButton;
    private CustomerController customerController;

    public CustomerManagementFrame(CustomerController controller) {
        this.customerController = controller;
        setTitle("Customer Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
        customerController.setView(this);
        customerController.loadAllCustomers();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        searchCriteria = new JComboBox<>(new String[]{
            "Customer Code", "First Name", "Last Name", "Contact Number"
        });
        filterPanel.add(filterLabel);
        filterPanel.add(searchCriteria);
        
        // search bar panel
        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        searchButton = new JButton("Search");
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.EAST);
        
        searchPanel.add(filterPanel);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacing
        searchPanel.add(searchBarPanel);

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
        exitButton.addActionListener(e -> dispose()); // Keep this simple action here
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

        // table selection listener (keep this here since it's UI-specific)
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

    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getViewAppointmentsButton() { return viewAppointmentsButton; }
    public JButton getViewDevicesButton() { return viewDevicesButton; }
    public JButton getSearchButton() { return searchButton; }
    public JTextField getSearchField() { return searchField; }
    public String getSearchCriteria() { return (String) searchCriteria.getSelectedItem(); }
    
    public Customer getCustomerFromFields() {
        return new Customer(
            customerCodeField.getText(),
            firstNameField.getText(),
            lastNameField.getText(),
            contactNumberField.getText(),
            addressField.getText()
        );
    }

    public String getSelectedCustomerCode() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) return null;
        return (String) tableModel.getValueAt(selectedRow, 0);
    }
    
    public Customer getSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) return null;
        
        return new Customer(
            (String) tableModel.getValueAt(selectedRow, 0),
            (String) tableModel.getValueAt(selectedRow, 1),
            (String) tableModel.getValueAt(selectedRow, 2),
            (String) tableModel.getValueAt(selectedRow, 3),
            (String) tableModel.getValueAt(selectedRow, 4)
        );
    }

    public void updateTableWithResults(List<Customer> customers) {
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

    public void clearFields() {
        customerCodeField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        contactNumberField.setText("");
        addressField.setText("");
    }

    public void showAppointmentsDialog(List<String[]> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments found for this customer.");
            return;
        }

        int selectedRow = customerTable.getSelectedRow();
        String customerName = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);
        
        JDialog appointmentsDialog = new JDialog(this, "Appointments for: " + customerName, true);
        appointmentsDialog.setSize(800, 400);

        String[] columns = {"Invoice #", "Date & Time", "Service Status", "Payment Status", "Device", "Technician"};
        DefaultTableModel appointmentsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable appointmentsTable = new JTable(appointmentsModel);

        // Style the status columns
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

        appointmentsTable.getColumnModel().getColumn(0).setPreferredWidth(70);   // Invoice
        appointmentsTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Date
        appointmentsTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Service Status
        appointmentsTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Payment Status
        appointmentsTable.getColumnModel().getColumn(4).setPreferredWidth(200);  // Device
        appointmentsTable.getColumnModel().getColumn(5).setPreferredWidth(150);  // Technician

        // Load appointments
        for (String[] appointment : appointments) {
            appointmentsModel.addRow(appointment);
        }

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Legend
        JPanel legendPanel = createStatusLegendPanel();
        mainPanel.add(legendPanel, BorderLayout.NORTH);

        mainPanel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> appointmentsDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        appointmentsDialog.add(mainPanel);
        appointmentsDialog.setLocationRelativeTo(this);
        appointmentsDialog.setVisible(true);
    }

    public void showDevicesDialog(String customerCode) {
        int selectedRow = customerTable.getSelectedRow();
        String customerName = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);
        
        DeviceManagementDialog dialog = new DeviceManagementDialog(this, Integer.parseInt(customerCode), customerName);
        dialog.setVisible(true);
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
} 
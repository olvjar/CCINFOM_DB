package view.dialog;

import controller.DeviceController;
import model.entity.Device;
import model.service.DeviceService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DeviceManagementDialog extends JDialog {
    private int customerCode;
    private JTable deviceTable;
    private DefaultTableModel tableModel;
    private JTextField deviceTypeField, brandField, modelField, serialNumberField, searchField;
    private JTextArea descriptionArea;
    private JComboBox<String> searchCriteria;
    private JButton addButton, updateButton, deleteButton, searchButton;
    private DeviceController deviceController;

    public DeviceManagementDialog(JFrame parent, int customerCode, String customerName) {
        super(parent, "Devices for: " + customerName, true);
        this.customerCode = customerCode;
        
        // Initialize controller with service
        DeviceService deviceService = new DeviceService();
        this.deviceController = new DeviceController(deviceService);
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        
        initComponents();
        loadDeviceData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Device Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add input fields
        deviceTypeField = new JTextField(20);
        brandField = new JTextField(20);
        modelField = new JTextField(20);
        serialNumberField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Add labels and fields
        addLabelAndField(inputPanel, "Device Type:", deviceTypeField, gbc, 0);
        addLabelAndField(inputPanel, "Brand:", brandField, gbc, 1);
        addLabelAndField(inputPanel, "Model:", modelField, gbc, 2);
        addLabelAndField(inputPanel, "Serial Number:", serialNumberField, gbc, 3);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton addButton = new JButton("Add Device");
        JButton updateButton = new JButton("Update Device");
        JButton deleteButton = new JButton("Delete Device");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // Create table
        String[] columns = {"Device ID", "Type", "Brand", "Model", "Serial Number", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        deviceTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(deviceTable);

        // Add components to main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Add action listeners
        addButton.addActionListener(e -> addDevice());
        updateButton.addActionListener(e -> updateDevice());
        deleteButton.addActionListener(e -> deleteDevice());
        closeButton.addActionListener(e -> dispose());

        // Add table selection listener
        deviceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = deviceTable.getSelectedRow();
                if (selectedRow != -1) {
                    deviceTypeField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    brandField.setText((String) tableModel.getValueAt(selectedRow, 2));
                    modelField.setText((String) tableModel.getValueAt(selectedRow, 3));
                    serialNumberField.setText((String) tableModel.getValueAt(selectedRow, 4));
                    descriptionArea.setText((String) tableModel.getValueAt(selectedRow, 5));
                }
            }
        });
    }

    private void addLabelAndField(JPanel panel, String label, JComponent field, 
                                GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    private void loadDeviceData() {
        tableModel.setRowCount(0);
        try {
            List<Device> devices = deviceController.getCustomerDevices(customerCode);
            
            for (Device device : devices) {
                Object[] row = {
                    device.getDeviceId(),
                    device.getDeviceType(),
                    device.getBrand(),
                    device.getModel(),
                    device.getSerialNumber(),
                    device.getDescription()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("Error loading devices: " + e.getMessage());
        }
    }

    private void addDevice() {
        try {
            if (deviceTypeField.getText().trim().isEmpty()) {
                showError("Device type cannot be empty");
                return;
            }

            Device device = createDeviceFromFields(0); // 0 for new device
            deviceController.addDevice(device);
            showMessage("Device added successfully!");
            clearFields();
            loadDeviceData();
        } catch (SQLException e) {
            showError("Error adding device: " + e.getMessage());
        }
    }

    private void updateDevice() {
        try {
            int row = deviceTable.getSelectedRow();
            if (row == -1) {
                showError("Please select a device to update");
                return;
            }

            int deviceId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            Device device = createDeviceFromFields(deviceId);
            
            deviceController.updateDevice(device);
            showMessage("Device updated successfully!");
            clearFields();
            loadDeviceData();
        } catch (SQLException e) {
            showError("Error updating device: " + e.getMessage());
        }
    }

    private void deleteDevice() {
        try {
            int row = deviceTable.getSelectedRow();
            if (row == -1) {
                showError("Please select a device to delete");
                return;
            }

            int deviceId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            
            if (confirmDelete()) {
                deviceController.deleteDevice(deviceId);
                showMessage("Device deleted successfully!");
                clearFields();
                loadDeviceData();
            }
        } catch (SQLException e) {
            showError("Error deleting device: " + e.getMessage());
        }
    }

    private Device createDeviceFromFields(int deviceId) {
        return new Device(
            deviceId,
            customerCode,
            deviceTypeField.getText().trim(),
            brandField.getText().trim(),
            modelField.getText().trim(),
            serialNumberField.getText().trim(),
            descriptionArea.getText().trim()
        );
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this device?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearFields() {
        deviceTypeField.setText("");
        brandField.setText("");
        modelField.setText("");
        serialNumberField.setText("");
        descriptionArea.setText("");
        deviceTable.clearSelection();
    }

    // Controller methods
    public JButton getSearchButton() { return searchButton; }
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JTextField getSearchField() { return searchField; }
    public String getSearchCriteria() { return searchCriteria.getSelectedItem().toString(); }
    
    public Device getDeviceFromFields() {
        return createDeviceFromFields(
            deviceTable.getSelectedRow() != -1 ? 
            Integer.parseInt(tableModel.getValueAt(deviceTable.getSelectedRow(), 0).toString()) : 
            0
        );
    }
    
    public int getSelectedDeviceId() {
        int row = deviceTable.getSelectedRow();
        if (row == -1) return -1;
        return Integer.parseInt(tableModel.getValueAt(row, 0).toString());
    }
    
    public void updateTableWithResults(List<Device> devices) {
        tableModel.setRowCount(0);
        for (Device device : devices) {
            Object[] row = {
                device.getDeviceId(),
                device.getDeviceType(),
                device.getBrand(),
                device.getModel(),
                device.getSerialNumber(),
                device.getDescription()
            };
            tableModel.addRow(row);
        }
    }
} 
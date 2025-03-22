package view.dialog;

import controller.DeviceController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import model.entity.Device;

public class DeviceManagementDialog extends JDialog {
    private int customerCode;
    private JTable deviceTable;
    private DefaultTableModel tableModel;
    private DeviceController deviceController = new DeviceController();
    private JTextField deviceTypeField, brandField, modelField, serialNumberField;
    private JTextArea descriptionArea;

    public DeviceManagementDialog(JFrame parent, int customerCode, String customerName) {
        super(parent, "Devices for: " + customerName, true);
        this.customerCode = customerCode;
        
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
            for (Device device : deviceController.getCustomerDevices(customerCode)) {
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
            JOptionPane.showMessageDialog(this, 
                "Error loading devices: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDevice() {
        try {
            if (deviceTypeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Device type cannot be empty");
                return;
            }

            Device device = new Device(
                0,  // deviceID will be generated by database
                customerCode,
                deviceTypeField.getText().trim(),
                brandField.getText().trim(),
                modelField.getText().trim(),
                serialNumberField.getText().trim(),
                descriptionArea.getText().trim()
            );
            
            deviceController.addDevice(device);
            JOptionPane.showMessageDialog(this, "Device added successfully!");
            clearFields();
            loadDeviceData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error adding device: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDevice() {
        try {
            int row = deviceTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a device to update");
                return;
            }

            int deviceId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            
            Device device = new Device(
                deviceId,
                customerCode,
                deviceTypeField.getText().trim(),
                brandField.getText().trim(),
                modelField.getText().trim(),
                serialNumberField.getText().trim(),
                descriptionArea.getText().trim()
            );
            
            deviceController.updateDevice(device);
            JOptionPane.showMessageDialog(this, "Device updated successfully!");
            clearFields();
            loadDeviceData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating device: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDevice() {
        try {
            int row = deviceTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a device to delete");
                return;
            }

            int deviceId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this device?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                deviceController.deleteDevice(deviceId);
                JOptionPane.showMessageDialog(this, "Device deleted successfully!");
                clearFields();
                loadDeviceData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error deleting device: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        deviceTypeField.setText("");
        brandField.setText("");
        modelField.setText("");
        serialNumberField.setText("");
        descriptionArea.setText("");
        deviceTable.clearSelection();
    }
} 
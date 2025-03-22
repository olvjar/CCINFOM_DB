package view.dialog;

import controller.DeviceController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import model.entity.Device;
import view.utils.ColorUtils;

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
        
        // Create main panel with white background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(ColorUtils.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create input panel with styled border
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(ColorUtils.SECONDARY);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorUtils.BORDER),
                "Device Details"
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Style input fields
        deviceTypeField = createStyledTextField();
        brandField = createStyledTextField();
        modelField = createStyledTextField();
        serialNumberField = createStyledTextField();
        descriptionArea = createStyledTextArea();

        // Add input fields
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

        // Style the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setBackground(ColorUtils.BACKGROUND);
        
        JButton addButton = createStyledButton("Add Device");
        JButton updateButton = createStyledButton("Update Device");
        JButton deleteButton = createStyledButton("Delete Device", ColorUtils.DANGER);
        JButton closeButton = createStyledButton("Close", ColorUtils.SECONDARY_DARK);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // Style the table
        String[] columns = {"Device ID", "Type", "Brand", "Model", "Serial Number", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        deviceTable = new JTable(tableModel);
        deviceTable.setBackground(ColorUtils.BACKGROUND);
        deviceTable.setForeground(ColorUtils.TEXT_PRIMARY);
        deviceTable.setSelectionBackground(ColorUtils.PRIMARY_LIGHT);
        deviceTable.setSelectionForeground(ColorUtils.TEXT_LIGHT);
        deviceTable.setGridColor(ColorUtils.BORDER);
        deviceTable.getTableHeader().setBackground(ColorUtils.PRIMARY);
        deviceTable.getTableHeader().setForeground(ColorUtils.TEXT_LIGHT);
        deviceTable.setRowHeight(25);

        JScrollPane tableScrollPane = new JScrollPane(deviceTable);
        tableScrollPane.getViewport().setBackground(ColorUtils.BACKGROUND);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(ColorUtils.BORDER));

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

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(ColorUtils.BACKGROUND);
        field.setForeground(ColorUtils.TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorUtils.BORDER),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setBackground(ColorUtils.BACKGROUND);
        area.setForeground(ColorUtils.TEXT_PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorUtils.BORDER),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return area;
    }

    private JButton createStyledButton(String text) {
        return createStyledButton(text, ColorUtils.PRIMARY);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(ColorUtils.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void addLabelAndField(JPanel panel, String label, JComponent field, 
                                GridBagConstraints gbc, int row) {
        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(ColorUtils.TEXT_PRIMARY);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(labelComp, gbc);
        
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
package view.management;

import model.entity.Technician;
import controller.TechnicianController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class TechnicianManagementFrame extends JFrame {
    private JTable technicianTable;
    private DefaultTableModel tableModel;
    private JTextField firstNameField, lastNameField, contactNumberField, addressField;
    private JComboBox<String> availabilityCombo;
    private JTextField searchField;
    private JComboBox<String> searchCriteria;
    private JButton addButton, updateButton, deleteButton, viewAppointmentsButton, searchButton;
    private TechnicianController technicianController;

    public TechnicianManagementFrame(TechnicianController controller) {
        this.technicianController = controller;
        setTitle("Technician Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
        technicianController.setView(this);
        technicianController.loadAllTechnicians();
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
        
        // Filter dropdown
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filterLabel = new JLabel("Search by:");
        searchCriteria = new JComboBox<>(new String[]{
            "First Name", "Last Name", "Contact", "Address", "Availability"
        });
        filterPanel.add(filterLabel);
        filterPanel.add(searchCriteria);
        
        // Search bar panel
        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        searchButton = new JButton("Search");
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.EAST);
        
        searchPanel.add(filterPanel);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        searchPanel.add(searchBarPanel);

        // Left content panel
        JPanel leftContentPanel = new JPanel();
        leftContentPanel.setLayout(new BoxLayout(leftContentPanel, BoxLayout.Y_AXIS));
        
        leftContentPanel.add(searchPanel);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Technician Details"));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        contactNumberField = new JTextField();
        addressField = new JTextField();
        availabilityCombo = new JComboBox<>(new String[]{"Available", "Busy", "On Leave"});

        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Contact Number:"));
        inputPanel.add(contactNumberField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Availability:"));
        inputPanel.add(availabilityCombo);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Technician");
        updateButton = new JButton("Update Technician");
        deleteButton = new JButton("Delete Technician");
        viewAppointmentsButton = new JButton("View Appointments");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewAppointmentsButton);
        
        // Exit button
        JPanel exitPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> dispose());
        exitPanel.add(exitButton);

        leftContentPanel.add(inputPanel);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftContentPanel.add(buttonPanel);
        leftContentPanel.add(exitPanel);

        leftPanel.add(leftContentPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = {"ID", "First Name", "Last Name", "Contact", "Address", "Availability"};
        tableModel = new DefaultTableModel(columns, 0);
        technicianTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(technicianTable);

        // Table selection listener
        technicianTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = technicianTable.getSelectedRow();
                if (selectedRow != -1) {
                    firstNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    lastNameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    contactNumberField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    addressField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    availabilityCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
                }
            }
        });

        // Finish main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    // Getters for controller
    public JButton getAddButton() { return addButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getViewAppointmentsButton() { return viewAppointmentsButton; }
    public JButton getSearchButton() { return searchButton; }
    public JTextField getSearchField() { return searchField; }
    public String getSearchCriteria() { return searchCriteria.getSelectedItem().toString(); }
    
    public Technician getTechnicianFromFields() {
        return new Technician(
            0, // ID will be generated/retrieved from table selection
            firstNameField.getText(),
            lastNameField.getText(),
            contactNumberField.getText(),
            addressField.getText(),
            (String) availabilityCombo.getSelectedItem()
        );
    }

    public int getSelectedTechnicianId() {
        int selectedRow = technicianTable.getSelectedRow();
        if (selectedRow == -1) return -1;
        return (int) tableModel.getValueAt(selectedRow, 0);
    }
    
    public Technician getSelectedTechnician() {
        int selectedRow = technicianTable.getSelectedRow();
        if (selectedRow == -1) return null;
        
        return new Technician(
            (int) tableModel.getValueAt(selectedRow, 0),
            (String) tableModel.getValueAt(selectedRow, 1),
            (String) tableModel.getValueAt(selectedRow, 2),
            (String) tableModel.getValueAt(selectedRow, 3),
            (String) tableModel.getValueAt(selectedRow, 4),
            (String) tableModel.getValueAt(selectedRow, 5)
        );
    }

    public void updateTableWithResults(List<Technician> technicians) {
        tableModel.setRowCount(0);
        for (Technician technician : technicians) {
            Object[] row = {
                technician.getTechnicianID(),
                technician.getFirstName(),
                technician.getLastName(),
                technician.getContactNumber(),
                technician.getAddress(),
                technician.getAvailability()
            };
            tableModel.addRow(row);
        }
    }

    public void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        contactNumberField.setText("");
        addressField.setText("");
        availabilityCombo.setSelectedIndex(0);
    }

    public void showAppointmentsDialog(List<String[]> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments found for this technician.");
            return;
        }

        int selectedRow = technicianTable.getSelectedRow();
        String technicianName = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);
        
        JDialog appointmentsDialog = new JDialog(this, "Appointments for: " + technicianName, true);
        appointmentsDialog.setSize(800, 400);

        String[] columns = {"Invoice #", "Date & Time", "Service Status", "Payment Status", "Device", "Customer"};
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

        // Load appointments
        for (String[] appointment : appointments) {
            appointmentsModel.addRow(appointment);
        }

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

    private Color getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "PENDING": return new Color(128, 128, 128);
            case "IN PROGRESS": return new Color(0, 0, 255);
            case "FOR PICKUP": return new Color(255, 140, 0);
            case "COMPLETED": return new Color(0, 128, 0);
            case "CANCELLED": return new Color(255, 0, 0);
            default: return Color.BLACK;
        }
    }

    public void updateTechnicianTable(List<Technician> technicians) {
        tableModel.setRowCount(0);
        for (Technician tech : technicians) {
            Object[] row = {
                tech.getTechnicianID(),
                tech.getFirstName(),
                tech.getLastName(),
                tech.getContactNumber(),
                tech.getAddress(),
                tech.getAvailability()
            };
            tableModel.addRow(row);
        }
    }
}

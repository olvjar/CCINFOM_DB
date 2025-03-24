package view.management;

import model.entity.Customer;
import model.entity.Technician;
import model.entity.Appointment;
import controller.AppointmentController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentManagementFrame extends JFrame {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField customerCodeField, technicianIDField, dateAndTimeField, invoiceNumberField, amountPaidField, deviceIDField;
    private JComboBox<String> paymentStatusCombo, serviceStatusCombo;
    private JButton addButton, updateButton, deleteButton, viewCustomerButton, viewTechnicianButton, generateInvoiceButton;
    private AppointmentController appointmentController;

    public AppointmentManagementFrame (AppointmentController controller) 
    {
        this.appointmentController = controller;
        setTitle("Appointment Management");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
        loadAppointmentData();
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
            "Customer Code", "Technician ID", "Service Status", "Date and Time", "Invoice Number", "Payment Status", "Amount Paid", "Device ID"
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
        searchButton.addActionListener(e -> 
        {
            String searchText = searchField.getText().trim();
            String criteria = (String) searchCriteria.getSelectedItem();
            
            if (searchText.isEmpty()) {
                loadAppointmentData(); // Reset to show all if search is empty
                return;
            }
            
            try {
                List<Appointment> results = appointmentController.searchAppointments(criteria, searchText);
                updateTableWithResults(results);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error searching appointments: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // reset search
        searchField.addActionListener(e -> {
            if (searchField.getText().trim().isEmpty()) {
                loadAppointmentData();
            }
        });

        // left content panel
        JPanel leftContentPanel = new JPanel();
        leftContentPanel.setLayout(new BoxLayout(leftContentPanel, BoxLayout.Y_AXIS));
        
        leftContentPanel.add(searchPanel);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing

        // input panel
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Appointment Details"));

        customerCodeField = new JTextField ();
        technicianIDField = new JTextField ();
        serviceStatusCombo = new JComboBox<>(new String[]{
            "Pending", "In Progress", "For Pickup",
            "Completed", "Cancelled"
        });
        dateAndTimeField = new JTextField ();
        invoiceNumberField = new JTextField ();
        paymentStatusCombo = new JComboBox<>(new String [] {
           "Pending", "Paid" 
        });
        amountPaidField = new JTextField ();
        deviceIDField = new JTextField ();
        
        inputPanel.add (new JLabel ("Customer Code:"));
        inputPanel.add (customerCodeField);
        inputPanel.add (new JLabel ("Technician ID:"));
        inputPanel.add (technicianIDField);
        inputPanel.add (new JLabel ("Service Status:"));
        inputPanel.add (serviceStatusCombo);
        inputPanel.add (new JLabel ("Date and Time"));
        inputPanel.add (dateAndTimeField);
        inputPanel.add (new JLabel ("Invoice Number:"));
        inputPanel.add (invoiceNumberField);
        inputPanel.add (new JLabel ("Payment Status:"));
        inputPanel.add (paymentStatusCombo);
        inputPanel.add (new JLabel ("Amount Paid:"));
        inputPanel.add (amountPaidField);
        inputPanel.add (new JLabel ("Device ID:"));
        inputPanel.add (deviceIDField);

        // button panel
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Appointment");
        updateButton = new JButton("Update Appointment");
        deleteButton = new JButton("Delete Appointment");
        viewCustomerButton = new JButton ("View Customer");
        viewTechnicianButton = new JButton ("View Technician");
        generateInvoiceButton = new JButton ("Generate Invoice");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add (viewCustomerButton);
        buttonPanel.add (viewTechnicianButton);
        buttonPanel.add (generateInvoiceButton);
        
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
        String[] columns = {"Customer Code", "Technician ID", "Service Status", "Date and Time", "Invoice Number", "Payment Status", "Amount Paid", "Device ID"};
        tableModel = new DefaultTableModel(columns, 0);
        appointmentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(appointmentTable);

        // finish main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // action listeners
        addButton.addActionListener(e -> addAppointment());
        updateButton.addActionListener(e -> updateAppointment());
        deleteButton.addActionListener(e -> deleteAppointment());
        viewCustomerButton.addActionListener (e -> viewCustomer ());
        viewTechnicianButton.addActionListener (e -> viewTechnician ());
        generateInvoiceButton.addActionListener (e -> generateInvoice ());
        exitButton.addActionListener(e -> dispose());

        // table selection listener
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = appointmentTable.getSelectedRow();
                if (selectedRow != -1) {
                    customerCodeField.setText (tableModel.getValueAt(selectedRow, 0).toString ());
                    technicianIDField.setText (tableModel.getValueAt(selectedRow, 1).toString ());
                    serviceStatusCombo.setSelectedItem (tableModel.getValueAt(selectedRow, 2).toString ());
                    dateAndTimeField.setText (tableModel.getValueAt(selectedRow, 3).toString ());
                    invoiceNumberField.setText (tableModel.getValueAt(selectedRow, 4).toString ());
                    paymentStatusCombo.setSelectedItem (tableModel.getValueAt(selectedRow, 5).toString ());
                    amountPaidField.setText (tableModel.getValueAt(selectedRow, 6).toString ());
                    deviceIDField.setText (tableModel.getValueAt(selectedRow, 7).toString ());
                }
            }
        });
    }

    private void loadAppointmentData() {
        tableModel.setRowCount(0);
        try {
            for (Appointment appointment : appointmentController.getAllAppointments()) {
                Object[] row = {
                    appointment.getCustomerCode (),
                    appointment.getTechnicianID (),
                    appointment.getServiceStatus (),
                    appointment.getDateAndTime (),
                    appointment.getInvoiceNumber (),
                    appointment.getPaymentStatus (),
                    appointment.getAmountPaid (),
                    appointment.getDeviceID ()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    private void addAppointment() {
        try {
            Appointment appointment = new Appointment(
                Integer.parseInt (customerCodeField.getText ()),
                Integer.parseInt (technicianIDField.getText ()),
                (String) serviceStatusCombo.getSelectedItem (),
                dateAndTimeField.getText (),
                Integer.parseInt (invoiceNumberField.getText ()),
                (String) paymentStatusCombo.getSelectedItem (),
                Double.parseDouble (amountPaidField.getText ()),
                Integer.parseInt (deviceIDField.getText ())
            );
            
            appointmentController.addAppointment (appointment);
            loadAppointmentData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Appointment added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding appointment: " + e.getMessage());
        }
    }

    private void updateAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to update!");
            return;
        }

        try {
            Appointment appointment = new Appointment(
                Integer.parseInt (customerCodeField.getText ()),
                Integer.parseInt (technicianIDField.getText ()),
                (String) serviceStatusCombo.getSelectedItem (),
                dateAndTimeField.getText (),
                Integer.parseInt (invoiceNumberField.getText ()),
                (String) paymentStatusCombo.getSelectedItem (),
                Double.parseDouble (amountPaidField.getText ()),
                Integer.parseInt (deviceIDField.getText ())
            );
            
            appointmentController.updateAppointment (appointment);
            loadAppointmentData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Appointment updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating appointment: " + e.getMessage());
        }
    }

    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete!");
            return;
        }

        int invoiceNumber = (int) tableModel.getValueAt (selectedRow, 4);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this appointment?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                appointmentController.deleteAppointment (invoiceNumber);
                loadAppointmentData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Appointment deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting Appointment: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        customerCodeField.setText ("");
        technicianIDField.setText ("");
        serviceStatusCombo.setSelectedIndex (0);
        dateAndTimeField.setText ("");
        invoiceNumberField.setText ("");
        paymentStatusCombo.setSelectedIndex (0);
        amountPaidField.setText ("");
        deviceIDField.setText ("");
    }

    private void updateTableWithResults(List<Appointment> appointments) {
        tableModel.setRowCount(0);
        for (Appointment appointment : appointments) {
            Object[] row = {
                appointment.getCustomerCode (),
                appointment.getTechnicianID (),
                appointment.getServiceStatus (),
                appointment.getDateAndTime (),
                appointment.getInvoiceNumber (),
                appointment.getPaymentStatus (),
                appointment.getAmountPaid (),
                appointment.getDeviceID ()
            };
            tableModel.addRow(row);
        }
    }
    
    
    private void viewCustomer() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }
        
        JDialog customerDialog = new JDialog(this, "Customer Details", true);
        customerDialog.setSize(500, 400);

        try {
            String customerCode = tableModel.getValueAt(selectedRow, 0).toString();
            Customer customer = appointmentController.getCustomerByCode(customerCode);

            // Main panel with padding
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Customer Details Panel
            JPanel customerPanel = new JPanel(new GridBagLayout());
            customerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Customer Information"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Customer details
            addLabelAndValue(customerPanel, "Customer ID:", customer.getCustomerCode(), gbc, 0);
            addLabelAndValue(customerPanel, "Name:", customer.getFirstName() + " " + customer.getLastName(), gbc, 1);
            addLabelAndValue(customerPanel, "Contact:", customer.getContactNumber(), gbc, 2);
            addLabelAndValue(customerPanel, "Address:", customer.getAddress(), gbc, 3);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> customerDialog.dispose());
        buttonPanel.add(closeButton);

            // Add panels to main panel
            mainPanel.add(customerPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(buttonPanel);

        customerDialog.add(mainPanel);
        customerDialog.setLocationRelativeTo(this);
        customerDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading customer details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewTechnician ()
    {
        int selectedRow = appointmentTable.getSelectedRow ();
        if (selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }
        
        JDialog technicianDialog = new JDialog (this, "Technician Assignment", true);
        technicianDialog.setSize (600, 500);
        
        try
        {
            int technicianId = Integer.parseInt (tableModel.getValueAt (selectedRow, 1).toString ());
            Technician currentTechnician = appointmentController.getTechnicianByID (technicianId);
            
            // Main panel with padding
            JPanel mainPanel = new JPanel ();
            mainPanel.setLayout (new BoxLayout (mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder (BorderFactory.createEmptyBorder (20, 20, 20, 20));
            
            // Current Technician Panel
            JPanel currentTechPanel = new JPanel (new GridBagLayout ());
            currentTechPanel.setBorder (BorderFactory.createCompoundBorder (
                BorderFactory.createTitledBorder ("Current Technician"),
                BorderFactory.createEmptyBorder (10, 10, 10, 10)
            ));
            
            GridBagConstraints gbc = new GridBagConstraints ();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets (5, 5, 5, 5);
            
            // Current technician details
            addLabelAndValue (currentTechPanel, "ID:", String.valueOf (currentTechnician.getTechnicianID ()), gbc, 0);
            addLabelAndValue (currentTechPanel, "Name:", currentTechnician.getFirstName () + " " + currentTechnician.getLastName (), gbc, 1);
            addLabelAndValue (currentTechPanel, "Contact:", currentTechnician.getContactNumber (), gbc, 2);
            addLabelAndValue (currentTechPanel, "Status:", currentTechnician.getAvailability (), gbc, 3);
            
            // Reassignment Panel
            JPanel reassignPanel = new JPanel ();
            reassignPanel.setLayout (new BoxLayout (reassignPanel, BoxLayout.Y_AXIS));
            reassignPanel.setBorder (BorderFactory.createCompoundBorder (
                BorderFactory.createTitledBorder ("Reassign Technician"),
                BorderFactory.createEmptyBorder (10, 10, 10, 10)
            ));
            
            // Available technicians combo with custom renderer
            JComboBox<Technician> availableTechCombo = new JComboBox<> ();
            availableTechCombo.setRenderer (new DefaultListCellRenderer () {
            @Override
                public Component getListCellRendererComponent (JList<?> list, Object value, 
                        int index, boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof Technician) {
                        Technician tech = (Technician) value;
                        String text = String.format ("%s %s (ID: %d) - %s", 
                            tech.getFirstName (), 
                            tech.getLastName (),
                            tech.getTechnicianID (),
                            tech.getAvailability ());
                        return super.getListCellRendererComponent (list, text, index, isSelected, cellHasFocus);
                    }
                    return super.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);
                }
            });
            
            // Load available technicians
            List<Technician> availableTechs = appointmentController.getAllTechnicians ().stream ()
                .filter (t -> "Available".equalsIgnoreCase (t.getAvailability ()))
                .filter (t -> t.getTechnicianID () != technicianId)
                .collect (Collectors.toList ());
            
            if (!availableTechs.isEmpty ()) {
                availableTechs.forEach (availableTechCombo::addItem);
            } else {
                JLabel noTechLabel = new JLabel ("No other technicians are currently available");
                noTechLabel.setForeground (Color.RED);
                reassignPanel.add (noTechLabel);
            }
            
            // Add combo box with label
            JPanel comboPanel = new JPanel (new BorderLayout (5, 0));
            comboPanel.add (new JLabel ("Select Technician:"), BorderLayout.WEST);
            comboPanel.add (availableTechCombo, BorderLayout.CENTER);
            reassignPanel.add (comboPanel);
            reassignPanel.add (Box.createRigidArea (new Dimension (0, 10)));
            
            // Assign button
            JButton assignButton = new JButton ("Assign Selected Technician");
            assignButton.setEnabled (!availableTechs.isEmpty ());
            assignButton.addActionListener (e -> {
                Technician selectedTech = (Technician) availableTechCombo.getSelectedItem ();
                if (selectedTech != null) {
                    try {
                        int invoiceNumber = Integer.parseInt (tableModel.getValueAt (selectedRow, 4).toString ());
                        Appointment currentAppointment = appointmentController.getAppointmentByInvoiceNumber (invoiceNumber);
                        
                        Appointment updatedAppointment = new Appointment (
                            currentAppointment.getCustomerCode (),
                            selectedTech.getTechnicianID (),
                            currentAppointment.getServiceStatus (),
                            currentAppointment.getDateAndTime (),
                            currentAppointment.getInvoiceNumber (),
                            currentAppointment.getPaymentStatus (),
                            currentAppointment.getAmountPaid (),
                            currentAppointment.getDeviceID ()
                        );
                        
                        appointmentController.updateAppointment (updatedAppointment);
                        loadAppointmentData ();
                        JOptionPane.showMessageDialog (technicianDialog, 
                            "Technician successfully reassigned!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        technicianDialog.dispose ();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog (technicianDialog,
                            "Error reassigning technician: " + ex.getMessage (),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            // Button panel
            JPanel buttonPanel = new JPanel (new FlowLayout (FlowLayout.RIGHT));
            buttonPanel.add (assignButton);
            JButton closeButton = new JButton ("Close");
            closeButton.addActionListener (e -> technicianDialog.dispose ());
            buttonPanel.add (closeButton);
            
            // Add all panels to main panel
            mainPanel.add (currentTechPanel);
            mainPanel.add (Box.createRigidArea (new Dimension (0, 20)));
            mainPanel.add (reassignPanel);
            mainPanel.add (Box.createRigidArea (new Dimension (0, 20)));
            mainPanel.add (buttonPanel);
            
            technicianDialog.add (mainPanel);
            technicianDialog.setLocationRelativeTo (this);
            technicianDialog.setVisible (true);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog (this, "Error loading technician details: " + e.getMessage ());
        }
    }
    
    private void addLabelAndValue (JPanel panel, String labelText, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel (labelText);
        label.setFont (label.getFont ().deriveFont (Font.BOLD));
        panel.add (label, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add (new JLabel (value), gbc);
    }
    
    private void generateInvoice() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        String serviceStatus = String.valueOf(tableModel.getValueAt(selectedRow, 2));
        String invoiceNumber = String.valueOf(tableModel.getValueAt(selectedRow, 4));
        String paymentStatus = String.valueOf(tableModel.getValueAt(selectedRow, 5));

        if (!serviceStatus.equals("For Pickup") || paymentStatus.equals("Paid")) {
            JOptionPane.showMessageDialog(this, 
                "Invoice can only be generated for repairs that are for-pickup and haven't been paid.",
                "Cannot Generate Invoice",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Appointment appointment = appointmentController.getAppointmentByInvoiceNumber(
                Integer.parseInt(invoiceNumber));
            Customer customer = appointmentController.getCustomerByCode(
                String.valueOf(appointment.getCustomerCode()));

            JDialog invoiceDialog = new JDialog(this, "Generate Invoice #" + invoiceNumber, true);
            invoiceDialog.setSize(500, 400);

            // Main panel with padding
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Customer Details Panel
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Invoice Details"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Add invoice details
            addLabelAndValue(detailsPanel, "Invoice Number:", invoiceNumber, gbc, 0);
            addLabelAndValue(detailsPanel, "Customer:", customer.getFirstName() + " " + customer.getLastName(), gbc, 1);
            addLabelAndValue(detailsPanel, "Customer ID:", customer.getCustomerCode(), gbc, 2);
            addLabelAndValue(detailsPanel, "Device ID:", String.valueOf(appointment.getDeviceID()), gbc, 3);
            addLabelAndValue(detailsPanel, "Service Status:", serviceStatus, gbc, 4);

            // Cost Input Panel
            JPanel costPanel = new JPanel();
            costPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Service Cost"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Create a sub-panel for the cost input with FlowLayout
            JPanel costInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            JLabel amountLabel = new JLabel("Enter Amount: ₱");
            JTextField costField = new JTextField(15);
            costField.setHorizontalAlignment(JTextField.RIGHT);
            
            // Add components to the input panel
            costInputPanel.add(amountLabel);
            costInputPanel.add(costField);
            
            // Add input panel to the cost panel
            costPanel.add(costInputPanel);

            // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton generateButton = new JButton("Generate");
            JButton cancelButton = new JButton("Cancel");

            generateButton.addActionListener(e -> {
                try {
                    double amount = Double.parseDouble(costField.getText().trim());
                    if (amount <= 0) {
                        throw new NumberFormatException();
                    }
                    
                    Appointment updatedAppointment = new Appointment(
                        appointment.getCustomerCode(),
                        appointment.getTechnicianID(),
                        appointment.getServiceStatus(),
                        appointment.getDateAndTime(),
                        appointment.getInvoiceNumber(),
                        "Pending",
                        amount,
                        appointment.getDeviceID()
                    );

                    appointmentController.updateAppointment(updatedAppointment);
                    loadAppointmentData();
                    JOptionPane.showMessageDialog(invoiceDialog,
                        "Invoice generated!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    invoiceDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(invoiceDialog,
                        "Please enter a valid amount greater than 0",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(invoiceDialog,
                        "Error generating invoice: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> invoiceDialog.dispose());
            buttonPanel.add(generateButton);
            buttonPanel.add(cancelButton);

            // Add all panels to main panel
            mainPanel.add(detailsPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            mainPanel.add(costPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            mainPanel.add(buttonPanel);

            invoiceDialog.add(mainPanel);
            invoiceDialog.setLocationRelativeTo(this);
            invoiceDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading appointment details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 

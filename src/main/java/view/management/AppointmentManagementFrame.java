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

public class AppointmentManagementFrame extends JFrame {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField customerCodeField, technicianIDField, dateAndTimeField, invoiceNumberField, amountPaidField, deviceIDField;
    private JComboBox<String> paymentStatusCombo, serviceStatusCombo;
    private JButton addButton, updateButton, deleteButton, viewCustomerButton, viewTechnicianButton, generateInvoiceButton;
    private AppointmentController appointmentController = new AppointmentController();

    public AppointmentManagementFrame() 
    {
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
    
    
    private void viewCustomer () 
    {
        int selectedRow = appointmentTable.getSelectedRow ();
        if (selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select an appointment to view customer.");
            return;
        }
        
        String customerCode = String.valueOf (tableModel.getValueAt (selectedRow, 0));
        
        JDialog customerDialog = new JDialog (this, "Customer Record for customer code " + customerCode, true);
        customerDialog.setSize (800, 400);
        
        String[] columns = {
            "Customer Code",
            "Last Name",
            "First Name",
            "Contact Number",
            "Address"
        };
        DefaultTableModel customerModel = new DefaultTableModel (columns, 0)
        {
            @Override
            public boolean isCellEditable (int row, int column)
            {
                return false;
            }
        };
        
        JTable customerTable = new JTable (customerModel);
        
        customerTable.getColumnModel().getColumn(0).setPreferredWidth (100);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth (100);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth (100);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth (100);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth (100);
        
        try
        {
            Customer currentCustomer = appointmentController.getCustomerByCode (customerCode);
            String[] customerInfo = {currentCustomer.getCustomerCode (),
                                    currentCustomer.getLastName (),
                                    currentCustomer.getFirstName (),
                                    currentCustomer.getContactNumber (),
                                    currentCustomer.getAddress ()};
            customerModel.addRow (customerInfo);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog (this, "Error loading customer." + e.getMessage ());
        }
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add table in the center
        mainPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        
        // Add close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> customerDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        customerDialog.add(mainPanel);
        customerDialog.setLocationRelativeTo(this);
        customerDialog.setVisible(true);
    }
    
    private void viewTechnician ()
    {
        int selectedRow = appointmentTable.getSelectedRow ();
        if (selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select an appointment to view technician.");
            return;
        }
        
        String technicianID = String.valueOf (tableModel.getValueAt (selectedRow, 1));
        
        JDialog technicianDialog = new JDialog (this, "Technician Record for technician ID " + technicianID, true);
        technicianDialog.setSize (800, 400);
        
        String[] columns = {
            "Technician ID",
            "Last Name",
            "First Name",
            "Contact Number",
            "Address",
            "Availability"
        };
        DefaultTableModel technicianModel = new DefaultTableModel (columns, 0)
        {
            @Override
            public boolean isCellEditable (int row, int column)
            {
                return false;
            }
        };
        JTable technicianTable = new JTable (technicianModel);
        
        technicianTable.getColumnModel().getColumn(0).setPreferredWidth (100);
        technicianTable.getColumnModel().getColumn(1).setPreferredWidth (100);
        technicianTable.getColumnModel().getColumn(2).setPreferredWidth (100);
        technicianTable.getColumnModel().getColumn(3).setPreferredWidth (100);
        technicianTable.getColumnModel().getColumn(4).setPreferredWidth (100);
        technicianTable.getColumnModel().getColumn(5).setPreferredWidth (100);
        
        try
        {
            Technician currentTechnician = appointmentController.getTechnicianByID (Integer.parseInt (technicianID));
            String[] technicianInfo = {String.valueOf (currentTechnician.getTechnicianID ()),
                                    currentTechnician.getLastName (),
                                    currentTechnician.getFirstName (),
                                    currentTechnician.getContactNumber (),
                                    currentTechnician.getAddress (),
                                    currentTechnician.getAvailability ()};
            technicianModel.addRow (technicianInfo);
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog (this, "Error loading technician." + e.getMessage ());
        }
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add table in the center
        mainPanel.add(new JScrollPane(technicianTable), BorderLayout.CENTER);
        
        // Add close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> technicianDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        technicianDialog.add(mainPanel);
        technicianDialog.setLocationRelativeTo(this);
        technicianDialog.setVisible(true);
    }
    
    public void generateInvoice ()
    {
        int selectedRow = appointmentTable.getSelectedRow ();
        if (selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select an appointment to generate an invoice for.");
            return;
        }
        
        String serviceStatus = String.valueOf (tableModel.getValueAt (selectedRow, 2));
        String invoiceNumber = String.valueOf (tableModel.getValueAt (selectedRow, 4));
        String paymentStatus = String.valueOf (tableModel.getValueAt (selectedRow, 5));
        
        if (!serviceStatus.equals ("Completed") || paymentStatus.equals ("Paid"))
        {
            JOptionPane.showMessageDialog (this, "Unable to generate invoice.");
            return;
        }
        
        JDialog invoiceDialog = new JDialog (this, "Invoice #" + invoiceNumber, true);
        invoiceDialog.setSize (400, 300);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // North 
        JLabel lblNorth = new JLabel ("<html><h2> Generate Invoice </h2></html>", SwingConstants.CENTER);
        mainPanel.add (lblNorth, BorderLayout.NORTH);
    
        // Center panel stuff
        JPanel centerPanel = new JPanel ();
        centerPanel.setLayout (new GridBagLayout ());
        mainPanel.add (centerPanel, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints ();
        gbc.insets = new Insets (10, 10, 10, 10);
        
            // input cost
        gbc.gridx = 1;
        gbc.gridy = 0;
        
        JPanel inputCostPanel = new JPanel ();
        BoxLayout inputCostBoxLayout = new BoxLayout (inputCostPanel, BoxLayout.X_AXIS);
        
        JLabel inputCostText = new JLabel ("Enter cost of service: ");
        inputCostPanel.add (inputCostText);
        
        JTextField inputCostTextField = new JTextField (5);
        inputCostPanel.add (inputCostTextField);
        
        centerPanel.add (inputCostPanel, gbc);
        
            // create invoice button
        gbc.gridy = 1;
    
        JButton createInvoiceButton = new JButton ("Generate");
        createInvoiceButton.addActionListener (e -> 
        {
            double userInput = Double.parseDouble (inputCostTextField.getText ());
            if (userInput <= 0)
            {
                JOptionPane.showMessageDialog (this, "Invalid amount.");
            }
            else
            {
                createInvoice (Integer.parseInt (invoiceNumber), userInput);
                invoiceDialog.dispose ();
            }
        });
        
        centerPanel.add (createInvoiceButton, gbc);
        
        // Add close button at the bottom
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> invoiceDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        invoiceDialog.add(mainPanel);
        invoiceDialog.setLocationRelativeTo(this);
        invoiceDialog.setVisible(true);
    }
    
    public void createInvoice (int invoiceNumber, double userInput)
    {
        JDialog invoiceDialog = new JDialog (this, "Invoice #" + invoiceNumber, true);
        invoiceDialog.setSize (400, 300);

        Appointment appointment = null;
        Customer customer = null;

        try
        {
            appointment = appointmentController.getAppointmentByInvoiceNumber (invoiceNumber);
        }
        catch (SQLException e)
        {
            e.printStackTrace ();
        }
        
        try
        {
        customer = appointmentController.getCustomerByCode (String.valueOf (appointment.getCustomerCode ()));
        }
        catch (SQLException e)
        {
            e.printStackTrace ();
        }
        
        String customerCode = customer.getCustomerCode ();
        String customerName = customer.getFullName ();
        int deviceID = appointment.getDeviceID ();
        double cost = userInput;
        
        Appointment updatedAppointment = new Appointment(
                        appointment.getCustomerCode (),
                        appointment.getTechnicianID (),
                        appointment.getServiceStatus (),
                        appointment.getDateAndTime (),
                        appointment.getInvoiceNumber (),
                        "Paid",
                        cost,
                        appointment.getDeviceID ());
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // north
        JLabel lblNorth = new JLabel (
            "<html><h2>"
            + "Customer Code: " + customerCode
            + "<br>" + "Customer Name: " + customerName
            + "<br>" + "Device ID: " + deviceID
            + "<br>" + "Amount to be paid: " + cost
            + "</h2></html>", SwingConstants.CENTER
        );
        mainPanel.add (lblNorth, BorderLayout.NORTH);
        
        // center panel
        JPanel centerPanel = new JPanel ();
        centerPanel.setLayout (new BoxLayout (centerPanel, BoxLayout.Y_AXIS));
        mainPanel.add (centerPanel, BorderLayout.CENTER);
        
            // buttons for center panel
            // paid
        JButton paidButton = new JButton ("Paid");
        paidButton.addActionListener (e -> 
        {
            try
            {
                appointmentController.updateAppointment (updatedAppointment);
                loadAppointmentData ();
                invoiceDialog.dispose ();
            }
            catch (SQLException f)
            {
                f.printStackTrace ();
            }
        });

        centerPanel.add (paidButton);
        
            // close
        JButton closeButton = new JButton ("Close");
        closeButton.addActionListener(e -> invoiceDialog.dispose());
        
        centerPanel.add (closeButton);
        
        //
        invoiceDialog.add (mainPanel);
        invoiceDialog.setLocationRelativeTo(this);
        invoiceDialog.setVisible(true);
    }
} 

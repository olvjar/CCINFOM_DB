package view.management;

import model.entity.Customer;
import controller.CustomerController;
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
    private JTextField customerCodeField, technicianIDField, serviceStatusField, dateAndTimeField, invoiceNumberField, paymentStatusField, amountPaidField, deviceIDField;
    private JButton addButton, updateButton, deleteButton, viewCustomerButton, viewTechnicianButton;
    private AppointmentController appointmentController = new AppointmentController();
    private CustomerController customerController = new CustomerController();

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
        searchButton.addActionListener(e -> {
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
        serviceStatusField = new JTextField ();
        dateAndTimeField = new JTextField ();
        invoiceNumberField = new JTextField ();
        paymentStatusField = new JTextField ();
        amountPaidField = new JTextField ();
        deviceIDField = new JTextField ();
        
        inputPanel.add (new JLabel ("Customer Code:"));
        inputPanel.add (customerCodeField);
        inputPanel.add (new JLabel ("Technician ID:"));
        inputPanel.add (technicianIDField);
        inputPanel.add (new JLabel ("Service Status:"));
        inputPanel.add (serviceStatusField);
        inputPanel.add (new JLabel ("Date and Time"));
        inputPanel.add (dateAndTimeField);
        inputPanel.add (new JLabel ("Invoice Number:"));
        inputPanel.add (invoiceNumberField);
        inputPanel.add (new JLabel ("Payment Status:"));
        inputPanel.add (paymentStatusField);
        inputPanel.add (new JLabel ("Amount Paid:"));
        inputPanel.add (amountPaidField);
        inputPanel.add (new JLabel ("Device ID:"));
        inputPanel.add (deviceIDField);

        // button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        addButton = new JButton("Add Appointment");
        updateButton = new JButton("Update Appointment");
        deleteButton = new JButton("Delete Appointment");
        viewCustomerButton = new JButton ("View Customer");
        viewTechnicianButton = new JButton ("View Technician");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add (viewCustomerButton);
        buttonPanel.add (viewTechnicianButton);
        
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
        exitButton.addActionListener(e -> dispose());

        // table selection listener
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = appointmentTable.getSelectedRow();
                if (selectedRow != -1) {
                    customerCodeField.setText (tableModel.getValueAt(selectedRow, 0).toString ());
                    technicianIDField.setText (tableModel.getValueAt(selectedRow, 1).toString ());
                    serviceStatusField.setText (tableModel.getValueAt(selectedRow, 2).toString ());
                    dateAndTimeField.setText (tableModel.getValueAt(selectedRow, 3).toString ());
                    invoiceNumberField.setText (tableModel.getValueAt(selectedRow, 4).toString ());
                    paymentStatusField.setText (tableModel.getValueAt(selectedRow, 5).toString ());
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
                serviceStatusField.getText (),
                dateAndTimeField.getText (),
                Integer.parseInt (invoiceNumberField.getText ()),
                paymentStatusField.getText (),
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
                serviceStatusField.getText (),
                dateAndTimeField.getText (),
                Integer.parseInt (invoiceNumberField.getText ()),
                paymentStatusField.getText (),
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
        serviceStatusField.setText ("");
        dateAndTimeField.setText ("");
        invoiceNumberField.setText ("");
        paymentStatusField.setText ("");
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
            Customer currentCustomer = customerController.getCustomerByCode (customerCode);
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
        
    }
} 

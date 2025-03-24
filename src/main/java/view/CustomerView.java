package view;

import controller.CustomerController;
import controller.DeviceController;
import model.entity.Customer;
import model.entity.Device;
import model.service.CustomerService;
import model.service.DeviceService;
import view.utils.ColorUtils;
import view.utils.GuiUtils;
import view.dialog.ScheduleRepairDialog;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class CustomerView extends JFrame {
    private String customerCode;
    private CustomerController customerController;
    private DeviceController deviceController;
    private Customer currentCustomer;
    
    public CustomerView(String customerCode) {
        this.customerCode = customerCode;
        CustomerService customerService = new CustomerService();
        this.customerController = new CustomerController(customerService);
        DeviceService deviceService = new DeviceService();
        this.deviceController = new DeviceController(deviceService);
        
        initializeFrame();
        loadCustomerData();
        setupComponents();
        setLookAndFeel();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeFrame() {
        setTitle("Customer Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
    }

    private void loadCustomerData() {
        try {
            currentCustomer = customerController.getCustomerByCode(customerCode);
            if (currentCustomer != null) {
                setTitle("Customer - " + currentCustomer.getFullName());
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading customer data: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorUtils.BACKGROUND);

        mainPanel.add(createNavigationBar(), BorderLayout.NORTH);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(ColorUtils.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 30, 30, 30));

        contentPanel.add(createCustomerInfoPanel(), BorderLayout.NORTH);
        contentPanel.add(createActionCardsPanel(), BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createNavigationBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(ColorUtils.PRIMARY);
        navBar.setPreferredSize(new Dimension(getWidth(), 60));
        navBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel logoLabel = new JLabel("Customer Portal");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(ColorUtils.TEXT_LIGHT);
        
        JPanel rightPanel = createRightPanel();
        navBar.add(logoLabel, BorderLayout.WEST);
        navBar.add(rightPanel, BorderLayout.EAST);

        return navBar;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new LandingView(customerController).setVisible(true);
            dispose();
        });
        rightPanel.add(logoutButton);

        return rightPanel;
    }

    private JPanel createCustomerInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 20, 10));
        panel.setBackground(ColorUtils.SECONDARY);
        panel.setBorder(new CompoundBorder(
            new LineBorder(ColorUtils.BORDER, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        try {
            // Get statistics
            List<String[]> activeRepairs = customerController.getCustomerAppointments(customerCode);
            long activeCount = activeRepairs.stream()
                .filter(repair -> repair[2].equals("In Progress"))
                .count();
            List<Device> devices = deviceController.getCustomerDevices(Integer.parseInt(customerCode));

            // Row 1: Customer ID and Stats Headers
            addInfoField(panel, "Customer ID:", customerCode);
            addStatHeader(panel, "Active Repairs");
            addStatHeader(panel, "Registered Devices");
            addStatHeader(panel, "Total Repairs");

            // Row 2: Customer Name and Stats Values
            if (currentCustomer != null) {
                addInfoField(panel, "Name:", currentCustomer.getFullName());
                addStatValue(panel, String.valueOf(activeCount));
                addStatValue(panel, String.valueOf(devices.size()));
                addStatValue(panel, String.valueOf(activeRepairs.size()));
            }

            // Row 3: Contact Info (spans all columns)
            if (currentCustomer != null) {
                addInfoField(panel, "Contact:", 
                    currentCustomer.getContactNumber());
                panel.add(new JLabel(""));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panel;
    }

    private void addStatHeader(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(ColorUtils.TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);
    }

    private void addStatValue(JPanel panel, String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(ColorUtils.PRIMARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);
    }

    private void addInfoField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fieldPanel.setOpaque(false);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 14));
        labelComp.setForeground(ColorUtils.TEXT_PRIMARY);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 14));
        valueComp.setForeground(ColorUtils.TEXT_PRIMARY);
        
        fieldPanel.add(labelComp);
        fieldPanel.add(valueComp);
        panel.add(fieldPanel);
    }

    private JPanel createActionCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(ColorUtils.BACKGROUND);

        panel.add(createCard("Schedule Repair", 
            "Request a new repair service for your device",
            "📅", this::showScheduleRepair));
            
        panel.add(createCard("View Devices", 
            "Manage your registered devices and their history",
            "💻", this::showDevices));
            
        panel.add(createCard("Repair History", 
            "View all your past repair records and invoices",
            "📋", this::showRepairHistory));
            
        panel.add(createCard("Check Status", 
            "Track the status of your ongoing repairs",
            "🔍", this::checkStatus));

        return panel;
    }

    private JPanel createCard(String title, String description, String icon, Runnable action) {
        JPanel card = new JPanel() {
            {
                setBackground(ColorUtils.BACKGROUND);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        setBackground(ColorUtils.SECONDARY);
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        setBackground(ColorUtils.BACKGROUND);
                    }
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        action.run();
                    }
                });
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
            new LineBorder(ColorUtils.BORDER, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(ColorUtils.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align: center; width: 100%;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(ColorUtils.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel);

        return card;
    }

    private void styleButton(JButton button) {
        button.setBackground(ColorUtils.PRIMARY);
        button.setForeground(ColorUtils.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ColorUtils.PRIMARY_DARK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ColorUtils.PRIMARY);
            }
        });
    }

    private void showRepairHistory() {
        try {
            List<String[]> history = customerController.getCustomerAppointments(customerCode);
            
            // Filter only completed and cancelled appointments
            List<String[]> filteredHistory = history.stream()
                .filter(app -> app[2].equals("Completed") || app[2].equals("Cancelled"))
                .collect(Collectors.toList());
            
            if (filteredHistory.isEmpty()) {
                showErrorDialog("No repair history found.");
                return;
            }

            // history table
            String[] columnNames = {"Invoice", "Date", "Status", "Payment", "Device"};
            Object[][] data = filteredHistory.toArray(new Object[0][]);

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);

            JDialog dialog = new JDialog(this, "Repair History", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            dialog.add(scrollPane);
            dialog.setVisible(true);

        } catch (SQLException e) {
            showErrorDialog("Error loading repair history: " + e.getMessage());
        }
    }

    private void showDevices() {
        try {
            List<Device> devices = deviceController.getCustomerDevices(Integer.parseInt(customerCode));
            if (devices.isEmpty()) {
                showErrorDialog("No devices registered.");
                return;
            }

            String[] columnNames = {"Device ID", "Type", "Brand", "Model", "Serial Number"};
            Object[][] data = devices.stream()
                .map(d -> new Object[]{
                    d.getDeviceId(),
                    d.getDeviceType(),
                    d.getBrand(),
                    d.getModel(),
                    d.getSerialNumber()
                })
                .toArray(Object[][]::new);

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);

            JDialog dialog = new JDialog(this, "Your Devices", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            dialog.add(scrollPane);
            dialog.setVisible(true);

        } catch (SQLException e) {
            showErrorDialog("Error loading devices: " + e.getMessage());
        }
    }

    private void showScheduleRepair() {
        try {
            List<Device> devices = deviceController.getCustomerDevices(Integer.parseInt(customerCode));
            
            ScheduleRepairDialog dialog = new ScheduleRepairDialog(
                this,  // parent frame
                Integer.parseInt(customerCode),
                devices
            );
            dialog.setVisible(true);
            
        } catch (SQLException ex) {
            showErrorDialog("Error loading customer data: " + ex.getMessage());
        }
    }

    private void checkStatus() {
        try {
            List<String[]> appointments = customerController.getCustomerAppointments(customerCode);
            
            // Active appointments
            List<String[]> activeAppointments = appointments.stream()
                .filter(app -> app[2].equals("Pending") || app[2].equals("In Progress") || app[2].equals("For Pickup"))
                .collect(Collectors.toList());

            if (activeAppointments.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No active repair services found.",
                    "Status Check",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // status table
            String[] columnNames = {"Invoice #", "Date & Time", "Service Status", "Payment Status", "Device", "Technician"};
            Object[][] data = activeAppointments.toArray(new Object[0][]);

            JTable table = new JTable(data, columnNames);
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    if (column == 2) { // Service Status column
                        String status = value.toString();
                        switch (status) {
                            case "Pending":
                                setForeground(new Color(128, 128, 128)); // Gray
                                break;
                            case "In Progress":
                                setForeground(new Color(0, 0, 255));     // Blue
                                break;
                            case "For Pickup":
                                setForeground(new Color(255, 140, 0));   // Orange
                                break;
                        }
                    } else {
                        setForeground(table.getForeground());
                    }
                    return c;
                }
            });

            table.getColumnModel().getColumn(0).setPreferredWidth(70);   // Invoice
            table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Date
            table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Status
            table.getColumnModel().getColumn(3).setPreferredWidth(200);  // Device
            table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Technician
            table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Payment

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            legendPanel.setBorder(BorderFactory.createTitledBorder("Status Legend"));
            
            String[] statuses = {"Pending", "In Progress", "For Pickup"};
            Color[] colors = {
                new Color(128, 128, 128),
                new Color(0, 0, 255),
                new Color(255, 140, 0)
            };
            
            for (int i = 0; i < statuses.length; i++) {
                JLabel label = new JLabel(statuses[i]);
                label.setForeground(colors[i]);
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                legendPanel.add(label);
            }

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(legendPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            JDialog dialog = new JDialog(this, "Repair Status", true);
            dialog.setSize(800, 400);
            dialog.setLocationRelativeTo(this);
            
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            showErrorDialog("Error checking repair status: " + ex.getMessage());
        }
    }
} 
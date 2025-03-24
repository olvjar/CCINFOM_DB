package view;

import view.dialog.LoginDialog;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import view.utils.GuiUtils;
import view.utils.ColorUtils;
import controller.CustomerController;
import model.entity.Customer;
import javax.swing.border.EmptyBorder;

public class LandingView extends JFrame {
    private JPanel loginPanel;
    private JPanel signupPanel;
    private CustomerController customerController;
    private CardLayout cardLayout;
    
    public LandingView(CustomerController customerController) {
        this.customerController = customerController;
        
        setTitle("Computer Repair Shop");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Computer Repair Shop");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton technicianButton = new JButton("Technician Access");
        technicianButton.addActionListener(e -> {
            LoginDialog loginDialog = new LoginDialog(this, false, customerController);
            loginDialog.setVisible(true);
            
            if (loginDialog.isAuthenticated()) {
                new TechnicianView(loginDialog.getAuthenticatedId(), customerController).setVisible(true);
                dispose();
            }
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(technicianButton, BorderLayout.EAST);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        loginPanel = createLoginPanel();
        signupPanel = createSignupPanel();
        
        contentPanel.add(loginPanel, "login");
        contentPanel.add(signupPanel, "signup");
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Customer Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JButton loginButton = new JButton("Login");
        JButton signupLink = new JButton("Don't have any records? Sign up");
        signupLink.setBorderPainted(false);
        signupLink.setContentAreaFilled(false);
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
        
        gbc.gridy = 4;
        panel.add(signupLink, gbc);
        
        loginButton.addActionListener(e -> handleLogin(firstNameField.getText(), lastNameField.getText()));
        signupLink.addActionListener(e -> cardLayout.show(panel.getParent(), "signup"));
        
        return panel;
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField contactField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JButton signupButton = new JButton("Sign Up");
        JButton loginLink = new JButton("Already have a record? Login");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(signupButton, gbc);
        
        gbc.gridy = 6;
        panel.add(loginLink, gbc);
        
        loginLink.addActionListener(e -> cardLayout.show(panel.getParent(), "login"));
        signupButton.addActionListener(e -> handleSignup(firstNameField, lastNameField, 
            contactField, addressField));
        
        return panel;
    }
    
    private void handleLogin(String firstName, String lastName) {
        try {
            String customerCode = customerController.validateCustomer(firstName, lastName);
            if (customerCode != null) {
                new CustomerView(customerCode).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Customer not found. Please check your name or sign up.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error during login: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleSignup(JTextField firstNameField, JTextField lastNameField,
                            JTextField contactField, JTextField addressField) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String customerCode = customerController.validateCustomer(firstName, lastName);
            if (customerCode != null) {
                int option = JOptionPane.showConfirmDialog(this,
                    "A customer with this name already exists. Would you like to login instead?",
                    "Customer Exists",
                    JOptionPane.YES_NO_OPTION);
                    
                if (option == JOptionPane.YES_OPTION) {
                    cardLayout.show(loginPanel.getParent(), "login");
                }
            } else {
                Customer customer = new Customer(null, firstName, lastName, contact, address);
                customerController.addCustomer(customer);
                
                customerCode = customerController.validateCustomer(firstName, lastName);
                
                JOptionPane.showMessageDialog(this,
                    "Account created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                new CustomerView(customerCode).setVisible(true);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error creating account: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 
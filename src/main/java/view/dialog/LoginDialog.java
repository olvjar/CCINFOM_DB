package view.dialog;

import controller.CustomerController;
import controller.TechnicianController;
import view.CustomerView;
import view.TechnicianView;
import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField idField;
    private boolean isCustomer;
    private boolean authenticated = false;
    private String authenticatedId;
    private CustomerController customerController = new CustomerController();
    private TechnicianController technicianController = new TechnicianController();

    public LoginDialog(JFrame parent, boolean isCustomer) {
        super(parent, "Login", true);
        this.isCustomer = isCustomer;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (isCustomer) {
            firstNameField = new JTextField(20);
            lastNameField = new JTextField(20);
            inputPanel.add(new JLabel("First Name:"));
            inputPanel.add(firstNameField);
            inputPanel.add(new JLabel("Last Name:"));
            inputPanel.add(lastNameField);
        } else {
            idField = new JTextField(20);
            firstNameField = new JTextField(20);
            lastNameField = new JTextField(20);
            inputPanel.add(new JLabel("Technician ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("First Name:"));
            inputPanel.add(firstNameField);
            inputPanel.add(new JLabel("Last Name:"));
            inputPanel.add(lastNameField);
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        loginButton.addActionListener(e -> validateLogin());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Add panels
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void validateLogin() {
        try {
            if (isCustomer) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                
                String customerCode = customerController.validateCustomer(firstName, lastName);
                if (customerCode != null) {
                    authenticated = true;
                    authenticatedId = customerCode;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid customer name. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                String techId = idField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                
                if (technicianController.validateTechnician(techId, firstName, lastName)) {
                    authenticated = true;
                    authenticatedId = techId;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid technician credentials. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during login: " + e.getMessage(),
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getAuthenticatedId() {
        return authenticatedId;
    }
} 
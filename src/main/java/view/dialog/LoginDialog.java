package view.dialog;

import controller.CustomerController;
import controller.TechnicianController;
import java.awt.*;
import javax.swing.*;

public class LoginDialog extends JDialog {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField idField;
    private boolean isCustomer;
    private boolean authenticated = false;
    private String authenticatedId;
    private CustomerController customerController = new CustomerController();
    private TechnicianController technicianController =
        new TechnicianController();

    public LoginDialog(JFrame parent, boolean isCustomer) {
        super(parent, "Login", true);
        this.isCustomer = isCustomer;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        if (isCustomer) {
            addInputField(
                inputPanel,
                gbc,
                "First Name:",
                firstNameField = new JTextField(20)
            );
            addInputField(
                inputPanel,
                gbc,
                "Last Name:",
                lastNameField = new JTextField(20)
            );
        } else {
            addInputField(
                inputPanel,
                gbc,
                "Technician ID:",
                idField = new JTextField(20)
            );
            addInputField(
                inputPanel,
                gbc,
                "First Name:",
                firstNameField = new JTextField(20)
            );
            addInputField(
                inputPanel,
                gbc,
                "Last Name:",
                lastNameField = new JTextField(20)
            );
        }

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(inputPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        // Style buttons
        styleButton(loginButton);
        styleButton(cancelButton);

        loginButton.addActionListener(e -> validateLogin());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 3;
        add(buttonPanel, gbc);
    }

    private void addInputField(
        JPanel panel,
        GridBagConstraints gbc,
        String labelText,
        JTextField textField
    ) {
        JLabel label = new JLabel(labelText);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(textField, gbc);
        gbc.gridy++;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 40));
    }

    private void validateLogin() {
        try {
            if (isCustomer) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();

                String customerCode = customerController.validateCustomer(
                    firstName,
                    lastName
                );
                if (customerCode != null) {
                    authenticated = true;
                    authenticatedId = customerCode;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid customer name. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                String techId = idField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();

                if (
                    technicianController.validateTechnician(
                        techId,
                        firstName,
                        lastName
                    )
                ) {
                    authenticated = true;
                    authenticatedId = techId;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid technician credentials. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error during login: " + e.getMessage(),
                "Login Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getAuthenticatedId() {
        return authenticatedId;
    }
}

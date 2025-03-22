package view;

import view.dialog.LoginDialog;
import javax.swing.*;
import java.awt.*;
import view.utils.GuiUtils;

public class LandingView extends JFrame {
    public LandingView() {
        setTitle("Computer Repair Shop");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel welcomeLabel = new JLabel("Welcome to Computer Repair Shop");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Please select your role to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        JButton customerButton = view.utils.GuiUtils.createModuleButton("Customer Access", 
            "Schedule repairs and view status");
        JButton technicianButton = view.utils.GuiUtils.createModuleButton("Technician Access", 
            "Manage repairs and inventory");
            

        // action listeners
        customerButton.addActionListener(e -> {
            LoginDialog loginDialog = new LoginDialog(this, true);
            loginDialog.setVisible(true);
            
            if (loginDialog.isAuthenticated()) {
                new CustomerView(loginDialog.getAuthenticatedId()).setVisible(true);
                dispose();
            }
        });
        
        technicianButton.addActionListener(e -> {
            LoginDialog loginDialog = new LoginDialog(this, false);
            loginDialog.setVisible(true);
            
            if (loginDialog.isAuthenticated()) {
                new TechnicianView(loginDialog.getAuthenticatedId()).setVisible(true);
                dispose();
            }
        });
        

        buttonsPanel.add(customerButton);
        buttonsPanel.add(technicianButton);
        
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(buttonsPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel);
    }
} 
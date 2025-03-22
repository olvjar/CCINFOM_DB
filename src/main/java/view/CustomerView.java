package view;

import view.management.CustomerManagementFrame;
import javax.swing.*;
import java.awt.*;

public class CustomerView extends JFrame {
    private String customerCode;
    
    public CustomerView(String customerCode) {
        this.customerCode = customerCode;
        setTitle("Customer Portal - ID: " + customerCode);
        initializeFrame();
        setupComponents();
    }

    private void initializeFrame() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Customer Portal", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        JButton viewAppointmentsButton = new JButton("View My Appointments");
        JButton scheduleAppointmentButton = new JButton("Schedule New Appointment");
        JButton logoutButton = new JButton("Logout");

        viewAppointmentsButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "View appointments feature coming soon!")
        );
        
        scheduleAppointmentButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Schedule appointment feature coming soon!")
        );
        
        logoutButton.addActionListener(e -> {
            new LandingView().setVisible(true);
            dispose();
        });

        buttonsPanel.add(viewAppointmentsButton);
        buttonsPanel.add(scheduleAppointmentButton);
        buttonsPanel.add(logoutButton);

        // Add padding around buttons panel
        JPanel centeredButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centeredButtonsPanel.add(buttonsPanel);
        mainPanel.add(centeredButtonsPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
} 
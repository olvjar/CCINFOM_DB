package view;

import view.panel.ModuleButtonsPanel;
import view.panel.ReportsPanel;
import javax.swing.*;
import java.awt.*;

public class TechnicianView extends JFrame {
    private String technicianId;
    
    public TechnicianView(String technicianId) {
        this.technicianId = technicianId;
        setTitle("Repair Shop Management - Technician: " + technicianId);
        initializeFrame();
        setupComponents();
    }

    private void initializeFrame() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        addTitle(mainPanel, gbc);

        // module panel
        ModuleButtonsPanel moduleButtonsPanel = new ModuleButtonsPanel();
        gbc.gridy = 1;
        mainPanel.add(moduleButtonsPanel, gbc);

        // reports panel
        ReportsPanel reportsPanel = new ReportsPanel();
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(reportsPanel, gbc);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LandingView().setVisible(true);
            dispose();
        });
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(logoutButton, gbc);

        add(mainPanel);
    }

    private void addTitle(JPanel panel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Repair Shop Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
    }
} 
package view.panel;

import view.management.CustomerManagementFrame;
import view.management.TechnicianManagementFrame;
import view.management.InventoryManagementFrame;
import view.management.AppointmentManagementFrame;
import util.GuiUtils;
import javax.swing.*;
import java.awt.*;

public class ModuleButtonsPanel extends JPanel {
    public ModuleButtonsPanel() {
        setLayout(new GridLayout(2, 2, 10, 10));
        initializeButtons();
    }

    private void initializeButtons() {
        JButton customerButton = GuiUtils.createModuleButton(
            "Customer Management", 
            "Manage customer records and services"
        );
        JButton technicianButton = GuiUtils.createModuleButton(
            "Technician Management", 
            "Manage technician records and assignments"
        );
        JButton inventoryButton = GuiUtils.createModuleButton(
            "Inventory Management", 
            "Manage product inventory and stock"
        );
        JButton appointmentButton = GuiUtils.createModuleButton(
            "Appointment Management", 
            "Manage service appointments and schedules"
        );

        setupActionListeners(customerButton, technicianButton, inventoryButton, appointmentButton);

        add(customerButton);
        add(technicianButton);
        add(inventoryButton);
        add(appointmentButton);
    }

    private void setupActionListeners(JButton... buttons) {
        buttons[0].addActionListener(e -> 
            new CustomerManagementFrame().setVisible(true)
        );
        buttons[1].addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Technician Management module is under development")
        );
        buttons[2].addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Inventory Management module is under development")
        );
        buttons[3].addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Appointment Management module is under development")
        );
    }
} 
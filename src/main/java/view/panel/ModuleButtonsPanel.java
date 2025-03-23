package view.panel;

import controller.CustomerController;
import model.service.CustomerService;
import view.management.CustomerManagementFrame;
import view.management.TechnicianManagementFrame;
import view.management.InventoryManagementFrame;
import view.management.AppointmentManagementFrame;
import view.utils.GuiUtils;
import javax.swing.*;
import java.awt.*;

public class ModuleButtonsPanel extends JPanel {
    public ModuleButtonsPanel() {
        setLayout(new GridLayout(2, 3, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Modules"));
        initializeButtons();
    }

    private void initializeButtons() {
        JButton customerButton = view.utils.GuiUtils.createModuleButton(
            "Customer Management", 
            "Manage customer records and services"
        );
        JButton technicianButton = view.utils.GuiUtils.createModuleButton(
            "Technician Management", 
            "Manage technician records and assignments"
        );
        JButton inventoryButton = view.utils.GuiUtils.createModuleButton(
            "Inventory Management", 
            "Manage product inventory and stock"
        );
        JButton appointmentButton = view.utils.GuiUtils.createModuleButton(
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
        buttons[0].addActionListener(e -> openCustomerManagement());
        buttons[1].addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Technician Management module is under development")
        );
        buttons[2].addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Inventory Management module is under development")
        );
        buttons[3].addActionListener(e -> 
            new AppointmentManagementFrame().setVisible(true)
        );
    }

    private void openCustomerManagement() {
        CustomerService customerService = new CustomerService();
        CustomerController customerController = new CustomerController(customerService);
        CustomerManagementFrame frame = new CustomerManagementFrame(customerController);
        frame.setVisible(true);
    }
} 

package view.panel;

import controller.InventoryController;
import model.service.InventoryService;
import controller.CustomerController;
import model.service.CustomerService;
import controller.AppointmentController;
import model.service.AppointmentService;
import view.management.CustomerManagementFrame;
import view.management.TechnicianManagementFrame;
import view.management.InventoryManagementFrame;
import view.management.AppointmentManagementFrame;
import view.utils.GuiUtils;
import javax.swing.*;
import java.awt.*;
import controller.TechnicianController;
import model.service.TechnicianService;

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
        buttons[1].addActionListener(e -> openTechnicianManagement());
        buttons[2].addActionListener(e -> openInventoryManagement());
        buttons[3].addActionListener(e -> openAppointmentManagement());
    }
    

    private void openCustomerManagement() {
        CustomerService customerService = new CustomerService();
        CustomerController customerController = new CustomerController(customerService);
        CustomerManagementFrame frame = new CustomerManagementFrame(customerController);
        frame.setVisible(true);
    }

    private void openTechnicianManagement() {
        TechnicianService technicianService = new TechnicianService();
        TechnicianController technicianController = new TechnicianController(technicianService);
        TechnicianManagementFrame frame = new TechnicianManagementFrame(technicianController);
        frame.setVisible(true);
    }

    private void openInventoryManagement() {
        InventoryManagementFrame frame = new InventoryManagementFrame(new InventoryController(new InventoryService()));
        frame.setVisible(true);
    }

    private void openAppointmentManagement() {
        AppointmentManagementFrame frame = new AppointmentManagementFrame(new AppointmentController(new AppointmentService()));
        frame.setVisible(true);
    }

} 

package view.panel;

import controller.InventoryController;
import reports.*;
import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel {
    private final InventoryController inventoryController;

    public ReportsPanel(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        initializePanel();
    }

    private void initializePanel() {
        setBorder(BorderFactory.createTitledBorder("Reports"));
        setLayout(new GridLayout(2, 2, 10, 10));
        
        JButton repairHistoryButton = createReportButton("Repair History Report", "View repair history");
        JButton customerEngagementButton = createReportButton("Customer Engagement Report", "View customer engagement");
        JButton inventoryUsageButton = createReportButton("Inventory Usage Report", "View inventory usage");
        JButton revenueButton = createReportButton("Revenue Report", "View revenue statistics");
        
        repairHistoryButton.addActionListener(e -> {
            RepairHistoryReport report = new RepairHistoryReport();
            report.generateReport();
        });

        customerEngagementButton.addActionListener(e -> {
            CustomerEngagementReport report = new CustomerEngagementReport();
            report.generateReport();
        });
        
        inventoryUsageButton.addActionListener(e -> {
            InventoryUsageReport report = new InventoryUsageReport(inventoryController);
            report.generateReport();
        });
        
        revenueButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Revenue Report is under development.", 
                "Report Generation", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        add(repairHistoryButton);
        add(customerEngagementButton);
        add(inventoryUsageButton);
        add(revenueButton);
    }
    
    private JButton createReportButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        return button;
    }
}

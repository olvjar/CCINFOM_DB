package view.panel;

import reports.*;
import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel {
    public ReportsPanel() {
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
            JOptionPane.showMessageDialog(this, 
                "Customer Engagement Report is under development.", 
                "Report Generation", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        inventoryUsageButton.addActionListener(e -> {
            // TEMP
            JOptionPane.showMessageDialog(this, 
                "Inventory Usage Report is under development.", 
                "Report Generation", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        revenueButton.addActionListener(e -> {
            // TEMP
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
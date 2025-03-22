package view.panel;

import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setBorder(BorderFactory.createTitledBorder("Reports"));
        setLayout(new GridLayout(2, 2, 10, 10));
        
        add(createReportButton("Repair History Report", "View repair history"));
        add(createReportButton("Customer Engagement Report", "View customer engagement"));
        add(createReportButton("Inventory Usage Report", "View inventory usage"));
        add(createReportButton("Revenue Report", "View revenue statistics"));
    }
    
    private JButton createReportButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                text + " report is under development", 
                "Report", 
                JOptionPane.INFORMATION_MESSAGE)
        );
        return button;
    }
} 
package reports;

import controller.InventoryController;
import util.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class InventoryUsageReport {
    private JDialog reportDialog;
    private static final String[] COLUMN_NAMES = {
        "Product Code", "Product Name", "Unit Price", "Quantity Used", "Total Cost"
    };
    private static final String[] MONTHS = {
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    };
    
    private final InventoryController controller;
    private final NumberFormat pesoFormat;

    public InventoryUsageReport(InventoryController controller) {
        this.controller = controller;
        this.pesoFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    }

    public void generateReport() {
        try {
            showPeriodFilterDialog();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error generating report: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPeriodFilterDialog() {
        JComboBox<Integer> yearCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>(MONTHS);
        monthCombo.insertItemAt("All Months", 0);
        monthCombo.setSelectedIndex(0);

        // Populate years (2023-2024 based on sample data)
        for (int year = 2023; year <= 2024; year++) {
            yearCombo.addItem(year);
        }
        yearCombo.setSelectedItem(LocalDate.now().getYear());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearCombo);
        inputPanel.add(new JLabel("Month:"));
        inputPanel.add(monthCombo);

        int result = JOptionPane.showConfirmDialog(null, 
            inputPanel, 
            "Select Reporting Period", 
            JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int year = (Integer) yearCombo.getSelectedItem();
            int month = monthCombo.getSelectedIndex(); // 0 = All Months

            try {
                List<Map<String, Object>> reportData = controller.getInventoryUsageReport(
                    year, 
                    month > 0 ? month : -1
                );
                
                if (reportData.isEmpty()) {
                    JOptionPane.showMessageDialog(null, 
                        "No inventory usage found for selected period", 
                        "Report Result", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                displayReport(reportData, year, month);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Database error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayReport(List<Map<String, Object>> reportData, int year, int month) {
        if (reportDialog != null && reportDialog.isDisplayable()) {
            reportDialog.dispose();
        }

        reportDialog = new JDialog((Frame) null, "Inventory Usage Report", true);
        reportDialog.setSize(800, 600);
        reportDialog.setLayout(new BorderLayout(10, 10));
        reportDialog.setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String periodText = month > 0 ? 
            MONTHS[month-1] + " " + year : 
            "Year " + year;
        
        JLabel titleLabel = new JLabel("Inventory Usage Report - " + periodText);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Table Data
        Object[][] data = new Object[reportData.size()][COLUMN_NAMES.length];
        double grandTotal = 0;

        for (int i = 0; i < reportData.size(); i++) {
            Map<String, Object> row = reportData.get(i);
            data[i][0] = row.get("productCode");
            data[i][1] = row.get("productName");
            data[i][2] = pesoFormat.format(row.get("priceEach"));
            data[i][3] = row.get("totalUsed");
            data[i][4] = pesoFormat.format(row.get("totalCost"));
            grandTotal += (Double) row.get("totalCost");
        }

        // Table
        JTable table = new JTable(data, COLUMN_NAMES);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);

        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Grand Total: " + pesoFormat.format(grandTotal));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(totalLabel);

        // Assemble components
        reportDialog.add(headerPanel, BorderLayout.NORTH);
        reportDialog.add(scrollPane, BorderLayout.CENTER);
        reportDialog.add(summaryPanel, BorderLayout.SOUTH);
        reportDialog.setVisible(true);
    }
}

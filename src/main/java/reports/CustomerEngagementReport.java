package reports;

import util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerEngagementReport {

    private JDialog reportDialog;
    private DefaultTableModel tableModel;

    public void generateReport() {
        try {
            List<CustomerEngagementRecord> customerEngagementData = getCustomerEngagementData();

            if (customerEngagementData.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No customer engagement data available.",
                        "Report Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            displayReport(customerEngagementData);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error generating customer engagement report: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<CustomerEngagementRecord> getCustomerEngagementData() throws SQLException {
        String sql = "SELECT c.customerCode, c.firstName, c.lastName, " +
                "COUNT(a.invoiceNumber) AS totalRepairs, " +
                "SUM(CASE WHEN a.serviceStatus = 'Completed' THEN 1 ELSE 0 END) AS completedRepairs, " +
                "IFNULL(AVG(a.amountPaid), 0) AS averageRepairCost " +
                "FROM customers c " +
                "LEFT JOIN appointments a ON c.customerCode = a.customerCode " +
                "GROUP BY c.customerCode, c.firstName, c.lastName " +
                "ORDER BY totalRepairs DESC";

        List<CustomerEngagementRecord> customerData = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CustomerEngagementRecord record = new CustomerEngagementRecord(
                        rs.getString("customerCode"),
                        rs.getString("firstName") + " " + rs.getString("lastName"),
                        rs.getInt("totalRepairs"),
                        rs.getInt("completedRepairs"),
                        rs.getDouble("averageRepairCost")
                );
                customerData.add(record);
            }
        }
        return customerData;
    }

    private void displayReport(List<CustomerEngagementRecord> customerData) {
        if (reportDialog != null && reportDialog.isDisplayable()) {
            reportDialog.dispose();
        }

        reportDialog = new JDialog((Frame) null, "Customer Engagement Report", true);
        reportDialog.setLayout(new BorderLayout(10, 10));
        reportDialog.setSize(800, 600);
        reportDialog.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Customer Engagement Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        String[] columnNames = {"Customer Code", "Customer Name", "Total Repairs", "Completed Repairs", "Average Repair Cost"};
        tableModel = new DefaultTableModel(columnNames, 0);

        for (CustomerEngagementRecord record : customerData) {
            Object[] row = {
                    record.getCustomerCode(),
                    record.getCustomerName(),
                    record.getTotalRepairs(),
                    record.getCompletedRepairs(),
                    String.format("₱%.2f", record.getAverageRepairCost())
            };
            tableModel.addRow(row);
        }

        JTable reportTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        reportDialog.add(headerPanel, BorderLayout.NORTH);
        reportDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> reportDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        reportDialog.add(buttonPanel, BorderLayout.SOUTH);

        reportDialog.setVisible(true);
    }

    private static class CustomerEngagementRecord {
        private final String customerCode;
        private final String customerName;
        private final int totalRepairs;
        private final int completedRepairs;
        private final double averageRepairCost;

        public CustomerEngagementRecord(String customerCode, String customerName, int totalRepairs, int completedRepairs, double averageRepairCost) {
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.totalRepairs = totalRepairs;
            this.completedRepairs = completedRepairs;
            this.averageRepairCost = averageRepairCost;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public String getCustomerName() {
            return customerName;
        }

        public int getTotalRepairs() {
            return totalRepairs;
        }

        public int getCompletedRepairs() {
            return completedRepairs;
        }

        public double getAverageRepairCost() {
            return averageRepairCost;
        }
    }
}

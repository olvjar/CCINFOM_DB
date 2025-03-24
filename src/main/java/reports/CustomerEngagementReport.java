package reports;

import util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
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

            displayReport(customerEngagementData, "All Time");

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

    private List<CustomerEngagementRecord> getEngagementsByYear(int year) throws SQLException {
        String sql = "SELECT c.customerCode, c.firstName, c.lastName, " +
                "COUNT(a.invoiceNumber) AS totalRepairs, " +
                "SUM(CASE WHEN a.serviceStatus = 'Completed' THEN 1 ELSE 0 END) AS completedRepairs, " +
                "IFNULL(AVG(a.amountPaid), 0) AS averageRepairCost " +
                "FROM customers c " +
                "LEFT JOIN appointments a ON c.customerCode = a.customerCode " +
                "WHERE YEAR(a.dateAndTime) = ? " +
                "GROUP BY c.customerCode, c.firstName, c.lastName " +
                "ORDER BY totalRepairs DESC";
    
        List<CustomerEngagementRecord> customerData = new ArrayList<>();
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customerData.add(new CustomerEngagementRecord(
                            rs.getString("customerCode"),
                            rs.getString("firstName") + " " + rs.getString("lastName"),
                            rs.getInt("totalRepairs"),
                            rs.getInt("completedRepairs"),
                            rs.getDouble("averageRepairCost")
                    ));
                }
            }
        }
        return customerData;
    }

    private List<CustomerEngagementRecord> getEngagementsByMonth(int month) throws SQLException {
        String sql = "SELECT c.customerCode, c.firstName, c.lastName, " +
                "COUNT(a.invoiceNumber) AS totalRepairs, " +
                "SUM(CASE WHEN a.serviceStatus = 'Completed' THEN 1 ELSE 0 END) AS completedRepairs, " +
                "IFNULL(AVG(a.amountPaid), 0) AS averageRepairCost " +
                "FROM customers c " +
                "LEFT JOIN appointments a ON c.customerCode = a.customerCode " +
                "WHERE MONTH(a.dateAndTime) = ? " +
                "GROUP BY c.customerCode, c.firstName, c.lastName " +
                "ORDER BY totalRepairs DESC";
    
        List<CustomerEngagementRecord> customerData = new ArrayList<>();
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, month);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customerData.add(new CustomerEngagementRecord(
                            rs.getString("customerCode"),
                            rs.getString("firstName") + " " + rs.getString("lastName"),
                            rs.getInt("totalRepairs"),
                            rs.getInt("completedRepairs"),
                            rs.getDouble("averageRepairCost")
                    ));
                }
            }
        }
        return customerData;
    }
    

    private List<CustomerEngagementRecord> getEngagementsByYearAndMonth(int year, int month) throws SQLException {
        String sql = "SELECT c.customerCode, c.firstName, c.lastName, " +
                "COUNT(a.invoiceNumber) AS totalRepairs, " +
                "SUM(CASE WHEN a.serviceStatus = 'Completed' THEN 1 ELSE 0 END) AS completedRepairs, " +
                "IFNULL(AVG(a.amountPaid), 0) AS averageRepairCost " +
                "FROM customers c " +
                "LEFT JOIN appointments a ON c.customerCode = a.customerCode " +
                "WHERE YEAR(a.dateAndTime) = ? AND MONTH(a.dateAndTime) = ? " +
                "GROUP BY c.customerCode, c.firstName, c.lastName " +
                "ORDER BY totalRepairs DESC";

        List<CustomerEngagementRecord> customerData = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            try (ResultSet rs = pstmt.executeQuery()) {
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
        }
        return customerData;
    }

    private void displayReport(List<CustomerEngagementRecord> customerData, String period) {
        if (reportDialog != null && reportDialog.isDisplayable()) {
            reportDialog.dispose();
        }

        reportDialog = new JDialog((Frame) null, "Customer Engagement Report (" + period + ")", true);
        reportDialog.setLayout(new BorderLayout(10, 10));
        reportDialog.setSize(800, 600);
        reportDialog.setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Customer Engagement Report (" + period + ")");
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

        JButton filterButton = new JButton("Filter by Year/Month");
        filterButton.addActionListener(e -> showMonthFilterDialog());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(filterButton);
        reportDialog.add(buttonPanel, BorderLayout.SOUTH);

        reportDialog.setVisible(true);
    }

    private void showMonthFilterDialog() {
        JComboBox<Integer> yearCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
    
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear; year >= currentYear - 10; year--) {
            yearCombo.addItem(year);
        }
    
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearCombo);
        inputPanel.add(new JLabel("Month:"));
        inputPanel.add(monthCombo);
    
        String[] filterOptions = {"Year + Month", "Year Only", "Month Only"};
        JComboBox<String> filterCombo = new JComboBox<>(filterOptions);
        inputPanel.add(new JLabel("Filter By:"));
        inputPanel.add(filterCombo);
    
        int result = JOptionPane.showConfirmDialog(reportDialog, inputPanel,
                "Filter Options", JOptionPane.OK_CANCEL_OPTION);
    
        if (result == JOptionPane.OK_OPTION) {
            int year = (Integer) yearCombo.getSelectedItem();
            int month = monthCombo.getSelectedIndex() + 1;
            String filterType = (String) filterCombo.getSelectedItem();
    
            try {
                List<CustomerEngagementRecord> filteredData = new ArrayList<>();
    
                switch (filterType) {
                    case "Year + Month":
                        filteredData = getEngagementsByYearAndMonth(year, month);
                        displayReport(filteredData, monthCombo.getSelectedItem() + " " + year);
                        break;
                    case "Year Only":
                        filteredData = getEngagementsByYear(year);
                        displayReport(filteredData, "Year: " + year);
                        break;
                    case "Month Only":
                        filteredData = getEngagementsByMonth(month);
                        displayReport(filteredData, "Month: " + monthCombo.getSelectedItem());
                        break;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(reportDialog,
                        "Error filtering data: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private static class CustomerEngagementRecord {
        private String customerCode;
        private String customerName;
        private int totalRepairs;
        private int completedRepairs;
        private double averageRepairCost;

        public CustomerEngagementRecord(String customerCode, String customerName, int totalRepairs, int completedRepairs, double averageRepairCost) {
            this.customerCode = customerCode;
            this.customerName = customerName;
            this.totalRepairs = totalRepairs;
            this.completedRepairs = completedRepairs;
            this.averageRepairCost = averageRepairCost;
        }
        public String getCustomerCode() { return customerCode; }
        public String getCustomerName() { return customerName; }
        public int getTotalRepairs() { return totalRepairs; }
        public int getCompletedRepairs() { return completedRepairs; }
        public double getAverageRepairCost() { return averageRepairCost; }
    }
}

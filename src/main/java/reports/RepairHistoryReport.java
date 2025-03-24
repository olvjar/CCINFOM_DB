package reports;

import model.entity.Appointment;
import util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RepairHistoryReport {
    
    private JDialog reportDialog;
    private List<RepairRecord> allRepairs;
    private static final String[] COLUMN_NAMES = {
        "Invoice #", "Date", "Customer", "Device", "Device Description", "Technician", "Amount"
    };
    private static final String[] MONTHS = {
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    };
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    private static class RepairRecord {
        final int invoiceNumber;
        final LocalDateTime dateTime;
        final String status;
        final double amountPaid;
        final String customerName;
        final String technicianName;
        final int deviceId;
        final String deviceDetails;
        final String serialNumber;
        final String deviceDescription;
        
        public RepairRecord(int invoiceNumber, LocalDateTime dateTime, String status,
                           double amountPaid, String customerName, String technicianName,
                           int deviceId, String deviceDetails, String serialNumber,
                           String deviceDescription) {
            this.invoiceNumber = invoiceNumber;
            this.dateTime = dateTime;
            this.status = status;
            this.amountPaid = amountPaid;
            this.customerName = customerName;
            this.technicianName = technicianName;
            this.deviceId = deviceId;
            this.deviceDetails = deviceDetails;
            this.serialNumber = serialNumber;
            this.deviceDescription = deviceDescription;
        }
    }

    public void generateReport() {
        try {
            allRepairs = getAllCompletedRepairs();
            
            if (allRepairs.isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                        "No completed repairs found in the system.", 
                        "Report Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            displayReport(allRepairs, "All Time");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                    "Error generating report: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<RepairRecord> getAllCompletedRepairs() throws SQLException {
        String sql = "SELECT a.invoiceNumber, a.dateAndTime, a.serviceStatus, a.amountPaid, "
                + "c.firstName, c.lastName, CONCAT(t.firstName, ' ', t.lastName) as techName, "
                + "d.deviceID, d.deviceType, d.brand, d.model, d.serialNumber, d.description "
                + "FROM appointments a "
                + "JOIN customers c ON a.customerCode = c.customerCode "
                + "LEFT JOIN technicians t ON a.technicianID = t.technicianID "
                + "JOIN devices d ON a.deviceID = d.deviceID "
                + "WHERE a.serviceStatus = 'Completed' "
                + "ORDER BY a.dateAndTime DESC";
        
        List<RepairRecord> repairs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                repairs.add(createRepairRecordFromResultSet(rs));
            }
        }
        return repairs;
    }
    
    private RepairRecord createRepairRecordFromResultSet(ResultSet rs) throws SQLException {
        String technicianName = rs.getString("techName") != null ?
            rs.getString("techName") : "Not assigned";
            
        String deviceDetails = String.format("%s - %s %s",
            rs.getString("deviceType"),
            rs.getString("brand"),
            rs.getString("model"));

        return new RepairRecord(
            rs.getInt("invoiceNumber"),
            rs.getTimestamp("dateAndTime").toLocalDateTime(),
            rs.getString("serviceStatus"),
            rs.getDouble("amountPaid"),
            rs.getString("firstName") + " " + rs.getString("lastName"),
            technicianName,
            rs.getInt("deviceID"),
            deviceDetails,
            rs.getString("serialNumber"),
            rs.getString("description")
        );
    }
    
    private List<RepairRecord> getCompletedRepairsByYear(int year) {
        return allRepairs.stream()
            .filter(r -> r.dateTime.getYear() == year)
            .toList();
    }
    
    private List<RepairRecord> getCompletedRepairsByYearAndMonth(int year, int month) {
        return allRepairs.stream()
            .filter(r -> r.dateTime.getYear() == year && r.dateTime.getMonthValue() == month)
            .toList();
    }
    
    private void displayReport(List<RepairRecord> repairs, String periodText) {
        if (reportDialog != null && reportDialog.isDisplayable()) {
            reportDialog.dispose();
        }
        
        reportDialog = createReportDialog();
        JPanel headerPanel = createHeaderPanel(periodText);
        JPanel contentPanel = createContentPanel(repairs);
        
        reportDialog.add(headerPanel, BorderLayout.NORTH);
        reportDialog.add(contentPanel, BorderLayout.CENTER);
        reportDialog.setVisible(true);
    }
    
    private JDialog createReportDialog() {
        JDialog dialog = new JDialog((Frame)null, "Repair History Report", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(1200, 600);
        dialog.setLocationRelativeTo(null);
        return dialog;
    }
    
    private JPanel createHeaderPanel(String periodText) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        headerPanel.add(createTitlePanel(periodText), BorderLayout.WEST);
        headerPanel.add(createFilterPanel(), BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTitlePanel(String periodText) {
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Completed Repairs Report");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        titlePanel.add(titleLabel);
        titlePanel.add(new JLabel("Period: " + periodText));
        return titlePanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton filterByYearButton = new JButton("Generate by Year");
        JButton filterByMonthButton = new JButton("Generate by Month");
        JButton showAllButton = new JButton("Show All");
        
        filterByYearButton.addActionListener(e -> showYearFilterDialog());
        filterByMonthButton.addActionListener(e -> showMonthFilterDialog());
        showAllButton.addActionListener(e -> displayReport(allRepairs, "All Time"));
        
        filterPanel.add(filterByYearButton);
        filterPanel.add(filterByMonthButton);
        filterPanel.add(showAllButton);
        
        return filterPanel;
    }
    
    private JPanel createContentPanel(List<RepairRecord> repairs) {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.add(createTableScrollPane(repairs), BorderLayout.CENTER);
        contentPanel.add(createSummaryPanel(repairs), BorderLayout.EAST);
        return contentPanel;
    }
    
    private JScrollPane createTableScrollPane(List<RepairRecord> repairs) {
        Object[][] data = new Object[repairs.size()][COLUMN_NAMES.length];
        
        for (int i = 0; i < repairs.size(); i++) {
            RepairRecord repair = repairs.get(i);
            data[i][0] = repair.invoiceNumber;
            data[i][1] = repair.dateTime.format(DATE_FORMATTER);
            data[i][2] = repair.customerName;
            data[i][3] = repair.deviceDetails + " (ID: " + repair.deviceId + ")";
            data[i][4] = repair.deviceDescription != null ? repair.deviceDescription : "No description";
            data[i][5] = repair.technicianName;
            data[i][6] = String.format("₱%.2f", repair.amountPaid);
        }
        
        JTable table = new JTable(data, COLUMN_NAMES);
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Device column
        table.getColumnModel().getColumn(4).setPreferredWidth(250); // Device Description column
        
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font(table.getFont().getName(), Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
    
    private JPanel createSummaryPanel(List<RepairRecord> repairs) {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.setPreferredSize(new Dimension(250, 0));
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        
        Object[][] statsData = {
            {"Total Completed Repairs", String.valueOf(repairs.size())},
            {"Unique Devices Serviced", String.valueOf(repairs.stream().map(r -> r.deviceId).distinct().count())},
            {"Technicians Involved", String.valueOf(repairs.stream().map(r -> r.technicianName).filter(name -> !name.equals("Not assigned")).distinct().count())}
        };
        
        statsPanel.add(Box.createVerticalStrut(10));
        
        for (Object[] statData : statsData) {
            JPanel statPanel = createStatPanel(
                (String) statData[0],
                (String) statData[1]
            );
            statsPanel.add(statPanel);
            statsPanel.add(Box.createVerticalStrut(15));
        }
        
        JScrollPane scrollPane = new JScrollPane(statsPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        summaryPanel.add(scrollPane, BorderLayout.CENTER);
        
        return summaryPanel;
    }
    
    private JPanel createStatPanel(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Label
        JLabel titleLabel = new JLabel(label);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 12));
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setFont(new Font(valueLabel.getFont().getName(), Font.BOLD, 24));
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(valueLabel);
        
        return panel;
    }
    
    private void showYearFilterDialog() {
        JComboBox<Integer> yearCombo = createYearComboBox();
        
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearCombo);
        
        int result = JOptionPane.showConfirmDialog(reportDialog, inputPanel, 
                "Filter by Year", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            int year = (Integer) yearCombo.getSelectedItem();
            List<RepairRecord> filteredRepairs = getCompletedRepairsByYear(year);
            
            if (filteredRepairs.isEmpty()) {
                JOptionPane.showMessageDialog(reportDialog, 
                        "No completed repairs found for the year " + year, 
                        "Filter Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            displayReport(filteredRepairs, "Year " + year);
        }
    }
    
    private void showMonthFilterDialog() {
        JComboBox<Integer> yearCombo = createYearComboBox();
        JComboBox<String> monthCombo = createMonthComboBox();
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearCombo);
        inputPanel.add(new JLabel("Month:"));
        inputPanel.add(monthCombo);
        
        int result = JOptionPane.showConfirmDialog(reportDialog, inputPanel, 
                "Filter by Month", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            int year = (Integer) yearCombo.getSelectedItem();
            int month = monthCombo.getSelectedIndex() + 1;
            
            List<RepairRecord> filteredRepairs = getCompletedRepairsByYearAndMonth(year, month);
            
            if (filteredRepairs.isEmpty()) {
                JOptionPane.showMessageDialog(reportDialog, 
                        "No completed repairs found for " + MONTHS[month-1] + " " + year, 
                        "Filter Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            displayReport(filteredRepairs, MONTHS[month-1] + " " + year);
        }
    }
    
    private JComboBox<Integer> createYearComboBox() {
        JComboBox<Integer> yearCombo = new JComboBox<>();
        
        List<Integer> years = allRepairs.stream()
            .map(r -> r.dateTime.getYear())
            .distinct()
            .sorted((a, b) -> b.compareTo(a))
            .toList();
        
        for (Integer year : years) {
            yearCombo.addItem(year);
        }
        
        if (yearCombo.getItemCount() == 0) {
            yearCombo.addItem(LocalDate.now().getYear());
        }
        
        return yearCombo;
    }
    
    private JComboBox<String> createMonthComboBox() {
        JComboBox<String> monthCombo = new JComboBox<>(MONTHS);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1); // Current month
        return monthCombo;
    }
}
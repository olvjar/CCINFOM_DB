package model.service;

import model.entity.Customer;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    
    public void addCustomer(Customer customer) throws SQLException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer code cannot be empty");
        }
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        
        String sql = "INSERT INTO customers (customerCode, firstName, lastName, contactNumber, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerCode());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setString(4, customer.getContactNumber());
            pstmt.setString(5, customer.getAddress());
            pstmt.executeUpdate();
        }
    }

    public void updateCustomer(Customer customer) throws SQLException {
        // Validate input
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer code cannot be empty");
        }
        
        // Check if customer exists
        Customer existingCustomer = getCustomerByCode(customer.getCustomerCode());
        if (existingCustomer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        String sql = "UPDATE customers SET firstName = ?, lastName = ?, contactNumber = ?, address = ? WHERE customerCode = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getContactNumber());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getCustomerCode());
            pstmt.executeUpdate();
        }
    }

    public void deleteCustomer(String customerCode) throws SQLException {
        // Validate input
        if (customerCode == null || customerCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer code cannot be empty");
        }
        
        // Check if customer exists
        Customer existingCustomer = getCustomerByCode(customerCode);
        if (existingCustomer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction
            
            // First delete related appointments
            String deleteAppointments = "DELETE FROM appointments WHERE customerCode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAppointments)) {
                pstmt.setString(1, customerCode);
                pstmt.executeUpdate();
            }
            
            // Then delete the customer
            String deleteCustomer = "DELETE FROM customers WHERE customerCode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteCustomer)) {
                pstmt.setString(1, customerCode);
                pstmt.executeUpdate();
            }
            
            conn.commit();  // Commit transaction
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback on error
                } catch (SQLException ex) {
                    throw new SQLException("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw new SQLException("Error deleting customer: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);  // Reset auto-commit
                conn.close();
            }
        }
    }

    public List<Customer> searchCustomers(String criteria, String searchText) throws SQLException {
        String columnName;
        switch (criteria) {
            case "Customer Code":
                columnName = "customerCode";
                break;
            case "First Name":
                columnName = "firstName";
                break;
            case "Last Name":
                columnName = "lastName";
                break;
            case "Contact Number":
                columnName = "contactNumber";
                break;
            default:
                columnName = "customerCode";
        }
        
        String sql = "SELECT * FROM customers WHERE " + columnName + " LIKE ?";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customerCode"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("contactNumber"),
                    rs.getString("address")
                );
                customers.add(customer);
            }
        }
        return customers;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customerCode"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("contactNumber"),
                    rs.getString("address")
                );
                customers.add(customer);
            }
        }
        return customers;
    }

    public Customer getCustomerByCode(String customerCode) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customerCode = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getString("customerCode"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("contactNumber"),
                        rs.getString("address")
                    );
                }
            }
        }
        return null;
    }

    public List<String[]> getCustomerAppointments(String customerCode) throws SQLException {
        List<String[]> appointments = new ArrayList<>();
        String sql = "SELECT a.invoiceNumber, a.dateAndTime, a.serviceStatus, a.paymentStatus, " +
                    "d.deviceType, d.brand, d.model " +
                    "FROM appointments a " +
                    "LEFT JOIN devices d ON a.deviceID = d.deviceID " +
                    "WHERE a.customerCode = ? " +
                    "ORDER BY a.dateAndTime DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerCode);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String deviceInfo = rs.getString("deviceType");
                if (deviceInfo != null) {
                    deviceInfo += " - " + rs.getString("brand") + " " + rs.getString("model");
                } else {
                    deviceInfo = "No device specified";
                }
                
                String[] appointment = {
                    String.valueOf(rs.getInt("invoiceNumber")),
                    rs.getTimestamp("dateAndTime").toString(),
                    rs.getString("serviceStatus"),
                    rs.getString("paymentStatus"),
                    deviceInfo
                };
                appointments.add(appointment);
            }
        }
        return appointments;
    }

    public String validateCustomer(String firstName, String lastName) throws SQLException {
        String sql = "SELECT customerCode FROM customers WHERE firstName = ? AND lastName = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("customerCode");
            }
            return null;
        }
    }
} 
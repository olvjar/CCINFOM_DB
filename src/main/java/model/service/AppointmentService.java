package model.service;

import model.entity.Appointment;
import util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class AppointmentService {
    // Add appointment.
    public void addAppointment(Appointment appointment) throws SQLException {
        // Validate input.
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        if (appointment.getCustomerCode() <= 0) {
            throw new IllegalArgumentException("Customer code must be a positive number.");
        }
        if (appointment.getTechnicianID() <= 0) {
            throw new IllegalArgumentException("Technician ID must be a positive number.");
        }
        if (appointment.getInvoiceNumber() <= 0) {
            throw new IllegalArgumentException("Invoice number must be a positive number.");
        }
        
        // Insert appointment.
        String sql = "INSERT INTO appointments (customerCode, technicianID, serviceStatus, dateAndTime, invoiceNumber, paymentStatus, amountPaid, deviceID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointment.getCustomerCode());
            pstmt.setInt(2, appointment.getTechnicianID());
            pstmt.setString(3, appointment.getServiceStatus());
            pstmt.setString(4, appointment.getDateAndTime());
            pstmt.setInt(5, appointment.getInvoiceNumber());
            pstmt.setString(6, appointment.getPaymentStatus());
            pstmt.setDouble(7, appointment.getAmountPaid());
            pstmt.setInt(8, appointment.getDeviceID());
            
            pstmt.executeUpdate();
        }
    }

    // Update appointment.
    public void updateAppointment(Appointment appointment) throws SQLException {
        // Validate input.
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        if (appointment.getInvoiceNumber() <= 0) {
            throw new IllegalArgumentException("Invoice number must be a positive number.");
        }
        
        // Check if appointment exists.
        Appointment existingAppointment = getAppointmentByInvoiceNumber(appointment.getInvoiceNumber());
        if (existingAppointment == null) {
            throw new IllegalArgumentException("Appointment not found.");
        }
        
        String sql = "UPDATE appointments SET customerCode = ?, technicianID = ?, serviceStatus = ?, dateAndTime = ?, paymentStatus = ?, amountPaid = ?, deviceID = ? WHERE invoiceNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointment.getCustomerCode());
            pstmt.setInt(2, appointment.getTechnicianID());
            pstmt.setString(3, appointment.getServiceStatus());
            pstmt.setString(4, appointment.getDateAndTime());
            pstmt.setString(5, appointment.getPaymentStatus());
            pstmt.setDouble(6, appointment.getAmountPaid());
            pstmt.setInt(7, appointment.getDeviceID());
            pstmt.setInt(8, appointment.getInvoiceNumber());
            
            pstmt.executeUpdate();
        }
    }
    
    // Delete appointment
    public void deleteAppointment(int invoiceNumber) throws SQLException {
        // Validate input
        if (invoiceNumber <= 0) {
            throw new IllegalArgumentException("Invoice number must be a positive number.");
        }

        // Check if appointment exists.
        Appointment existingAppointment = getAppointmentByInvoiceNumber(invoiceNumber);
        if (existingAppointment == null) {
            throw new IllegalArgumentException("Appointment not found.");
        }
        
        // Delete
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction
            
            String deleteAppointments = "DELETE FROM appointments WHERE invoiceNumber = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAppointments)) {
                pstmt.setInt(1, invoiceNumber);
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
            throw new SQLException("Error deleting appointment: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);  // Reset auto-commit
                conn.close();
            }
        }
    }
    
    public List<Appointment> searchAppointments(String criteria, String searchText) throws SQLException {
        String columnName;
        switch (criteria) {
            case "Customer Code":
                columnName = "customerCode";
                break;
            case "Technician ID":
                columnName = "technicianID";
                break;
            case "Service Status":
                columnName = "serviceStatus";
                break;
            case "Date and Time":
                columnName = "dateAndTime";
                break;
            case "Invoice Number":
                columnName = "invoiceNumber";
                break;
            case "Payment Status":
                columnName = "paymentStatus";
                break;
            case "Amount Paid":
                columnName = "amountPaid";
                break;
            case "Device ID":
                columnName = "deviceID";
                break;
            default:
                throw new SQLException("Invalid search criteria");
        }
        
        String sql = "SELECT * FROM appointments WHERE " + columnName + " LIKE ?";
        List<Appointment> appointments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getInt("customerCode"),
                    rs.getInt("technicianID"),
                    rs.getString("serviceStatus"),
                    rs.getTimestamp("dateAndTime").toString(),
                    rs.getInt("invoiceNumber"),
                    rs.getString("paymentStatus"),
                    rs.getDouble("amountPaid"),
                    rs.getInt("deviceID")
                );
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    
    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getInt("customerCode"),
                    rs.getInt("technicianID"),
                    rs.getString("serviceStatus"),
                    rs.getTimestamp("dateAndTime").toString(),
                    rs.getInt("invoiceNumber"),
                    rs.getString("paymentStatus"),
                    rs.getDouble("amountPaid"),
                    rs.getInt("deviceID")
                );
                appointments.add(appointment);
            }
        }
        return appointments;
    }
    
    public Appointment getAppointmentByInvoiceNumber(int invoiceNumber) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE invoiceNumber = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Appointment(
                        rs.getInt("customerCode"),
                        rs.getInt("technicianID"),
                        rs.getString("serviceStatus"),
                        rs.getTimestamp("dateAndTime").toString(),
                        rs.getInt("invoiceNumber"),
                        rs.getString("paymentStatus"),
                        rs.getDouble("amountPaid"),
                        rs.getInt("deviceID")
                    );
                }
            }
        }
        return null;
    }
} 

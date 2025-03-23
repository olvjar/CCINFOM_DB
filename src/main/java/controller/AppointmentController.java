package controller;

import model.entity.Appointment;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentController
{    
    // Add appointment.
    public void addAppointment (Appointment appointment) throws SQLException
    {
        // Valdiate input.
        if (appointment == null)
        {
            throw new IllegalArgumentException ("Appointment cannot be null.");
        }
        if (appointment.getCustomerCode() == null)
        {
            throw new IllegalArgumentException ("Customer code cannot be empty.");
        }
        if (appointment.getTechnicianID() == null)
        {
            throw new IllegalArgumentException ("Technician ID cannot be empty.");
        }
        if (appointment.getInvoiceNumber() == null)
        {
            throw new IllegalArgumentException ("Invoice number cannot be empty.");
        }
        
        // Insert appointment.
        String sql = "INSERT INTO appointments (customerCode, technicianID, serviceStatus, dateAndTime, invoiceNumber, paymentStatus, amountPaid, deviceID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) 
        {
            pstmt.setString (1, appointment.getCustomerCode ());
            pstmt.setString (2, appointment.getTechnicianID ());
            pstmt.setString (3, appointment.getServiceStatus ());
            pstmt.setString (4, appointment.getDateAndTime ());
            pstmt.setString (5, appointment.getInvoiceNumber ());
            pstmt.setString (6, appointment.getPaymentStatus ());
            pstmt.setDouble (7, appointment.getAmountPaid ());
            pstmt.setInt (8, appointment.getDeviceID ());
            pstmt.executeUpdate();
        }
    }

    // Update appointment.
    public void updateAppointment (Appointment appointment) throws SQLException
    {
        // Validate input.
        if (appointment == null)
        {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        if (appointment.getInvoiceNumber () == null)
        {
            throw new IllegalArgumentException ("Invoice number cannot be empty.");
        }
        
        // Check if appointment exists.
        Appointment existingAppointment = getAppointmentByInvoiceNumber (appointment.getInvoiceNumber ());
        if (existingAppointment == null)
        {
            throw new IllegalArgumentException ("Appointment not found.");
        }
        
        String sql = "UPDATE appointments SET customerCode = ?, technicianID = ?, serviceStatus = ?, dateAndTime = ?, invoiceNumber = ?, paymentStatus = ?, amountPaid = ?, deviceID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString (1, appointment.getCustomerCode ());
            pstmt.setString (2, appointment.getTechnicianID ());
            pstmt.setString (3, appointment.getServiceStatus ());
            pstmt.setString (4, appointment.getDateAndTime ());
            pstmt.setString (5, appointment.getInvoiceNumber ());
            pstmt.setString (6, appointment.getPaymentStatus ());
            pstmt.setDouble (7, appointment.getAmountPaid ());
            pstmt.setInt (8, appointment.getDeviceID ());
            pstmt.executeUpdate();
        }
    }
    
    // Delete appointmentttt
    public void deleteAppointment (String invoiceNumber) throws SQLException
    {
        // Validate input
        if (invoiceNumber == null)
        {
            throw new IllegalArgumentException ("Invoice number cannot be empty.");
        }

        // Check if appointment exists.
        Appointment existingAppointment = getAppointmentByInvoiceNumber (invoiceNumber);
        if (existingAppointment == null)
        {
            throw new IllegalArgumentException ("Appointment not found.");
        }
        
        // Deleteeee
        Connection conn = null;
        try 
        {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction
            
            String deleteAppointments = "DELETE FROM appointments WHERE invoiceNumber = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAppointments)) 
            {
                pstmt.setString(1, invoiceNumber);
                pstmt.executeUpdate();
            }
            
            conn.commit();  // Commit transaction
        } 
        catch (SQLException e) 
        {
            if (conn != null) 
            {
                try 
                {
                    conn.rollback();  // Rollback on error
                } 
                catch (SQLException ex) 
                {
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
    
    public List<Appointment> searchAppointments (String criteria, String searchText) throws SQLException {
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
                    rs.getString ("customerCode"),
                    rs.getString ("technicianID"),
                    rs.getString ("serviceStatus"),
                    rs.getString ("dateAndTime"),
                    rs.getString ("invoiceNumber"),
                    rs.getString ("paymentStatus"),
                    Double.parseDouble (rs.getString ("amountPaid")),
                    Integer.parseInt (rs.getString ("deviceID"))
                );
                appointments.add (appointment);
            }
        }
        return appointments;
    }
    
    public List<Appointment> getAllAppointments () throws SQLException
    {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) 
        {
            while (rs.next ())
            {
                Appointment appointment = new Appointment(
                    rs.getString ("customerCode"),
                    rs.getString ("technicianID"),
                    rs.getString ("serviceStatus"),
                    rs.getString ("dateAndTime"),
                    rs.getString ("invoiceNumber"),
                    rs.getString ("paymentStatus"),
                    Double.parseDouble (rs.getString ("amountPaid")),
                    Integer.parseInt (rs.getString ("deviceID"))
                );
                appointments.add (appointment);
            }
        }
        return appointments;
    }
    
    public Appointment getAppointmentByInvoiceNumber (String invoiceNumber) throws SQLException
    {
        String sql = "SELECT * FROM appointments WHERE invoiceNumber = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, invoiceNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Appointment(
                        rs.getString ("customerCode"),
                        rs.getString ("technicianID"),
                        rs.getString ("serviceStatus"),
                        rs.getString ("dateAndTime"),
                        rs.getString ("invoiceNumber"),
                        rs.getString ("paymentStatus"),
                        Double.parseDouble (rs.getString ("amountPaid")),
                        Integer.parseInt (rs.getString ("deviceID"))
                    );
                }
            }
        }
        return null;
    }
}

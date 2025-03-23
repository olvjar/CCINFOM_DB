package model.service;

import model.entity.Technician;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicianService {

    public void addTechnician(Technician technician) throws SQLException {
        String sql = "INSERT INTO technicians (firstName, lastName, contactNumber, address, availability) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, technician.getFirstName());
            pstmt.setString(2, technician.getLastName());
            pstmt.setString(3, technician.getContactNumber());
            pstmt.setString(4, technician.getAddress());
            pstmt.setString(5, technician.getAvailability());
            pstmt.executeUpdate();
        }
    }

    public void updateTechnician(Technician technician) throws SQLException {
        String sql = "UPDATE technicians SET firstName = ?, lastName = ?, contactNumber = ?, address = ?, availability = ? WHERE technicianID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, technician.getFirstName());
            pstmt.setString(2, technician.getLastName());
            pstmt.setString(3, technician.getContactNumber());
            pstmt.setString(4, technician.getAddress());
            pstmt.setString(5, technician.getAvailability());
            pstmt.setInt(6, technician.getTechnicianID());
            pstmt.executeUpdate();
        }
    }

    public void deleteTechnician(int technicianID) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete related appointments
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM appointments WHERE technicianID = ?")) {
                pstmt.setInt(1, technicianID);
                pstmt.executeUpdate();
            }

            // Delete technician
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM technicians WHERE technicianID = ?")) {
                pstmt.setInt(1, technicianID);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public List<Technician> searchTechnicians(String criteria, String searchText) throws SQLException {
        String columnName = switch (criteria) {
            case "First Name" -> "firstName";
            case "Last Name" -> "lastName";
            case "Contact" -> "contactNumber";
            case "Address" -> "address";
            case "Availability" -> "availability";
            default -> "technicianID";
        };

        String sql = "SELECT * FROM technicians WHERE " + columnName + " LIKE ?";
        List<Technician> technicians = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                technicians.add(new Technician(
                        rs.getInt("technicianID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("contactNumber"),
                        rs.getString("address"),
                        rs.getString("availability")
                ));
            }
        }
        return technicians;
    }

    public List<Technician> getAllTechnicians() throws SQLException {
        List<Technician> technicians = new ArrayList<>();
        String sql = "SELECT * FROM technicians";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                technicians.add(new Technician(
                        rs.getInt("technicianID"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("contactNumber"),
                        rs.getString("address"),
                        rs.getString("availability")
                ));
            }
        }
        return technicians;
    }

    public List<String[]> getTechnicianAppointments(int technicianID) throws SQLException {
        String sql = "SELECT a.invoiceNumber, a.dateAndTime, a.serviceStatus, a.paymentStatus, "
                + "d.deviceType, d.brand, d.model, "
                + "c.firstName || ' ' || c.lastName AS customerName "
                + "FROM appointments a "
                + "LEFT JOIN devices d ON a.deviceID = d.deviceID "
                + "LEFT JOIN customers c ON a.customerCode = c.customerCode "
                + "WHERE a.technicianID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, technicianID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String deviceInfo = rs.getString("deviceType") != null ?
                        rs.getString("deviceType") + " - " + rs.getString("brand") + " " + rs.getString("model") :
                        "No device specified";

                appointments.add(new String[] {
                        String.valueOf(rs.getInt("invoiceNumber")),
                        rs.getTimestamp("dateAndTime").toString(),
                        rs.getString("serviceStatus"),
                        rs.getString("paymentStatus"),
                        deviceInfo,
                        rs.getString("customerName")
                });
            }
        }
        return appointments;
    }
}

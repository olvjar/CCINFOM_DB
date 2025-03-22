package controller;

import model.entity.Device;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeviceController {
    
    private Device createDeviceFromResultSet(ResultSet rs) throws SQLException {
        return new Device(
            rs.getInt("deviceID"),
            rs.getInt("customerCode"),
            rs.getString("deviceType"),
            rs.getString("brand"),
            rs.getString("model"),
            rs.getString("serialNumber"),
            rs.getString("description")
        );
    }
    
    private void validateDevice(Device device) {
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null");
        }
        if (device.getDeviceType() == null || device.getDeviceType().trim().isEmpty()) {
            throw new IllegalArgumentException("Device type cannot be empty");
        }
    }
    
    private void setDeviceParameters(PreparedStatement pstmt, Device device, boolean isUpdate) throws SQLException {
        int paramIndex = 1;
        if (!isUpdate) {
            pstmt.setInt(paramIndex++, device.getDeviceId());
        }
        pstmt.setInt(paramIndex++, device.getCustomerCode());
        pstmt.setString(paramIndex++, device.getDeviceType());
        pstmt.setString(paramIndex++, device.getBrand());
        pstmt.setString(paramIndex++, device.getModel());
        pstmt.setString(paramIndex++, device.getSerialNumber());
        pstmt.setString(paramIndex++, device.getDescription());
        if (isUpdate) {
            pstmt.setInt(paramIndex, device.getDeviceId());
        }
    }
    
    public void addDevice(Device device) throws SQLException {
        validateDevice(device);

        String sql = "INSERT INTO devices (deviceID, customerCode, deviceType, brand, model, serialNumber, description) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setDeviceParameters(pstmt, device, false);
            pstmt.executeUpdate();
        }
    }

    public void updateDevice(Device device) throws SQLException {
        validateDevice(device);

        String sql = "UPDATE devices SET customerCode = ?, deviceType = ?, brand = ?, " +
                    "model = ?, serialNumber = ?, description = ? WHERE deviceID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setDeviceParameters(pstmt, device, true);
            pstmt.executeUpdate();
        }
    }

    public void deleteDevice(int deviceId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First update appointments to remove device reference
            String updateAppointments = "UPDATE appointments SET deviceID = NULL WHERE deviceID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateAppointments)) {
                pstmt.setInt(1, deviceId);
                pstmt.executeUpdate();
            }
            
            // Then delete the device
            String deleteDevice = "DELETE FROM devices WHERE deviceID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteDevice)) {
                pstmt.setInt(1, deviceId);
                pstmt.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new SQLException("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw new SQLException("Error deleting device: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Device> getAllDevices() throws SQLException {
        List<Device> devices = new ArrayList<>();
        String sql = "SELECT * FROM devices";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                devices.add(createDeviceFromResultSet(rs));
            }
        }
        return devices;
    }

    public Device getDeviceById(int deviceId) throws SQLException {
        String sql = "SELECT * FROM devices WHERE deviceID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, deviceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createDeviceFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Device> getCustomerDevices(int customerCode) throws SQLException {
        List<Device> devices = new ArrayList<>();
        String sql = "SELECT * FROM devices WHERE customerCode = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerCode);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                devices.add(createDeviceFromResultSet(rs));
            }
        }
        return devices;
    }

    public List<Device> searchDevices(String criteria, String searchText) throws SQLException {
        String columnName;
        switch (criteria) {
            case "Device ID":
                columnName = "deviceID";
                break;
            case "Customer Code":
                columnName = "customerCode";
                break;
            case "Device Type":
                columnName = "deviceType";
                break;
            case "Brand":
                columnName = "brand";
                break;
            case "Model":
                columnName = "model";
                break;
            case "Serial Number":
                columnName = "serialNumber";
                break;
            default:
                throw new SQLException("Invalid search criteria");
        }
        
        String sql = "SELECT * FROM devices WHERE " + columnName + " LIKE ?";
        List<Device> devices = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchText + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                devices.add(createDeviceFromResultSet(rs));
            }
        }
        return devices;
    }
} 
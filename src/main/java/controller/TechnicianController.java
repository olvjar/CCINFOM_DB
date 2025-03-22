package controller;

import model.entity.Technician;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicianController {
// For technician login
    public boolean validateTechnician(String technicianId, String firstName, String lastName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM technicians " +
                    "WHERE technicianID = ? AND firstName = ? AND lastName = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, technicianId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
} 
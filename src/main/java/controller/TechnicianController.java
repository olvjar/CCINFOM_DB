package controller;

import model.entity.Technician;
import model.service.TechnicianService;
import view.management.TechnicianManagementFrame;
import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class TechnicianController {
    private final TechnicianService technicianService;
    private TechnicianManagementFrame view;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

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

    public void setView(TechnicianManagementFrame view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        view.getSearchButton().addActionListener(e -> searchTechnicians());
        view.getAddButton().addActionListener(e -> addTechnician());
        view.getUpdateButton().addActionListener(e -> updateTechnician());
        view.getDeleteButton().addActionListener(e -> deleteTechnician());
        view.getViewAppointmentsButton().addActionListener(e -> viewAppointments());
    }

    private void searchTechnicians() {
        try {
            String searchText = view.getSearchText().trim();
            String criteria = view.getSearchCriteria();

            List<Technician> results = searchText.isEmpty() ?
                    technicianService.getAllTechnicians() :
                    technicianService.searchTechnicians(criteria, searchText);

            view.updateTechnicianTable(results);
        } catch (SQLException ex) {
            showError("Search error: " + ex.getMessage());
        }
    }

    private void addTechnician() {
        try {
            Technician tech = view.getTechnicianFromFields();
            technicianService.addTechnician(tech);
            refreshTable();
            showMessage("Technician added successfully!");
        } catch (Exception ex) {
            showError("Add error: " + ex.getMessage());
        }
    }

    private void updateTechnician() {
        try {
            Technician selected = view.getSelectedTechnician();
            Technician updated = view.getTechnicianFromFields();
            Technician newTech = new Technician(
                    selected.getTechnicianID(),
                    updated.getFirstName(),
                    updated.getLastName(),
                    updated.getContactNumber(),
                    updated.getAddress(),
                    updated.getAvailability()
            );
            technicianService.updateTechnician(newTech);
            refreshTable();
            showMessage("Technician updated successfully!");
        } catch (Exception ex) {
            showError("Update error: " + ex.getMessage());
        }
    }

    private void deleteTechnician() {
        try {
            Technician selected = view.getSelectedTechnician();
            if (selected == null) {
                showMessage("Please select a technician first!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Delete technician and all associated appointments?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                technicianService.deleteTechnician(selected.getTechnicianID());
                refreshTable();
                showMessage("Technician deleted successfully!");
            }
        } catch (Exception ex) {
            showError("Delete error: " + ex.getMessage());
        }
    }

    private void viewAppointments() {
        try {
            Technician selected = view.getSelectedTechnician();
            if (selected == null) {
                showMessage("Please select a technician first!");
                return;
            }

            List<String[]> appointments = technicianService.getTechnicianAppointments(selected.getTechnicianID());
            view.showAppointmentsDialog(appointments);
        } catch (SQLException ex) {
            showError("Error loading appointments: " + ex.getMessage());
        }
    }

    private void refreshTable() throws SQLException {
        view.updateTechnicianTable(technicianService.getAllTechnicians());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

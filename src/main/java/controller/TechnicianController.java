package controller;

import model.entity.Technician;
import model.service.TechnicianService;
import view.management.TechnicianManagementFrame;
import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class TechnicianController {
    private TechnicianService technicianService;
    private TechnicianManagementFrame view;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    public void setView(TechnicianManagementFrame view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        if (view == null) return;
        
        view.getSearchButton().addActionListener(e -> {
            try {
                String searchText = view.getSearchField().getText().trim();
                String criteria = view.getSearchCriteria();
                
                if (searchText.isEmpty()) {
                    loadAllTechnicians();
                    return;
                }
                
                List<Technician> results = searchTechnicians(criteria, searchText);
                view.updateTechnicianTable(results);
            } catch (SQLException ex) {
                showError("Error searching technicians: " + ex.getMessage());
            }
        });
        view.getAddButton().addActionListener(e -> addTechnician());
        view.getUpdateButton().addActionListener(e -> updateTechnician());
        view.getDeleteButton().addActionListener(e -> deleteTechnician());
        view.getViewAppointmentsButton().addActionListener(e -> viewAppointments());
    }

    public void loadAllTechnicians() {
        try {
            List<Technician> technicians = technicianService.getAllTechnicians();
            view.updateTechnicianTable(technicians);
        } catch (SQLException e) {
            showError("Error loading technicians: " + e.getMessage());
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
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message) {
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean validateTechnician(String techId, String firstName, String lastName) throws SQLException {
        return technicianService.validateTechnician(techId, firstName, lastName);
    }

    public List<Technician> searchTechnicians(String criteria, String searchText) throws SQLException {
        return technicianService.searchTechnicians(criteria, searchText);
    }
}

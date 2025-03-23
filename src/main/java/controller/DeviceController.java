package controller;

import model.entity.Device;
import model.service.DeviceService;
import view.dialog.DeviceManagementDialog;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

public class DeviceController {
    private DeviceService deviceService;
    private DeviceManagementDialog view;
    
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
    
    public void setView(DeviceManagementDialog view) {
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
                    loadAllDevices();
                    return;
                }
                
                List<Device> results = searchDevices(criteria, searchText);
                view.updateTableWithResults(results);
            } catch (SQLException ex) {
                showError("Error searching devices: " + ex.getMessage());
            }
        });
        
        view.getAddButton().addActionListener(e -> {
            try {
                Device device = view.getDeviceFromFields();
                addDevice(device);
                view.clearFields();
                loadAllDevices();
                showMessage("Device added successfully!");
            } catch (SQLException ex) {
                showError("Error adding device: " + ex.getMessage());
            }
        });
        
        view.getUpdateButton().addActionListener(e -> {
            try {
                Device device = view.getDeviceFromFields();
                updateDevice(device);
                loadAllDevices();
                showMessage("Device updated successfully!");
            } catch (SQLException ex) {
                showError("Error updating device: " + ex.getMessage());
            }
        });
        
        view.getDeleteButton().addActionListener(e -> {
            try {
                int deviceId = view.getSelectedDeviceId();
                if (deviceId == -1) {
                    showMessage("Please select a device to delete!");
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(
                    view,
                    "Are you sure you want to delete this device?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteDevice(deviceId);
                    loadAllDevices();
                    showMessage("Device deleted successfully!");
                }
            } catch (SQLException ex) {
                showError("Error deleting device: " + ex.getMessage());
            }
        });
    }
    
    public void loadAllDevices() {
        try {
            List<Device> devices = getAllDevices();
            view.updateTableWithResults(devices);
        } catch (SQLException e) {
            showError("Error loading devices: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showMessage(String message) {
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Service methods
    public void addDevice(Device device) throws SQLException {
        deviceService.addDevice(device);
    }

    public void updateDevice(Device device) throws SQLException {
        deviceService.updateDevice(device);
    }

    public void deleteDevice(int deviceId) throws SQLException {
        deviceService.deleteDevice(deviceId);
    }

    public List<Device> getAllDevices() throws SQLException {
        return deviceService.getAllDevices();
    }

    public Device getDeviceById(int deviceId) throws SQLException {
        return deviceService.getDeviceById(deviceId);
    }

    public List<Device> getCustomerDevices(int customerCode) throws SQLException {
        return deviceService.getCustomerDevices(customerCode);
    }

    public List<Device> searchDevices(String criteria, String searchText) throws SQLException {
        return deviceService.searchDevices(criteria, searchText);
    }
} 
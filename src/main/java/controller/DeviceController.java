package controller;

import model.entity.Device;
import model.service.DeviceService;
import java.sql.SQLException;
import java.util.List;

public class DeviceController {
    private DeviceService deviceService;
    
    public DeviceController() {
        this.deviceService = new DeviceService();
    }
    
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
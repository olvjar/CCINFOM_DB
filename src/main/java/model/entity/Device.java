package model.entity;

public class Device {
    private String deviceId;
    private String customerCode;
    private String deviceType;
    private String brand;
    private String model;
    private String serialNumber;
    private String description;

    // Constructor
    public Device(int deviceId, String customerCode, String deviceType, String brand, 
                 String model, String serialNumber, String description) {
        this.deviceId = String.valueOf(deviceId);
        this.customerCode = customerCode;
        this.deviceType = deviceType;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.description = description;
    }

    // Getters and setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 
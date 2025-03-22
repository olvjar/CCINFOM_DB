package model.entity;

public class Device {
    private int deviceId;
    private int customerCode;  // Keep as int to match database
    private String deviceType;
    private String brand;
    private String model;
    private String serialNumber;
    private String description;

    // Constructor
    public Device(int deviceId, int customerCode, String deviceType, String brand, 
                 String model, String serialNumber, String description) {
        this.deviceId = deviceId;
        this.customerCode = customerCode;
        this.deviceType = deviceType;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.description = description;
    }

    // Getters and setters
    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }
    
    public int getCustomerCode() { return customerCode; }
    public void setCustomerCode(int customerCode) { this.customerCode = customerCode; }
    
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
package model.entity;

public class Appointment {

    private String customerCode;
    private String technicianID;
    private String serviceStatus;
    private String dateAndTime;
    private String invoiceNumber;
    private String paymentStatus;
    private double amountPaid;
    private int deviceID;
   
    public Appointment (String customerCode, String technicianID, 
                        String serviceStatus, String dateAndTime,
                        String invoiceNumber, String paymentStatus, 
                        double amountPaid, int deviceID)
    {
        this.customerCode = customerCode;
        this.technicianID = technicianID;
        this.serviceStatus = serviceStatus;
        this.invoiceNumber = invoiceNumber;
        this.paymentStatus = paymentStatus;
        this.amountPaid = amountPaid;
        this.deviceID = deviceID;
    }
    
    // Getters
    public String getCustomerCode ()
    {
        return customerCode;
    }
    
    public String getTechnicianID ()
    {
        return technicianID;
    }
    
    public String getServiceStatus ()
    {
        return serviceStatus;
    }
    
    public String getDateAndTime ()
    {
        return dateAndTime;
    }

    public String getInvoiceNumber ()
    {
        return invoiceNumber;
    }
    
    public String getPaymentStatus ()
    {
        return paymentStatus;
    }
    
    public double getAmountPaid ()
    {
        return amountPaid;
    }
    
    public int getDeviceID ()
    {
        return deviceID;
    }
} 

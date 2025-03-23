package model.entity;

public class Appointment {

    private int customerCode;
    private int technicianID;
    private String serviceStatus;
    private String dateAndTime;
    private int invoiceNumber;
    private String paymentStatus;
    private double amountPaid;
    private int deviceID;
   
    public Appointment (int customerCode, int technicianID, 
                        String serviceStatus, String dateAndTime,
                        int invoiceNumber, String paymentStatus, 
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
    public int getCustomerCode ()
    {
        return customerCode;
    }
    
    public int getTechnicianID ()
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

    public int getInvoiceNumber ()
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

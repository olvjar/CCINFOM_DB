package model.entity;

public class Customer {
    private String customerCode;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String address;

    public Customer(String customerCode, String firstName, String lastName, String contactNumber, String address) {
        this.customerCode = customerCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    // Getters
    public String getCustomerCode() { return customerCode; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    
    // Convenience method to get full name
    public String getFullName() { return firstName + " " + lastName; }
} 
package model.entity;

public class Technician {
    private int technicianID;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String address;
    private String availability;

    public Technician(int technicianID, String firstName, String lastName, String contactNumber, String address, String availability) {
        this.technicianID = technicianID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.address = address;
        this.availability = availability;
    }

    public int getTechnicianID() {
        return technicianID;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getAvailability() {
        return availability;
    }
}

package model.entity;

public class Technician {
    private int technicianID;
    private String technicianName;
    private String contactNumber;
    private String completeAddress;
    private boolean availability;

    public Technician(int technicianID, String technicianName, String contactNumber, String completeAddress, boolean availability) {
        this.technicianID = technicianID;
        this.technicianName = technicianName;
        this.contactNumber = contactNumber;
        this.completeAddress = completeAddress;
        this.availability = availability;
    }

    public int getTechnicianID() {
        return technicianID;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCompleteAddress() {
        return completeAddress;
    }

    public boolean isAvailable() {
        return availability;
    }
}

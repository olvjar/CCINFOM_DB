package controller;

import model.entity.Customer;
import model.entity.Technician;
import model.entity.Appointment;
import model.service.AppointmentService;
import java.sql.SQLException;
import java.util.List;

public class AppointmentController 
{
    private AppointmentService appointmentService;
    
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    public void addAppointment(Appointment appointment) throws SQLException {
        appointmentService.addAppointment(appointment);
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        appointmentService.updateAppointment(appointment);
    }

    public void deleteAppointment(int invoiceNumber) throws SQLException {
        appointmentService.deleteAppointment(invoiceNumber);
    }

    public List<Appointment> searchAppointments(String criteria, String searchText) throws SQLException {
        return appointmentService.searchAppointments(criteria, searchText);
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        return appointmentService.getAllAppointments();
    }

    public Appointment getAppointmentByInvoiceNumber(int invoiceNumber) throws SQLException
    {
        return appointmentService.getAppointmentByInvoiceNumber (invoiceNumber);
    }
    
    public Customer getCustomerByCode (String customerCode) throws SQLException
    {
        return appointmentService.getCustomerByCode (customerCode);
    }
    
    public Technician getTechnicianByID (int technicianID) throws SQLException
    {
        return appointmentService.getTechnicianByID (technicianID);
    }

    public int generateInvoiceNumber() throws SQLException {
        return appointmentService.generateInvoiceNumber();
    }
}

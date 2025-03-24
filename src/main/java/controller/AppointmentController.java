package controller;

import model.entity.Appointment;
import model.service.AppointmentService;
import java.sql.SQLException;
import java.util.List;

public class AppointmentController 
{
    private AppointmentService appointmentService;
    
    public AppointmentController() {
        this.appointmentService = new AppointmentService();
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
        return appointmentService.getAppointmentByInvoiceNumber(invoiceNumber);
    }
}

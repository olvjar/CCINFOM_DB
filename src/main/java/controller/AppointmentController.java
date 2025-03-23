/*
    Note: I think I should prolly make add appointment check if 
    the customer, technician, and device exist. But for now haven't added
    'cause our technician and device controller are also blank for now.
*/

package controller;

import model.entity.Appointment;
import model.service.AppointmentService;
import java.sql.SQLException;
import java.util.List;

public class AppointmentController {
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

    public Appointment getAppointmentByInvoiceNumber(int invoiceNumber) throws SQLException {
        return appointmentService.getAppointmentByInvoiceNumber(invoiceNumber);
    }
}

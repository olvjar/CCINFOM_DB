package controller;

import model.entity.Customer;
import model.service.CustomerService;

import model.entity.Appointment;
import model.service.AppointmentService;
import java.sql.SQLException;
import java.util.List;

public class AppointmentController 
{
    private AppointmentService appointmentService;
    private CustomerService customerService;
    
    public AppointmentController() {
        this.appointmentService = new AppointmentService();
        this.customerService = new CustomerService ();
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
    
    public Customer getCustomerByCode(String customerCode) throws SQLException {
        return customerService.getCustomerByCode(customerCode);
    }
}

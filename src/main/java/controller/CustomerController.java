package controller;

import model.entity.Customer;
import model.service.CustomerService;
import java.sql.SQLException;
import java.util.List;

public class CustomerController {
    private CustomerService customerService;
    
    public CustomerController() {
        this.customerService = new CustomerService();
    }
    
    public void addCustomer(Customer customer) throws SQLException {
        customerService.addCustomer(customer);
    }

    public void updateCustomer(Customer customer) throws SQLException {
        customerService.updateCustomer(customer);
    }

    public void deleteCustomer(String customerCode) throws SQLException {
        customerService.deleteCustomer(customerCode);
    }

    public List<Customer> searchCustomers(String criteria, String searchText) throws SQLException {
        return customerService.searchCustomers(criteria, searchText);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerService.getAllCustomers();
    }

    public Customer getCustomerByCode(String customerCode) throws SQLException {
        return customerService.getCustomerByCode(customerCode);
    }

    public List<String[]> getCustomerAppointments(String customerCode) throws SQLException {
        return customerService.getCustomerAppointments(customerCode);
    }

    public String validateCustomer(String firstName, String lastName) throws SQLException {
        return customerService.validateCustomer(firstName, lastName);
    }
} 
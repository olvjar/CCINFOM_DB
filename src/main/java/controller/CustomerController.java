package controller;

import model.entity.Customer;
import model.service.CustomerService;
import view.management.CustomerManagementFrame;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

public class CustomerController {
    private CustomerService customerService;
    private CustomerManagementFrame view;
    
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    public void setView(CustomerManagementFrame view) {
        this.view = view;
        setupListeners();
    }
    
    private void setupListeners() {
        if (view == null) return;
        
        view.getSearchButton().addActionListener(e -> {
            try {
                String searchText = view.getSearchField().getText().trim();
                String criteria = view.getSearchCriteria();
                
                if (searchText.isEmpty()) {
                    loadAllCustomers();
                    return;
                }
                
                List<Customer> results = searchCustomers(criteria, searchText);
                view.updateTableWithResults(results);
            } catch (SQLException ex) {
                showError("Error searching customers: " + ex.getMessage());
            }
        });
        
        view.getAddButton().addActionListener(e -> {
            try {
                Customer customer = view.getCustomerFromFields();
                addCustomer(customer);
                view.clearFields();
                loadAllCustomers();
                showMessage("Customer added successfully!");
            } catch (SQLException ex) {
                showError("Error adding customer: " + ex.getMessage());
            }
        });
        
        view.getUpdateButton().addActionListener(e -> {
            try {
                if (view.getSelectedCustomer() == null) {
                    showMessage("Please select a customer to update!");
                    return;
                }
                Customer customer = view.getCustomerFromFields();
                updateCustomer(customer);
                loadAllCustomers();
                view.clearFields();
                showMessage("Customer updated successfully!");
            } catch (SQLException ex) {
                showError("Error updating customer: " + ex.getMessage());
            }
        });
        
        view.getDeleteButton().addActionListener(e -> {
            try {
                String customerCode = view.getSelectedCustomerCode();
                if (customerCode == null) {
                    showMessage("Please select a customer to delete!");
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(view,
                    "Are you sure you want to delete this customer?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteCustomer(customerCode);
                    loadAllCustomers();
                    view.clearFields();
                    showMessage("Customer deleted successfully!");
                }
            } catch (SQLException ex) {
                showError("Error deleting customer: " + ex.getMessage());
            }
        });
        
        view.getViewAppointmentsButton().addActionListener(e -> {
            try {
                String customerCode = view.getSelectedCustomerCode();
                if (customerCode == null) {
                    showMessage("Please select a customer to view appointments!");
                    return;
                }
                
                List<String[]> appointments = getCustomerAppointments(customerCode);
                view.showAppointmentsDialog(appointments);
            } catch (SQLException ex) {
                showError("Error loading appointments: " + ex.getMessage());
            }
        });
        
        view.getViewDevicesButton().addActionListener(e -> {
            try {
                String customerCode = view.getSelectedCustomerCode();
                if (customerCode == null) {
                    showMessage("Please select a customer to view devices!");
                    return;
                }
                
                view.showDevicesDialog(customerCode);
            } catch (Exception ex) {
                showError("Error loading devices: " + ex.getMessage());
            }
        });
    }
    
    public void loadAllCustomers() {
        try {
            List<Customer> customers = getAllCustomers();
            view.updateTableWithResults(customers);
        } catch (SQLException e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showMessage(String message) {
        if (view != null) {
            JOptionPane.showMessageDialog(view, message, "Information", JOptionPane.INFORMATION_MESSAGE);
        }
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
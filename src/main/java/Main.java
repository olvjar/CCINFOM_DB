import util.DatabaseInitializer;
import view.LandingView;
import javax.swing.*;
import model.service.CustomerService;
import controller.CustomerController;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            DatabaseInitializer.initialize();
            
            // Create single instances for the entire application
            CustomerService customerService = new CustomerService();
            CustomerController customerController = new CustomerController(customerService);
            
            // Landing Page
            SwingUtilities.invokeLater(() -> {
                LandingView landingView = new LandingView(customerController);
                landingView.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error starting application: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
} 
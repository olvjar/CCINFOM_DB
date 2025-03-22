import util.DatabaseInitializer;
import view.LandingView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Initialize database
            DatabaseInitializer.initialize();
            
            // Launch GUI with landing page
            SwingUtilities.invokeLater(() -> {
                new LandingView().setVisible(true);
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
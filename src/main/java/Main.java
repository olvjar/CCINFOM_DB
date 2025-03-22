import util.DatabaseInitializer;
import view.LandingView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Initialize database
            DatabaseInitializer.initialize();
            
            SwingUtilities.invokeLater(() -> {
                new LandingView().setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error starting application: " + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
} 
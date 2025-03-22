package main;

import database.DatabaseInitializer;
import gui.frames.LandingFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Initialize database
            DatabaseInitializer.initialize();
            
            // Launch GUI with landing page
            SwingUtilities.invokeLater(() -> {
                new LandingFrame().setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error starting application: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 
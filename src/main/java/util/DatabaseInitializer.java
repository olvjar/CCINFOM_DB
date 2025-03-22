package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;

public class DatabaseInitializer {
    public static void initialize() {
        // First, create the database if it doesn't exist
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", 
                Constants.DB_USER, 
                Constants.DB_PASSWORD)) {
            
            Statement stmt = conn.createStatement();
            
            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS computerrepairshop");
            
            // Use the database
            stmt.executeUpdate("USE computerrepairshop");
            
            // Set character set
            stmt.executeUpdate("SET NAMES utf8mb4");
            stmt.executeUpdate("SET CHARACTER SET utf8mb4");
            
            // Read and execute SQL script
            try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/dbComputerRepairShop.sql");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                
                StringBuilder script = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    script.append(line).append("\n");
                }
                
                // Split script into individual statements
                String[] statements = script.toString().split(";");
                
                // Execute each statement
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        try {
                            stmt.executeUpdate(statement);
                        } catch (SQLException e) {
                            System.err.println("Error executing statement: " + statement);
                            System.err.println("Error message: " + e.getMessage());
                            // Continue with other statements
                        }
                    }
                }
            }
            
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
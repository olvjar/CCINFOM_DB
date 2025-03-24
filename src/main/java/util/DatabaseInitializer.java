package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DatabaseInitializer {
    public static void initialize() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return;
        } catch (SQLException e) {
            createDatabase();
        }
    }

    private static void createDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", 
                Constants.DB_USER, 
                Constants.DB_PASSWORD)) {
            
            Statement stmt = conn.createStatement();
            
            // Check if database exists
            ResultSet rs = conn.getMetaData().getCatalogs();
            boolean dbExists = false;
            while (rs.next()) {
                if ("computerrepairshop".equals(rs.getString(1))) {
                    dbExists = true;
                    break;
                }
            }
            rs.close();
            
            if (!dbExists) {
                System.out.println("Database not found. Creating new database...");
                
                // Read and execute SQL script
                try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/S14-02.sql");
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
                            }
                        }
                    }
                }
                System.out.println("Database initialized successfully");
            } else {
                System.out.println("Database already exists. Skipping initialization.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
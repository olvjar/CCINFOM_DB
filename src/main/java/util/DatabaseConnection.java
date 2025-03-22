package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    static {
        try {
            // Register JDBC driver
            Class.forName(Constants.JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            Constants.DB_URL, 
            Constants.DB_USER, 
            Constants.DB_PASSWORD
        );
    }
} 
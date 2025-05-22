package com.library.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;
    private static final Properties properties = new Properties();

    static {
        try {
            // Load configuration from properties file
            properties.load(DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties"));
            
            Class.forName(properties.getProperty("db.driver"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database driver", e);
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
                );
            } catch (SQLException e) {
                throw new RuntimeException("Failed to establish database connection", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
    }
}

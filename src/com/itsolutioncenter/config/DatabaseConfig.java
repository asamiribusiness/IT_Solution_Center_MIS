package com.itsolutioncenter.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static Properties properties = new Properties();
    private static Connection connection = null;
     // Database configuration constants
    public static final String DB_URL;
    public static final String DB_USER;
    public static final String DB_PASSWORD;
    public static final String DB_NAME;
    public static final String DB_HOST;
    public static final String DB_PORT;
    public static final int DB_POOL_SIZE;
    public static final boolean DB_USE_SSL;
    static {
        // Load configuration from properties file
        loadProperties();
        // Initialize constants from properties
        DB_HOST = properties.getProperty("db.host", "localhost");
        DB_PORT = properties.getProperty("db.port", "3306");
        DB_NAME = properties.getProperty("db.name", "it_solution_center");
        DB_USER = properties.getProperty("db.user", "root");
        DB_PASSWORD = properties.getProperty("db.password", "admin");
        DB_POOL_SIZE = Integer.parseInt(properties.getProperty("db.pool.size", "10"));
        DB_USE_SSL = Boolean.parseBoolean(properties.getProperty("db.use.ssl", "false"));
         // Construct the URL
//        StringBuilder urlBuilder = new StringBuilder();
//        urlBuilder.append("jdbc:mysql://")
//                 .append(DB_HOST)
//                 .append(":")
//                 .append(DB_PORT)
//                 .append("/")
//                 .append(DB_NAME)
//                 .append("?useUnicode=true&characterEncoding=UTF-8");
        if (DB_USE_SSL) {
            DB_URL="jdbc:mysql://localhost:3306/it_solution_center?useUnicode=true&characterEncoding=UTF-8"
                    + "&useSSL=true&requireSSL=true";
           // urlBuilder.append("&useSSL=true&requireSSL=true");
        } else {
           DB_URL="jdbc:mysql://localhost:3306/it_solution_center?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
            //urlBuilder.append("&useSSL=false");
        }
        ////////////DB_URL = urlBuilder.toString();
        
        // Load MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOGGER.info("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,"MySQL JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }
    public static String getProperty(String key) {return properties.getProperty(key); }
    public static String getDatabaseURL() {return getProperty("db.url");  }
    public static String getDatabaseUsername() { return getProperty("db.username"); }
    public static String getDatabasePassword() { return getProperty("db.password");  }
    public static String getAppName() { return getProperty("app.name"); }
    public static String getAppVersion() { return getProperty("app.version");  }
    /**
     * Load database configuration from properties file
     */
    private static void loadProperties() {
        try {
            // Try to load from current directory first
            FileInputStream fis = new FileInputStream("src/resources/config.properties");
            properties.load(fis);
            LOGGER.info("Configuration loaded from config.properties");
        } catch (IOException e1) {
            LOGGER.warning("Could not load config.properties from file system, using defaults");
            // Set default values
            properties.setProperty("db.host", "localhost");
            properties.setProperty("db.port", "3306");
            properties.setProperty("db.name", "it_solution_center");
            properties.setProperty("db.user", "root");
            properties.setProperty("db.password", "admin");
            properties.setProperty("db.pool.size", "10");
            properties.setProperty("db.use.ssl", "false");
            properties.setProperty("app.name", "IT Solution Center MIS");
            properties.setProperty("app.version", "1.0.0");
        }
    }
    /**
     * Get a database connection
     * @return Connection object
     */
    public static Connection getConnection() {
        if (connection == null || isConnectionClosed(connection)) {
            try {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                LOGGER.info("Database connection established: " + DB_URL);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return connection;
    }
    /**
     * Check if connection is closed
     */
    private static boolean isConnectionClosed(Connection conn) {
        try {
            return conn == null || conn.isClosed();
        } catch (SQLException e) {
            LOGGER.warning("Error checking connection status: " + e.getMessage());
            return true;
        }
    }
    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    LOGGER.info("Database connection closed.");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            } finally {
                connection = null;
            }
        }
    }
    /**
     * Close any connection
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        Connection testConn = null;
        try {
            testConn = getConnection();
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed", e);
            return false;
        } finally {
            closeConnection(testConn);
        }
    }
    /**
     * Get database information
     * @return String with database info
     */
    public static String getDatabaseInfo() {
        return String.format("Database Configuration:\nHost: %s:%s\nDatabase: %s\nUser: %s\nURL: %s",
            DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_URL);
    }
    /**
     * Get application properties
     * @return Properties object
     */
    public static Properties getProperties() { return new Properties(properties);  }
    /**
     * Get a specific property
     * @param key Property key
     * @param defaultValue Default value if not found
     * @return Property value
     */
    public static String getProperty(String key, String defaultValue) {return properties.getProperty(key, defaultValue);    }
    /**
     * Set a property (for runtime configuration)
     * @param key Property key
     * @param value Property value
     */
    public static void setProperty(String key, String value) { properties.setProperty(key, value); }
     // private static Properties properties = new Properties();
//   
// private static void setDefaultProperties() {
//        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/it_solution_center");
//        properties.setProperty("db.username", "root");
//        properties.setProperty("db.password", "admin");
//        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
//        properties.setProperty("app.name", "Supermarket Management System");
//        properties.setProperty("app.version", "1.0");
//    }   
    /**
     * Get a new database connection (not from pool)
     * @return New Connection object
     */
//    public static Connection getNewConnection() {
//        try {
//            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null,"Failed to create new database connection"+e.getMessage());
//            throw new RuntimeException("New database connection failed", e);
//        }
//    }
}
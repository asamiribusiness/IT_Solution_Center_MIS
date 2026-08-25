package com.itsolutioncenter.config;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;

public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static Properties appProperties;
    // Application constants
    public static final String APP_NAME;
    public static final String APP_VERSION;
    public static final String APP_BUILD_DATE;
    public static final String COMPANY_NAME;
    public static final String COPYRIGHT;
    // UI Configuration
    public static final Color PRIMARY_COLOR;
    public static final Color SECONDARY_COLOR;
    public static final Color SUCCESS_COLOR;
    public static final Color ERROR_COLOR;
    public static final Color WARNING_COLOR;
    public static final Color INFO_COLOR;
    public static final Font HEADER_FONT;
    public static final Font TITLE_FONT;
    public static final Font NORMAL_FONT;
    public static final Font SMALL_FONT;
    // Path Configuration
    public static final String APP_HOME;
    public static final String CONFIG_DIR;
    public static final String LOG_DIR;
    public static final String REPORT_DIR;
    public static final String BACKUP_DIR;
    public static final String TEMP_DIR;
    // Date/Time Formats
    public static final SimpleDateFormat DATE_FORMAT;
    public static final SimpleDateFormat TIME_FORMAT;
    public static final SimpleDateFormat DATETIME_FORMAT;
    public static final SimpleDateFormat DB_DATE_FORMAT;
    // Application Settings
    public static final int SESSION_TIMEOUT_MINUTES;
    public static final int MAX_LOGIN_ATTEMPTS;
    public static final boolean DEBUG_MODE;
    public static final boolean LOG_TO_FILE;
    public static final Level LOG_LEVEL;

    static {
        // Initialize properties
        appProperties = DatabaseConfig.getProperties();       
        // Load application properties
        APP_NAME = appProperties.getProperty("app.name", "IT Solution Center MIS");
        APP_VERSION = appProperties.getProperty("app.version", "1.0.0");
        APP_BUILD_DATE = appProperties.getProperty("app.build.date", "2026-01-01");
        COMPANY_NAME = appProperties.getProperty("company.name", "IT Solution Center");
        COPYRIGHT = appProperties.getProperty("app.copyright", "© 2026 IT Solution Center. All rights reserved.");    
        // UI Colors
        PRIMARY_COLOR = new Color(41, 128, 185);   // Blue
        SECONDARY_COLOR = new Color(52, 73, 94);    // Dark Blue
        SUCCESS_COLOR = new Color(46, 204, 113);    // Green
        ERROR_COLOR = new Color(231, 76, 60);       // Red
        WARNING_COLOR = new Color(241, 196, 15);    // Yellow
        INFO_COLOR = new Color(52, 152, 219);       // Light Blue
        // Fonts
        HEADER_FONT = new Font("Arial", Font.BOLD, 18);
        TITLE_FONT = new Font("Arial", Font.BOLD, 14);
        NORMAL_FONT = new Font("Arial", Font.PLAIN, 12);
        SMALL_FONT = new Font("Arial", Font.PLAIN, 11);
        // Directory paths
        String userHome = System.getProperty("user.home");
        APP_HOME = userHome + File.separator + ".itsolutioncenter";
        CONFIG_DIR = APP_HOME + File.separator + "config";
        LOG_DIR = APP_HOME + File.separator + "logs";
        REPORT_DIR = APP_HOME + File.separator + "reports";
        BACKUP_DIR = APP_HOME + File.separator + "backups";
        TEMP_DIR = APP_HOME + File.separator + "temp";
        // Create directories if they don't exist
        createDirectories();
        // Date formats
        DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
        DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        // Application settings
        SESSION_TIMEOUT_MINUTES = Integer.parseInt(appProperties.getProperty("session.timeout", "30"));
        MAX_LOGIN_ATTEMPTS = Integer.parseInt(appProperties.getProperty("max.login.attempts", "3"));
        DEBUG_MODE = Boolean.parseBoolean(appProperties.getProperty("debug.mode", "false"));
        LOG_TO_FILE = Boolean.parseBoolean(appProperties.getProperty("log.to.file", "true"));
        String logLevelStr = appProperties.getProperty("log.level", "INFO");
        LOG_LEVEL = Level.parse(logLevelStr);
        // Setup logging
        setupLogging();
        LOGGER.info("Application configuration initialized");
        LOGGER.info(APP_NAME + " v" + APP_VERSION);
    }
    /**
     * Create necessary directories
     */
    private static void createDirectories() {
        String[] dirs = {APP_HOME, CONFIG_DIR, LOG_DIR, REPORT_DIR, BACKUP_DIR, TEMP_DIR};
        for (String dir : dirs) {
            File directory = new File(dir);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    LOGGER.info("Created directory: " + dir);
                } else {
                    LOGGER.warning("Failed to create directory: " + dir);
                }
            }
        }
    }
    /**
     * Setup application logging
     */
    private static void setupLogging() {
        try {
            Logger rootLogger = Logger.getLogger("");      
            // Remove default handlers
            java.util.logging.Handler[] handlers = rootLogger.getHandlers();
            for (java.util.logging.Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
            // Create console handler
            java.util.logging.ConsoleHandler consoleHandler = new java.util.logging.ConsoleHandler();
            consoleHandler.setLevel(LOG_LEVEL);
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);
            // Create file handler if enabled
            if (LOG_TO_FILE) {
                String logFile = LOG_DIR + File.separator + "application_%g.log";
                FileHandler fileHandler = new FileHandler(logFile, 1024 * 1024, 5, true);
                fileHandler.setLevel(LOG_LEVEL);
                fileHandler.setFormatter(new SimpleFormatter());
                rootLogger.addHandler(fileHandler);
            }
            // Set root logger level
            rootLogger.setLevel(LOG_LEVEL);
        } catch (Exception e) {
            System.err.println("Failed to setup logging: " + e.getMessage());
        }
    }
    /**
     * Get application property
     */
    public static String getProperty(String key) { return appProperties.getProperty(key);  }
    /**
     * Get application property with default
     */
  public static String getProperty(String key, String defaultValue) {return appProperties.getProperty(key, defaultValue);    }
    /**
     * Set application property
     */
    public static void setProperty(String key, String value) { appProperties.setProperty(key, value); }
    /**
     * Get all application properties
     */
    public static Properties getProperties() {return new Properties(appProperties); }
    /**
     * Get application information
     */
    public static String getAppInfo() {
        return String.format("%s v%s\nBuild Date: %s\nCompany: %s\nDatabase: %s@%s:%s\nHome Directory: %s",
            APP_NAME, APP_VERSION, APP_BUILD_DATE, COMPANY_NAME,
            DatabaseConfig.DB_NAME, DatabaseConfig.DB_HOST, DatabaseConfig.DB_PORT,
            APP_HOME
        );
    }
    /**
     * Check if application is in debug mode
     */
    public static boolean isDebugMode() {return DEBUG_MODE; }
    /**
     * Get the main application directory
     */
    public static File getAppHomeDirectory() {return new File(APP_HOME); }
    /**
     * Get the configuration directory
     */
    public static File getConfigDirectory() {return new File(CONFIG_DIR);  }
    /**
     * Get the reports directory
     */
    public static File getReportDirectory() {return new File(REPORT_DIR); }
    /**
     * Get the backups directory
     */
    public static File getBackupDirectory() {return new File(BACKUP_DIR);   }
    /**
     * Get a formatted date string
     */
    public static String formatDate(java.util.Date date) {  if (date == null) return "";
        return DATE_FORMAT.format(date);   }
    /**
     * Get a formatted date-time string
     */
    public static String formatDateTime(java.util.Date date) { if (date == null) return "";
    return DATETIME_FORMAT.format(date);    }
    /**
     * Validate application environment
     */
    public static boolean validateEnvironment() {
        try {
            // Check database connection
            if (!DatabaseConfig.testConnection()) {
                JOptionPane.showMessageDialog(null,"Database connection test failed");
                return false;
            }
            // Check write permissions
            File testFile = new File(TEMP_DIR + File.separator + "test.tmp");
            if (!testFile.getParentFile().canWrite()) {
                JOptionPane.showMessageDialog(null,"No write permission in application directory");
                return false;
            }      
            LOGGER.info("Environment validation successful");
            return true;
           
        } catch (HeadlessException e) {
            LOGGER.log(Level.SEVERE, "Environment validation failed", e);
            return false;
        }
    }
    /**
     * Initialize application configuration
     * Call this at application startup
     */
    public static void initialize() {
        LOGGER.info("Application initialization started");
        // Validate environment
        if (!validateEnvironment()) {
            throw new RuntimeException("Application environment validation failed");
        }
        LOGGER.info("Application initialization completed");
    }
}
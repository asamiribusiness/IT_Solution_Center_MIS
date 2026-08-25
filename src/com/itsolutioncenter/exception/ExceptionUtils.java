package com.itsolutioncenter.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
* Utility class for exception handling.
*/
public class ExceptionUtils {
   
    private ExceptionUtils() {
        // Utility class - prevent instantiation
    }
   
    /**
     * Converts an exception stack trace to a string.
     */
    public static String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
       
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
   
    /**
     * Gets the root cause of an exception.
     */
    public static Throwable getRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
       
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
   
    /**
     * Gets the root cause message.
     */
    public static String getRootCauseMessage(Throwable throwable) {
        Throwable rootCause = getRootCause(throwable);
        return rootCause != null ? rootCause.getMessage() : null;
    }
   
    /**
     * Checks if the exception chain contains a specific exception type.
     */
    public static boolean containsException(Throwable throwable, Class<?> exceptionType) {
        if (throwable == null || exceptionType == null) {
            return false;
        }
       
        Throwable current = throwable;
        while (current != null) {
            if (exceptionType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
   
    /**
     * Wraps an exception in a DatabaseException if it's a SQLException.
     */
    public static DatabaseException wrapAsDatabaseException(String operation, Throwable cause) {
        if (cause instanceof DatabaseException) {
            return (DatabaseException) cause;
        } else if (cause instanceof java.sql.SQLException) {
            return new DatabaseException(operation, cause);
        } else {
            return new DatabaseException(operation + " failed", cause);
        }
    }
   
    /**
     * Logs an exception with appropriate level.
     */
    public static void logException(java.util.logging.Logger logger, Throwable throwable) {
        if (throwable == null || logger == null) {
            return;
        }
       
        if (throwable instanceof DatabaseException) {
            ((DatabaseException) throwable).log(logger);
        } else if (throwable instanceof ValidationException) {
            logger.warning("Validation Error: " + throwable.getMessage());
        } else {
            logger.severe("Unexpected Error: " + getStackTraceAsString(throwable));
        }
    }
   
    /**
     * Shows an error dialog for any exception.
     */
    public static void showErrorDialog(java.awt.Component parent, Throwable throwable) {
        if (throwable == null) {
            return;
        }
       
        if (throwable instanceof DatabaseException) {
            ((DatabaseException) throwable).showErrorDialog(parent);
        } else if (throwable instanceof ValidationException) {
            ((ValidationException) throwable).showErrorDialog(parent);
        } else {
            String message = "An unexpected error occurred:\n\n" +
                           throwable.getMessage() +
                           "\n\nPlease contact support if this persists.";
           
            javax.swing.JOptionPane.showMessageDialog(
                parent,
                message,
                "Unexpected Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }
   
    /**
     * Creates a formatted error message for display.
     */
    public static String createErrorMessage(Throwable throwable, boolean includeStackTrace) {
        if (throwable == null) {
            return "Unknown error";
        }
       
        StringBuilder message = new StringBuilder();
       
        if (throwable instanceof DatabaseException) {
            message.append(((DatabaseException) throwable).getUserFriendlyMessage());
        } else if (throwable instanceof ValidationException) {
            message.append(((ValidationException) throwable).getUserFriendlyMessage());
        } else {
            message.append("Error: ").append(throwable.getMessage());
        }
       
        if (includeStackTrace) {
            message.append("\n\nTechnical Details:\n");
            message.append(getStackTraceAsString(throwable));
        }
       
        return message.toString();
    }
}
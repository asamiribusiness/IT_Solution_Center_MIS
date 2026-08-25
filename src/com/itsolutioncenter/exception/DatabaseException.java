package com.itsolutioncenter.exception;

import java.sql.SQLException;

/**
* Custom exception class for database-related errors.
* Provides more meaningful error messages and preserves SQL state.
*/
public class DatabaseException extends Exception {
   
    private final String sqlState;
    private final int errorCode;
    private final String operation;
    private final String tableName;
   
    // Constructors
   
    /**
     * Constructs a new DatabaseException with the specified detail message.
     * @param message the detail message
     */
    public DatabaseException(String message) {
        super(message);
        this.sqlState = null;
        this.errorCode = 0;
        this.operation = null;
        this.tableName = null;
    }
   
    /**
     * Constructs a new DatabaseException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause (usually a SQLException)
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.sqlState = extractSqlState(cause);
        this.errorCode = extractErrorCode(cause);
        this.operation = null;
        this.tableName = null;
    }
   
    /**
     * Constructs a new DatabaseException with SQLException details.
     * @param message the detail message
     * @param sqlException the SQLException
     */
    public DatabaseException(String message, SQLException sqlException) {
        super(message, sqlException);
        this.sqlState = sqlException.getSQLState();
        this.errorCode = sqlException.getErrorCode();
        this.operation = null;
        this.tableName = null;
    }
   
    /**
     * Constructs a new DatabaseException with operation and table context.
     * @param operation the database operation (INSERT, UPDATE, DELETE, SELECT)
     * @param tableName the table name
     * @param cause the cause
     */
    public DatabaseException(String operation, String tableName, Throwable cause) {
        super(String.format("Database error during %s operation on table '%s': %s",
              operation, tableName, cause.getMessage()), cause);
        this.operation = operation;
        this.tableName = tableName;
        this.sqlState = extractSqlState(cause);
        this.errorCode = extractErrorCode(cause);
    }
   
    /**
     * Constructs a new DatabaseException with operation context.
     * @param operation the database operation
     * @param cause the cause
     */
    public DatabaseException(Throwable cause,String operation) {
        super(String.format("Database error during %s operation: %s",
              operation, cause.getMessage()), cause);
        this.operation = operation;
        this.tableName = null;
        this.sqlState = extractSqlState(cause);
        this.errorCode = extractErrorCode(cause);
    }
   
    /**
     * Constructs a new DatabaseException with connection error details.
     * @param url the database URL
     * @param username the database username
     * @param cause the cause
     */
    public DatabaseException(String url, Throwable cause, String username) {
        super(String.format("Failed to connect to database '%s' as user '%s': %s",
              url, username, cause.getMessage()), cause);
        this.sqlState = extractSqlState(cause);
        this.errorCode = extractErrorCode(cause);
        this.operation = "CONNECT";
        this.tableName = null;
    }
   
    /**
     * Constructs a new DatabaseException with constraint violation details.
     * @param constraintName the constraint name
     * @param cause the cause
     */
    public DatabaseException(Throwable cause, String constraintName, String message) {
        super(String.format("Constraint violation '%s': %s",
              constraintName, message), cause);
        this.sqlState = extractSqlState(cause);
        this.errorCode = extractErrorCode(cause);
        this.operation = null;
        this.tableName = null;
    }
   
    // Helper methods to extract SQL state and error code
   
    private String extractSqlState(Throwable cause) {
        if (cause instanceof SQLException) {
            return ((SQLException) cause).getSQLState();
        }
        return null;
    }
   
    private int extractErrorCode(Throwable cause) {
        if (cause instanceof SQLException) {
            return ((SQLException) cause).getErrorCode();
        }
        return 0;
    }
   
    // Getters
   
    /**
     * Returns the SQL state from the underlying SQLException.
     * @return the SQL state, or null if not available
     */
    public String getSqlState() {
        return sqlState;
    }
   
    /**
     * Returns the vendor-specific error code from the underlying SQLException.
     * @return the error code, or 0 if not available
     */
    public int getErrorCode() {
        return errorCode;
    }
   
    /**
     * Returns the database operation that caused the exception.
     * @return the operation, or null if not specified
     */
    public String getOperation() {
        return operation;
    }
   
    /**
     * Returns the table name involved in the operation.
     * @return the table name, or null if not specified
     */
    public String getTableName() {
        return tableName;
    }
   
    /**
     * Checks if this exception represents a connection failure.
     * @return true if it's a connection failure
     */
    public boolean isConnectionError() {
        return "CONNECT".equals(operation) ||
               (sqlState != null && sqlState.startsWith("08")) ||
               (getCause() != null && getCause().getMessage() != null &&
                getCause().getMessage().toLowerCase().contains("connection"));
    }
   
    /**
     * Checks if this exception represents a duplicate key/unique constraint violation.
     * @return true if it's a duplicate key error
     */
    public boolean isDuplicateKeyError() {
        return (sqlState != null && ("23000".equals(sqlState) || "23505".equals(sqlState))) ||
               (getCause() != null && getCause().getMessage() != null &&
                getCause().getMessage().toLowerCase().contains("duplicate"));
    }
   
    /**
     * Checks if this exception represents a foreign key constraint violation.
     * @return true if it's a foreign key error
     */
    public boolean isForeignKeyError() {
        return (sqlState != null && ("23000".equals(sqlState) || "23503".equals(sqlState))) ||
               (getCause() != null && getCause().getMessage() != null &&
                getCause().getMessage().toLowerCase().contains("foreign key"));
    }
   
    /**
     * Checks if this exception represents a null constraint violation.
     * @return true if it's a null constraint error
     */
    public boolean isNullConstraintError() {
        return (sqlState != null && "23000".equals(sqlState)) ||
               (getCause() != null && getCause().getMessage() != null &&
                getCause().getMessage().toLowerCase().contains("null"));
    }
   
    /**
     * Returns a user-friendly error message.
     * @return formatted error message
     */
    public String getUserFriendlyMessage() {
        if (isConnectionError()) {
            return "Cannot connect to the database. Please check:\n" +
                   "1. Database server is running\n" +
                   "2. Network connection is available\n" +
                   "3. Database credentials are correct";
        }
       
        if (isDuplicateKeyError()) {
            return "This record already exists in the database.\n" +
                   "Please check for duplicate entries.";
        }
       
        if (isForeignKeyError()) {
            return "Cannot perform this operation because related records exist.\n" +
                   "Please delete or update related records first.";
        }
       
        if (isNullConstraintError()) {
            return "Required fields cannot be empty.\n" +
                   "Please fill in all required information.";
        }
       
        String baseMessage = getMessage();
        if (baseMessage != null && baseMessage.contains(":")) {
            // Extract the part after the last colon for cleaner message
            return baseMessage.substring(baseMessage.lastIndexOf(":") + 1).trim();
        }
       
        return baseMessage != null ? baseMessage : "A database error occurred.";
    }
   
    /**
     * Returns detailed technical information for logging.
     * @return detailed error information
     */
    public String getTechnicalDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Database Exception Details:\n");
        details.append("Message: ").append(getMessage()).append("\n");
       
        if (operation != null) {
            details.append("Operation: ").append(operation).append("\n");
        }
       
        if (tableName != null) {
            details.append("Table: ").append(tableName).append("\n");
        }
       
        if (sqlState != null) {
            details.append("SQL State: ").append(sqlState).append("\n");
        }
       
        if (errorCode != 0) {
            details.append("Error Code: ").append(errorCode).append("\n");
        }
       
        Throwable cause = getCause();
        if (cause != null) {
            details.append("Cause: ").append(cause.getClass().getName())
                   .append(" - ").append(cause.getMessage()).append("\n");
           
            // Add stack trace for debugging
            if (isConnectionError() || isDuplicateKeyError()) {
                for (StackTraceElement element : cause.getStackTrace()) {
                    if (element.getClassName().contains("com.itsolutioncenter")) {
                        details.append("Location: ").append(element).append("\n");
                        break;
                    }
                }
            }
        }
       
        return details.toString();
    }
   
    /**
     * Logs this exception with appropriate level.
     * @param logger the logger to use
     */
    public void log(java.util.logging.Logger logger) {
        if (isConnectionError()) {
            logger.severe(getTechnicalDetails());
        } else if (isDuplicateKeyError() || isForeignKeyError()) {
            logger.warning(getTechnicalDetails());
        } else {
            logger.severe(getTechnicalDetails());
        }
    }
   
    /**
     * Shows a dialog with user-friendly error message.
     * @param parent the parent component for the dialog
     */
    public void showErrorDialog(java.awt.Component parent) {
        String title;
        int messageType;
       
        if (isConnectionError()) {
            title = "Database Connection Error";
            messageType = javax.swing.JOptionPane.ERROR_MESSAGE;
        } else if (isDuplicateKeyError()) {
            title = "Duplicate Entry";
            messageType = javax.swing.JOptionPane.WARNING_MESSAGE;
        } else if (isForeignKeyError()) {
            title = "Constraint Violation";
            messageType = javax.swing.JOptionPane.WARNING_MESSAGE;
        } else {
            title = "Database Error";
            messageType = javax.swing.JOptionPane.ERROR_MESSAGE;
        }
       
        javax.swing.JOptionPane.showMessageDialog(
            parent,
            getUserFriendlyMessage(),
            title,
            messageType
        );
    }
   
    // Static factory methods for common database errors
   
    /**
     * Creates a DatabaseException for connection failures.
     */
    public static DatabaseException connectionError(String url, String username, Throwable cause) {
        return new DatabaseException(url, username, cause);
    }
   
    /**
     * Creates a DatabaseException for duplicate key errors.
     */
    public static DatabaseException duplicateKeyError(String tableName, String fieldName, Throwable cause) {
        return new DatabaseException(
            String.format("Duplicate value for field '%s' in table '%s'", fieldName, tableName),
            cause
        );
    }
   
    /**
     * Creates a DatabaseException for foreign key violations.
     */
    public static DatabaseException foreignKeyError(String tableName, String constraintName, Throwable cause) {
        return new DatabaseException(
            String.format("Foreign key violation in table '%s' (constraint: %s)",
                         tableName, constraintName),
            cause
        );
    }
   
    /**
     * Creates a DatabaseException for null constraint violations.
     */
    public static DatabaseException nullConstraintError(String tableName, String columnName, Throwable cause) {
        return new DatabaseException(
            String.format("Null value not allowed for column '%s' in table '%s'",
                         columnName, tableName),
            cause
        );
    }
   
    /**
     * Creates a DatabaseException for data too long errors.
     */
    public static DatabaseException dataTooLongError(String tableName, String columnName, Throwable cause) {
        return new DatabaseException(
            String.format("Data too long for column '%s' in table '%s'",
                         columnName, tableName),
            cause
        );
    }
   
    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
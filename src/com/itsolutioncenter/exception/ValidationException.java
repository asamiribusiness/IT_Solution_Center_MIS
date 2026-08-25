package com.itsolutioncenter.exception;

import java.util.ArrayList;
import java.util.List;

/**
* Custom exception class for validation errors.
* Supports multiple validation errors with field names and messages.
*/
public class ValidationException extends Exception {
   
    private final List<ValidationError> errors;
    private final String formName;
    private final String validationType;
   
    /**
     * Inner class representing a single validation error.
     */
    public static class ValidationError {
        private final String fieldName;
        private final String message;
        private final ErrorType errorType;
        private final Object invalidValue;
       
        public enum ErrorType {
            REQUIRED,           // Field is required but empty
            INVALID_FORMAT,     // Invalid format (email, phone, etc.)
            OUT_OF_RANGE,       // Value out of allowed range
            TOO_SHORT,          // Value too short (min length)
            TOO_LONG,           // Value too long (max length)
            INVALID_CHOICE,     // Invalid selection from choices
            DUPLICATE,          // Duplicate value not allowed
            INVALID_DATE,       // Invalid date or date format
            INVALID_NUMBER,     // Invalid numeric value
            CUSTOM              // Custom validation error
        }
       
        /**
         * Constructs a new ValidationError.
         */
        public ValidationError(String fieldName, String message, ErrorType errorType, Object invalidValue) {
            this.fieldName = fieldName;
            this.message = message;
            this.errorType = errorType;
            this.invalidValue = invalidValue;
        }
       
        /**
         * Constructs a new ValidationError without invalid value.
         */
        public ValidationError(String fieldName, String message, ErrorType errorType) {
            this(fieldName, message, errorType, null);
        }
       
        // Getters
       
        public String getFieldName() {
            return fieldName;
        }
       
        public String getMessage() {
            return message;
        }
       
        public ErrorType getErrorType() {
            return errorType;
        }
       
        public Object getInvalidValue() {
            return invalidValue;
        }
       
        /**
         * Returns a user-friendly error message.
         */
        public String getUserFriendlyMessage() {
            StringBuilder friendlyMessage = new StringBuilder();
           
            if (fieldName != null && !fieldName.isEmpty()) {
                // Convert field name to readable format (e.g., "userName" -> "User Name")
                String readableFieldName = fieldName.replaceAll("([A-Z])", " $1")
                                                   .replaceAll("_", " ")
                                                   .trim();
                if (!readableFieldName.isEmpty()) {
                    readableFieldName = readableFieldName.substring(0, 1).toUpperCase() +
                                       readableFieldName.substring(1);
                }
               
                friendlyMessage.append(readableFieldName).append(": ");
            }
           
            friendlyMessage.append(message);
           
            if (invalidValue != null) {
                friendlyMessage.append(" (Value: '").append(invalidValue).append("')");
            }
           
            return friendlyMessage.toString();
        }
       
        @Override
        public String toString() {
            return String.format("ValidationError{field='%s', type=%s, message='%s', value=%s}",
                               fieldName, errorType, message, invalidValue);
        }
    }
   
    // Constructors
   
    /**
     * Constructs a new ValidationException with a single error.
     */
    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationError(null, message, ValidationError.ErrorType.CUSTOM));
        this.formName = null;
        this.validationType = "general";
    }
   
    /**
     * Constructs a new ValidationException with field-specific error.
     */
    public ValidationException(String fieldName, String message) {
        super(String.format("Validation error for field '%s': %s", fieldName, message));
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationError(fieldName, message, ValidationError.ErrorType.CUSTOM));
        this.formName = null;
        this.validationType = "field";
    }
   
    /**
     * Constructs a new ValidationException with field-specific error and type.
     */
    public ValidationException(String fieldName, String message, ValidationError.ErrorType errorType) {
        super(String.format("%s error for field '%s': %s", errorType, fieldName, message));
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationError(fieldName, message, errorType));
        this.formName = null;
        this.validationType = errorType.name().toLowerCase();
    }
   
    /**
     * Constructs a new ValidationException with form context.
     */
//    public ValidationException(String formName, String message) {
//        super(String.format("Validation error in form '%s': %s", formName, message));
//        this.errors = new ArrayList<>();
//        this.errors.add(new ValidationError(null, message, ValidationError.ErrorType.CUSTOM));
//        this.formName = formName;
//        this.validationType = "form";
//    }
   
    /**
     * Constructs a new ValidationException with multiple errors.
     */
    public ValidationException(List<ValidationError> errors) {
        super("Multiple validation errors occurred");
        this.errors = new ArrayList<>(errors);
        this.formName = null;
        this.validationType = "multiple";
    }
   
    /**
     * Constructs a new ValidationException with form context and multiple errors.
     */
    public ValidationException(String formName, List<ValidationError> errors) {
        super(String.format("Multiple validation errors in form '%s'", formName));
        this.errors = new ArrayList<>(errors);
        this.formName = formName;
        this.validationType = "multiple";
    }
   
    // Factory methods for common validation errors
   
    /**
     * Creates a ValidationException for required field error.
     */
    public static ValidationException requiredField(String fieldName) {
        return new ValidationException(
            fieldName,
            "This field is required and cannot be empty",
            ValidationError.ErrorType.REQUIRED
        );
    }
   
    /**
     * Creates a ValidationException for invalid email format.
     */
    public static ValidationException invalidEmail(String fieldName, String value) {
        return new ValidationException(
            fieldName,
            "Please enter a valid email address (e.g., name@example.com)",
            ValidationError.ErrorType.INVALID_FORMAT
        );
    }
   
    /**
     * Creates a ValidationException for invalid phone number.
     */
    public static ValidationException invalidPhone(String fieldName, String value) {
        return new ValidationException(
            fieldName,
            "Please enter a valid phone number (10-15 digits, may include +, -, or spaces)",
            ValidationError.ErrorType.INVALID_FORMAT
        );
    }
   
    /**
     * Creates a ValidationException for value too short.
     */
    public static ValidationException tooShort(String fieldName, int minLength, int actualLength) {
        return new ValidationException(
            fieldName,
            String.format("Must be at least %d characters long (currently %d)",
                         minLength, actualLength),
            ValidationError.ErrorType.TOO_SHORT
        );
    }
   
    /**
     * Creates a ValidationException for value too long.
     */
    public static ValidationException tooLong(String fieldName, int maxLength, int actualLength) {
        return new ValidationException(
            fieldName,
            String.format("Cannot exceed %d characters (currently %d)",
                         maxLength, actualLength),
            ValidationError.ErrorType.TOO_LONG
        );
    }
   
    /**
     * Creates a ValidationException for invalid numeric value.
     */
    public static ValidationException invalidNumber(String fieldName, String value) {
        return new ValidationException(
            fieldName,
            "Please enter a valid number",
            ValidationError.ErrorType.INVALID_NUMBER
        );
    }
   
    /**
     * Creates a ValidationException for value out of range.
     */
    public static ValidationException outOfRange(String fieldName, Number min, Number max, Number value) {
        String rangeMessage;
        if (min != null && max != null) {
            rangeMessage = String.format("Must be between %s and %s (current: %s)", min, max, value);
        } else if (min != null) {
            rangeMessage = String.format("Must be at least %s (current: %s)", min, value);
        } else {
            rangeMessage = String.format("Cannot exceed %s (current: %s)", max, value);
        }
       
        return new ValidationException(
            fieldName,
            rangeMessage,
            ValidationError.ErrorType.OUT_OF_RANGE
        );
    }
   
    /**
     * Creates a ValidationException for invalid date.
     */
    public static ValidationException invalidDate(String fieldName, String value, String format) {
        return new ValidationException(
            fieldName,
            String.format("Please enter a valid date in format: %s", format),
            ValidationError.ErrorType.INVALID_DATE
        );
    }
   
    /**
     * Creates a ValidationException for duplicate value.
     */
    public static ValidationException duplicateValue(String fieldName, String value) {
        return new ValidationException(
            fieldName,
            String.format("The value '%s' already exists. Please use a different value.", value),
            ValidationError.ErrorType.DUPLICATE
        );
    }
   
    // Methods to add errors
   
    /**
     * Adds a validation error to this exception.
     */
    public void addError(ValidationError error) {
        this.errors.add(error);
    }
   
    /**
     * Adds a validation error to this exception.
     */
    public void addError(String fieldName, String message, ValidationError.ErrorType errorType) {
        this.errors.add(new ValidationError(fieldName, message, errorType));
    }
   
    /**
     * Adds multiple validation errors to this exception.
     */
    public void addErrors(List<ValidationError> errors) {
        this.errors.addAll(errors);
    }
   
    // Getters
   
    /**
     * Returns the list of validation errors.
     */
    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
    }
   
    /**
     * Returns the form name where validation failed.
     */
    public String getFormName() {
        return formName;
    }
   
    /**
     * Returns the type of validation.
     */
    public String getValidationType() {
        return validationType;
    }
   
    /**
     * Checks if there are any validation errors.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
   
    /**
     * Returns the number of validation errors.
     */
    public int getErrorCount() {
        return errors.size();
    }
   
    /**
     * Returns the first error message.
     */
    public String getFirstErrorMessage() {
        if (errors.isEmpty()) {
            return getMessage();
        }
        return errors.get(0).getUserFriendlyMessage();
    }
   
    /**
     * Returns all error messages as a list.
     */
    public List<String> getAllErrorMessages() {
        List<String> messages = new ArrayList<>();
        for (ValidationError error : errors) {
            messages.add(error.getUserFriendlyMessage());
        }
        return messages;
    }
   
    /**
     * Returns all error messages as a single string.
     */
    public String getAllErrorsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            sb.append(i + 1).append(". ").append(errors.get(i).getUserFriendlyMessage());
            if (i < errors.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
   
    /**
     * Returns errors for a specific field.
     */
    public List<ValidationError> getErrorsForField(String fieldName) {
        List<ValidationError> fieldErrors = new ArrayList<>();
        for (ValidationError error : errors) {
            if (fieldName.equals(error.getFieldName())) {
                fieldErrors.add(error);
            }
        }
        return fieldErrors;
    }
   
    /**
     * Checks if there are errors for a specific field.
     */
    public boolean hasErrorsForField(String fieldName) {
        for (ValidationError error : errors) {
            if (fieldName.equals(error.getFieldName())) {
                return true;
            }
        }
        return false;
    }
   
    /**
     * Returns a user-friendly error message.
     */
    public String getUserFriendlyMessage() {
        if (errors.isEmpty()) {
            return getMessage();
        }
       
        if (errors.size() == 1) {
            return errors.get(0).getUserFriendlyMessage();
        }
       
        StringBuilder message = new StringBuilder();
        message.append("Please correct the following errors:\n\n");
       
        for (int i = 0; i < errors.size(); i++) {
            message.append(i + 1).append(". ").append(errors.get(i).getUserFriendlyMessage());
            if (i < errors.size() - 1) {
                message.append("\n");
            }
        }
       
        return message.toString();
    }
   
    /**
     * Shows a dialog with validation errors.
     */
    public void showErrorDialog(java.awt.Component parent) {
        String title;
        int messageType;
       
        if (errors.isEmpty()) {
            title = "Validation Error";
            messageType = javax.swing.JOptionPane.ERROR_MESSAGE;
            javax.swing.JOptionPane.showMessageDialog(
                parent,
                getMessage(),
                title,
                messageType
            );
        } else if (errors.size() == 1) {
            ValidationError error = errors.get(0);
            switch (error.getErrorType()) {
                case REQUIRED:
                    title = "Required Field";
                    messageType = javax.swing.JOptionPane.WARNING_MESSAGE;
                    break;
                case DUPLICATE:
                    title = "Duplicate Value";
                    messageType = javax.swing.JOptionPane.WARNING_MESSAGE;
                    break;
                default:
                    title = "Validation Error";
                    messageType = javax.swing.JOptionPane.ERROR_MESSAGE;
            }
           
            javax.swing.JOptionPane.showMessageDialog(
                parent,
                error.getUserFriendlyMessage(),
                title,
                messageType
            );
        } else {
            title = "Multiple Validation Errors";
            messageType = javax.swing.JOptionPane.ERROR_MESSAGE;
           
            javax.swing.JOptionPane.showMessageDialog(
                parent,
                getUserFriendlyMessage(),
                title,
                messageType
            );
        }
    }
   
    /**
     * Highlights invalid fields in a form.
     * @param component the parent component containing form fields
     */
    public void highlightInvalidFields(java.awt.Container component) {
        if (component == null || errors.isEmpty()) {
            return;
        }
       
        // Find and highlight components with errors
        java.awt.Component[] components = component.getComponents();
        for (java.awt.Component comp : components) {
            if (comp instanceof javax.swing.JTextField) {
                javax.swing.JTextField textField = (javax.swing.JTextField) comp;
                String fieldName = textField.getName();
               
                if (fieldName != null && hasErrorsForField(fieldName)) {
                    textField.setBackground(new java.awt.Color(255, 230, 230)); // Light red
                    textField.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED, 2));
                   
                    // Add tooltip with error message
                    List<ValidationError> fieldErrors = getErrorsForField(fieldName);
                    StringBuilder tooltip = new StringBuilder("<html>");
                    for (ValidationError error : fieldErrors) {
                        tooltip.append(error.getMessage()).append("<br>");
                    }
                    tooltip.append("</html>");
                    textField.setToolTipText(tooltip.toString());
                }
            }
           
            // Recursively check child containers
            if (comp instanceof java.awt.Container) {
                highlightInvalidFields((java.awt.Container) comp);
            }
        }
    }
   
    /**
     * Clears field highlighting.
     */
    public static void clearFieldHighlighting(java.awt.Container component) {
        if (component == null) {
            return;
        }
       
        java.awt.Component[] components = component.getComponents();
        for (java.awt.Component comp : components) {
            if (comp instanceof javax.swing.JTextField) {
                javax.swing.JTextField textField = (javax.swing.JTextField) comp;
                textField.setBackground(java.awt.Color.WHITE);
                textField.setBorder(javax.swing.UIManager.getBorder("TextField.border"));
                textField.setToolTipText(null);
            }
           
            if (comp instanceof javax.swing.JComboBox) {
                javax.swing.JComboBox<?> comboBox = (javax.swing.JComboBox<?>) comp;
                comboBox.setBackground(java.awt.Color.WHITE);
                comboBox.setBorder(javax.swing.UIManager.getBorder("ComboBox.border"));
                comboBox.setToolTipText(null);
            }
           
            if (comp instanceof javax.swing.JTextArea) {
                javax.swing.JTextArea textArea = (javax.swing.JTextArea) comp;
                textArea.setBackground(java.awt.Color.WHITE);
                textArea.setBorder(javax.swing.UIManager.getBorder("TextArea.border"));
                textArea.setToolTipText(null);
            }
           
            // Recursively clear child containers
            if (comp instanceof java.awt.Container) {
                clearFieldHighlighting((java.awt.Container) comp);
            }
        }
    }
   
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationException{");
        if (formName != null) {
            sb.append("form='").append(formName).append("', ");
        }
        sb.append("type='").append(validationType).append("', ");
        sb.append("errors=[");
        for (int i = 0; i < errors.size(); i++) {
            sb.append(errors.get(i));
            if (i < errors.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
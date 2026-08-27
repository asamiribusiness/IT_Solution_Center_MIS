package com.itsolutioncenter.util;


import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Validator {
private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\s-]{10,15}$");  
    

    public static boolean validateRequired(JTextField field,String fieldName) {
        if (field == null || field.getText().trim().isEmpty()) {
            //highlightInvalidFields(field);
        field.requestFocus();
        field.setBackground(new Color(255,230,230));
        field.setForeground(Color.red);
        field.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            JOptionPane.showMessageDialog(null,
                fieldName + " is required!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    public static boolean validateRequired(String fieldName, String value) {
        
        return validateRequired(fieldName,value);
    }
    public static boolean validateDateRequired(JDateChooser field,String fieldName) {
        
        if (field == null){
            //highlightInvalidFields(field);
        field.requestFocusInWindow();
        field.setBackground(new Color(255,230,230));
        field.setForeground(Color.red);
        field.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            JOptionPane.showMessageDialog(null,
                fieldName + " is required!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
     public static boolean isEmailValid(String email,String fieldName,JTextField field) {
        if (!EMAIL_PATTERN.matcher(email).matches()) 
        {
            //highlightInvalidFields(field);
        field.requestFocusInWindow();
        field.setBackground(new Color(255,230,230));
        field.setForeground(Color.red);
        field.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            JOptionPane.showMessageDialog(null,
                "Valid "+fieldName + " is required!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }    
        return EMAIL_PATTERN.matcher(email).matches();    
    }
    public static boolean validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return true; // Optional
        String phoneRegex = "^[0-9+\\s-]{10,15}$";
            return phone.matches(phoneRegex);
    }
    public static boolean validateNumber(String number, String fieldName,JTextField field) {
        try {
            Double.valueOf(number);
            return true;
        } catch (NumberFormatException e) {
            //highlightInvalidFields(field);
        field.requestFocusInWindow();
        field.setBackground(new Color(255,230,230));
        field.setForeground(Color.red);
        field.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            JOptionPane.showMessageDialog(null,
                fieldName + " must be a valid number!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    public static boolean isEmpty(JTextField field) {
        return field == null || isEmpty(field.getText());
    }
    public static boolean dateRequired(JDateChooser field,String fieldName)
    {
        Date value=field.getDate();
        if(value==null)
        {
            JOptionPane.showMessageDialog(null,
                fieldName + " is required!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else
            return true;
    }
    public static boolean isPhoneValid(String phone,JTextField field,String fieldName) {
        if (isEmpty(phone)) return true; // Phone is optional
        if(PHONE_PATTERN.matcher(phone).matches())
        {
         return true;
        }
        else
        {
        field.requestFocusInWindow();
        field.setBackground(new Color(255,230,230));
        field.setForeground(Color.red);
        field.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
        JOptionPane.showMessageDialog(null,
                fieldName + " must be a valid phone number!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        return false;
        }
    }
    public static boolean isNumeric(String value) {
        if (isEmpty(value)) return false;
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isInteger(String value) {
        if (isEmpty(value)) return false;
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isPositiveNumber(String value) {
        if (!isNumeric(value)) return false;
        return Double.parseDouble(value) > 0;
    }
    public static boolean isWithinRange(String value, double min, double max) {
        if (!isNumeric(value)) return false;
        double num = Double.parseDouble(value);
        return num >= min && num <= max;
    }
    public static boolean isLengthValid(String value, int min, int max) {
        if (value == null) return false;
        int length = value.trim().length();
        return length >= min && length <= max;
    }
    public static boolean isDateValid(Date dateStr,JDateChooser field) {
        if(DateUtil.isValidDate(dateStr) || field!=null)
            return true;
        else {
            //highlightInvalidFields(field);
        field.requestFocusInWindow();
        field.setBackground(new Color(255,230,230));
        field.setForeground(Color.red);
        field.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            JOptionPane.showMessageDialog(null,
                "Field must be a valid Date!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public static void validateNumeric(String value, String fieldName) {
        if (!isEmpty(value) && !isNumeric(value)) {
            throw new IllegalArgumentException(fieldName + " must be a number");
        }
    }
    public static void validatePositive(String value, String fieldName) {
        if (!isEmpty(value) && !isPositiveNumber(value)) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }
   public static boolean isMatchedPassword(String newPassword,String confirmPassword,JTextField field)
    {
        if (!newPassword.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(null, "Passwords do not match","Error", JOptionPane.ERROR_MESSAGE);
        field.requestFocusInWindow();
        field.setBackground(new Color(255,230,230));
        field.setForeground(Color.red);
        field.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
                return false;
            }
       return true;
    }  

}
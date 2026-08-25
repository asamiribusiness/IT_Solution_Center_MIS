package com.itsolutioncenter.util;

import com.toedter.calendar.JDateChooser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;

public class DateUtil {
   
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_FORMAT = "dd/MM/yyyy";
    private static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
   
    private DateUtil() {
        // Utility class
    }
   
    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT);
        return sdf.format(date);
    }
   
    public static String formatDateForDB(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
   
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
       
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    public static boolean isValidDate(Date dateStr)
   {
        if (dateStr == null) return false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            String dString=formatDateForDB(dateStr);
            LocalDate.parse(dString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
   }
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null) return false;
       
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public static int calculateAge(Date birthDate) {
        if (birthDate == null) return 0;
       
        LocalDate birth = convertToLocalDate(birthDate);
        LocalDate now = LocalDate.now();
        return Period.between(birth, now).getYears();
    }
   
    public static boolean isFutureDate(Date date) {
        if (date == null) return false;
        return date.after(new Date());
    }
   
    public static boolean isPastDate(Date date) {
        if (date == null) return false;
        return date.before(new Date());
    }
   
    public static Date getCurrentDate() {
        return new Date();
    }
   
    public static String getCurrentDateString() {
        return formatDateForDB(new Date());
    }
   
    private static LocalDate convertToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
   
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
       
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.add(java.util.Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
   
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(date);
    }
    private void validateDateSelection(JDateChooser dateChooser) {
    Date selected = dateChooser.getDate();
    Date today = new Date();
   
    if (selected != null) {
        // Check if date is in future
        if (selected.after(today)) {
            JOptionPane.showMessageDialog(null,
                "Future dates are not allowed!",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE);
            dateChooser.setDate(today); // Reset to today
        }
       
        // Check minimum date (e.g., 18 years ago)
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -18);
        if (selected.after(minDate.getTime())) {
            JOptionPane.showMessageDialog(null,
                "Must be at least 18 years old!",
                "Age Restriction",
                JOptionPane.WARNING_MESSAGE);
        }
    }
}
}
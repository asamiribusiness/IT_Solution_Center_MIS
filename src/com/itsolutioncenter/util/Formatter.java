package com.itsolutioncenter.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Formatter {
   
    private static final NumberFormat CURRENCY_FORMAT =
        NumberFormat.getCurrencyInstance(new Locale("Dari", "AF"));
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat TIME_FORMAT =
        new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATETIME_FORMAT =
        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
   
    private Formatter() {
        // Utility class
    }
   
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
   public static String formatCurrency(Number amount)
   {
       return CURRENCY_FORMAT.format(amount);
   }
    public static String formatCurrency(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return formatCurrency(value);
        } catch (NumberFormatException e) {
            return amount;
        }
    }
   
    public static String formatDecimal(double number) {
        return DECIMAL_FORMAT.format(number);
    }
   
    public static String formatDecimal(String number) {
        try {
            double value = Double.parseDouble(number);
            return formatDecimal(value);
        } catch (NumberFormatException e) {
            return number;
        }
    }
   
    public static String formatPercentage(double percentage) {
        return String.format("%.1f%%", percentage);
    }
   
    public static String formatPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return "";
       
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 10) {
            return String.format("%s-%s-%s",
                digits.substring(0, 3),
                digits.substring(3, 6),
                digits.substring(6));
        }
        return phone;
    }
   
    public static String formatName(String name) {
        if (name == null || name.trim().isEmpty()) return "";
       
        String[] words = name.trim().split("\\s+");
        StringBuilder formatted = new StringBuilder();
       
        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
       
        return formatted.toString().trim();
    }
   
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }
   
    public static String formatTime(Date date) {
        if (date == null) return "";
        return TIME_FORMAT.format(date);
    }
   
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DATETIME_FORMAT.format(date);
    }
   
    public static String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
   
    public static String formatStatus(boolean status) {
        return status ? "Active" : "Inactive";
    }
   
    public static String formatYesNo(boolean value) {
        return value ? "Yes" : "No";
    }
   
    public static String formatRole(String role) {
        if (role == null) return "";
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
    }
   
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) str = "";
        if (str.length() >= length) return str;
       
        StringBuilder padded = new StringBuilder();
        for (int i = str.length(); i < length; i++) {
            padded.append(padChar);
        }
        padded.append(str);
        return padded.toString();
    }
   
    public static String padRight(String str, int length, char padChar) {
        if (str == null) str = "";
        if (str.length() >= length) return str;
       
        StringBuilder padded = new StringBuilder(str);
        for (int i = str.length(); i < length; i++) {
            padded.append(padChar);
        }
        return padded.toString();
    }
}
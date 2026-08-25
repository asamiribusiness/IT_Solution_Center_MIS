
package com.itsolutioncenter.forms;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import javax.swing.*;
import java.awt.*;

public class HelpMenu {
   
    public static JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
       
        // Contents
        JMenuItem mniContents = new JMenuItem("Contents");
        mniContents.addActionListener(e -> showHelpContents());
        helpMenu.add(mniContents);
       
        // Search
        JMenuItem mniSearch = new JMenuItem("Search Help");
        mniSearch.addActionListener(e -> searchHelp());
        helpMenu.add(mniSearch);
       
        helpMenu.addSeparator();
       
        // Quick Start Guide
        JMenuItem mniQuickStart = new JMenuItem("Quick Start Guide");
        mniQuickStart.addActionListener(e -> showQuickStart());
        helpMenu.add(mniQuickStart);
       
        // User Manual
        JMenuItem mniManual = new JMenuItem("User Manual");
        mniManual.addActionListener(e -> showUserManual());
        helpMenu.add(mniManual);
       
        helpMenu.addSeparator();
       
        // About
        JMenuItem mniAbout = new JMenuItem("About");
        mniAbout.addActionListener(e -> showAboutDialog());
        helpMenu.add(mniAbout);
       
        // Contact Support
        JMenuItem mniContact = new JMenuItem("Contact Support");
        mniContact.addActionListener(e -> contactSupport());
        helpMenu.add(mniContact);
       
        // System Info
        JMenuItem mniSystemInfo = new JMenuItem("System Information");
        mniSystemInfo.addActionListener(e -> showSystemInfo());
        helpMenu.add(mniSystemInfo);
       
        helpMenu.addSeparator();
       
        // Keyboard Shortcuts
        JMenuItem mniShortcuts = new JMenuItem("Keyboard Shortcuts");
        mniShortcuts.addActionListener(e -> showShortcuts());
        helpMenu.add(mniShortcuts);
       
        // FAQ
        JMenuItem mniFAQ = new JMenuItem("Frequently Asked Questions");
        mniFAQ.addActionListener(e -> showFAQ());
        helpMenu.add(mniFAQ);
       
        // Troubleshooting
        JMenuItem mniTroubleshoot = new JMenuItem("Troubleshooting Guide");
        mniTroubleshoot.addActionListener(e -> showTroubleshooting());
        helpMenu.add(mniTroubleshoot);
       
        return helpMenu;
    }
   
    private static void showHelpContents() {
        HelpForm form = new HelpForm("Contents");
        form.setVisible(true);
    }
   
    public static void searchHelp() {
        String query = JOptionPane.showInputDialog(null,
            "Enter search term:", "Search Help", JOptionPane.QUESTION_MESSAGE);
       
        if (query != null && !query.trim().isEmpty()) {
            // Implement search logic
            JOptionPane.showMessageDialog(null,
                "Search results for: " + query + "\n\n" +
                "1. User Management\n" +
                "2. Adding New Users\n" +
                "3. Editing User Permissions",
                "Search Results",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
   
    public static void showQuickStart() {
        String quickStart = """
            IT SOLUTION CENTER MIS - QUICK START GUIDE
           
            1. LOGIN
               • Use your username and password
               • Contact admin if you forget password
           
            2. NAVIGATION
               • Use the menu bar for different modules
               • Dashboard shows quick statistics
           
            3. USER MANAGEMENT (Admin only)
               • Add new users
               • Assign roles and permissions
               • Deactivate users
           
            4. CLIENT MANAGEMENT
               • Add new clients
               • View client details
               • Track client projects
           
            5. SUPPORT TICKETS
               • Create new tickets
               • Assign to team members
               • Track resolution status
           
            6. COURSES
               • Create training courses
               • Enroll students
               • Track attendance
           
            7. FINANCE
               • Record income/expenses
               • Generate reports
               • View financial summary
           
            For detailed instructions, refer to User Manual.
            """;
       
        showTextDialog("Quick Start Guide", quickStart);
    }
   
    public static void showUserManual() {
        String manual = """
            ========================================
            IT SOLUTION CENTER MIS - USER MANUAL
            ========================================
           
            TABLE OF CONTENTS
           
            1. SYSTEM OVERVIEW
               1.1 Introduction
               1.2 System Requirements
               1.3 Login Procedure
           
            2. USER MANAGEMENT
               2.1 Adding New Users
               2.2 Editing User Profiles
               2.3 User Permissions
               2.4 Password Management
           
            3. CLIENT MANAGEMENT
               3.1 Adding New Clients
               3.2 Client Categories
               3.3 Client Communication
               3.4 Client Reports
           
            4. COURSE MANAGEMENT
               4.1 Creating Courses
               4.2 Student Enrollment
               4.3 Attendance Tracking
               4.4 Certificate Generation
           
            5. SUPPORT MANAGEMENT
               5.1 Creating Support Tickets
               5.2 Ticket Assignment
               5.3 Resolution Tracking
               5.4 Customer Feedback
           
            6. PROJECT MANAGEMENT
               6.1 Creating Projects
               6.2 Team Assignment
               6.3 Milestone Tracking
               6.4 Budget Management
           
            7. FINANCIAL MANAGEMENT
               7.1 Recording Income
               7.2 Recording Expenses
               7.3 Financial Reports
               7.4 Tax Calculations
           
            8. ASSET MANAGEMENT
               8.1 Adding Assets
               8.2 Asset Assignment
               8.3 Maintenance Tracking
               8.4 Depreciation
           
            9. REPORTING
               9.1 Generating Reports
               9.2 Report Types
               9.3 Exporting Data
               9.4 Print Reports
           
            10. TROUBLESHOOTING
                10.1 Common Issues
                10.2 Error Messages
                10.3 Contact Support
           
            For the complete manual, please contact administration.
            """;
       
        showTextDialog("User Manual", manual);
    }
   
    public static void showAboutDialog() {
        String aboutText = """
            IT Solution Center Management Information System
            Version: 1.0.0
            Release Date: January 2024
           
            Developed by: IT Solution Center Development Team
            Contact: support@itsolutioncenter.com
            Website: www.itsolutioncenter.com
           
            © 2024 IT Solution Center. All rights reserved.
           
            This software is designed to manage:
            • User and Client Management
            • Course and Training Programs
            • Technical Support Services
            • Development Projects
            • Financial Transactions
            • Asset and Inventory
           
            License: Proprietary - For internal use only.
            """;
       
        JTextArea textArea = new JTextArea(aboutText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
       
        JOptionPane.showMessageDialog(null,
            new JScrollPane(textArea),
            "About IT Solution Center MIS",
            JOptionPane.INFORMATION_MESSAGE);
    }
   
    public static void contactSupport() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
       
        panel.add(new JLabel("SUPPORT CONTACTS"));
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Email: support@itsolutioncenter.com"));
        panel.add(new JLabel("Phone: +93(0)799063252"));
        panel.add(new JLabel("Hours: Sat-Thu, 9AM-6PM"));
        panel.add(new JLabel("Emergency: +93(0)705063252"));
       
        JOptionPane.showMessageDialog(null, panel, "Contact Support", JOptionPane.INFORMATION_MESSAGE);
    }
   
    public static void showSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("SYSTEM INFORMATION\n");
        info.append("===================\n\n");
       
        info.append("Application: IT Solution Center MIS\n");
        info.append("Version: 1.0.0\n");
        info.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        info.append("OS: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append("\n");
        info.append("Architecture: ").append(System.getProperty("os.arch")).append("\n");
        info.append("User: ").append(System.getProperty("user.name")).append("\n");
        info.append("Memory: ").append(Runtime.getRuntime().totalMemory() / 1024 / 1024).append(" MB\n");
        info.append("Available Memory: ").append(Runtime.getRuntime().freeMemory() / 1024 / 1024).append(" MB\n");
       
        showTextDialog("System Information", info.toString());
    }
   
    public static void showShortcuts() {
        String shortcuts = """
            KEYBOARD SHORTCUTS
           
            General Shortcuts:
            Ctrl+N      - New Record
            Ctrl+S      - Save
            Ctrl+E      - Edit
            Ctrl+D      - Delete
            Ctrl+F      - Find/Search
            Ctrl+P      - Print
            Ctrl+Q      - Quick Report
            F1          - Help
            F5          - Refresh
            Esc         - Cancel/Close
           
            Navigation:
            Tab         - Next Field
            Shift+Tab   - Previous Field
            Ctrl+Tab    - Next Tab
            Ctrl+Shift+Tab - Previous Tab
            Alt+[Letter] - Menu Access
           
            Data Entry:
            Ctrl+Enter  - Save and New
            Ctrl+Shift+S - Save and Close
            Ctrl+Z      - Undo
            Ctrl+Y      - Redo
            Ctrl+C      - Copy
            Ctrl+V      - Paste
            Ctrl+X      - Cut
           
            For module-specific shortcuts, see module help.
            """;
       
        showTextDialog("Keyboard Shortcuts", shortcuts);
    }
   
    public static void showFAQ() {
        String faq = """
            FREQUENTLY ASKED QUESTIONS
           
            Q1: How do I reset my password?
            A: Contact your system administrator to reset your password.
           
            Q2: Can I access the system from home?
            A: Yes, if VPN access is configured by your IT department.
           
            Q3: How do I generate monthly reports?
            A: Go to Reports menu → Financial Reports → Monthly Report
           
            Q4: What should I do if I encounter an error?
            A: Note the error message and contact support with screenshot.
           
            Q5: How do I add a new client?
            A: Go to Manage menu → Client Management → Add New Client
           
            Q6: Can I export data to Excel?
            A: Yes, most reports have Export to Excel/CSV option.
           
            Q7: How do I track support ticket status?
            A: Go to Support menu → View Tickets → Filter by status
           
            Q8: What are the user roles and permissions?
            A: Admin (full access), Manager (most access), Employee (limited), Intern (view only)
           
            Q9: How do I mark attendance for courses?
            A: Go to Course menu → Attendance → Select course → Mark attendance
           
            Q10: Where can I see financial summary?
            A: Dashboard shows monthly summary. For detailed report, go to Finance menu.
            """;
       
        showTextDialog("FAQ", faq);
    }
   
    public static void showTroubleshooting() {
        String troubleshooting = """
            TROUBLESHOOTING GUIDE
           
            1. CAN'T LOGIN
               • Check username/password
               • Caps Lock might be on
               • Account may be deactivated
               • Contact administrator
           
            2. SLOW PERFORMANCE
               • Check internet connection
               • Close other applications
               • Clear browser cache (if web app)
               • Restart application
           
            3. DATA NOT SAVING
               • Check required fields
               • Verify database connection
               • Check user permissions
               • Try saving again
           
            4. REPORT NOT GENERATING
               • Check date range
               • Verify data exists for period
               • Check printer connection
               • Export to file instead
           
            5. ERROR MESSAGES
               • Note exact error message
               • Take screenshot if possible
               • Check error log file
               • Contact support with details
           
            6. CAN'T PRINT
               • Check printer connection
               • Verify printer has paper
               • Check print permissions
               • Try printing to PDF first
           
            For persistent issues, contact support with:
            • Error message
            • Steps to reproduce
            • Screenshot
            • Your user ID
            """;
       
        showTextDialog("Troubleshooting Guide", troubleshooting);
    }
   
    private static void showTextDialog(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
       
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
       
        JOptionPane.showMessageDialog(null,
            scrollPane,
            title,
            JOptionPane.INFORMATION_MESSAGE);
    }
}

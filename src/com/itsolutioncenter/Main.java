package com.itsolutioncenter;

import com.itsolutioncenter.config.AppConfig;
import com.itsolutioncenter.forms.LoginForm;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
   
    public static void main(String[] args) {
       // AnimatedSplashScreen.showAndLaunch(() -> {
      // SplashScreen.showSplashAndLaunch(()->{
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
         //Initialize application
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize configuration
                AppConfig.initialize();
                // Show login form
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
               
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Failed to initialize application: " + e.getMessage() +
                    "\n\nPlease check:\n1. Database connection\n2. Configuration files",
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
       // });
    }}
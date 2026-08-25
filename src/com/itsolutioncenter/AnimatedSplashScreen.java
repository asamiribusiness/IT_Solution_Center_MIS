
package com.itsolutioncenter;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.config.AppConfig;
import com.itsolutioncenter.forms.LoginForm;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class AnimatedSplashScreen extends JWindow {
    private JProgressBar progressBar;
    private Timer timer;
    private int counter = 0;
   
    public AnimatedSplashScreen() {
        createUI();
    }
   
    private void createUI() {
        setSize(600, 350);
        centerOnScreen();
       
        // Main panel with gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(30, 60, 114);
                Color endColor = new Color(42, 82, 152);
                GradientPaint gradient = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
       
        // Logo/Title area
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
       
        // Company name with custom font style
        JLabel companyName = new JLabel("<html><div style='text-align: center;'>" +
                                       "<span style='font-size: 36px; font-weight: bold; color: #FFFFFF;'>FARAH IT</span><br>" +
                                       "<span style='font-size: 24px; color: #AED6F1;'>SOLUTION CENTER</span>" +
                                       "</div></html>");
        companyName.setHorizontalAlignment(SwingConstants.CENTER);
        logoPanel.add(companyName, BorderLayout.CENTER);
       
        // Tagline
        JLabel tagline = new JLabel("Your Trusted IT Partner", SwingConstants.CENTER);
        tagline.setFont(new Font("Arial", Font.ITALIC, 14));
        tagline.setForeground(new Color(200, 230, 255));
        tagline.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        logoPanel.add(tagline, BorderLayout.SOUTH);
       
        // Progress bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 10, 20));
       
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(52, 73, 94));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(30, 50, 70), 2));
        progressBar.setFont(new Font("Arial", Font.BOLD, 10));
       
        progressPanel.add(progressBar, BorderLayout.CENTER);
       
        // Loading dots animation
        JLabel loadingDots = new JLabel("Loading", SwingConstants.CENTER);
        loadingDots.setFont(new Font("Arial", Font.PLAIN, 12));
        loadingDots.setForeground(Color.WHITE);
        loadingDots.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
       
        Timer dotsTimer = new Timer(500, new ActionListener() {
            private int dotCount = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                dotCount = (dotCount + 1) % 4;
                String dots = "Loading";
                for (int i = 0; i < dotCount; i++) {
                    dots += ".";
                }
                loadingDots.setText(dots);
            }
        });
        dotsTimer.start();
       
        progressPanel.add(loadingDots, BorderLayout.SOUTH);
       
        // Version info
        JLabel versionLabel = new JLabel("v2.0.1 | © 2026 Farah IT Solution Center", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        versionLabel.setForeground(new Color(180, 200, 220));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
       
        // Add all components
        mainPanel.add(logoPanel, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);
        mainPanel.add(versionLabel, BorderLayout.PAGE_END);
       
        setContentPane(mainPanel);
    }
   
    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
    }
   
    public void startProgress(int totalTimeMillis) {
        timer = new Timer(totalTimeMillis / 100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                progressBar.setValue(counter);
                progressBar.setString(counter + "%");
               
                if (counter >= 100) {
                    timer.stop();
                    dispose();
                    // Launch your main application here
                    launchMainApplication();
                }
            }
        });
        timer.start();
    }
   
    private void launchMainApplication() {
        // This will be called when splash screen closes
   try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
        // Initialize application
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
            } });
    }
   
    // Static method to easily use the splash screen
    public static void showAndLaunch(Runnable mainAppStarter, int displayTime) {
        SwingUtilities.invokeLater(() -> {
            AnimatedSplashScreen splash = new AnimatedSplashScreen() {
               // @Override
                protected void launchMainApplication() {
                    mainAppStarter.run();
                }
            };
            splash.setVisible(true);
            splash.startProgress(displayTime);
        });
    }
}

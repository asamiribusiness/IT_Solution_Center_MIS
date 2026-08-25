
package com.itsolutioncenter;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.config.AppConfig;
import com.itsolutioncenter.forms.LoginForm;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SplashScreen extends JWindow {
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JProgressBar progressBar;
    private JLabel lblVersion;
    private JLabel lblLoading;
    private Timer timer;
    private int progress = 0;
   
    public SplashScreen() {
        initComponents();
        centerScreen();
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
       
        // Start progress simulation
        startProgress();
    }
   
    private void initComponents() {
        // Set size
        setSize(600, 400);
       
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               
                // Gradient background
                Color color1 = new Color(41, 128, 185); // Blue
                Color color2 = new Color(44, 62, 80);   // Dark blue
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
       
        // Center panel for content
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
       
        // Logo/Icon
        JLabel lblIcon = new JLabel();
        lblIcon.setIcon(createIcon()); // You can replace with your actual logo
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        // Title
        lblTitle = new JLabel("FARAH IT SOLUTION CENTER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        // Subtitle
        lblSubtitle = new JLabel("Innovative IT Solutions for Modern Businesses");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(200, 230, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        // Spacer
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(lblIcon);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(lblTitle);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(lblSubtitle);
        centerPanel.add(Box.createVerticalGlue());
       
        // Progress bar panel
        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
       
        // Loading label
        lblLoading = new JLabel("Loading application...");
        lblLoading.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoading.setForeground(Color.WHITE);
        lblLoading.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(400, 20));
        progressBar.setMaximumSize(new Dimension(400, 20));
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113)); // Green
        progressBar.setBackground(new Color(52, 73, 94));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(30, 50, 70), 1));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        // Version label
        lblVersion = new JLabel("Version 2.0.1 © 2024 Farah IT Solution Center");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(new Color(180, 200, 220));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        progressPanel.add(lblLoading);
        progressPanel.add(Box.createVerticalStrut(10));
        progressPanel.add(progressBar);
        progressPanel.add(Box.createVerticalStrut(15));
        progressPanel.add(lblVersion);
       
        // Add panels to main panel
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);
       
        // Set content
        setContentPane(mainPanel);
       
        // Make window undecorated for custom shape
        setBackground(new Color(0, 0, 0, 0));
    }
   
    private Icon createIcon() {
        // Create a simple icon if you don't have one
        // Replace this with your actual logo: new ImageIcon("path/to/logo.png")
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               
                // Draw a simple computer icon
                int size = 80;
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x + 10, y + 15, size - 20, size - 30);
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillRect(x + 15, y + 20, size - 30, size - 40);
               
                // Draw "F" in the center
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "FITSC";
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, x + (size - textWidth) / 2, y + (size + textHeight / 2) / 2);
            }
           
            @Override
            public int getIconWidth() {
                return 100;
            }
           
            @Override
            public int getIconHeight() {
                return 100;
            }
        };
    }
   
    private void centerScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }
   
    private void startProgress() {
        timer = new Timer(50, e -> {
            progress += (int) (Math.random() * 5) + 1;
            if (progress > 100) {
                progress = 100;
                timer.stop();
                // Close splash screen after a brief pause
                Timer closeTimer = new Timer(1000, ev -> {
                    dispose();
                    // Launch main application here
                    launchMainApplication();
                });
                closeTimer.setRepeats(false);
                closeTimer.start();
            }
            progressBar.setValue(progress);
            updateLoadingText();
        });
        timer.start();
    }
   
    private void updateLoadingText() {
        String[] loadingTexts = {
            "Loading application...",
            "Initializing database...",
            "Loading user interface...",
            "Setting up modules...",
            "Finalizing setup...",
            "Ready to launch!"
        };
       
        int textIndex = progress / 20;
        if (textIndex >= 0 && textIndex < loadingTexts.length) {
            lblLoading.setText(loadingTexts[textIndex]);
        }
    }
   
    private void launchMainApplication() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
     //  Initialize application
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
    }
   
    // Method to show splash screen from your main class
    public static void showSplashAndLaunch(Runnable mainAppLauncher) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen() {
            //   @Override
                protected void launchMainApplication() {
                    mainAppLauncher.run();
                }
            };
            splash.setVisible(true);
        });
    }
   
    public static void main(String[] args) {
        // Test the splash screen
        SplashScreen.showSplashAndLaunch(() -> {
            System.out.println("Main application started!");
            // Your main application window here
            JFrame mainFrame = new JFrame("Farah IT Solution Center - Main Application");
            mainFrame.setSize(800, 600);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLocationRelativeTo(null);
           
            JLabel welcomeLabel = new JLabel("Welcome to Farah IT Solution Center!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            mainFrame.add(welcomeLabel);
           
            mainFrame.setVisible(true);
        });
    }
}
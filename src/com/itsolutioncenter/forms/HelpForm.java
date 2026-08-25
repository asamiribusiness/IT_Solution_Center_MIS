package com.itsolutioncenter.forms;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class HelpForm extends JFrame {
    private JTabbedPane tabbedPane;
    private JEditorPane editorPane;
    private JTree helpTree;
    private JTextField txtSearch;
   
    public HelpForm(String defaultTab) {
        initComponents();
        if (defaultTab != null) {
            selectTab(defaultTab);
        }
    }
   
    private void initComponents() {
        setTitle("IT Solution Center MIS - Help");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
       
        JMenu mnFile = new JMenu("File");
        JMenuItem mniPrint = new JMenuItem("Print");
        mniPrint.addActionListener(e -> printHelp());
        mnFile.add(mniPrint);
       
        JMenuItem mniClose = new JMenuItem("Close");
        mniClose.addActionListener(e -> dispose());
        mnFile.add(mniClose);
        menuBar.add(mnFile);
       
        JMenu mnEdit = new JMenu("Edit");
        JMenuItem mniCopy = new JMenuItem("Copy");
        mniCopy.addActionListener(e -> copyContent());
        mnEdit.add(mniCopy);
        menuBar.add(mnEdit);
       
        setJMenuBar(menuBar);
       
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
       
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Help:"));
        txtSearch = new JTextField(30);
        searchPanel.add(txtSearch);
       
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchHelpContent());
        searchPanel.add(btnSearch);
       
        mainPanel.add(searchPanel, BorderLayout.NORTH);
       
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
       
        // Contents Tab
        tabbedPane.addTab("Contents", createContentsTab());
       
        // Index Tab
        tabbedPane.addTab("Index", createIndexTab());
       
        // Search Results Tab
        tabbedPane.addTab("Search", createSearchTab());
       
        // FAQ Tab
        tabbedPane.addTab("FAQ", createFaqTab());
       
        // Tutorials Tab
        tabbedPane.addTab("Tutorials", createTutorialsTab());
       
        // Contact Tab
        tabbedPane.addTab("Contact", createContactTab());
       
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
       
        // Status Bar
        JLabel lblStatus = new JLabel("Ready");
        mainPanel.add(lblStatus, BorderLayout.SOUTH);
       
        add(mainPanel);
    }
   
    private JPanel createContentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
       
        // Create tree for navigation
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("IT Solution Center MIS");
       
        // System Overview
        DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode("System Overview");
        systemNode.add(new DefaultMutableTreeNode("Introduction"));
        systemNode.add(new DefaultMutableTreeNode("System Requirements"));
        systemNode.add(new DefaultMutableTreeNode("Login Procedure"));
        systemNode.add(new DefaultMutableTreeNode("Dashboard Overview"));
        root.add(systemNode);
       
        // User Management
        DefaultMutableTreeNode userNode = new DefaultMutableTreeNode("User Management");
        userNode.add(new DefaultMutableTreeNode("Adding New Users"));
        userNode.add(new DefaultMutableTreeNode("Editing User Profiles"));
        userNode.add(new DefaultMutableTreeNode("User Permissions"));
        userNode.add(new DefaultMutableTreeNode("Password Management"));
        root.add(userNode);
       
        // Client Management
        DefaultMutableTreeNode clientNode = new DefaultMutableTreeNode("Client Management");
        clientNode.add(new DefaultMutableTreeNode("Adding New Clients"));
        clientNode.add(new DefaultMutableTreeNode("Client Categories"));
        clientNode.add(new DefaultMutableTreeNode("Client Communication"));
        clientNode.add(new DefaultMutableTreeNode("Client Reports"));
        root.add(clientNode);
       
        // Course Management
        DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode("Course Management");
        courseNode.add(new DefaultMutableTreeNode("Creating Courses"));
        courseNode.add(new DefaultMutableTreeNode("Student Enrollment"));
        courseNode.add(new DefaultMutableTreeNode("Attendance Tracking"));
        courseNode.add(new DefaultMutableTreeNode("Certificate Generation"));
        root.add(courseNode);
       
        // Support Management
        DefaultMutableTreeNode supportNode = new DefaultMutableTreeNode("Support Management");
        supportNode.add(new DefaultMutableTreeNode("Creating Support Tickets"));
        supportNode.add(new DefaultMutableTreeNode("Ticket Assignment"));
        supportNode.add(new DefaultMutableTreeNode("Resolution Tracking"));
        supportNode.add(new DefaultMutableTreeNode("Customer Feedback"));
        root.add(supportNode);
       
        // Project Management
        DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode("Project Management");
        projectNode.add(new DefaultMutableTreeNode("Creating Projects"));
        projectNode.add(new DefaultMutableTreeNode("Team Assignment"));
        projectNode.add(new DefaultMutableTreeNode("Milestone Tracking"));
        projectNode.add(new DefaultMutableTreeNode("Budget Management"));
        root.add(projectNode);
       
        // Financial Management
        DefaultMutableTreeNode financeNode = new DefaultMutableTreeNode("Financial Management");
        financeNode.add(new DefaultMutableTreeNode("Recording Income"));
        financeNode.add(new DefaultMutableTreeNode("Recording Expenses"));
        financeNode.add(new DefaultMutableTreeNode("Financial Reports"));
        financeNode.add(new DefaultMutableTreeNode("Tax Calculations"));
        root.add(financeNode);
       
        // Asset Management
        DefaultMutableTreeNode assetNode = new DefaultMutableTreeNode("Asset Management");
        assetNode.add(new DefaultMutableTreeNode("Adding Assets"));
        assetNode.add(new DefaultMutableTreeNode("Asset Assignment"));
        assetNode.add(new DefaultMutableTreeNode("Maintenance Tracking"));
        assetNode.add(new DefaultMutableTreeNode("Depreciation"));
        root.add(assetNode);
       
        // Reporting
        DefaultMutableTreeNode reportNode = new DefaultMutableTreeNode("Reporting");
        reportNode.add(new DefaultMutableTreeNode("Generating Reports"));
        reportNode.add(new DefaultMutableTreeNode("Report Types"));
        reportNode.add(new DefaultMutableTreeNode("Exporting Data"));
        reportNode.add(new DefaultMutableTreeNode("Print Reports"));
        root.add(reportNode);
       
        // Troubleshooting
        DefaultMutableTreeNode troubleNode = new DefaultMutableTreeNode("Troubleshooting");
        troubleNode.add(new DefaultMutableTreeNode("Common Issues"));
        troubleNode.add(new DefaultMutableTreeNode("Error Messages"));
        troubleNode.add(new DefaultMutableTreeNode("Contact Support"));
        root.add(troubleNode);
       
        helpTree = new JTree(root);
        helpTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) helpTree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                showTopicContent(node.getUserObject().toString());
            }
        });
       
        JScrollPane treeScroll = new JScrollPane(helpTree);
        treeScroll.setPreferredSize(new Dimension(200, 0));
       
        // Content Panel
        editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setText(getWelcomeContent());
       
        // Make links clickable
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (e.getURL() != null) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
       
        JScrollPane editorScroll = new JScrollPane(editorPane);
       
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, editorScroll);
        splitPane.setDividerLocation(250);
       
        panel.add(splitPane, BorderLayout.CENTER);
       
        return panel;
    }
   
    private String getWelcomeContent() {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    h1 { color: #2c3e50; }
                    h2 { color: #3498db; border-bottom: 1px solid #eee; padding-bottom: 5px; }
                    .highlight { background-color: #f8f9fa; padding: 10px; border-left: 4px solid #3498db; }
                    .topic { margin: 15px 0; padding: 10px; border: 1px solid #ddd; border-radius: 5px; }
                    .contact { color: #e74c3c; }
                </style>
            </head>
            <body>
                <h1>IT Solution Center MIS - Help System</h1>
               
                <div class="highlight">
                    <p>Welcome to the help system for IT Solution Center Management Information System.</p>
                    <p>Use the tree on the left to navigate through topics, or use the search function to find specific information.</p>
                </div>
               
                <h2>Getting Started</h2>
                <div class="topic">
                    <p><b>Quick Start Guide:</b> For new users, we recommend starting with the Quick Start Guide available in the Help menu.</p>
                    <p><b>System Requirements:</b> Ensure your system meets the minimum requirements for optimal performance.</p>
                    <p><b>Login Instructions:</b> Detailed instructions for logging into the system.</p>
                </div>
               
                <h2>Modules Overview</h2>
                <div class="topic">
                    <p>The system consists of several modules:</p>
                    <ul>
                        <li><b>User Management:</b> Manage system users and permissions</li>
                        <li><b>Client Management:</b> Track clients and their information</li>
                        <li><b>Course Management:</b> Organize training courses and enrollments</li>
                        <li><b>Support Management:</b> Handle technical support tickets</li>
                        <li><b>Project Management:</b> Manage development projects</li>
                        <li><b>Financial Management:</b> Track income and expenses</li>
                        <li><b>Asset Management:</b> Manage company assets</li>
                        <li><b>Reporting:</b> Generate various reports</li>
                    </ul>
                </div>
               
                <h2>Need More Help?</h2>
                <div class="topic">
                    <p>If you can't find what you're looking for:</p>
                    <ul>
                        <li>Check the <b>FAQ</b> section for common questions</li>
                        <li>Use the <b>Search</b> function above</li>
                        <li class="contact">Contact support: support@itsolutioncenter.com</li>
                        <li>Phone: +93(0)799063252 (Sat-Thu, 8AM-4PM)</li>
                    </ul>
                </div>
            </body>
            </html>
            """;
    }
   
    private void showTopicContent(String topic) {
        String content = "";
       
        switch (topic) {
            case "Introduction":
                content = getIntroductionContent();
                break;
            case "Adding New Users":
                content = getAddUserContent();
                break;
            case "Creating Support Tickets":
                content = getSupportTicketContent();
                break;
            // Add more cases for other topics
            default:
                content = "<h1>" + topic + "</h1><p>Content for " + topic + " will be available soon.</p>";
        }
       
        editorPane.setText(content);
    }
   
    private String getIntroductionContent() {
        return """
            <html>
            <body>
                <h1>Introduction to IT Solution Center MIS</h1>
               
                <h2>Overview</h2>
                <p>The IT Solution Center Management Information System (MIS) is a comprehensive software solution designed to manage all aspects of an IT solution center's operations.</p>
               
                <h2>Purpose</h2>
                <p>This system helps in:</p>
                <ul>
                    <li>Managing client relationships and projects</li>
                    <li>Organizing training courses and student enrollments</li>
                    <li>Tracking technical support requests</li>
                    <li>Managing financial transactions</li>
                    <li>Tracking company assets</li>
                    <li>Generating reports for decision making</li>
                </ul>
               
                <h2>Key Features</h2>
                <ul>
                    <li><b>User-friendly Interface:</b> Easy to navigate and use</li>
                    <li><b>Role-based Access:</b> Different permissions for different user roles</li>
                    <li><b>Real-time Updates:</b> Instant updates across the system</li>
                    <li><b>Reporting:</b> Comprehensive reporting capabilities</li>
                    <li><b>Security:</b> Secure data storage and transmission</li>
                </ul>
               
                <h2>Getting Started</h2>
                <p>To get started with the system:</p>
                <ol>
                    <li>Log in with your credentials</li>
                    <li>Explore the dashboard for quick overview</li>
                    <li>Use the menu to navigate to different modules</li>
                    <li>Refer to specific help topics for each module</li>
                </ol>
            </body>
            </html>
            """;
    }
   
    private String getAddUserContent() {
        return """
            <html>
            <body>
                <h1>Adding New Users</h1>
               
                <h2>Prerequisites</h2>
                <ul>
                    <li>You must have Admin or Manager role</li>
                    <li>You need 'Add User' permission</li>
                    <li>Collect user information beforehand</li>
                </ul>
               
                <h2>Steps to Add New User</h2>
                <ol>
                    <li>Go to <b>Manage → User Management</b></li>
                    <li>Click on <b>Add New User</b> button</li>
                    <li>Fill in the required information:
                        <ul>
                            <li><b>Username:</b> Unique identifier for login</li>
                            <li><b>Full Name:</b> User's complete name</li>
                            <li><b>Email:</b> Valid email address</li>
                            <li><b>Role:</b> Select appropriate role (Admin/Manager/Employee/Intern)</li>
                            <li><b>Phone:</b> Contact number</li>
                        </ul>
                    </li>
                    <li>Set initial password (default is 'password123')</li>
                    <li>Click <b>Save</b> to create the user</li>
                </ol>
               
                <h2>Important Notes</h2>
                <ul>
                    <li>Username must be unique</li>
                    <li>Email must be valid format</li>
                    <li>User will be prompted to change password on first login</li>
                    <li>Permissions are automatically assigned based on role</li>
                </ul>
               
                <h2>Troubleshooting</h2>
                <p><b>Issue:</b> "Username already exists" error<br>
                <b>Solution:</b> Choose a different username</p>
               
                <p><b>Issue:</b> "Invalid email format" error<br>
                <b>Solution:</b> Enter valid email address (e.g., user@example.com)</p>
            </body>
            </html>
            """;
    }
   
    private String getSupportTicketContent() {
        return """
            <html>
            <body>
                <h1>Creating Support Tickets</h1>
               
                <h2>When to Create a Ticket</h2>
                <ul>
                    <li>Client reports technical issue</li>
                    <li>Hardware maintenance required</li>
                    <li>Software installation needed</li>
                    <li>Network connectivity issues</li>
                    <li>Any technical support request</li>
                </ul>
               
                <h2>Steps to Create Ticket</h2>
                <ol>
                    <li>Go to <b>Manage → Support Tickets</b></li>
                    <li>Click <b>Create New Ticket</b></li>
                    <li>Select client from dropdown</li>
                    <li>Enter ticket details:
                        <ul>
                            <li><b>Title:</b> Brief description of issue</li>
                            <li><b>Description:</b> Detailed problem description</li>
                            <li><b>Service Type:</b> Onsite/Home/Center/Remote</li>
                            <li><b>Priority:</b> Low/Medium/High/Urgent</li>
                        </ul>
                    </li>
                    <li>Click <b>Save</b> to create ticket</li>
                </ol>
               
                <h2>Ticket Lifecycle</h2>
                <p>Ticket goes through these stages:</p>
                <ol>
                    <li><b>Open:</b> Newly created ticket</li>
                    <li><b>In Progress:</b> Assigned to technician</li>
                    <li><b>Resolved:</b> Issue fixed</li>
                    <li><b>Closed:</b> Client confirmed resolution</li>
                </ol>
               
                <h2>Best Practices</h2>
                <ul>
                    <li>Always set appropriate priority</li>
                    <li>Include as much detail as possible</li>
                    <li>Assign tickets promptly</li>
                    <li>Update ticket status regularly</li>
                    <li>Collect client feedback after resolution</li>
                </ul>
            </body>
            </html>
            """;
    }
private JPanel createIndexTab() {
        JPanel panel = new JPanel(new BorderLayout());
       
        String[] indexItems = {
            "A", "Access Control", "Add Client", "Add Course", "Add Expense", "Add Income", "Add Project", "Add User",
            "B", "Backup", "Budget Management",
            "C", "Client Management", "Course Enrollment", "Create Ticket",
            "D", "Dashboard", "Delete Records", "Depreciation",
            "E", "Edit Profile", "Email Configuration", "Error Messages", "Export Data",
            "F", "FAQ", "Financial Reports", "Forgot Password",
            "I", "Import Data", "Income Tracking",
            "L", "Login", "Logout",
            "M", "Maintenance", "Monthly Reports",
            "P", "Password Reset", "Permissions", "Print Reports", "Project Tracking",
            "R", "Reports", "Role Management",
            "S", "Search", "Security", "Support Tickets", "System Requirements",
            "T", "Troubleshooting", "Training",
            "U", "User Management", "User Roles"
        };
       
        JList<String> indexList = new JList<>(indexItems);
        indexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        indexList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = indexList.getSelectedValue();
                if (selected != null && !selected.matches("[A-Z]")) {
                    JOptionPane.showMessageDialog(this,
                        "Show help for: " + selected,
                        "Index",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
       
        panel.add(new JScrollPane(indexList), BorderLayout.CENTER);
       
        return panel;
    }
   
    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout());
       
        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setText("Search results will appear here.\n\nEnter search term in the search box above and click Search.");
       
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
       
        return panel;
    }
   
    private JPanel createFaqTab() {
        JPanel panel = new JPanel(new BorderLayout());
       
        String[] questions = {
            "How do I reset my password?",
            "Can I access the system from home?",
            "How do I generate monthly reports?",
            "What should I do if I encounter an error?",
            "How do I add a new client?",
            "Can I export data to Excel?",
            "How do I track support ticket status?",
            "What are the user roles and permissions?",
            "How do I mark attendance for courses?",
            "Where can I see financial summary?"
        };
       
        JList<String> questionList = new JList<>(questions);
        questionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String question = questionList.getSelectedValue();
                if (question != null) {
                    showAnswer(question);
                }
            }
        });
       
        panel.add(new JScrollPane(questionList), BorderLayout.WEST);
       
        JTextArea answerArea = new JTextArea();
        answerArea.setEditable(false);
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(answerArea), BorderLayout.CENTER);
       
        return panel;
    }
   
    private void showAnswer(String question) {
        // In a real implementation, you would have a database of Q&A
        String answer = "Answer to: " + question + "\n\nThis is a sample answer. In the full version, detailed answers would be provided here.";
       
        // Find the component and update it
        Component[] components = tabbedPane.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JScrollPane) {
                        Component view = ((JScrollPane) subComp).getViewport().getView();
                        if (view instanceof JTextArea) {
                            ((JTextArea) view).setText(answer);
                            return;
                        }
                    }
                }
            }
        }
    }
   
    private JPanel createTutorialsTab() {
        JPanel panel = new JPanel(new BorderLayout());
       
        JTextArea tutorialsArea = new JTextArea();
        tutorialsArea.setEditable(false);
        tutorialsArea.setText("""
            VIDEO TUTORIALS
           
            1. Getting Started (5:23)
               • System overview
               • Basic navigation
               • Dashboard explanation
           
            2. User Management (8:15)
               • Adding new users
               • Setting permissions
               • Managing roles
           
            3. Client Management (6:45)
               • Adding clients
               • Client communication
               • Client reports
           
            4. Support Tickets (7:30)
               • Creating tickets
               • Assigning tickets
               • Tracking resolution
           
            5. Financial Management (9:10)
               • Recording transactions
               • Generating reports
               • Budget tracking
           
            6. Course Management (6:55)
               • Creating courses
               • Student enrollment
               • Attendance tracking
           
            Note: Video tutorials are available on the company intranet.
            Contact IT department for access.
            """);
       
        panel.add(new JScrollPane(tutorialsArea), BorderLayout.CENTER);
       
        return panel;
    }
   
    private JPanel createContactTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
       
        int row = 0;
       
        // Header
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel lblHeader = new JLabel("Contact Support", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setForeground(new Color(41, 128, 185));
        panel.add(lblHeader, gbc);
       
        gbc.gridwidth = 1;
       
        // Email
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        JTextField txtEmail = new JTextField("support@itsolutioncenter.com", 25);
        txtEmail.setEditable(false);
        panel.add(txtEmail, gbc);
       
        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        JTextField txtPhone = new JTextField("+93(0)799063252", 25);
        txtPhone.setEditable(false);
        panel.add(txtPhone, gbc);
       
        // Emergency
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Emergency:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        JTextField txtEmergency = new JTextField("+93(0)705063252", 25);
        txtEmergency.setEditable(false);
        panel.add(txtEmergency, gbc);
       
        // Hours
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Working Hours:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        JTextField txtHours = new JTextField("Saturday-Thursday, 8:00 AM - 4:00 PM", 25);
        txtHours.setEditable(false);
        panel.add(txtHours, gbc);
       
        // Website
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Website:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        JTextField txtWebsite = new JTextField("www.itsolutioncenter.com", 25);
        txtWebsite.setEditable(false);
        panel.add(txtWebsite, gbc);
       
        // Address
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        JTextArea txtAddress = new JTextArea("IT Solution Center\n123 Sayed Mir Ajab Street\nFarah City, Farah Province - 123456\nGreat Afghanistan", 3, 25);
        txtAddress.setEditable(false);
        txtAddress.setLineWrap(true);
        panel.add(new JScrollPane(txtAddress), gbc);
       
        // Support Form Button
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JButton btnSupportForm = new JButton("Open Support Request Form");
        btnSupportForm.addActionListener(e -> openSupportRequestForm());
        panel.add(btnSupportForm, gbc);
       
        return panel;
    }
   
    private void searchHelpContent() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term");
            return;
        }
       
        tabbedPane.setSelectedIndex(2); // Switch to Search tab
        // In real implementation, you would search through help content
    }
   
    private void printHelp() {
        JOptionPane.showMessageDialog(this,
            "Print functionality would be implemented here.\n" +
            "The current topic would be sent to the printer.",
            "Print",
            JOptionPane.INFORMATION_MESSAGE);
    }
   
    private void copyContent() {
        String selectedText = editorPane.getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(selectedText);
            java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(this, "Text copied to clipboard");
        }
    }
   
    private void openSupportRequestForm() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
       
        formPanel.add(new JLabel("Your Name:"));
        formPanel.add(new JTextField());
       
        formPanel.add(new JLabel("Email:"));
        formPanel.add(new JTextField());
       
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(new JTextField());
       
        formPanel.add(new JLabel("Issue Type:"));
        JComboBox<String> cmbIssue = new JComboBox<>(new String[]{
            "Technical Problem", "Feature Request", "Bug Report", "General Inquiry"
        });
        formPanel.add(cmbIssue);
       
        formPanel.add(new JLabel("Description:"));
        JTextArea txtDesc = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(txtDesc));
       
        int result = JOptionPane.showConfirmDialog(this, formPanel,
            "Support Request Form", JOptionPane.OK_CANCEL_OPTION);
       
        if (result == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this,
                "Support request submitted successfully.\n" +
                "We will contact you within 24 hours.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
   
    private void selectTab(String tabName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equalsIgnoreCase(tabName)) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }
}
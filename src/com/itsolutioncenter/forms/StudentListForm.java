
package com.itsolutioncenter.forms;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.util.ImageUtils;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.itsolutioncenter.model.Student;

public class StudentListForm extends JFrame {
    private JPanel studentGridPanel;
   private DatabaseManager dbManager=DatabaseManager.getInstance();
    public StudentListForm() {
        initComponents();
        loadStudents();
    }
   
    private void initComponents() {
        setTitle("Student List with Images");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
       
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.add(new JLabel("Student Directory", SwingConstants.CENTER));
        headerPanel.setFont(new Font("Arial", Font.BOLD, 18));
       
        // Grid panel for students
        studentGridPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // 4 columns
        studentGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(studentGridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
       
        // Refresh button
        JButton btnRefresh = new JButton("Refresh List");
        btnRefresh.addActionListener(e -> loadStudents());
       
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnRefresh, BorderLayout.SOUTH);
       
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
   
    private void loadStudents() {
        studentGridPanel.removeAll();
       
        List<Student> students = getAllStudents();
       
        for (Student student : students) {
            JPanel studentCard = createStudentCard(student);
            studentGridPanel.add(studentCard);
        }
       
        studentGridPanel.revalidate();
        studentGridPanel.repaint();
    }
   
    private JPanel createStudentCard(Student student) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setPreferredSize(new Dimension(200, 250));
        card.setBackground(Color.WHITE);
       
        // Image panel
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
       
        // Load and resize image
        ImageIcon imageIcon;
        if (student.getImagePath() != null && !student.getImagePath().isEmpty()) {
            imageIcon = ImageUtils.loadImage(student.getImagePath());
        } else {
            imageIcon = ImageUtils.getDefaultImage();
        }
       
        ImageIcon resizedIcon = ImageUtils.resizeImage(imageIcon, 150, 150);
        imageLabel.setIcon(resizedIcon);
       
        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       
        JLabel idLabel = new JLabel("ID: " + student.getStudentId());
        JLabel nameLabel = new JLabel(student.getFullName());
        JLabel courseLabel = new JLabel(student.getFathername());
        JLabel emailLabel = new JLabel(student.getEmail());
       
        // Style labels
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        idLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        courseLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 9));
       
        infoPanel.add(nameLabel);
        infoPanel.add(idLabel);
        infoPanel.add(courseLabel);
        infoPanel.add(emailLabel);
       
        card.add(imageLabel, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.SOUTH);
       
        // Add click listener
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showStudentDetails(student);
            }
        });
       
        return card;
    }
   
    private List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
       
        try  {
            String query = "SELECT * FROM students ORDER BY student_id";
            ResultSet rs = DatabaseManager.executeSimpleQuery(query);
           
            while (rs.next()) {
                Student student = new Student(
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("father_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("image_path")
                );
                students.add(student);
            }
           
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading students: " + e.getMessage());
        }
       
        return students;
    }
   
    private void showStudentDetails(Student student) {
        JDialog dialog = new JDialog(this, "Student Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
       
        // Image panel
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
       
        ImageIcon imageIcon;
        if (student.getImagePath() != null && !student.getImagePath().isEmpty()) {
            imageIcon = ImageUtils.loadImage(student.getImagePath());
        } else {
            imageIcon = ImageUtils.getDefaultImage();
        }
       
        ImageIcon resizedIcon = ImageUtils.resizeImage(imageIcon, 200, 200);
        imageLabel.setIcon(resizedIcon);
       
        // Details panel
        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
       
        detailsPanel.add(new JLabel("Student ID:"));
        detailsPanel.add(new JLabel(student.getStudentId()));
       
        detailsPanel.add(new JLabel("Full Name:"));
        detailsPanel.add(new JLabel(student.getFullName()));
        
        detailsPanel.add(new JLabel("Father Name:"));
        detailsPanel.add(new JLabel(student.getFathername()));
       
        detailsPanel.add(new JLabel("Email:"));
        detailsPanel.add(new JLabel(student.getEmail()));
       
        detailsPanel.add(new JLabel("Phone:"));
        detailsPanel.add(new JLabel(student.getPhone()));
       
        // Close button
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
       
        dialog.add(imageLabel, BorderLayout.NORTH);
        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(btnClose, BorderLayout.SOUTH);
       
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentListForm().setVisible(true);
        });
    }
}
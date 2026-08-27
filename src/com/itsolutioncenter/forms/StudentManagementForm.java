
package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.util.ImageUtils;
import java.awt.Color;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class StudentManagementForm extends javax.swing.JInternalFrame {
    private DatabaseManager dbManager=DatabaseManager.getInstance();
    private String query,searchText,currentImagePath = "";
    private int rows,ID;
    private File selectedImageFile = null;
    
    public StudentManagementForm() {
        initComponents();
        loadDefaultImage();
    }
    private void searchData(){
    // Proceed with action
         searchText=txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter search criteria: Student ID, Student Full Name or Phone Number");
            return;
        }
        boolean isNumeric = searchText.matches("\\d+");  
            if (isNumeric) {
                // Search by ID (numeric ID)
                ID=Integer.parseInt(searchText);
                query = "select * from students where student_id = '"+ID+"'";
            } else {
                // Search by username OR full name only (non-numeric)
                query= "select * from students where full_name LIKE '"+searchText+"' OR phone LIKE '"+searchText+"'";
            }
            try{
                List<Map<String,Object>>data=dbManager.select(query);
                for(Map<String,Object>search:data){
                txtID.setText(search.get("student_id").toString());
                txtFullName.setText(search.get("full_name").toString());
                txtFName.setText(search.get("father_name").toString());
                txtPhone.setText(search.get("phone").toString());
                txtEmail.setText(search.get("email").toString());
                String imagePath = search.get("image_path").toString();
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageIcon imageIcon = ImageUtils.loadImage(imagePath);
                    ImageIcon resizedIcon = ImageUtils.resizeImage(imageIcon, 200, 200);
                lblImagePreview.setIcon(resizedIcon);
                    currentImagePath = imagePath;
                } else {
                    loadDefaultImage();
                }
               txtSearch.setText("");
                } 
            }catch(SQLException e)
            {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
            if(txtID.getText().length()>0)
               {
                   JOptionPane.showMessageDialog(null, "Data found successfully!");
               }else
               {
                   JOptionPane.showMessageDialog(null, "Data doesn't exist!");
                   txtSearch.requestFocus();
                   txtSearch.setBackground(new Color(255,230,230));
                   txtSearch.setForeground(Color.red);
                   txtSearch.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
               }
    }
 private void loadDefaultImage() {
        ImageIcon defaultIcon = ImageUtils.getDefaultImage();
        ImageIcon resizedIcon = ImageUtils.resizeImage(defaultIcon, 200, 200);
        lblImagePreview.setIcon(resizedIcon);
    }
 private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Student Photo");
       
        // Set file filter for images only
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                       name.endsWith(".png") || name.endsWith(".gif") ||
                       name.endsWith(".bmp");
            }
           
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif, *.bmp)";
            }
        });
       
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            displaySelectedImage(selectedImageFile);
        }
    }
   
    private void displaySelectedImage(File imageFile) {
        try {
            ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
            ImageIcon resizedIcon = ImageUtils.resizeImage(originalIcon, 200, 200);
            lblImagePreview.setIcon(resizedIcon);
           
            // Store the original file for later saving
            selectedImageFile = imageFile;
            currentImagePath = imageFile.getAbsolutePath();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading image: " + e.getMessage(),
                "Image Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void clearImage() {
        loadDefaultImage();
        selectedImageFile = null;
        currentImagePath = "";
    }
     private void saveStudent() {
        // Validate input
        if (!validateInput()) {
            return;
        }
       
        String studentId = txtID.getText();
        String fullName = txtFullName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String fName = txtFName.getText();
       
        // Save image to project folder
        String savedImagePath = null;
        if (selectedImageFile != null) {
            savedImagePath = ImageUtils.saveImageToFolder(selectedImageFile, studentId);
            if (savedImagePath == null) {
                JOptionPane.showMessageDialog(this,
                    "Failed to save image!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
       
        // Save to database
        try {
            query = "INSERT INTO students (student_id, full_name, father_name, email, phone, image_path) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
           
            rows = dbManager.executeUpdate(query, studentId,fullName,fName,email,phone,savedImagePath);
           
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                    "Student saved successfully!\nImage saved to: " + savedImagePath,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to save student!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
           
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
           JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
   
    private void updateStudent() {
        String studentId = txtID.getText();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter Student ID to update!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
       
        // Check if student exists
        if (!studentExists(studentId)) {
            JOptionPane.showMessageDialog(this,
                "Student ID not found!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
       
        // Save new image if selected
        String savedImagePath = null;
        if (selectedImageFile != null) {
            // Delete old image if exists
            String oldImagePath = getCurrentImagePath(studentId);
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                ImageUtils.deleteImage(oldImagePath);
            }
           
            // Save new image
            savedImagePath = ImageUtils.saveImageToFolder(selectedImageFile, studentId);
        }
            try{
            if (savedImagePath != null) {
                query = "UPDATE students SET full_name=?, father_name=?, email=?, phone=?, image_path=? " +
                       "WHERE student_id=?";
                      
                rows= dbManager.executeUpdate(query,txtFullName.getText(),txtFName.getText(),
                txtEmail.getText(),txtPhone.getText(), savedImagePath,studentId);
            } else {
                query = "UPDATE students SET full_name=?, father_name=?, email=?, phone=? " +
                       "WHERE student_id=?";
                    rows= dbManager.executeUpdate(query,txtFullName.getText(),txtFName.getText(),
                txtEmail.getText(),txtPhone.getText(), savedImagePath,studentId);}

           
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                    "Student updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update student!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
           
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
   
    private void deleteStudent() {
        String studentId = txtID.getText();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter Student ID!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
       
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete student " + studentId + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
       
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete image file first
            String imagePath = getCurrentImagePath(studentId);
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageUtils.deleteImage(imagePath);}
            // query = "DELETE FROM students WHERE student_id=?";
            rows = dbManager.delete("students", "student_id=?", studentId);
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Student deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Student ID not found!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void searchStudent() {
        String studentId = JOptionPane.showInputDialog(this,
            "Enter Student ID:",
            "Search Student",
            JOptionPane.QUESTION_MESSAGE);
        if (studentId != null && !studentId.trim().isEmpty()) {
            loadStudentData(studentId.trim());
        }
    }
    private void loadStudentData(String studentId) {
        try  {
            query = "SELECT * FROM students WHERE student_id='"+studentId+"'";
            ResultSet rs =dbManager.executeSimpleQuery(query);
            if (rs.next()) {
                // Populate form fields
  txtID.setText(rs.getString("student_id"));
   txtFullName.setText(rs.getString("full_name"));
                txtEmail.setText(rs.getString("email"));
             txtPhone.setText(rs.getString("phone"));
       txtFName.setText(rs.getString("father_name"));
                // Load and display image
                String imagePath = rs.getString("image_path");
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageIcon imageIcon = ImageUtils.loadImage(imagePath);
                    ImageIcon resizedIcon = ImageUtils.resizeImage(imageIcon, 200, 200);
      lblImagePreview.setIcon(resizedIcon);
                    currentImagePath = imagePath;
                } else {
                    loadDefaultImage();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Student ID not found!",
                    "Search Result",
           JOptionPane.INFORMATION_MESSAGE);
            }       
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    private boolean studentExists(String studentId) {
        boolean exists=dbManager.exists("students", "student_id=?", studentId);
        return exists;
    }
    private String getCurrentImagePath(String studentId) {
        try  {
            List<Map<String,Object>>image=dbManager.select("students", "student_id=?", studentId);
            for(Map<String,Object>path:image){
                return path.get("image_path").toString();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return null;
    }
    private boolean validateInput() {
        if (txtID.getText().trim().isEmpty()) {
            showValidationError("Student ID is required!");
            txtID.requestFocus();
            return false;
        }
        if (txtFullName.getText().trim().isEmpty()) {
            showValidationError("Full Name is required!");
            txtFullName.requestFocus();
            return false;
        }   
        return true;
    }  
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
      JOptionPane.WARNING_MESSAGE);
    }
   
    private void clearForm() {
        txtID.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtFName.setText("");
        clearImage();
        txtID.requestFocus();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        pnlOperation = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        btnInsert = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnSearch2 = new javax.swing.JButton();
        pnlImage = new javax.swing.JPanel();
        btnBrowse = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblImagePreview = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle("Student Registeration Form");
        setPreferredSize(new java.awt.Dimension(700, 350));
        setVerifyInputWhenFocusTarget(false);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        pnlOperation.setBorder(javax.swing.BorderFactory.createTitledBorder("Student Operation"));

        jLabel1.setText("Student ID:");

        jLabel2.setText("Full Name:");

        jLabel3.setText("Father's Name:");

        jLabel4.setText("Email:");

        jLabel5.setText("Phone:");

        btnInsert.setText("Insert");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnSearch2.setText("Search");
        btnSearch2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearch2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOperationLayout = new javax.swing.GroupLayout(pnlOperation);
        pnlOperation.setLayout(pnlOperationLayout);
        pnlOperationLayout.setHorizontalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFName, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail))
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(pnlOperationLayout.createSequentialGroup()
                                        .addComponent(btnInsert)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnUpdate)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnDelete)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton1)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(btnSearch2)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlOperationLayout.setVerticalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtFName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsert)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(btnSearch2)
                .addGap(23, 23, 23))
        );

        pnlImage.setBorder(javax.swing.BorderFactory.createTitledBorder("Student Image"));

        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        lblImagePreview.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlImageLayout = new javax.swing.GroupLayout(pnlImage);
        pnlImage.setLayout(pnlImageLayout);
        pnlImageLayout.setHorizontalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlImageLayout.createSequentialGroup()
                        .addComponent(btnBrowse)
                        .addGap(18, 18, 18)
                        .addComponent(btnClear)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblImagePreview, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlImageLayout.setVerticalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlImageLayout.createSequentialGroup()
                .addComponent(lblImagePreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBrowse)
                    .addComponent(btnClear))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlOperation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlOperation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        browseImage();
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearImage();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        saveStudent();
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateStudent();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteStudent();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchData();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        clearForm();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnSearch2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearch2ActionPerformed
        searchStudent();
    }//GEN-LAST:event_btnSearch2ActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setBorder(UIManager.getBorder("TextField.border"));
        txtSearch.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtSearchKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearch2;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblImagePreview;
    private javax.swing.JPanel pnlImage;
    private javax.swing.JPanel pnlOperation;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFName;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

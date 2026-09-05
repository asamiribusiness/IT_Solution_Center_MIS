package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.model.Permission;
import com.itsolutioncenter.model.User;
import com.itsolutioncenter.util.Validator;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class InternManagementForm extends javax.swing.JInternalFrame {
DatabaseManager dbManager = DatabaseManager.getInstance();
private DefaultTableModel tableModel=new DefaultTableModel();
private User currentUser;
private Permission permissionService;
private ResultSet rs;
private String query,internName,department;
private Date hireDate,endDate;
private int userID,row,applicationId,supervisorId;
private boolean insertion=true;

    public InternManagementForm(User user, Permission permissionService) {
        initComponents();
        loadInterns();
        initAction();   
    }
    private void initAction()
    {
        tablePopupMenu.add("Edit").addActionListener(e ->loadSelectedRowToForm());
        tablePopupMenu.add("Delete").addActionListener(e ->{ deleteUser();});
        tablePopupMenu.add("Refresh").addActionListener(e ->loadInterns());
        //Double click to load data into controls
        userTable.addMouseListener(new MouseAdapter(){
        @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount()==2)
        {
            int rows=userTable.rowAtPoint(e.getPoint());
            int col=userTable.columnAtPoint(e.getPoint());
            if(rows>=0 && col>=0)
            {
                loadSelectedRowToForm();
            } 
        }        
    }
 });}
 
    private void searchData() {
        String searchText=txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter search criteria: ID, Username or Full Name");
            return;
        }
        boolean isNumeric = searchText.matches("\\d+");  
            if (isNumeric) {
                // Search by ID (numeric ID)
                userID=Integer.parseInt(searchText);
                query = "select * from users where user_id = '"+userID+"'";
            } else {
                // Search by username OR full name only (non-numeric)
                query= "select * from users where username LIKE '"+searchText+"' OR full_name LIKE '"+searchText+"'";
            }
            tableModel=dbManager.getTableModel(query,userTable);
            userTable.setModel(tableModel); 
            txtSearch.setText("");
    }
       private void insertData()
    {
         if(!ValidateData()) return;


    }
    private void deleteUser() {
        row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        userID = (int) userTable.getValueAt(row, 0);
        internName = userTable.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + internName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
               dbManager.delete("users", "user_id = ?", userID);
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadInterns(); }
        }      
   private void updateData() {
        if(!ValidateData()) return;
 
}
    private boolean ValidateData()
    {
        if(!Validator.validateRequired(txtApplicationId, "Application ID"))
        {
              return false; 
        } 
        if(!Validator.validateRequired(txtSupervisorId, "Supervisor ID"))
        {
            return false;
        }
        if(!Validator.dateRequired(txtHireDate,"Start Date"))
                {
                    return false;
                }
        if(!Validator.isDateValid(txtHireDate.getDate(),txtHireDate))
        {
            return false;
        }
        
        return true;
    }
    private void clear()
    {
    txtApplicationId.setText("");
    txtEndDate.setDate(null);
    txtRating.setText("");
    txtSupervisorId.setText("");
    txtStipend.setText("");
    txtHireDate.setDate(null);
    txtUserId.setText("");
    chkCertificate.setSelected(false);
    txtNote.setText("");
    txtDepartment.setText("");
    insertion=true;
}
    private void loadInterns() 
    {
        try
        {
            query="SELECT interns.intern_id, interns.application_id, intern_applications.applicant_name," +
            "  interns.start_date, interns.end_date,interns.department,u_super.full_name AS supervisor_name," +
            "  interns.stipend,interns.performance_rating,interns.certificate_issued,interns.notes " +
            "FROM  interns "+
            "  INNER JOIN intern_applications ON intern_applications.application_id = interns.application_id" +
            "  LEFT JOIN users AS u_super ON u_super.user_id = interns.supervisor_id";
            tableModel=dbManager.getTableModel(query,userTable);
            userTable.setModel(tableModel); 
        }catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
   
    private void loadSelectedRowToForm() {
        row = userTable.getSelectedRow();
        try
        {
             if (row >= 0) {
                 userID=Integer.parseInt(userTable.getValueAt(row, 0).toString());
            txtApplicationId.setText(userTable.getValueAt(row, 1).toString());
            //txtPassword.setText(userTable.getValueAt(row, 2).toString());
        //    txtEmail.setText(userTable.getValueAt(row, 3).toString());
        //    txtFullName.setText(userTable.getValueAt(row, 4).toString());
         //   cmbUserType.setSelectedItem(userTable.getValueAt(row, 5));
            txtSupervisorName.setText(userTable.getValueAt(row, 6).toString());
            txtRating.setText(userTable.getValueAt(row, 7).toString());
            txtHireDate.setDate((Date)userTable.getValueAt(row, 8));
            txtStipend.setText(userTable.getValueAt(row, 9).toString());
             }
        }catch(NullPointerException e) 
        {
            JOptionPane.showMessageDialog(null, "Some Fields Are Null");
        }
        insertion=false;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePopupMenu = new javax.swing.JPopupMenu();
        tblPopupMenu = new javax.swing.JPopupMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        pnlOperation = new javax.swing.JPanel();
        txtApplicationId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtSupervisorName = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtRating = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtStipend = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnInsert = new javax.swing.JButton();
        txtHireDate = new com.toedter.calendar.JDateChooser();
        progressBar = new javax.swing.JProgressBar();
        txtUserId = new javax.swing.JTextField();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtSupervisorId = new javax.swing.JTextField();
        chkCertificate = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        txtNote = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDepartment = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("User Management Form");
        setPreferredSize(new java.awt.Dimension(1280, 600));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }

        userTable.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Application ID", "Full Name", "Start Date", "End Date", "Department", "Supervisor", "Stipend", "Performance Rating", "Certificate Issued", "Notes"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        userTable.setColumnSelectionAllowed(true);
        userTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        userTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setUpdateSelectionOnSort(false);
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(userTable);
        userTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (userTable.getColumnModel().getColumnCount() > 0) {
            userTable.getColumnModel().getColumn(1).setMinWidth(70);
            userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            userTable.getColumnModel().getColumn(1).setMaxWidth(100);
            userTable.getColumnModel().getColumn(2).setMinWidth(100);
            userTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            userTable.getColumnModel().getColumn(2).setMaxWidth(200);
            userTable.getColumnModel().getColumn(3).setMinWidth(80);
            userTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            userTable.getColumnModel().getColumn(3).setMaxWidth(100);
            userTable.getColumnModel().getColumn(4).setMinWidth(80);
            userTable.getColumnModel().getColumn(4).setPreferredWidth(100);
            userTable.getColumnModel().getColumn(4).setMaxWidth(100);
            userTable.getColumnModel().getColumn(5).setMinWidth(100);
            userTable.getColumnModel().getColumn(5).setPreferredWidth(100);
            userTable.getColumnModel().getColumn(5).setMaxWidth(100);
            userTable.getColumnModel().getColumn(6).setMinWidth(100);
            userTable.getColumnModel().getColumn(6).setPreferredWidth(100);
            userTable.getColumnModel().getColumn(6).setMaxWidth(100);
            userTable.getColumnModel().getColumn(10).setMinWidth(100);
            userTable.getColumnModel().getColumn(10).setPreferredWidth(100);
            userTable.getColumnModel().getColumn(10).setMaxWidth(150);
        }

        pnlOperation.setBackground(new java.awt.Color(102, 204, 255));
        pnlOperation.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Intern Management Operation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Black", 1, 12))); // NOI18N

        txtApplicationId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtApplicationIdKeyPressed(evt);
            }
        });

        jLabel2.setText("Application ID:");

        jLabel3.setText("User ID:");

        txtSupervisorName.setEditable(false);

        jLabel9.setText("Supervisor Name:");

        jLabel10.setText("Performance Rating:");

        jLabel11.setText("Start Date:");

        jLabel12.setText("Stipend");

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnClear.setText("Clear Controls");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnInsert.setText("Insert");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        txtHireDate.setDateFormatString("dd/MM/yyyy");
        txtHireDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtHireDateKeyPressed(evt);
            }
        });

        txtEndDate.setDateFormatString("dd/MM/yyyy");

        jLabel13.setText("End Date:");

        jLabel14.setText("Supervisor ID:");

        txtSupervisorId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSupervisorIdKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSupervisorIdKeyTyped(evt);
            }
        });

        chkCertificate.setText("Certificate Issued");

        jLabel1.setText("Notes:");

        jLabel4.setText("Department:");

        javax.swing.GroupLayout pnlOperationLayout = new javax.swing.GroupLayout(pnlOperation);
        pnlOperation.setLayout(pnlOperationLayout);
        pnlOperationLayout.setHorizontalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtStipend, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtRating, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(82, 82, 82)
                                .addComponent(btnClear))
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(chkCertificate)
                                .addGap(37, 37, 37)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNote, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(392, 392, 392))
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApplicationId, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addGap(12, 12, 12)
                        .addComponent(txtHireDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addGap(12, 12, 12)
                        .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSupervisorId, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSupervisorName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDepartment)
                        .addContainerGap())))
        );
        pnlOperationLayout.setVerticalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2)
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtApplicationId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addComponent(txtHireDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSupervisorId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14))
                    .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSupervisorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12)
                    .addComponent(txtStipend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCertificate)
                    .addComponent(jLabel1)
                    .addComponent(txtNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnInsert)
                        .addComponent(btnUpdate)
                        .addComponent(btnDelete)
                        .addComponent(btnClear))))
        );

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGap(103, 103, 103)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pnlOperation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOperation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTableMouseClicked
         if (evt.getButton() == MouseEvent.BUTTON3) { // Right-click
         row = userTable.rowAtPoint(evt.getPoint());
        if (row >= 0 && row < userTable.getRowCount()) {
            userTable.setRowSelectionInterval(row, row);
            tablePopupMenu.show(userTable, evt.getX(), evt.getY());
        }
    }
    }//GEN-LAST:event_userTableMouseClicked
    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
    clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
           updateData();   
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        insertData();
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteUser();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchData();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        loadInterns();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtApplicationIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApplicationIdKeyPressed
        txtApplicationId.setBackground(Color.WHITE);
        txtApplicationId.setBorder(UIManager.getBorder("TextField.border"));
        txtApplicationId.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtApplicationIdKeyPressed

    private void txtHireDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHireDateKeyPressed
        txtHireDate.setBackground(Color.WHITE);
        txtHireDate.setBorder(UIManager.getBorder("TextField.border"));
        txtHireDate.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtHireDateKeyPressed

    private void txtSupervisorIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupervisorIdKeyPressed
        txtSupervisorId.setBackground(Color.WHITE);
        txtSupervisorId.setBorder(UIManager.getBorder("TextField.border"));
        txtSupervisorId.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtSupervisorIdKeyPressed

    private void txtSupervisorIdKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSupervisorIdKeyTyped
                    try {
        String text = txtSupervisorId.getText();
        if (text.isEmpty()) {
            txtSupervisorName.setText("");
            return;
        }
        int supervisorID = Integer.parseInt(text);
        query = "Select full_name from users where user_id='"+supervisorID+"'";
        List<Map<String,Object>> supervisorNames = dbManager.select(query);
        txtSupervisorName.setText(supervisorNames.isEmpty()
                ? "N/A"
                : supervisorNames.get(0).get("full_name").toString());
    } catch (NumberFormatException ex) {
        txtSupervisorName.setText(""); // not a full number yet — not an error
    } catch (SQLException ex) {
        txtSupervisorName.setText("N/A");
    }
    }//GEN-LAST:event_txtSupervisorIdKeyTyped
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chkCertificate;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlOperation;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JPopupMenu tblPopupMenu;
    private javax.swing.JTextField txtApplicationId;
    private javax.swing.JTextField txtDepartment;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private com.toedter.calendar.JDateChooser txtHireDate;
    private javax.swing.JTextField txtNote;
    private javax.swing.JTextField txtRating;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStipend;
    private javax.swing.JTextField txtSupervisorId;
    private javax.swing.JTextField txtSupervisorName;
    private javax.swing.JTextField txtUserId;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables
}
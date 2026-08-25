package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.model.Permission;
import com.itsolutioncenter.model.User;
import com.itsolutioncenter.service.UserService;
import com.itsolutioncenter.util.Validator;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.mindrot.jbcrypt.BCrypt;

public class UserManagementForm extends javax.swing.JInternalFrame {
DatabaseManager dbManager = DatabaseManager.getInstance();
private DefaultTableModel tableModel=new DefaultTableModel();
private User currentUser;
private Permission permissionService;
private ResultSet rs;
private String query,username,password,fullName,email,role,phone,address,hashPass;
private Date hireDate;
private String[] userType;
private int userID,row;
private double salary;
private boolean insertion,active=true;
private Timestamp createdDate;
private UserService userService=new UserService();
    public UserManagementForm(User user, Permission permissionService) {
        initComponents();
        loadUsers();
        initAction();   
    }
    private void initAction()
    {
        tablePopupMenu.add("Edit").addActionListener(e ->loadSelectedRowToForm());
        tablePopupMenu.add("Delete").addActionListener(e ->{ deleteUser();});
        tablePopupMenu.add("Refresh").addActionListener(e ->loadUsers());
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
      /* private void insertData()
    {
        if(!Validator.validateRequired(txtPassword, "Password"))
        {
            return;
        }
         if(!Validator.isLengthValid(txtPassword.getText(), 5, 10))
         {
              JOptionPane.showMessageDialog(this, "Password must be at least 5 or maximum 10 characters",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocusInWindow();
            txtPassword.setBackground(new Color(255,230,230));
            txtPassword.setForeground(Color.red);
            txtPassword.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            return;
         }
         if(!ValidateData()) return;
        username=txtUserName.getText();
        password=new String(txtPassword.getPassword());
        email=txtEmail.getText();
        fullName=txtFullName.getText();
        role=cmbUserType.getSelectedItem().toString();
        phone=txtPhone.getText();
        address=txtAddress.getText();
        hireDate=txtHireDate.getDate();
        salary=Double.parseDouble(txtSalary.getText());
        // Check if email exists
        if (dbManager.exists("users", "email = ?", email)) {
            JOptionPane.showMessageDialog(this, "Email is already registered by another user",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocusInWindow();
            txtEmail.setBackground(new Color(255,230,230));
            txtEmail.setForeground(Color.red);
            txtEmail.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            return;
        }
        if (dbManager.exists("users", "username = ?", username)) {
            JOptionPane.showMessageDialog(this, "Username is already registered by another user",
                    "Error", JOptionPane.ERROR_MESSAGE);
            txtUserName.requestFocusInWindow();
            txtUserName.setBackground(new Color(255,230,230));
            txtUserName.setForeground(Color.red);
            txtUserName.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
            return;
        }
        try
        {
        hashPass=BCrypt.hashpw(password, BCrypt.gensalt());
        row= userService.registerUser(username, hashPass, email, fullName, role, phone, address, hireDate, salary);
        if(row>0)
        {
            JOptionPane.showMessageDialog(this, "Data Inserted Successfully");
            loadUsers();
            clear();
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Data Didn't Inserted!");
        }
        }catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }*/
    private void insertData() {
    // 1. RUN VALIDATION FIRST (Must be on the Event Dispatch Thread)
    if (!Validator.validateRequired(txtPassword, "Password")) {
        return;
    }
    if (!Validator.isLengthValid(txtPassword.getText(), 5, 10)) {
        JOptionPane.showMessageDialog(this, "Password must be at least 5 or maximum 10 characters",
                "Error", JOptionPane.ERROR_MESSAGE);
        txtPassword.requestFocusInWindow();
        txtPassword.setBackground(new Color(255, 230, 230));
        txtPassword.setForeground(Color.red);
        txtPassword.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
        return;
    }
    if (!ValidateData()) return;

    // Capture text fields variables on EDT before moving to background thread
    final String username = txtUserName.getText();
    final String password = new String(txtPassword.getPassword());
    final String email = txtEmail.getText();
    final String fullName = txtFullName.getText();
    final String role = cmbUserType.getSelectedItem().toString();
    final String phone = txtPhone.getText();
    final String address = txtAddress.getText();
    final java.util.Date hireDate = txtHireDate.getDate(); 
    final double salary = Double.parseDouble(txtSalary.getText());

    // Check duplicate validations before launching thread
    if (dbManager.exists("users", "email = ?", email)) {
        JOptionPane.showMessageDialog(this, "Email is already registered by another user",
                "Error", JOptionPane.ERROR_MESSAGE);
        txtEmail.requestFocusInWindow();
        txtEmail.setBackground(new Color(255, 230, 230));
        txtEmail.setForeground(Color.red);
        txtEmail.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
        return;
    }
    if (dbManager.exists("users", "username = ?", username)) {
        JOptionPane.showMessageDialog(this, "Username is already registered by another user",
                "Error", JOptionPane.ERROR_MESSAGE);
        txtUserName.requestFocusInWindow();
        txtUserName.setBackground(new Color(255, 230, 230));
        txtUserName.setForeground(Color.red);
        txtUserName.setBorder(BorderFactory.createLineBorder(Color.yellow, 2));
        return;
    }

    // 2. CONCURRENCY CONTROL & SECURITY PROTECTION
    btnInsert.setEnabled(false);        // Block user from double-clicking the button
    txtPassword.setText("");            // Instantly clear plain text password from UI memory
    progressBar.setValue(0);          // Reset the progress bar status

    // 3. CREATE AND EXECUTE SWINGWORKER FOR BACKGROUND TASK
    SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
        
        @Override
        protected Integer doInBackground() throws Exception {
            // Step A: Hashing (25% progress)
            setProgress(25); 
            String hashPass = BCrypt.hashpw(password, BCrypt.gensalt());
            
            // Step B: Simulating preparation step (50% progress)
            setProgress(50);
            Thread.sleep(250); // Small 250ms visual delay for smooth animation
            
            // Step C: Execute database write (75% progress)
            setProgress(75);
            int resultRow = userService.registerUser(username, hashPass, email, fullName, role, phone, address, hireDate, salary);
            
            // Step D: Task Finish (100% progress)
            setProgress(100);
            return resultRow;
        }

        @Override
        protected void done() {
            // This method automatically returns to the EDT thread when finished
            try {
                int insertedRow = get(); // Grab result from doInBackground
                
                if (insertedRow > 0) {
                    JOptionPane.showMessageDialog(null, "Data Inserted Successfully");
                    loadUsers();
                    clear();
                } else {
                    JOptionPane.showMessageDialog(null, "Data Didn't Inserted!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            } finally {
                // 4. CLEANUP INTERFACE RESTORATION
                btnInsert.setEnabled(true);     // Unlock submission capability
                progressBar.setValue(0);      // Reset bar back to zero state
            }
        }
    };

    // 5. LINK PROGRESS CHANGES TO PROGRESSBAR
    worker.addPropertyChangeListener(evt -> {
        if ("progress".equals(evt.getPropertyName())) {
            progressBar.setValue((Integer) evt.getNewValue());
        }
    });

    // 6. START THREAD EXECUTION
    worker.execute();
}

    private void deleteUser() {
        row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        userID = (int) userTable.getValueAt(row, 0);
        username = userTable.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + username + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
               dbManager.delete("users", "user_id = ?", userID);
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsers(); }
        }      
   private void updateData() {
        if(!ValidateData()) return;
        username=txtUserName.getText();
        email=txtEmail.getText();
        fullName=txtFullName.getText();
        role=cmbUserType.getSelectedItem().toString();
        phone=txtPhone.getText();
        address=txtAddress.getText();
        hireDate=txtHireDate.getDate();
        salary=Double.parseDouble(txtSalary.getText());
       int row=userService.updateUser(userID, username, email, fullName, role, phone, address, hireDate, salary);
       if(row>0)
       {
           JOptionPane.showMessageDialog(this, "Data Updated Successfully!");
           loadUsers();
           clear();
       }
        else
       {
           JOptionPane.showMessageDialog(null, "Data Couldn't Updated");
       }   
}
    private boolean ValidateData()
    {
        if(!Validator.validateRequired(txtUserName, "User Name"))
        {
              return false; 
        }
        
        if(!Validator.isEmailValid(txtEmail.getText(),"Email",txtEmail))
        {
            return false;
        }
        
        if(!Validator.validateRequired(txtFullName, "Full Name"))
        {
            return false;
        }
        if(cmbUserType.getSelectedIndex()==0)
        {
            JOptionPane.showMessageDialog(this, "Please Select User Type","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbUserType.requestFocusInWindow();
            cmbUserType.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
       if(!Validator.isPhoneValid(txtPhone.getText(),txtPhone,"Phone Number"))
        {
            return false;
        }
        if(!Validator.isDateValid(txtHireDate.getDate(),txtHireDate))
        {
            return false;
        }
        if(!Validator.validateRequired(txtSalary, "Salary"))
        {
            return false;
        }
        if(!Validator.validateNumber(txtSalary.getText(), "Salary",txtSalary))
        {
            return false;
        }
        
        return true;
    }
    private void clear()
    {
    txtUserName.setText("");
    txtPassword.setText("");
    txtFullName.setText("");
    txtAddress.setText("");
    txtEmail.setText("");
    txtPhone.setText("");
    txtSalary.setText("");
    txtHireDate.setDate(null);
    cmbUserType.setSelectedIndex(0);
}
    private void loadUsers() 
    {
        try
        {
            query="Select * from users";
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
            txtUserName.setText(userTable.getValueAt(row, 1).toString());
            //txtPassword.setText(userTable.getValueAt(row, 2).toString());
            txtEmail.setText(userTable.getValueAt(row, 3).toString());
            txtFullName.setText(userTable.getValueAt(row, 4).toString());
            cmbUserType.setSelectedItem(userTable.getValueAt(row, 5));
            txtPhone.setText(userTable.getValueAt(row, 6).toString());
            txtAddress.setText(userTable.getValueAt(row, 7).toString());
            txtHireDate.setDate((Date)userTable.getValueAt(row, 8));
            txtSalary.setText(userTable.getValueAt(row, 9).toString());
             }
        }catch(NullPointerException e) 
        {
            JOptionPane.showMessageDialog(null, "Some Fields Are Null");
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePopupMenu = new javax.swing.JPopupMenu();
        tblPopupMenu = new javax.swing.JPopupMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        pnlOperation = new javax.swing.JPanel();
        txtUserName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        cmbUserType = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtSalary = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnInsert = new javax.swing.JButton();
        txtHireDate = new com.toedter.calendar.JDateChooser();
        progressBar = new javax.swing.JProgressBar();
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
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "User Name", "Password", "Email", "Full Name", "User Type", "Phone", "Address", "Hire Date", "Salary", "Active", "Created Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
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
            userTable.getColumnModel().getColumn(0).setMinWidth(20);
            userTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            userTable.getColumnModel().getColumn(0).setMaxWidth(30);
            userTable.getColumnModel().getColumn(1).setMinWidth(70);
            userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            userTable.getColumnModel().getColumn(1).setMaxWidth(100);
            userTable.getColumnModel().getColumn(2).setMinWidth(100);
            userTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            userTable.getColumnModel().getColumn(2).setMaxWidth(200);
            userTable.getColumnModel().getColumn(3).setMinWidth(100);
            userTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            userTable.getColumnModel().getColumn(3).setMaxWidth(150);
            userTable.getColumnModel().getColumn(4).setMinWidth(100);
            userTable.getColumnModel().getColumn(4).setPreferredWidth(150);
            userTable.getColumnModel().getColumn(4).setMaxWidth(200);
            userTable.getColumnModel().getColumn(5).setMinWidth(50);
            userTable.getColumnModel().getColumn(5).setPreferredWidth(80);
            userTable.getColumnModel().getColumn(5).setMaxWidth(90);
            userTable.getColumnModel().getColumn(6).setPreferredWidth(50);
            userTable.getColumnModel().getColumn(8).setPreferredWidth(50);
            userTable.getColumnModel().getColumn(9).setPreferredWidth(30);
            userTable.getColumnModel().getColumn(10).setMinWidth(30);
            userTable.getColumnModel().getColumn(10).setPreferredWidth(50);
            userTable.getColumnModel().getColumn(10).setMaxWidth(60);
            userTable.getColumnModel().getColumn(11).setMinWidth(100);
            userTable.getColumnModel().getColumn(11).setPreferredWidth(150);
            userTable.getColumnModel().getColumn(11).setMaxWidth(200);
        }

        pnlOperation.setBackground(new java.awt.Color(0, 204, 204));
        pnlOperation.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "User Management Operation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Black", 1, 12))); // NOI18N

        txtUserName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUserNameKeyPressed(evt);
            }
        });

        jLabel2.setText("User Name:");

        jLabel3.setText("Password:");

        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });

        jLabel4.setText("Full Name:");

        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
        });

        cmbUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select User Role --", "Admin", "Manager", "Employee", "Instructor", "Intern" }));
        cmbUserType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbUserTypeItemStateChanged(evt);
            }
        });

        jLabel5.setText("User Type:");

        txtFullName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFullNameKeyPressed(evt);
            }
        });

        jLabel8.setText("Email:");

        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPhoneKeyPressed(evt);
            }
        });

        jLabel9.setText("Cell Phone:");

        jLabel10.setText("Address:");

        txtAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAddressKeyPressed(evt);
            }
        });

        jLabel11.setText("Hire Date:");

        jLabel12.setText("Salary:");

        txtSalary.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSalaryKeyPressed(evt);
            }
        });

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

        javax.swing.GroupLayout pnlOperationLayout = new javax.swing.GroupLayout(pnlOperation);
        pnlOperation.setLayout(pnlOperationLayout);
        pnlOperationLayout.setHorizontalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(12, 12, 12)
                                .addComponent(txtHireDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClear)))))
                .addGap(35, 35, 35)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(212, 212, 212))
        );
        pnlOperationLayout.setVerticalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addComponent(cmbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(jLabel11)
                        .addComponent(jLabel12)
                        .addComponent(txtSalary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtHireDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
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
        loadUsers();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtUserNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUserNameKeyPressed
        txtUserName.setBackground(Color.WHITE);
        txtUserName.setBorder(UIManager.getBorder("TextField.border"));
        txtUserName.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtUserNameKeyPressed

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setBorder(UIManager.getBorder("TextField.border"));
        txtPassword.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtPasswordKeyPressed

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed
        txtEmail.setBackground(Color.WHITE);
        txtEmail.setBorder(UIManager.getBorder("TextField.border"));
        txtEmail.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtEmailKeyPressed

    private void txtFullNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFullNameKeyPressed
        txtFullName.setBackground(Color.WHITE);
        txtFullName.setBorder(UIManager.getBorder("TextField.border"));
        txtFullName.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtFullNameKeyPressed

    private void txtPhoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyPressed
        txtPhone.setBackground(Color.WHITE);
        txtPhone.setBorder(UIManager.getBorder("TextField.border"));
        txtPhone.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtPhoneKeyPressed

    private void txtAddressKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAddressKeyPressed
        txtAddress.setBackground(Color.WHITE);
        txtAddress.setBorder(UIManager.getBorder("TextField.border"));
        txtAddress.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtAddressKeyPressed

    private void txtHireDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHireDateKeyPressed
        txtHireDate.setBackground(Color.WHITE);
        txtHireDate.setBorder(UIManager.getBorder("TextField.border"));
        txtHireDate.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtHireDateKeyPressed

    private void txtSalaryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSalaryKeyPressed
        txtSalary.setBackground(Color.WHITE);
        txtSalary.setBorder(UIManager.getBorder("TextField.border"));
        txtSalary.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtSalaryKeyPressed

    private void cmbUserTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbUserTypeItemStateChanged
        cmbUserType.setBackground(Color.WHITE);
        cmbUserType.setBorder(UIManager.getBorder("TextField.border"));
        cmbUserType.setForeground(Color.BLACK);
    }//GEN-LAST:event_cmbUserTypeItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbUserType;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlOperation;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JPopupMenu tblPopupMenu;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullName;
    private com.toedter.calendar.JDateChooser txtHireDate;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSalary;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUserName;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables
}
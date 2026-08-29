package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.service.InternService;
import com.itsolutioncenter.util.Validator;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class InternApplicationForm extends javax.swing.JInternalFrame {
private DatabaseManager dbManager=DatabaseManager.getInstance();
private InternService internservice=new InternService();
private ResultSet rs;
private DefaultTableModel model=new DefaultTableModel();
private String query,name,email,phone,university,course,qualification,path,applied_for,status,note,skills;
private Date date,interviewDate;
private int id,row;
private boolean insertion;
    /**
     * Creates new form InternApplicationForm
     */
    public InternApplicationForm() {
        initComponents();
        loadInternData();
        initCompnt();
    }
    private void loadInternData()
    {
        query="select * from intern_applications";
        model=dbManager.getTableModel(query, tblInterns);
        tblInterns.setModel(model);
    }
    private void searchData() {
        String searchText=txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter search criteria: Course ID, Course Code or Course Name");
            return;
        }
        boolean isNumeric = searchText.matches("\\d+");  
            if (isNumeric) {
                // Search by ID (numeric ID)
                id=Integer.parseInt(searchText);
                query = "select * from intern_applications where application_id = '"+id+"'";
            } else {
                // Search by username OR full name only (non-numeric)
                query= "select * from intern_applications where applicant_name LIKE '"+searchText+"' OR phone LIKE '"+searchText+"'";
            }
            model=dbManager.getTableModel(query,tblInterns);
            tblInterns.setModel(model); 
            txtSearch.setText("");
    }
   
     private void filterIntens() {
        String category = (String) cmbAppliedForFilter.getSelectedItem();
        String status = (String) cmbStatusFilter.getSelectedItem();
       
        try {
            List<Map<String, Object>> filteredInterns = internservice.getAllApplications();
           
            // Apply filters
            if (!category.equals("All Sectors")) {
                filteredInterns.removeIf(asset -> !category.equals(asset.get("applied_for")));
            }
           
            if (!status.equals("All Statuses")) {
                filteredInterns.removeIf(asset -> !status.equalsIgnoreCase((String) asset.get("status")));
            }
           
             DefaultTableModel model = (DefaultTableModel) tblInterns.getModel();
        model.setRowCount(0);
        for (Map<String, Object> application : filteredInterns) {
            model.addRow(new Object[]{
                application.get("application_id"),
                application.get("applicant_name"),
                application.get("email"),
                application.get("phone"),
                application.get("university"),
                application.get("course"),
                application.get("Qualification"),
                application.get("resume_path"),
                application.get("applied_for"),
                application.get("application_date"),
                application.get("status"),
                application.get("interview_date"),
                application.get("interview_note"),
                application.get("skills")
            });
        }      
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering applications: " + e.getMessage());
        }
    }
   
    private void clearFilters() {
        cmbAppliedForFilter.setSelectedIndex(0);
        cmbStatusFilter.setSelectedIndex(0);
        txtSearch.setText("");
        loadInternData();
    }
        private void initCompnt()
    {
        tablePopupMenu.add("Edit").addActionListener(e ->loadSelectedRowToForm());
        tablePopupMenu.add("Delete").addActionListener(e ->{ deleteIntern();});
        tablePopupMenu.add("Refresh").addActionListener(e ->loadInternData());
        tblInterns.addMouseListener(new MouseAdapter(){
        @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount()==2)
        {
            int rows=tblInterns.rowAtPoint(e.getPoint());
            int col=tblInterns.columnAtPoint(e.getPoint());
            if(rows>=0 && col>=0)
            {
                loadSelectedRowToForm();
            } 
        }
    }
 });}
    private void loadSelectedRowToForm() {
        row = tblInterns.getSelectedRow();
        try{
             if (row >= 0) {
            id=Integer.parseInt(tblInterns.getValueAt(row, 0).toString());
            txtApplicant.setText(tblInterns.getValueAt(row, 1).toString());
            txtEmail.setText(tblInterns.getValueAt(row, 2).toString());
            txtPhone.setText(tblInterns.getValueAt(row, 3).toString());
            txtUniversity.setText(tblInterns.getValueAt(row, 4).toString());
            txtCourse.setText(tblInterns.getValueAt(row, 5).toString());
            cmbQualification.setSelectedItem(tblInterns.getValueAt(row, 6));
            txtPath.setText(tblInterns.getValueAt(row, 7).toString());
            cmbAppliedFor.setSelectedItem(tblInterns.getValueAt(row, 8));
            txtDate.setDate((Date) tblInterns.getValueAt(row, 9));
            cmbStatus.setSelectedItem(tblInterns.getValueAt(row, 10));
            txtInterviewDate.setDate((Date)tblInterns.getValueAt(row, 11));
            txtNotes.setText(tblInterns.getValueAt(row, 12).toString());
            txtSkill.setText(tblInterns.getValueAt(row, 13).toString());
            insertion=false; 
             }
        }catch(NullPointerException e)
                     {
                     JOptionPane.showMessageDialog(this, "Some fields are null");
                     }
    }
     private boolean ValidateData()
    {
        if(!Validator.validateRequired(txtApplicant, "Applicant Name"))
        {
              return false; 
        }
        
        if(!Validator.isEmailValid(txtEmail.getText(),"Email",txtEmail))
        {
            return false;
        }
        if(!Validator.isPhoneValid(txtPhone.getText(),txtPhone,"Phone Number"))
        {
            return false;
        }
      
        if(cmbQualification.getSelectedIndex()==0)
        {
            JOptionPane.showMessageDialog(this, "Please Select Qualification","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbQualification.requestFocusInWindow();
            cmbQualification.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
        if(cmbAppliedFor.getSelectedIndex()==0)
        {
            JOptionPane.showMessageDialog(this, "Please Select Applied For","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbAppliedFor.requestFocusInWindow();
            cmbAppliedFor.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
       
        if(!Validator.isDateValid(txtDate.getDate(),txtDate))
        {
            return false;
        }
         if(cmbStatus.getSelectedIndex()==0)
        {
            JOptionPane.showMessageDialog(this, "Please Select Status","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbStatus.requestFocusInWindow();
            cmbStatus.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
        
        return true;
    }
     private void deleteIntern() {
        row = tblInterns.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        id = (int) tblInterns.getValueAt(row, 0);
        name = tblInterns.getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + name + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
               dbManager.delete("users", "user_id = ?", id);
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadInternData(); }
        } 
        private void updateData() {
        if(!ValidateData()) return;
        name=txtApplicant.getText();
        email=txtEmail.getText();
        phone=txtPhone.getText();
        university=txtUniversity.getText();
        course=txtCourse.getText();
        qualification=cmbQualification.getSelectedItem().toString();
        path=txtPath.getText();
        applied_for=cmbAppliedFor.getSelectedItem().toString();
        date=txtDate.getDate();
        status=cmbStatus.getSelectedItem().toString();
        interviewDate=txtInterviewDate.getDate();
        note=txtNotes.getText();
        skills=txtSkill.getText();
       int row=internservice.updateIntern(id,name, email, phone, university, course, qualification, path, applied_for, 
               date, status, interviewDate, note, skills);
       if(row>0) 
       {
           JOptionPane.showMessageDialog(this, "Data Updated Successfully!");
           loadInternData();
           clearData();
       }
        else
       {
           JOptionPane.showMessageDialog(null, "Data Couldn't Updated");
       }   
}
        private void clearData()
        {
            txtApplicant.setText("");
        cmbAppliedFor.setSelectedIndex(0);
        txtCourse.setText("");
        txtDate.setDate(null);
        txtEmail.setText("");
        txtInterviewDate.setDate(null);
        txtNotes.setText("");
        txtPath.setText("");
        txtPhone.setText("");
        txtSkill.setText("");
        txtUniversity.setText("");
        cmbQualification.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePopupMenu = new javax.swing.JPopupMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInterns = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        cmbStatusFilter = new javax.swing.JComboBox<>();
        btnFilter = new javax.swing.JButton();
        cmbAppliedForFilter = new javax.swing.JComboBox<>();
        btnRefresh = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        pnlOperation1 = new javax.swing.JPanel();
        txtApplicant = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        cmbQualification = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        txtUniversity = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnInsert = new javax.swing.JButton();
        txtDate = new com.toedter.calendar.JDateChooser();
        progressBar = new javax.swing.JProgressBar();
        txtCourse = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtPath = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        cmbAppliedFor = new javax.swing.JComboBox<>();
        cmbStatus = new javax.swing.JComboBox<>();
        txtInterviewDate = new com.toedter.calendar.JDateChooser();
        jLabel21 = new javax.swing.JLabel();
        txtNotes = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtSkill = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Intern Application Form");

        tblInterns.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Applicant", "Email", "Phone", "University", "Course", "Qualification", "Resume Path", "Applied For", "Date", "Status", "Interview Date", "Interview Notes", "Skills"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInterns.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblInterns);
        tblInterns.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblInterns.getColumnModel().getColumnCount() > 0) {
            tblInterns.getColumnModel().getColumn(0).setResizable(false);
            tblInterns.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblInterns.getColumnModel().getColumn(1).setResizable(false);
            tblInterns.getColumnModel().getColumn(2).setResizable(false);
            tblInterns.getColumnModel().getColumn(3).setResizable(false);
            tblInterns.getColumnModel().getColumn(3).setPreferredWidth(50);
            tblInterns.getColumnModel().getColumn(4).setResizable(false);
            tblInterns.getColumnModel().getColumn(5).setResizable(false);
            tblInterns.getColumnModel().getColumn(6).setResizable(false);
            tblInterns.getColumnModel().getColumn(6).setPreferredWidth(55);
            tblInterns.getColumnModel().getColumn(7).setResizable(false);
            tblInterns.getColumnModel().getColumn(8).setResizable(false);
            tblInterns.getColumnModel().getColumn(8).setPreferredWidth(55);
            tblInterns.getColumnModel().getColumn(9).setResizable(false);
            tblInterns.getColumnModel().getColumn(9).setPreferredWidth(40);
            tblInterns.getColumnModel().getColumn(10).setResizable(false);
            tblInterns.getColumnModel().getColumn(11).setResizable(false);
            tblInterns.getColumnModel().getColumn(11).setPreferredWidth(90);
            tblInterns.getColumnModel().getColumn(12).setResizable(false);
            tblInterns.getColumnModel().getColumn(13).setResizable(false);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inquiry Panel"));

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        cmbStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Statuses", "Received", "Reviewed", "Interviewed", "Accepted", "Rejected", "Hired" }));

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        cmbAppliedForFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Sectors", "Development", "Support", "Training", "General" }));

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jButton1.setText("Clear Filter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRefresh)
                .addGap(118, 118, 118)
                .addComponent(cmbAppliedForFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(115, 115, 115))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilter)
                    .addComponent(cmbAppliedForFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        pnlOperation1.setBackground(new java.awt.Color(102, 204, 255));
        pnlOperation1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Intern Application Operation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial Black", 1, 12))); // NOI18N

        txtApplicant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtApplicantKeyPressed(evt);
            }
        });

        jLabel6.setText("Applicant Name:");

        jLabel13.setText("University:");

        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
        });

        cmbQualification.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select User Role --", "Vocatinoal", "High School", "Institute", "Bachelor", "Master", " " }));
        cmbQualification.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbQualificationItemStateChanged(evt);
            }
        });

        jLabel14.setText("Qualification:");

        txtUniversity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUniversityKeyPressed(evt);
            }
        });

        jLabel15.setText("Email:");

        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPhoneKeyPressed(evt);
            }
        });

        jLabel16.setText("Cell Phone:");

        jLabel17.setText("Applied For:");

        jLabel18.setText("Date:");

        jLabel19.setText("Status:");

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

        txtDate.setDateFormatString("dd/MM/yyyy");
        txtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDateKeyPressed(evt);
            }
        });

        txtCourse.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCourseKeyPressed(evt);
            }
        });

        jLabel20.setText("Course:");

        txtPath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPathKeyPressed(evt);
            }
        });

        jButton2.setText("Browse...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        cmbAppliedFor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Field --", "Development", "Support", "Training", "General" }));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Status --", "Received", "Reviewed", "Interviewed", "Accepted", "Rejected", "Hired" }));

        txtInterviewDate.setDateFormatString("dd/MM/yyyy");
        txtInterviewDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtInterviewDateKeyPressed(evt);
            }
        });

        jLabel21.setText("Interview Date:");

        txtNotes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNotesKeyPressed(evt);
            }
        });

        jLabel22.setText("Notes:");

        txtSkill.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSkillKeyPressed(evt);
            }
        });

        jLabel23.setText("Skills:");

        javax.swing.GroupLayout pnlOperation1Layout = new javax.swing.GroupLayout(pnlOperation1);
        pnlOperation1.setLayout(pnlOperation1Layout);
        pnlOperation1Layout.setHorizontalGroup(
            pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperation1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperation1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApplicant, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUniversity, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCourse, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbQualification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlOperation1Layout.createSequentialGroup()
                        .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperation1Layout.createSequentialGroup()
                                .addComponent(btnInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(138, 138, 138))
                            .addGroup(pnlOperation1Layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPath, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmbAppliedFor, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(pnlOperation1Layout.createSequentialGroup()
                                .addComponent(btnClear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26))
                            .addGroup(pnlOperation1Layout.createSequentialGroup()
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtInterviewDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSkill, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        pnlOperation1Layout.setVerticalGroup(
            pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperation1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15)
                        .addComponent(txtUniversity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(txtApplicant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20)
                        .addComponent(jLabel14)
                        .addComponent(cmbQualification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)))
                .addGap(0, 9, Short.MAX_VALUE)
                .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(jLabel18)
                        .addComponent(jLabel19)
                        .addComponent(txtPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2)
                        .addComponent(cmbAppliedFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21))
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtInterviewDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSkill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                        .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))))
                .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperation1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlOperation1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperation1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnInsert)
                                .addComponent(btnUpdate)
                                .addComponent(btnDelete))
                            .addComponent(btnClear))))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlOperation1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlOperation1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchData();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadInternData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        filterIntens();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        clearFilters();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtApplicantKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApplicantKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtApplicantKeyPressed

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailKeyPressed

    private void cmbQualificationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbQualificationItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbQualificationItemStateChanged

    private void txtUniversityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUniversityKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUniversityKeyPressed

    private void txtPhoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneKeyPressed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateData();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearData();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteIntern();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInsertActionPerformed

    private void txtDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDateKeyPressed

    private void txtCourseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCourseKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCourseKeyPressed

    private void txtPathKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPathKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPathKeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    // 1. Create the file chooser popup (it won't sit permanently on your form)
    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(); 
    
    // 2. OPTIONAL: If you want to select folders/directories only, uncomment the next line:
    // chooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
    
    // 3. Open the dialog popup overlaying your application
    int returnVal = chooser.showOpenDialog(this); 
    
    // 4. If the user successfully clicks "Open" or "Save"
    if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) { 
        // Get the absolute file path
        String filePath = chooser.getSelectedFile().getAbsolutePath(); 
        
        // Push the path directly into your text field!
        txtPath.setText(filePath); 
    }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtInterviewDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInterviewDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtInterviewDateKeyPressed

    private void txtNotesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNotesKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNotesKeyPressed

    private void txtSkillKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSkillKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSkillKeyPressed
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbAppliedFor;
    private javax.swing.JComboBox<String> cmbAppliedForFilter;
    private javax.swing.JComboBox<String> cmbQualification;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> cmbStatusFilter;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlOperation1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JTable tblInterns;
    private javax.swing.JTextField txtApplicant;
    private javax.swing.JTextField txtCourse;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtEmail;
    private com.toedter.calendar.JDateChooser txtInterviewDate;
    private javax.swing.JTextField txtNotes;
    private javax.swing.JTextField txtPath;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSkill;
    private javax.swing.JTextField txtUniversity;
    // End of variables declaration//GEN-END:variables
}

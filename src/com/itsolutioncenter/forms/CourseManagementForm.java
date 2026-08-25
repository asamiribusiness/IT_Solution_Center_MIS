package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.model.Permission;
import com.itsolutioncenter.model.User;
import com.itsolutioncenter.service.CourseService;
import com.itsolutioncenter.util.Formatter;
import com.itsolutioncenter.util.Validator;
import com.toedter.calendar.JCalendar;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class CourseManagementForm extends javax.swing.JInternalFrame {
private CourseService service=new CourseService();
private User currentUser;
private Permission permissionService;
DatabaseManager db=DatabaseManager.getInstance();
private DefaultTableModel tableModel=new DefaultTableModel();
private Date SDate,EDate;
private int row,courseID,durration,instructor_ID;
private double fee;
private ResultSet rs;
private boolean insertion;
private String query,courseCode,courseName,description,schedule,status,category,startDate,endDate,userType;

    public CourseManagementForm(User currentUser, Permission permissionService) {
        this.currentUser = currentUser;
        this.permissionService = permissionService;
        initComponents();
        loadCourses();
        loadInstructors();
        initCompnt();
        if(!currentUser.getRole().equals("Admin"))
        {
            pnlOperation.setVisible(false);
        }
    }
    private void initCompnt()
    {
        tablePopupMenu.add("Edit").addActionListener(e ->loadSelectedRowToForm());
        tablePopupMenu.add("Delete").addActionListener(e ->{ deleteCourse();});
        tablePopupMenu.add("Refresh").addActionListener(e ->loadCourses());
        tablePopupMenu.add("Active Courses").addActionListener(e ->activeCourses());
      
        tblCourses.addMouseListener(new MouseAdapter(){
        @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount()==2)
        {
            int rows=tblCourses.rowAtPoint(e.getPoint());
            int col=tblCourses.columnAtPoint(e.getPoint());
            if(rows>=0 && col>=0)
            {
                loadSelectedRowToForm();
            } 
        }
    }
 });}
    
private void loadCourses() 
    {
       query="SELECT courses.course_id,courses.course_code,courses.course_name," +
       "courses.description,courses.duration_hours,courses.fee, " +
       "courses.category, users.full_name, courses.start_date,courses.end_date," +
       "courses.`schedule`,courses.`status` FROM users INNER JOIN courses ON " +
       "courses.instructor_id = users.user_id order by course_id";
       tableModel=db.getTableModel(query,tblCourses);
       tblCourses.setModel(tableModel);     
       
       KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0);
    }
private void loadInstructors ()
{
    try
    {
        cmbInstructor.removeAllItems();
        cmbInstructor.addItem("-- Select Instructor --");
      List<Map<String,Object>> insts=db.select("users", "role=?", "Instructor");
      for(Map<String,Object>tutor:insts)
      {
          cmbInstructor.addItem(tutor.get("full_name").toString());
      }
    }catch(SQLException e)
    {
        JOptionPane.showMessageDialog(null, "Instructor Data Couldn't Load");
    }
}
private void loadSelectedRowToForm() {
        row = tblCourses.getSelectedRow();
        try
        {
             if (row >= 0) {
            courseID=Integer.parseInt(tblCourses.getValueAt(row, 0).toString());
            txtCourseCode.setText(tblCourses.getValueAt(row, 1).toString());
            txtCourseName.setText(tblCourses.getValueAt(row, 2).toString());
            txtDescription.setText(tblCourses.getValueAt(row, 3).toString());
            txtDurration.setText(tblCourses.getValueAt(row, 4).toString());
            txtFee.setText(tblCourses.getValueAt(row, 5).toString());
            cmbCategory.setSelectedItem(tblCourses.getValueAt(row, 6));
            cmbInstructor.setSelectedItem(tblCourses.getValueAt(row, 7));
            txtStartDate.setDate((Date) tblCourses.getValueAt(row, 8));
            txtEndDate.setDate((Date)tblCourses.getValueAt(row, 9));
            txtSchedule.setText(tblCourses.getValueAt(row, 10).toString());
            cmbStatus.setSelectedItem(tblCourses.getValueAt(row, 11));
            insertion=false; 
        }
        }catch(NullPointerException e) 
        {
            JOptionPane.showMessageDialog(null, "Some Fields Are Null");
        }
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
                courseID=Integer.parseInt(searchText);
                query = "select * from courses where course_id = '"+courseID+"'";
            } else {
                // Search by username OR full name only (non-numeric)
                query= "select * from courses where course_code LIKE '"+searchText+"' OR course_name LIKE '"+searchText+"'";
            }
            tableModel=db.getTableModel(query,tblCourses);
            tblCourses.setModel(tableModel); 
            txtSearch.setText("");
    }
    private void addCourses() 
    {
        if(!validationControls()) return;
        if(!insertion)
        {
            JOptionPane.showMessageDialog(this, "Data Existed & Can't Inserted Again!");
            clearControls();
            insertion=true;
        }
        courseCode=txtCourseCode.getText();
        courseName=txtCourseName.getText();
        description=txtDescription.getText();
        durration=Integer.parseInt(txtDurration.getText());
        fee=Double.parseDouble(txtFee.getText());
        category=cmbCategory.getSelectedItem().toString();
        instructor_ID=cmbInstructor.getSelectedIndex()+1;
        SDate=txtStartDate.getDate();
        EDate=txtEndDate.getDate();
        schedule=txtSchedule.getText();
        status=cmbStatus.getSelectedItem().toString();
        row=service.addCourse(courseCode, courseName, description, durration, fee, category, instructor_ID, SDate, EDate, schedule, status);
        if(row>0)
        {
            JOptionPane.showMessageDialog(this, "Course Added Succussfully");
            clearControls();
            loadCourses();
        }
    }
    private void deleteCourse() {
        row = tblCourses.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        if(permissionService.canDelete())
        {
             courseID = (int) tblCourses.getValueAt(row, 0);
        courseName = (String)tblCourses.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Course: " + courseName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
               db.delete("courses", "course_id = ?", courseID);
                JOptionPane.showMessageDialog(this, "Course deleted successfully!");
                loadCourses(); }
        }  
        else
        {
            showAccessDenied("Delete");
        }
        }
    private void updateData()
    {
        insertion=false;
        if(!validationControls()) return;
       // courseID=Integer.parseInt(txtCourseID.getText());
        courseCode=txtCourseCode.getText();
        courseName=txtCourseName.getText();
        description=txtDescription.getText();
        durration=Integer.parseInt(txtDurration.getText());
        fee=Double.parseDouble(txtFee.getText());
        category=cmbCategory.getSelectedItem().toString();
        String instructor=(String)cmbInstructor.getSelectedItem();
        try{
            List<Map<String , Object>> ins= db.select("users", "role=? AND full_name=?","Instructor",instructor);
            for(Map<String,Object>tutor:ins){
                instructor_ID=Integer.parseInt(tutor.get("user_id").toString());
            }
        }catch(SQLException e){e.getMessage();}
        SDate=txtStartDate.getDate();
        EDate=txtEndDate.getDate();
        schedule=txtSchedule.getText();
        status=cmbStatus.getSelectedItem().toString();
        row = service.updateCourse(courseID,courseCode, courseName, description, durration, fee, category, instructor_ID, SDate, EDate, schedule, status);
        if(row>0)
        {
            JOptionPane.showMessageDialog(this, "Course Data Updated Succussfully");
            clearControls();
            loadCourses();
        }
    }
    private void activeCourses()
    {
        DefaultTableModel model=(DefaultTableModel) tblCourses.getModel();
        model.setRowCount(0);
            try
            {
                List<Map<String, Object>> data = service.getActiveCourses();
               for(Map<String,Object> activeCourses:data)
               {
                   model.addRow(new Object[]{
                       activeCourses.get("course_id"),
                       activeCourses.get("course_code"),
                       activeCourses.get("course_name"),
                       activeCourses.get("description"),
                       activeCourses.get("duration_hours"),
                Formatter.formatCurrency((Number) activeCourses.get("fee")),
                       activeCourses.get("category"),
                       activeCourses.get("full_name"),
                       activeCourses.get("start_date"),
                       activeCourses.get("end_date"),
                       activeCourses.get("schedule"),
                       activeCourses.get("status")
            });
               }tblCourses.setModel(model);
            }catch(SQLException e)
            {
                e.getMessage();
            }
            //tblCourses.setModel(new MapTableModel(data));
//        query="select * from courses where status IN ('ongoing', 'upcoming') ORDER BY start_date";
//        tableModel=db.getTableModel(query,tblCourses);
//        tblCourses.setModel(tableModel);
         
    }
    private boolean validationControls()
    {
        if(!Validator.validateRequired(txtCourseCode, "Course Code"))
        {
            return false;
        }
        if(!Validator.validateRequired(txtCourseName, "Course Name"))
        {
            return false;
        }
        if(!Validator.validateRequired(txtDescription, "Description"))
        {
            return false;
        }
        if(!Validator.validateRequired(txtDurration, "Durration"))
        {
            return false;
        }
        if(!Validator.validateNumber(txtDurration.getText(), "Durration",txtDurration))
        {
            return false;
        }
        if(!Validator.validateRequired(txtFee, "Fee"))
        {
            return false;
        }
        if(!Validator.validateNumber(txtFee.getText(), "Fee",txtFee))
        {
            return false;
        }
        if(cmbCategory.getSelectedIndex()<1)
        {
            JOptionPane.showMessageDialog(this, "Please Select Course Category","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbCategory.requestFocusInWindow();
            cmbCategory.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
        if(cmbInstructor.getSelectedIndex()==-1)
        {
            JOptionPane.showMessageDialog(this, "Please Select Instructor ","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbInstructor.requestFocusInWindow();
            cmbInstructor.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
        if(!Validator.dateRequired(txtStartDate, "Start Date"))
        {
            return false;
        }
        if(!Validator.isDateValid(txtStartDate.getDate(),txtStartDate))
        {
            //txtStartDate.requestFocusInWindow();
            return false;
        }
//        if(!Validator.validateDateRequired(txtEndDate, "End Date"))
//        {
//            return false;
//        }
        if(!Validator.isDateValid(txtEndDate.getDate(),txtEndDate))
        {
            txtEndDate.requestFocusInWindow();
            return false;
        }
        if(!Validator.validateRequired(txtSchedule, "Schedule"))
        {
            return false;
        }
        if(cmbStatus.getSelectedIndex()<1)
        {   
            JOptionPane.showMessageDialog(this, "Please Select Payment Status ","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbStatus.requestFocusInWindow();
            cmbStatus.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
        return true;
    }
    private void clearControls()
    {
//            txtCourseID.setText("");
            txtCourseCode.setText("");
            txtCourseName.setText("");
            txtDescription.setText("");
            txtDurration.setText("");
            txtFee.setText("");
            cmbCategory.setSelectedIndex(0);
            cmbInstructor.setSelectedIndex(0);
            txtStartDate.setDate(null);
            txtEndDate.setDate(null);
            txtSchedule.setText("");
            cmbStatus.setSelectedIndex(0);
    }
       
    public void exportTableToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setSelectedFile(new File("معلومات مشتریان" + "("+
                java.time.LocalDate.now()+")"+".csv"));
       
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
           
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8))) {
               
                // Write UTF-8 BOM for Excel compatibility
                writer.write('\ufeff');
               
                // Get table model
                TableModel model = tblCourses.getModel();
               
                // Write column headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.write(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.println();
               
                // Write data rows
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        String cellValue = (value != null) ? value.toString() : "";
                       
                        // Escape commas and quotes in CSV
                        if (cellValue.contains(",") || cellValue.contains("\"")) {
                            cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                        }
                       
                        writer.write(cellValue);
                        if (j < model.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.println();
                }
               
                JOptionPane.showMessageDialog(null,
                    "فایل CSV با موفقیت صادر شد!\nCSV file exported successfully!",
                    "موفقیت/Success",
                    JOptionPane.INFORMATION_MESSAGE);
               
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    "خطا در صادر کردن فایل: " + e.getMessage(),
                    "خطا/Error",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
        private void showAccessDenied(String Action) {
        JOptionPane.showMessageDialog(this,
            "Access Denied!\n\n" +
            "You don't have permission to " + Action + ".\n" +
            "Please contact your administrator if you need permission.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE);
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
        tblCourses = new javax.swing.JTable();
        pnlOperation = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCourseCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtCourseName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDurration = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFee = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cmbInstructor = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSchedule = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        btnInsert = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Course Enrollment Form - " + currentUser.getFullName());
        setPreferredSize(new java.awt.Dimension(1200, 500));

        tblCourses.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        tblCourses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Course ID", "Course Code", "Course Name", "Description", "Durration", "Fee", "Category", "Instructor", "Start Date", "End Date", "Schedule", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tblCourses.setColumnSelectionAllowed(true);
        tblCourses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCourses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCourses.getTableHeader().setReorderingAllowed(false);
        tblCourses.setUpdateSelectionOnSort(false);
        tblCourses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCoursesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCourses);
        tblCourses.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tblCourses.getColumnModel().getColumnCount() > 0) {
            tblCourses.getColumnModel().getColumn(0).setResizable(false);
            tblCourses.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblCourses.getColumnModel().getColumn(1).setResizable(false);
            tblCourses.getColumnModel().getColumn(1).setPreferredWidth(40);
            tblCourses.getColumnModel().getColumn(2).setResizable(false);
            tblCourses.getColumnModel().getColumn(2).setPreferredWidth(70);
            tblCourses.getColumnModel().getColumn(3).setResizable(false);
            tblCourses.getColumnModel().getColumn(3).setPreferredWidth(150);
            tblCourses.getColumnModel().getColumn(4).setResizable(false);
            tblCourses.getColumnModel().getColumn(4).setPreferredWidth(20);
            tblCourses.getColumnModel().getColumn(5).setResizable(false);
            tblCourses.getColumnModel().getColumn(5).setPreferredWidth(40);
            tblCourses.getColumnModel().getColumn(6).setResizable(false);
            tblCourses.getColumnModel().getColumn(6).setPreferredWidth(70);
            tblCourses.getColumnModel().getColumn(7).setResizable(false);
            tblCourses.getColumnModel().getColumn(7).setPreferredWidth(80);
            tblCourses.getColumnModel().getColumn(8).setResizable(false);
            tblCourses.getColumnModel().getColumn(8).setPreferredWidth(30);
            tblCourses.getColumnModel().getColumn(9).setResizable(false);
            tblCourses.getColumnModel().getColumn(9).setPreferredWidth(30);
            tblCourses.getColumnModel().getColumn(10).setResizable(false);
            tblCourses.getColumnModel().getColumn(10).setPreferredWidth(50);
            tblCourses.getColumnModel().getColumn(11).setResizable(false);
            tblCourses.getColumnModel().getColumn(11).setPreferredWidth(40);
        }

        pnlOperation.setBackground(new java.awt.Color(102, 204, 255));
        pnlOperation.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Operation Section"));

        jLabel2.setText("Course Code:");

        txtCourseCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCourseCodeKeyPressed(evt);
            }
        });

        jLabel3.setText("Course Name:");

        txtCourseName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCourseNameKeyPressed(evt);
            }
        });

        jLabel4.setText("Description:");

        txtDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDescriptionKeyPressed(evt);
            }
        });

        jLabel5.setText("Durration:");

        txtDurration.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDurrationKeyPressed(evt);
            }
        });

        jLabel6.setText("Fee");

        txtFee.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFeeKeyPressed(evt);
            }
        });

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category", "Programming", "Networking", "Database", "Web Development", "Cyber Security", "Internet & Mailing", "Hardware", "ICT", "IT", "MOUS", "ICDL", "Other" }));
        cmbCategory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbCategoryMouseClicked(evt);
            }
        });

        jLabel7.setText("Category:");

        cmbInstructor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbInstructorMouseClicked(evt);
            }
        });

        jLabel8.setText("Instructor:");

        jLabel9.setText("Start Date:");

        jLabel10.setText("End Date:");

        txtSchedule.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtScheduleKeyPressed(evt);
            }
        });

        jLabel11.setText("Schedule:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Status", "Upcoming", "Ongoing", "Completed", "Cancelled" }));

        jLabel12.setText("Status:");

        btnInsert.setText("Insert");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jButton1.setText("Clear Controls");

        jButton2.setText("Active Courses");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtStartDate.setDateFormatString("yyyy-MM-dd");

        txtEndDate.setDateFormatString("yyyy-MM-dd");

        jButton3.setText("Export CSV");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        //btnRefresh.add(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOperationLayout = new javax.swing.GroupLayout(pnlOperation);
        pnlOperation.setLayout(pnlOperationLayout);
        pnlOperationLayout.setHorizontalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCourseCode, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCourseName, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDurration, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFee, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addComponent(btnInsert)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnDelete)
                                .addGap(28, 28, 28)
                                .addComponent(btnUpdate)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(pnlOperationLayout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmbInstructor, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(14, 14, 14)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10)
                                        .addGap(3, 3, 3)
                                        .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(pnlOperationLayout.createSequentialGroup()
                                        .addGap(190, 190, 190)
                                        .addComponent(btnRefresh)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton3)
                                        .addGap(30, 30, 30)))
                                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2)
                                    .addGroup(pnlOperationLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(pnlOperationLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)
                                .addGap(235, 235, 235)
                                .addComponent(btnSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(53, 53, 53))))
        );
        pnlOperationLayout.setVerticalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtCourseCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(txtCourseName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(txtDurration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(txtFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(cmbInstructor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12))
                            .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(pnlOperationLayout.createSequentialGroup()
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel10)))))
                    .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnInsert)
                            .addComponent(btnDelete)
                            .addComponent(btnUpdate)
                            .addComponent(btnSearch)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton3))
                        .addContainerGap())
                    .addGroup(pnlOperationLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnRefresh)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlOperation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOperation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblCoursesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCoursesMouseClicked
           if (evt.getButton() == MouseEvent.BUTTON3) { // Right-click
         row = tblCourses.rowAtPoint(evt.getPoint());
        if (row >= 0 && row < tblCourses.getRowCount()) {
            tblCourses.setRowSelectionInterval(row, row);
            tablePopupMenu.show(tblCourses, evt.getX(), evt.getY());}}
    }//GEN-LAST:event_tblCoursesMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchData();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
            addCourses();
    }//GEN-LAST:event_btnInsertActionPerformed

    private void txtCourseCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCourseCodeKeyPressed
        txtCourseCode.setBackground(Color.WHITE);
        txtCourseCode.setBorder(UIManager.getBorder("TextField.border"));
        txtCourseCode.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtCourseCodeKeyPressed

    private void txtCourseNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCourseNameKeyPressed
        txtCourseName.setBackground(Color.WHITE);
        txtCourseName.setBorder(UIManager.getBorder("TextField.border"));
        txtCourseName.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtCourseNameKeyPressed

    private void cmbInstructorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbInstructorMouseClicked
        cmbInstructor.setBorder(UIManager.getBorder("TextField.border"));
    }//GEN-LAST:event_cmbInstructorMouseClicked

    private void txtDescriptionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescriptionKeyPressed
        txtDescription.setBackground(Color.WHITE);
        txtDescription.setBorder(UIManager.getBorder("TextField.border"));
        txtDescription.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtDescriptionKeyPressed

    private void txtDurrationKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDurrationKeyPressed
        txtDurration.setBackground(Color.WHITE);
        txtDurration.setBorder(UIManager.getBorder("TextField.border"));
        txtDurration.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtDurrationKeyPressed

    private void txtFeeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFeeKeyPressed
        txtFee.setBackground(Color.WHITE);
        txtFee.setBorder(UIManager.getBorder("TextField.border"));
        txtFee.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtFeeKeyPressed

    private void cmbCategoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbCategoryMouseClicked
        cmbCategory.setBorder(UIManager.getBorder("TextField.border"));
    }//GEN-LAST:event_cmbCategoryMouseClicked

    private void txtScheduleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtScheduleKeyPressed
        txtSchedule.setBackground(Color.WHITE);
        txtSchedule.setBorder(UIManager.getBorder("TextField.border"));
        txtSchedule.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtScheduleKeyPressed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteCourse();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
     
        activeCourses();
              
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateData();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        //exportData();
        exportTableToCSV();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadCourses();
        btnRefresh.setMnemonic('R');
        
       // setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
                
        
    }//GEN-LAST:event_btnRefreshActionPerformed
      

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbInstructor;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlOperation;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JTable tblCourses;
    private javax.swing.JTextField txtCourseCode;
    private javax.swing.JTextField txtCourseName;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtDurration;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private javax.swing.JTextField txtFee;
    private javax.swing.JTextField txtSchedule;
    private javax.swing.JTextField txtSearch;
    private com.toedter.calendar.JDateChooser txtStartDate;
    // End of variables declaration//GEN-END:variables
}

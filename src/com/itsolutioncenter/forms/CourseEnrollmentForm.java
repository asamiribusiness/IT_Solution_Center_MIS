package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.model.Permission;
import com.itsolutioncenter.model.User;
import com.itsolutioncenter.service.CourseService;
import com.itsolutioncenter.util.Formatter;
import com.itsolutioncenter.util.Validator;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class CourseEnrollmentForm extends JInternalFrame {
CourseService service=new CourseService();
DatabaseManager db=DatabaseManager.getInstance();
private User user;
private DefaultTableModel tableModel=new DefaultTableModel();
private int row,courseID,enrollment_ID;
private double feePaid,totalFee,remainedFee,attendance;
private ResultSet rs;
private boolean insertion=true,issueCertificate;
private String query,courseName,studentName,phone,email,note,paymentStatus;
private Date enrollmentDate;
private Permission permissionService;
    /**
     * Creates new form CourseEnrollmentForm
     */
    public CourseEnrollmentForm(User user, Permission permissionService) {
        initComponents();
        loadEnrollments();
        loadCourseName();
        this.user=user;
        this.permissionService=permissionService;
        btnCSV.setEnabled(permissionService.canExport());
        initCompnt();
        }

    private void initCompnt()
    {
        tablePopupMenu.add("Edit").addActionListener(e ->loadSelectedRowToForm());
        tablePopupMenu.add("Delete").addActionListener(e ->{ deleteCourse();});
        tablePopupMenu.add("Refresh").addActionListener(e ->loadEnrollments());
        cmbCourseName.setSelectedIndex(-1);
        cmbPaymentSt.setSelectedIndex(-1);
        loadStatisticsTab();
        //Double click to load data into controls
        tblEnrollment.addMouseListener(new MouseAdapter(){
        @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount()==2)
        {
            int rows=tblEnrollment.rowAtPoint(e.getPoint());
            int col=tblEnrollment.columnAtPoint(e.getPoint());
            if(rows>=0 && col>=0)
            {
                loadSelectedRowToForm();
            } 
        }}
 });}
    
private void loadEnrollments() 
    {
           // Additional permission checks
//    if (!baseform.canPerform("view")) {
//        JOptionPane.showMessageDialog(this, "You don't have permission to view Courses");
//        dispose();
//        return;
//    }
        query="SELECT course_enrollments.enrollment_id, courses.course_name," +
        "course_enrollments.student_name,course_enrollments.student_email,course_enrollments.student_phone," +
        "course_enrollments.enrollment_date,course_enrollments.fee_paid,course_enrollments.total_fee," +
        "course_enrollments.payment_status,course_enrollments.attendance_percentage,course_enrollments.certificate_issued," +
        "course_enrollments.notes FROM courses INNER JOIN course_enrollments ON " +
        "course_enrollments.course_id = courses.course_id ORDER BY course_enrollments.enrollment_id";
       tableModel=db.getTableModel(query,tblEnrollment);
       tblEnrollment.setModel(tableModel);     
       colorTableRows();
    }
private void loadCourseName ()
{
    query="Select course_name from courses order by course_id";
//    rs=DatabaseManager.executeSimpleQuery(query);
    try
    {
//        cmbCourseName.removeAllItems();
//        cmbCourseName.addItem("Select Instructor");
        List<Map<String,Object>> courseNames=db.select(query);
        for (Map<String,Object> Course : courseNames) {
        cmbCourseName.addItem(Course.get("course_name").toString());
        }
    }catch(SQLException e)
    {
        JOptionPane.showMessageDialog(null, "Course Data Couldn't Load");
    }
}
private void loadSelectedRowToForm() { 
    row = tblEnrollment.getSelectedRow();
        try
        { 
             if (row >= 0) {
            txtEnrollmentID.setText(tblEnrollment.getValueAt(row, 0).toString());
            cmbCourseName.setSelectedItem(tblEnrollment.getValueAt(row, 1));
            txtStdName.setText(tblEnrollment.getValueAt(row, 2).toString());
            txtEmail.setText(tblEnrollment.getValueAt(row, 3).toString());
            txtPhone.setText(tblEnrollment.getValueAt(row, 4).toString());
            txtEnrollmentDate.setDate((Date)(tblEnrollment.getValueAt(row, 5)));
            txtFeePaid.setText(tblEnrollment.getValueAt(row, 6).toString());
            txtTotalFee.setText(tblEnrollment.getValueAt(row, 7).toString());
            cmbPaymentSt.setSelectedItem(tblEnrollment.getValueAt(row, 8));
            txtAttendance.setText(tblEnrollment.getValueAt(row, 9).toString());
            issueCertificate=(boolean) tblEnrollment.getValueAt(row, 10);
            if(issueCertificate)chkCertificate.setSelected(true);
            txtNote.setText(tblEnrollment.getValueAt(row, 11).toString());
            insertion=false; 
        }
        }catch(NullPointerException e) 
        {
            JOptionPane.showMessageDialog(null, "Some Fields Are Null");
        }
        double result=Double.parseDouble(txtTotalFee.getText())-Double.parseDouble(txtFeePaid.getText());
            txtRemained.setText(String.valueOf(result));   
        }

    // Proceed with action
    private void searchData() {
    
    // Proceed with action
        String searchText=txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter search criteria: Enrollment ID, Student Name or Phone Number");
            return;
        }
        boolean isNumeric = searchText.matches("\\d+");  
            if (isNumeric) {
                // Search by ID (numeric ID)
                enrollment_ID=Integer.parseInt(searchText);
                query = "select * from course_enrollments where enrollment_id = '"+enrollment_ID+"'";
            } else {
                // Search by username OR full name only (non-numeric)
                query= "select * from course_enrollments where student_name LIKE '"+searchText+"' OR student_phone LIKE '"+searchText+"'";
            }
            tableModel=db.getTableModel(query,tblEnrollment);
            tblEnrollment.setModel(tableModel); 
            txtSearch.setText("");
    }
    private void enrollStudent() throws SQLException 
    {
        if(!validationControls()) return;
        if(!insertion)
        {
            JOptionPane.showMessageDialog(this, "Data Existed & Can't Inserted Again!");
            clearControls();
            insertion=true;
        }
        //enrollment_ID=Integer.parseInt(txtEnrollmentID.getText());
        courseID=cmbCourseName.getSelectedIndex()+1;
        studentName=txtStdName.getText();
        phone=txtPhone.getText();
        email=txtEmail.getText();
        enrollmentDate=txtEnrollmentDate.getDate();
        feePaid=Double.parseDouble(txtFeePaid.getText());
        totalFee=Double.parseDouble(txtTotalFee.getText());
        paymentStatus=cmbPaymentSt.getSelectedItem().toString();
        attendance=Double.parseDouble(txtAttendance.getText());
        if(chkCertificate.isSelected())issueCertificate=true;
        note=txtNote.getText();
        row=service.enrollStudent(courseID, studentName, email, phone, enrollmentDate, feePaid, totalFee, paymentStatus, attendance, issueCertificate, note);
        if(row>0)
        {
            JOptionPane.showMessageDialog(this, "Course Added Succussfully");
            clearControls();
            loadEnrollments();
        }
    }
    private void deleteCourse() {
        row = tblEnrollment.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete");
            return;
        }
        enrollment_ID = (int) tblEnrollment.getValueAt(row, 0);
        studentName = tblEnrollment.getValueAt(row, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Student: " + studentName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
               db.delete("course_enrollments", "enrollment_id = ?", enrollment_ID);
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadEnrollments(); }
        }  
    private void updateData()
    {
        insertion=false;
        if(!validationControls()) return;
        enrollment_ID=Integer.parseInt(txtEnrollmentID.getText());
        courseID=cmbCourseName.getSelectedIndex()+1;
        studentName=txtStdName.getText();
        phone=txtPhone.getText();
        email=txtEmail.getText();
        enrollmentDate=txtEnrollmentDate.getDate();
        feePaid=Double.parseDouble(txtFeePaid.getText());
        totalFee=Double.parseDouble(txtTotalFee.getText());
        paymentStatus=cmbPaymentSt.getSelectedItem().toString();
        attendance=Double.parseDouble(txtAttendance.getText());
        if(chkCertificate.isSelected())issueCertificate=true;
        note=txtNote.getText();
        row=service.updateEnrollment(enrollment_ID, courseID, studentName, email, phone, enrollmentDate, feePaid, totalFee, paymentStatus, attendance, issueCertificate, note);
        if(row>0)
        {
            JOptionPane.showMessageDialog(this, "Course Data Updated Succussfully");
            clearControls();
            loadEnrollments();
            insertion=true;
        }
    }
    private void filterEnrollments()
    {
        String status;
        status=cmbPaymentStatusFilter.getSelectedItem().toString();
        if(status.equals("All Category"))
        {
            loadEnrollments();
        }
        else
        {
         query="SELECT course_enrollments.enrollment_id, courses.course_name," +
        "course_enrollments.student_name,course_enrollments.student_email,course_enrollments.student_phone," +
        "course_enrollments.enrollment_date,course_enrollments.fee_paid,course_enrollments.total_fee," +
        "course_enrollments.payment_status,course_enrollments.attendance_percentage,course_enrollments.certificate_issued," +
        "course_enrollments.notes FROM courses INNER JOIN course_enrollments ON " +
        "course_enrollments.course_id = courses.course_id WHERE payment_status='"+status+"' ORDER BY course_enrollments.enrollment_id";
       tableModel=db.getTableModel(query,tblEnrollment);
       tblEnrollment.setModel(tableModel);    
        }
    }
     private void loadStatistics() {
         Map<String, Object> stats = service.getCourseStatistics();
         String content="Number of Students: "+stats.get("No_of_Students")+
                 "\nTotal Fee Amount: "+Formatter.formatCurrency((Number)stats.get("Total_Fee"))+
                 "\nPaid Fee Amount: "+Formatter.formatCurrency((Number)stats.get("Fee_Paid"))+
                 "\nRemained Fee Amount: "+Formatter.formatCurrency((Number)stats.get("Remained_Payments"))+
                 "\nNumber of Pending Payments: "+stats.get("Pending_Payments")+
                 "\nNumber of Partial Payments:"+stats.get("Partial_Payments")+
                 "\nNumber of Paid Payments: "+stats.get("Paid_Payments")+
                 "\nNumber of Refunded Payments: "+stats.get("Refunded_Payments")+
                 "\nNumber of Students Received Certificates:"+stats.get("Certificate_Issued");
         JTextArea textArea = new JTextArea(content);
         textArea.setEditable(false);
         textArea.setFont(new Font("Monospaced", Font.BOLD, 12));
         textArea.setLineWrap(true);
         textArea.setWrapStyleWord(true);
         JScrollPane scrollPane = new JScrollPane(textArea);
         scrollPane.setPreferredSize(new Dimension(300, 200));
         JOptionPane.showMessageDialog(null,scrollPane,"Statistics of Enrollments:",JOptionPane.INFORMATION_MESSAGE);
    }
     private void loadStatisticsTab()
     {
         Map<String, Object> stats = service.getCourseStatistics();
         lblStudentCount.setText(stats.get("No_of_Students").toString());
         lblFees.setText(Formatter.formatCurrency((Number)stats.get("Total_Fee")));
         lblFeePaid.setText(Formatter.formatCurrency((Number)stats.get("Fee_Paid")));
         lblRemainedFees.setText(Formatter.formatCurrency((Number)stats.get("Remained_Payments")));
         lblPendingFees.setText(stats.get("Pending_Payments").toString());
         lblPartialFees.setText(stats.get("Partial_Payments").toString());
         lblPaidFees.setText(stats.get("Paid_Payments").toString());
         lblRefundedFees.setText(stats.get("Refunded_Payments").toString());
         lblCertificateIssued.setText(stats.get("Certificate_Issued").toString());
     }
    private boolean validationControls()
    {
        if(cmbCourseName.getSelectedIndex()==-1)
        {
            JOptionPane.showMessageDialog(this, "Please Select Course Name","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbCourseName.requestFocusInWindow();
            cmbCourseName.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
        if(!Validator.validateRequired(txtStdName, "Student Name"))
        {
            return false;
        }
        if(!Validator.validateRequired(txtPhone, "Cell Phone"))
        {
            return false;
        }
        if(!Validator.isPhoneValid(txtPhone.getText(),txtPhone,"Phone Number"))
        {
            return false;
        }
        if(!Validator.validateRequired(txtEmail, "Email"))
        {
            return false;
        }
        if(!Validator.isEmailValid(txtEmail.getText(), "Email", txtEmail))
        {
            return false;
        }
        if(!Validator.validateRequired(txtFeePaid, "Fee"))
        {
            return false;
        }
        if(!Validator.validateNumber(txtFeePaid.getText(), "Fee",txtFeePaid))
        {
            return false;
        }
         if(!Validator.validateRequired(txtTotalFee, "Total Fee"))
        {
            return false;
        }
        if(!Validator.validateNumber(txtTotalFee.getText(),"Total Fee",txtTotalFee))
        {
            return false;
        }
        if(cmbPaymentSt.getSelectedIndex()==-1)
        {
            JOptionPane.showMessageDialog(this, "Please Select Payment Status ","Validation Error",JOptionPane.ERROR_MESSAGE);
            cmbPaymentSt.requestFocusInWindow();
            cmbPaymentSt.setBorder(BorderFactory.createLineBorder(Color.red,2));
            return false;
        }
        if(!Validator.validateNumber(txtAttendance.getText(), "Attendance", txtAttendance))
        { 
            return false;
        }
        return true;
    }
    private void clearControls()
    {   
//        if (!baseform.canPerform("edit")) {
//        baseform.showAccessDenied("edit this record");
//        return;
//    }
            txtEnrollmentID.setText("");
            cmbCourseName.setSelectedIndex(-1);
            txtStdName.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtEnrollmentDate.setDate(null);
            txtFeePaid.setText("");
            txtTotalFee.setText("");
            txtRemained.setText("");
            cmbPaymentSt.setSelectedIndex(-1); 
            txtAttendance.setText("");
            chkCertificate.setSelected(false);
            txtNote.setText("");
    }
     private void clearFilters() {
        cmbPaymentStatusFilter.setSelectedIndex(0);
       // cmbStatusFilter.setSelectedIndex(0);
        txtSearch.setText("");
        loadEnrollments();
    }
private void colorTableRows() {
        tblEnrollment.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getValueAt(row, 8);
                if (!isSelected) {
                    switch (status) {
                        case "Pending":
                            c.setBackground(new Color(220, 255, 220)); // Light green
                            break;
                        case "Parial":
                            c.setBackground(new Color(255, 255, 200)); // Light yellow
                            break;
                        case "Refunded":
                            c.setBackground(new Color(255, 220, 220)); // Light red
                            break;
                        case "Paid":
                            c.setBackground(new Color(240, 240, 240)); // Light gray
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
    }
public void exportTableToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setSelectedFile(new File("معلومات شاگردان کورس" + "("+java.time.LocalDate.now()+")"+".csv"));
     
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
                TableModel model = tblEnrollment.getModel();
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
                JOptionPane.showMessageDialog(null,"فایل CSV با موفقیت صادر شد!\nCSV file exported successfully!",
                    "موفقیت",JOptionPane.INFORMATION_MESSAGE);
               
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,"خطا در صادر کردن فایل: " + e.getMessage(),
                    "خطا",JOptionPane.ERROR_MESSAGE);
                e.getMessage();
            }
        }
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
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbPaymentStatusFilter = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEnrollment = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtEnrollmentID = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtStdName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbPaymentSt = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txtTotalFee = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtAttendance = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        btnInsert = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        cmbCourseName = new javax.swing.JComboBox<>();
        txtFeePaid = new javax.swing.JTextField();
        chkCertificate = new javax.swing.JCheckBox();
        txtNote = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtRemained = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        txtEnrollmentDate = new com.toedter.calendar.JDateChooser();
        btnCSV = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblStudentCount = new javax.swing.JLabel();
        lblFeePaid = new javax.swing.JLabel();
        lblFees = new javax.swing.JLabel();
        lblRemainedFees = new javax.swing.JLabel();
        lblPendingFees = new javax.swing.JLabel();
        lblPartialFees = new javax.swing.JLabel();
        lblPaidFees = new javax.swing.JLabel();
        lblRefundedFees = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblCertificateIssued = new javax.swing.JLabel();

        tablePopupMenu.setEnabled(false);

        setClosable(true);
        setIconifiable(true);
        setTitle("Course Enrollment Form");

        jButton2.setText("Refresh");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jLabel2.setText("Payment Status:");

        cmbPaymentStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Category", "Pending", "Partial", "Paid", "Refunded" }));

        jButton3.setText("Filter");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Clear Filter");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Statistics");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        tblEnrollment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "آیدی شمولیت", "نام کورس", "نام متعلم", "ایمیل آدرس", "شماره تماس", "تاریخ شمولیت", "پرداخت شده", "مجموع فیس", "وضعیت پرداخت", "فیصدی حاضری", "تصدیقنامه", "یاداشت"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.Boolean.class, java.lang.String.class
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
        tblEnrollment.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblEnrollment.getTableHeader().setReorderingAllowed(false);
        tblEnrollment.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        jScrollPane1.setViewportView(tblEnrollment);
        tblEnrollment.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tblEnrollment.getColumnModel().getColumnCount() > 0) {
            tblEnrollment.getColumnModel().getColumn(0).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblEnrollment.getColumnModel().getColumn(1).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblEnrollment.getColumnModel().getColumn(2).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(2).setPreferredWidth(60);
            tblEnrollment.getColumnModel().getColumn(3).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(4).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(4).setPreferredWidth(40);
            tblEnrollment.getColumnModel().getColumn(5).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(5).setPreferredWidth(40);
            tblEnrollment.getColumnModel().getColumn(6).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(6).setPreferredWidth(20);
            tblEnrollment.getColumnModel().getColumn(7).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(7).setPreferredWidth(20);
            tblEnrollment.getColumnModel().getColumn(8).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(8).setPreferredWidth(30);
            tblEnrollment.getColumnModel().getColumn(9).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(9).setPreferredWidth(30);
            tblEnrollment.getColumnModel().getColumn(10).setResizable(false);
            tblEnrollment.getColumnModel().getColumn(10).setPreferredWidth(40);
            tblEnrollment.getColumnModel().getColumn(11).setResizable(false);
        }

        jPanel1.setBackground(new java.awt.Color(102, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Operation Section"));

        jLabel1.setText("Enrollment ID:");

        txtEnrollmentID.setEnabled(false);
        txtEnrollmentID.setFocusTraversalPolicyProvider(true);

        jLabel3.setText("Course Name:");

        jLabel4.setText("Student Name:");

        txtStdName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtStdNameKeyPressed(evt);
            }
        });

        jLabel5.setText("Email:");

        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
        });

        jLabel6.setText("Enrollment Date:");

        jLabel7.setText("Fee Paid:");

        cmbPaymentSt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "Partial", "Paid", "Refunded" }));
        cmbPaymentSt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbPaymentStMouseClicked(evt);
            }
        });

        jLabel8.setText("Payment Status:");

        txtTotalFee.setToolTipText("");
        txtTotalFee.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTotalFeeKeyPressed(evt);
            }
        });

        jLabel9.setText("Total Fee:");

        jLabel10.setText("Attendance:");

        txtAttendance.setToolTipText("فیصدی حاضری درج گردد");
        txtAttendance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAttendanceKeyPressed(evt);
            }
        });

        jLabel12.setText("Note:");

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

        jButton1.setText("Clear Controls");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cmbCourseName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbCourseNameMouseClicked(evt);
            }
        });

        txtFeePaid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFeePaidKeyPressed(evt);
            }
        });

        chkCertificate.setText("Certificate Issued");

        jLabel11.setText("Remainded:");

        txtRemained.setEditable(false);
        txtRemained.setToolTipText("");
        txtRemained.setEnabled(false);

        jLabel13.setText("Cell Phone:");

        txtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPhoneKeyPressed(evt);
            }
        });

        txtEnrollmentDate.setToolTipText("Date format is yyyy-MM-dd");
        txtEnrollmentDate.setDateFormatString("yyyy-MM-dd");

        btnCSV.setText("Export to CSV");
        btnCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnInsert)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete)
                                .addGap(34, 34, 34)
                                .addComponent(btnUpdate)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFeePaid, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtRemained, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEnrollmentID, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTotalFee, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbCourseName, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtStdName, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEnrollmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addGap(117, 117, 117)
                                .addComponent(btnCSV))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbPaymentSt, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCertificate)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNote)))
                .addGap(16, 16, 16))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtEnrollmentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(txtStdName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbCourseName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))
                            .addComponent(txtEnrollmentDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtFeePaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(txtTotalFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnInsert)
                            .addComponent(btnDelete)
                            .addComponent(btnUpdate)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRemained, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmbPaymentSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8)
                                .addComponent(jLabel10)
                                .addComponent(txtAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(chkCertificate)
                                .addComponent(jLabel12)
                                .addComponent(txtNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addComponent(btnCSV))))
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(84, 84, 84)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(cmbPaymentStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addGap(43, 43, 43)
                .addComponent(jButton5)
                .addGap(160, 160, 160))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPaymentStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jLabel2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Enrollment", jPanel2);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("تعداد شاگردان:");

        jLabel15.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel15.setText("احصائیه شاگردان مرکز آی تی سلوشن فراه");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("فیس پرداخت شده:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText("مجموع فیس:");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("فیس باقیمانده:");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("تعداد شاگردانیکه مقدار فیس شان باقی مانده است:");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("تعداد شاگردانیکه کامل فیس شان باقی مانده است:");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setText("تعداد شاگردانیکه فیس شان کامل پرداخت شده است:");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setText("تعداد شاگردانیکه فیس شان برگشت شده است:");

        lblStudentCount.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblStudentCount.setText("lblStudentCount");

        lblFeePaid.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblFeePaid.setText("lblFeePaid");

        lblFees.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblFees.setText("lblFees");

        lblRemainedFees.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblRemainedFees.setText("lblRemainedFees");

        lblPendingFees.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblPendingFees.setText("lblPendingFees");

        lblPartialFees.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblPartialFees.setText("lblPartialFees");

        lblPaidFees.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblPaidFees.setText("lblPaidFees");

        lblRefundedFees.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblRefundedFees.setText("lblRefundedFees");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("تعداد شاگردانیکه سرتیفیکت اخذ نموده اند:");

        lblCertificateIssued.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        lblCertificateIssued.setText("lblCertificateIssued");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(363, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStudentCount)
                    .addComponent(lblFeePaid)
                    .addComponent(lblFees)
                    .addComponent(lblPartialFees)
                    .addComponent(lblPendingFees)
                    .addComponent(lblRemainedFees)
                    .addComponent(lblPaidFees)
                    .addComponent(lblRefundedFees)
                    .addComponent(lblCertificateIssued))
                .addGap(46, 46, 46)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel19)
                    .addComponent(jLabel16)
                    .addComponent(jLabel14)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addGap(455, 455, 455))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(419, 419, 419)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel15)
                .addGap(52, 52, 52)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(lblStudentCount))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(lblFeePaid))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(lblFees))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(lblRemainedFees))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(lblPartialFees))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(lblPendingFees))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(lblPaidFees))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(lblRefundedFees))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(lblCertificateIssued))
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Statistics", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane4)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtStdNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStdNameKeyPressed
        txtStdName.setBackground(Color.WHITE);
        txtStdName.setBorder(UIManager.getBorder("TextField.border"));
        txtStdName.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtStdNameKeyPressed

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed
        txtEmail.setBackground(Color.WHITE);
        txtEmail.setBorder(UIManager.getBorder("TextField.border"));
        txtEmail.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtEmailKeyPressed

    private void cmbPaymentStMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbPaymentStMouseClicked
        cmbPaymentSt.setBorder(UIManager.getBorder("TextField.border"));
    }//GEN-LAST:event_cmbPaymentStMouseClicked

    private void txtTotalFeeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotalFeeKeyPressed
        txtTotalFee.setBackground(Color.WHITE);
        txtTotalFee.setBorder(UIManager.getBorder("TextField.border"));
        txtTotalFee.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtTotalFeeKeyPressed

    private void txtAttendanceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAttendanceKeyPressed
        txtAttendance.setBackground(Color.WHITE);
        txtAttendance.setBorder(UIManager.getBorder("TextField.border"));
        txtAttendance.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtAttendanceKeyPressed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
    try {
        enrollStudent();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage());
    }
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteCourse();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateData();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchData();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtPhoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneKeyPressed
        txtPhone.setBackground(Color.WHITE);
        txtPhone.setBorder(UIManager.getBorder("TextField.border"));
        txtPhone.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtPhoneKeyPressed

    private void cmbCourseNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbCourseNameMouseClicked
        cmbCourseName.setBackground(Color.WHITE);
        cmbCourseName.setBorder(UIManager.getBorder("TextField.border"));
        cmbCourseName.setForeground(Color.BLACK);
    }//GEN-LAST:event_cmbCourseNameMouseClicked

    private void txtFeePaidKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFeePaidKeyPressed
        txtFeePaid.setBackground(Color.WHITE);
        txtFeePaid.setBorder(UIManager.getBorder("TextField.border"));
        txtFeePaid.setForeground(Color.BLACK);
    }//GEN-LAST:event_txtFeePaidKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        clearControls();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        loadEnrollments();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        clearFilters();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        filterEnrollments();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        loadStatistics();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCSVActionPerformed
        exportTableToCSV();
    }//GEN-LAST:event_btnCSVActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCSV;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chkCertificate;
    private javax.swing.JComboBox<String> cmbCourseName;
    private javax.swing.JComboBox<String> cmbPaymentSt;
    private javax.swing.JComboBox<String> cmbPaymentStatusFilter;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JLabel lblCertificateIssued;
    private javax.swing.JLabel lblFeePaid;
    private javax.swing.JLabel lblFees;
    private javax.swing.JLabel lblPaidFees;
    private javax.swing.JLabel lblPartialFees;
    private javax.swing.JLabel lblPendingFees;
    private javax.swing.JLabel lblRefundedFees;
    private javax.swing.JLabel lblRemainedFees;
    private javax.swing.JLabel lblStudentCount;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JTable tblEnrollment;
    private javax.swing.JTextField txtAttendance;
    private javax.swing.JTextField txtEmail;
    private com.toedter.calendar.JDateChooser txtEnrollmentDate;
    private javax.swing.JTextField txtEnrollmentID;
    private javax.swing.JTextField txtFeePaid;
    private javax.swing.JTextField txtNote;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtRemained;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStdName;
    private javax.swing.JTextField txtTotalFee;
    // End of variables declaration//GEN-END:variables
}

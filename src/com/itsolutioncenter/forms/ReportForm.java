
package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.service.ReportService;
import com.itsolutioncenter.util.Formatter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class ReportForm extends javax.swing.JInternalFrame {
private DatabaseManager dbManager=DatabaseManager.getInstance();
private ReportService reportService=new ReportService();
//private DefaultTableModel model=new DefaultTableModel();
private String query;
    public ReportForm() {
        initComponents();
    }
    private void generateTabularReport() {
        String reportType = (String) cmbReportCategory.getSelectedItem();
       
        try {
            switch (reportType) {
                case "Dashboard Summary":
                    showDashboardSummary();
                    break;
                   
                case "Financial Monthly Report":
                    showFinancialReport();
                    break;
                   
                case "Course Enrollment":
                    showCourseReport();
                    break;
                   
                case "Project Progress":
                    showProjectReport();
                    break;
                   
                case "Intern Performance":
                    showInternReport();
                    break;
                   
                case "Employee Performance":
                    showEmployeeReport();
                    break;
                case "Recent Activities":
                    showRecentActivitiesReport();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Report type not implemented yet");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
        }
    }
    private void showDashboardSummary() throws SQLException {
        Map<String, Object> summary = reportService.getDashboardSummary();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Metric");
        model.addColumn("Value");
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            String metric = formatMetricName(entry.getKey());
            Object value = entry.getValue();
            // Format numeric values
            if (value instanceof Number) {
                if (entry.getKey().contains("income") || entry.getKey().contains("expense")) {
                    value = Formatter.formatCurrency((Number) value);
                }
            }
            model.addRow(new Object[]{metric, value});
        }
        tblReport.setModel(model);
        txtReport.setText("Dashboard Summary generated on " + new Date());
    }
    private void showFinancialReport() throws SQLException {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Map<String, Object>> report = reportService.getMonthlyFinancialReport(currentYear);
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Month");
        model.addColumn("Income");
        model.addColumn("Expense");
        model.addColumn("Profit");
        double totalIncome = 0, totalExpense = 0, totalProfit = 0;
        for (Map<String, Object> row : report) {
            double income = ((Number) row.get("income")).doubleValue();
            double expense = ((Number) row.get("expense")).doubleValue();
            double profit = ((Number) row.get("profit")).doubleValue();  
            model.addRow(new Object[]{
                row.get("month"),
                Formatter.formatCurrency(income),
                Formatter.formatCurrency(expense),
                Formatter.formatCurrency(profit)
            });
            totalIncome += income;
            totalExpense += expense;
            totalProfit += profit;
        }
        // Add totals row
        model.addRow(new Object[]{
            "TOTAL",
            Formatter.formatCurrency(totalIncome),
            Formatter.formatCurrency(totalExpense),
            Formatter.formatCurrency(totalProfit)
        });
        tblReport.setModel(model);
        txtReport.setText(String.format("Financial Report for %d. Total Profit: %s",
            currentYear, Formatter.formatCurrency(totalProfit)));
    }
     private void showCourseReport() throws SQLException {
        List<Map<String, Object>> report = reportService.getCourseEnrollmentReport();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Course Code");
        model.addColumn("Course Name");
        model.addColumn("Fee");
        model.addColumn("Start Date");
        model.addColumn("Status");
        model.addColumn("Students");
        model.addColumn("Fee Collected");
        model.addColumn("Avg Attendance");
       
        int totalStudents = 0;
        double totalFee = 0;
       
        for (Map<String, Object> row : report) {
            int students = ((Number) row.get("enrolled_students")).intValue();
            double feeCollected = ((Number) row.get("total_fee_collected")).doubleValue();
            model.addRow(new Object[]{
                row.get("course_code"),
                row.get("course_name"),
                Formatter.formatCurrency((Number) row.get("fee")),
                row.get("start_date"),
                row.get("status"),
                students,
                Formatter.formatCurrency(feeCollected),
                String.format("%.1f%%", row.get("avg_attendance"))
            });
            totalStudents += students;
            totalFee += feeCollected;
        }       
        tblReport.setModel(model);
        txtReport.setText(String.format("Course Report: %d courses, %d students, Total Revenue: %s",
            report.size(), totalStudents, Formatter.formatCurrency(totalFee)));
    }
      private void showProjectReport() throws SQLException {
        List<Map<String, Object>> report = reportService.getProjectDetailReport();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Project Code");
        model.addColumn("Project Name");
        model.addColumn("Project Manager");
        model.addColumn("Client");
        model.addColumn("Start Date");
        model.addColumn("End Date");
        model.addColumn("Budget");
        model.addColumn("Status");
        int activeProjects = 0;
        double totalBudget = 0;
        for (Map<String, Object> row : report) {
//            String status = (String) row.get("Status");
//            if (status.equals("Active") || status.equals("Planning")) {
//                activeProjects++;
//            }
//            double budget = ((Number) row.get("Budget")).doubleValue();
//            totalBudget += budget;
            model.addRow(new Object[]{
                row.get("Project Code"),
                row.get("Project Name"),
                row.get("Project Manager"),
                row.get("Client Name"),
                row.get("Start Date"),
                row.get("End Date"),
                //Formatter.formatCurrency((Number)
                        row.get("Budget"), 
                row.get("Status") 
                //status,
            });
        }
        tblReport.setModel(model);
        txtReport.setText(String.format("Project Report: %d total projects, %d active, Total Budget: %s",
            report.size(), activeProjects, Formatter.formatCurrency(totalBudget)));
    }
      private void showInternReport() throws SQLException {
        List<Map<String, Object>> report = reportService.getInternPerformanceReport();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Intern Name");
        model.addColumn("Email");
        model.addColumn("Department");
        model.addColumn("Start Date");
        model.addColumn("End Date");
        model.addColumn("Stipend");
         model.addColumn("Projects");
        model.addColumn("Performance");
        model.addColumn("Certificate");
       
        for (Map<String, Object> row : report) {
            model.addRow(new Object[]{
                row.get("full_name"),
                row.get("email"),
                row.get("department"),
                row.get("start_date"),
                row.get("end_date"),
                Formatter.formatCurrency((Number) row.get("stipend")),
                row.get("project_name"),
                String.format("%.1f/5", row.get("performance_rating")),
                (Boolean) row.get("certificate_issued") ? "Yes" : "No"
            });
        }
        tblReport.setModel(model);
        txtReport.setText(String.format("Intern Performance Report: %d interns", report.size()));
    }
   private void showEmployeeReport() throws SQLException {
        List<Map<String, Object>> report = reportService.getEmployeePerformanceReport();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Employee");
        model.addColumn("Role");
        model.addColumn("Hire Date");
        model.addColumn("Projects Managed");
        model.addColumn("Courses Taught");
        model.addColumn("Interns Supervised");
        for (Map<String, Object> row : report) {
            model.addRow(new Object[]{
                row.get("full_name"),
                row.get("role"),
                row.get("hire_date"),
                row.get("projects_managed"),
                row.get("courses_taught"),
                row.get("interns_supervised")
            });
        }
        tblReport.setModel(model);
        txtReport.setText(String.format("Employee Performance Report: %d employees", report.size()));
    }
   private void showRecentActivitiesReport() throws SQLException {
       int limit=Integer.parseInt(JOptionPane.showInternalInputDialog(this,"Enter recent activities limit","Recent Activities",
               JOptionPane.QUESTION_MESSAGE));
       List<Map<String, Object>> report = reportService.getRecentActivities(limit);
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Activity Type");
        model.addColumn("Description");
        model.addColumn("Activity Date");
        for (Map<String, Object> row : report) {
            model.addRow(new Object[]{
                row.get("activity_type"),
                row.get("description"),
                row.get("activity_date"),
                row.get("projects_managed")
            });
        }
        tblReport.setModel(model);
       txtReport.setText(String.format("Number of Recent Activities: %d Activities", report.size()));
    }
   private void exportReport() {
        String reportType = (String) cmbReportCategory.getSelectedItem();
        String tableName = getTableNameForReport(reportType);
       
        if (tableName == null) {
            JOptionPane.showMessageDialog(this, "Export not available for this report type");
            return;
        }
       
        try {
            String csvData = reportService.exportToCSV(tableName);
           
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(reportType + ".csv"));
           
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                java.nio.file.Files.write(file.toPath(), csvData.getBytes());
                JOptionPane.showMessageDialog(this, "Report exported successfully to: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage());
        }
    }
   
    private String getTableNameForReport(String reportType) {
        switch (reportType) {
            case "Financial Monthly Report": return "income_transactions";
            case "Course Enrollment": return "courses";
            case "Support Performance": return "support_tickets";
            case "Project Progress": return "development_projects";
            case "Intern Performance": return "interns";
            case "Employee Performance": return "users";
            default: return null;
        }
    }
    private String formatMetricName(String metric) {
        // Convert camelCase to readable text
        return metric.replaceAll("([A-Z])", " $1")
                    .replaceAll("_", " ")
                    .trim()
                    .toLowerCase()
                    .replaceFirst(".", String.valueOf(Character.toUpperCase(metric.charAt(0))));
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmbReportCategory = new javax.swing.JComboBox<>();
        btnTabReport = new javax.swing.JButton();
        btnCsvReport = new javax.swing.JButton();
        btnJasperReport = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();
        txtReport = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setTitle("Reports Form");

        jLabel1.setText("Report Category:");

        cmbReportCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Category --", "Dashboard Summary", "Financial Monthly Report", "Income by Source", "Expenses by Category", "Course Enrollment", "Project Progress", "Project Profitability", "Intern Performance", "Client Engagement", "Employee Performance", "Recent Activities" }));

        btnTabReport.setText("Generate Tabular Report");
        btnTabReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTabReportActionPerformed(evt);
            }
        });

        btnCsvReport.setText("Generate CSV Report");
        btnCsvReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCsvReportActionPerformed(evt);
            }
        });

        btnJasperReport.setText("Generate Jasper Report");

        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tblReport.setColumnSelectionAllowed(true);
        tblReport.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblReport);
        tblReport.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbReportCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTabReport)
                .addGap(33, 33, 33)
                .addComponent(btnCsvReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnJasperReport)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 781, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtReport, javax.swing.GroupLayout.PREFERRED_SIZE, 781, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbReportCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTabReport)
                    .addComponent(btnCsvReport)
                    .addComponent(btnJasperReport))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtReport, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTabReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTabReportActionPerformed
        generateTabularReport();
    }//GEN-LAST:event_btnTabReportActionPerformed

    private void btnCsvReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCsvReportActionPerformed
        exportReport();
    }//GEN-LAST:event_btnCsvReportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCsvReport;
    private javax.swing.JButton btnJasperReport;
    private javax.swing.JButton btnTabReport;
    private javax.swing.JComboBox<String> cmbReportCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReport;
    private javax.swing.JTextField txtReport;
    // End of variables declaration//GEN-END:variables
}

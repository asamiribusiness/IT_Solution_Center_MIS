package com.itsolutioncenter.service;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.util.Formatter;
import java.sql.SQLException;
import java.util.*;

public class ReportService {
    private DatabaseManager db = DatabaseManager.getInstance();
   
    // ============ FINANCIAL REPORTS ============
   
    /**
     * Get income vs expense report by month
     */
    public List<Map<String, Object>> getMonthlyFinancialReport(int year) throws SQLException {
        String sql = "SELECT " +
                     "  DATE_FORMAT(transaction_date, '%Y-%m') as month, " +
                     "  COALESCE(SUM(CASE WHEN t.type = 'Income' THEN amount END), 0) as income, " +
                     "  COALESCE(SUM(CASE WHEN t.type = 'Expense' THEN amount END), 0) as expense, " +
                     "  COALESCE(SUM(CASE WHEN t.type = 'Income' THEN amount END), 0) - " +
                     "  COALESCE(SUM(CASE WHEN t.type = 'Expense' THEN amount END), 0) as profit " +
                     "FROM (" +
                     "  SELECT transaction_date, amount, 'Income' as type FROM income_transactions WHERE status = 'received' " +
                     "  UNION ALL " +
                     "  SELECT transaction_date, amount, 'Expense' as type FROM expense_transactions WHERE status = 'paid'" +
                     ") t " +
                     "WHERE YEAR(transaction_date) = ? " +
                     "GROUP BY DATE_FORMAT(transaction_date, '%Y-%m') " +
                     "ORDER BY month";
       
        return db.query(sql, year);
    }
   
    /**
     * Get income by source type
     */
    public List<Map<String, Object>> getIncomeBySource(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT " +
                     "  source_type, " +
                     "  COUNT(*) as transaction_count, " +
                     "  SUM(amount) as total_amount, " +
                     "  AVG(amount) as average_amount " +
                     "FROM income_transactions " +
                     "WHERE transaction_date BETWEEN ? AND ? " +
                     "  AND status = 'received' " +
                     "GROUP BY source_type " +
                     "ORDER BY total_amount DESC";
       
        return db.query(sql, startDate, endDate);
    }
   
    /**
     * Get expenses by category
     */
    public List<Map<String, Object>> getExpensesByCategory(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT " +
                     "  category, " +
                     "  COUNT(*) as transaction_count, " +
                     "  SUM(amount) as total_amount, " +
                     "  AVG(amount) as average_amount " +
                     "FROM expense_transactions " +
                     "WHERE transaction_date BETWEEN ? AND ? " +
                     "  AND status = 'paid' " +
                     "GROUP BY category " +
                     "ORDER BY total_amount DESC";
       
        return db.query(sql, startDate, endDate);
    }
   
    /**
     * Get financial summary for dashboard
     */
    public Map<String, Object> getFinancialDashboard() throws SQLException {
        String sql = "SELECT " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE MONTH(transaction_date) = MONTH(CURRENT_DATE()) " +
                     "   AND YEAR(transaction_date) = YEAR(CURRENT_DATE()) " +
                     "   AND status = 'received') as monthly_income, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM expense_transactions " +
                     "   WHERE MONTH(transaction_date) = MONTH(CURRENT_DATE()) " +
                     "   AND YEAR(transaction_date) = YEAR(CURRENT_DATE()) " +
                     "   AND status = 'paid') as monthly_expense, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE YEAR(transaction_date) = YEAR(CURRENT_DATE()) " +
                     "   AND status = 'received') as yearly_income, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM expense_transactions " +
                     "   WHERE YEAR(transaction_date) = YEAR(CURRENT_DATE()) " +
                     "   AND status = 'paid') as yearly_expense, " +
                     "  (SELECT COUNT(*) FROM income_transactions " +
                     "   WHERE DATE(transaction_date) = CURRENT_DATE() " +
                     "   AND status = 'received') as today_income_count, " +
                     "  (SELECT COUNT(*) FROM expense_transactions " +
                     "   WHERE DATE(transaction_date) = CURRENT_DATE() " +
                     "   AND status = 'paid') as today_expense_count";
       
        List<Map<String, Object>> results = db.query(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
   
    // ============ COURSE REPORTS ============
   
    /**
     * Get course enrollment statistics
     */
    public List<Map<String, Object>> getCourseEnrollmentReport() throws SQLException {
        String sql = "SELECT c.course_code, c.course_name, c.fee, " +
                     "c.start_date, c.status, COUNT(e.enrollment_id) as enrolled_students, " +
                     "SUM(e.fee_paid) as total_fee_collected, " +
                     "AVG(e.attendance_percentage) as avg_attendance " +
                     "FROM courses c " +
                     "LEFT JOIN course_enrollments e ON c.course_id = e.course_id " +
                     "GROUP BY c.course_id, c.course_code, c.course_name, c.fee, c.start_date, c.status " +
                     "ORDER BY c.start_date DESC";
        return db.query(sql);
    }
  
    /**
     * Get course revenue by month
     */
    public List<Map<String, Object>> getCourseRevenueReport(int year) throws SQLException {
        String sql = "SELECT " +
                     "  DATE_FORMAT(i.transaction_date, '%Y-%m') as month, " +
                     "  c.course_name, " +
                     "  COUNT(DISTINCT i.income_id) as enrollments, " +
                     "  SUM(i.amount) as revenue " +
                     "FROM income_transactions i " +
                     "JOIN courses c ON i.source_id = c.course_id " +
                     "WHERE i.source_type = 'Course Fee' " +
                     "  AND YEAR(i.transaction_date) = ? " +
                     "  AND i.status = 'received' " +
                     "GROUP BY DATE_FORMAT(i.transaction_date, '%Y-%m'), c.course_id, c.course_name " +
                     "ORDER BY month, revenue DESC";
       
        return db.query(sql, year);
    }
   
    // ============ PROJECT REPORTS ============
   
    /**
     * Get project progress report
     */
    public List<Map<String, Object>> getProjectDetailReport() throws SQLException {
        String sql = "SELECT projects.project_code as 'Project Code'," +
        "projects.project_name As 'Project Name',users.full_name as 'Project Manager'," +
        "clients.client_name as 'Client Name',projects.start_date as 'Start Date'," +
        "projects.end_date as 'End Date',projects.budget 'Budget'," +
        "projects.`status` as 'Status' FROM users " +
        "INNER JOIN projects ON users.user_id = projects.project_manager " +
        "INNER JOIN clients ON clients.client_id = projects.client_id";
       
        return db.query(sql);
    }
   
    /**
     * Get project profitability report
     */
    public List<Map<String, Object>> getProjectProfitabilityReport() throws SQLException {
        String sql = "SELECT " +
                     "  p.project_id, " +
                     "  p.project_code, " +
                     "  p.project_name, " +
                     "  p.budget, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE source_type = 'development_project' AND source_id = p.project_id) as revenue, " +
                     "  (SELECT COALESCE(SUM(pt.hours_worked * pt.hourly_rate), 0) FROM project_team pt " +
                     "   WHERE pt.project_id = p.project_id) as labor_cost, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM expense_transactions " +
                     "   WHERE category IN ('software', 'equipment') AND description LIKE CONCAT('%Project ', p.project_code, '%')) as material_cost " +
                     "FROM development_projects p " +
                     "WHERE p.status = 'completed' " +
                     "ORDER BY p.project_id";
       
        return db.query(sql);
    }
   
    // ============ INTERN REPORTS ============
   
    /**
     * Get intern performance report
     */
    public List<Map<String, Object>> getInternPerformanceReport() throws SQLException {
        String sql = "SELECT " +
        "  users.full_name," +
        "  users.email," +
        "  interns.department," +
        "  interns.start_date," +
        "  interns.end_date," +
        "  interns.stipend," +
        "  projects.project_name," +
        "  interns.performance_rating," +
        "  interns.certificate_issued " +
        "FROM " +
        "  users " +
        "  INNER JOIN interns ON interns.user_id = users.user_id " +
        "  INNER JOIN projects ON projects.project_manager = users.user_id " +
        "ORDER BY " +
        "  performance_rating";
       
        return db.query(sql);
    }
   
    /**
     * Get intern application statistics
     */
    public Map<String, Object> getInternApplicationStats() throws SQLException {
        String sql = "SELECT " +
                     "  applied_for, " +
                     "  COUNT(*) as total_applications, " +
                     "  SUM(CASE WHEN status = 'received' THEN 1 ELSE 0 END) as new_applications, " +
                     "  SUM(CASE WHEN status = 'interviewed' THEN 1 ELSE 0 END) as interviewed, " +
                     "  SUM(CASE WHEN status = 'accepted' THEN 1 ELSE 0 END) as accepted, " +
                     "  SUM(CASE WHEN status = 'hired' THEN 1 ELSE 0 END) as hired, " +
                     "  ROUND(SUM(CASE WHEN status = 'hired' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as hire_rate " +
                     "FROM intern_applications " +
                     "GROUP BY applied_for " +
                     "ORDER BY total_applications DESC";
       
        List<Map<String, Object>> results = db.query(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
   
    // ============ CLIENT REPORTS ============
   
    /**
     * Get client engagement report
     */
    public List<Map<String, Object>> getClientEngagementReport() throws SQLException {
        String sql = "SELECT " +
                     "  c.client_id, " +
                     "  c.client_name, " +
                     "  c.client_type, " +
                     "  c.registration_date, " +
                     "  COUNT(DISTINCT p.project_id) as total_projects, " +
                     "  COUNT(DISTINCT t.ticket_id) as total_tickets, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE source_type = 'development_project' AND " +
                     "   source_id IN (SELECT project_id FROM development_projects WHERE client_id = c.client_id)) as project_revenue, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE source_type = 'support_service' AND " +
                     "   source_id IN (SELECT ticket_id FROM support_tickets WHERE client_id = c.client_id)) as support_revenue " +
                     "FROM clients c " +
                     "LEFT JOIN development_projects p ON c.client_id = p.client_id " +
                     "LEFT JOIN support_tickets t ON c.client_id = t.client_id " +
                     "WHERE c.status = 'active' " +
                     "GROUP BY c.client_id, c.client_name, c.client_type, c.registration_date " +
                     "ORDER BY (project_revenue + support_revenue) DESC";
       
        return db.query(sql);
    }
   
    // ============ EMPLOYEE PERFORMANCE ============
   
    /**
     * Get employee performance report
     */
    public List<Map<String, Object>> getEmployeePerformanceReport() throws SQLException {
        String sql = "SELECT " +
            "users.full_name,  users.role, users.hire_date," +
            "COUNT(DISTINCT projects.project_id) AS projects_managed," +
            "COUNT(DISTINCT courses.course_id) AS courses_taught," +
            "COUNT(DISTINCT interns.intern_id) AS interns_supervised " +
            "FROM users " +
            "LEFT JOIN projects ON users.user_id = projects.project_manager " +
            "LEFT JOIN courses ON users.user_id = courses.instructor_id " +
            "LEFT JOIN interns ON users.user_id = interns.supervisor_id " +
            "WHERE " +
            "users.role IN ('employee', 'manager') " +
            "AND users.is_active = TRUE " +
            "GROUP BY users.user_id,users.full_name,users.role,users.hire_date " +
            "ORDER BY users.full_name ASC";
       
        return db.query(sql);
    }
   // ============ DASHBOARD SUMMARY ============
   
    /**
     * Get dashboard summary statistics
     */
    public Map<String, Object> getDashboardSummary() throws SQLException {
        String sql = "SELECT " +
                     "  (SELECT COUNT(*) FROM users WHERE is_active = true) as total_users, " +
                     "  (SELECT COUNT(*) FROM clients WHERE status = 'active') as total_clients, " +
                     "  (SELECT COUNT(*) FROM courses WHERE status IN ('ongoing', 'upcoming')) as active_courses, " +
                     "  (SELECT COUNT(*) FROM projects WHERE status IN ('planning', 'Ongoing', 'completed')) as active_projects, " +
                     "  (SELECT COUNT(*) FROM interns WHERE end_date >= CURRENT_DATE() OR end_date IS NULL) as active_interns, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE MONTH(transaction_date) = MONTH(CURRENT_DATE()) AND status = 'received') as monthly_income, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM expense_transactions " +
                     "   WHERE MONTH(transaction_date) = MONTH(CURRENT_DATE()) AND status = 'paid') as monthly_expense";
        List<Map<String, Object>> results = db.query(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
    /**
     * Get recent activities for dashboard
     */
    public List<Map<String, Object>> getRecentActivities(int limit) throws SQLException {
        String sql = "SELECT " +
                     "  'New User' as activity_type, " +
                     "  CONCAT('User registered: ', full_name) as description, " +
                     "  created_at as activity_date " +
                     "FROM users " +
                     "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                     "UNION ALL " +
                     "SELECT " +
                     "  'New Enrollment' as activity_type, " +
                     "  CONCAT('Student enrolled: ', student_name) as description, " +
                     "  enrollment_date as activity_date " +
                     "FROM course_enrollments " +
                     "WHERE enrollment_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                     "UNION ALL " +
                     "SELECT " +
                     "  'New Income' as activity_type, " +
                     "  CONCAT('Payment received: AFN', amount, ' from ', payer_name) as description, " +
                     "  transaction_date as activity_date " +
                     "FROM income_transactions " +
                     "WHERE transaction_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                     "  AND status = 'received' " +
                     "ORDER BY activity_date DESC " +
                     "LIMIT ?";
       
        return db.query(sql, limit);
    }
   
    // ============ EXPORT DATA METHODS ============
   
    /**
     * Export data to CSV format (simplified)
     */
    public String exportToCSV(String tableName) throws SQLException {
        List<Map<String, Object>> data = db.selectAll(tableName);
        if (data.isEmpty()) return "";
       
        StringBuilder csv = new StringBuilder();
       
        // Add headers
        Map<String, Object> firstRow = data.get(0);
        for (String column : firstRow.keySet()) {
            csv.append(column).append(",");
        }
        csv.deleteCharAt(csv.length() - 1); // Remove last comma
        csv.append("\n");
       
        // Add data rows
        for (Map<String, Object> row : data) {
            for (Object value : row.values()) {
                String strValue = value != null ? value.toString() : "";
                // Escape commas and quotes
                if (strValue.contains(",") || strValue.contains("\"")) {
                    strValue = "\"" + strValue.replace("\"", "\"\"") + "\"";
                }
                csv.append(strValue).append(",");
            }
            csv.deleteCharAt(csv.length() - 1);
            csv.append("\n");
        }
       
        return csv.toString();
    }
   
    /**
     * Get formatted report for printing
     */
    public List<Map<String, Object>> getFormattedReport(String reportType,
                                                       Map<String, Object> params) throws SQLException {
        List<Map<String, Object>> report = new ArrayList<>();
       
        switch (reportType.toLowerCase()) {
            case "financial_summary":
                report = getMonthlyFinancialReport((int) params.get("year"));
                // Format amounts
                for (Map<String, Object> row : report) {
                    row.put("income_formatted", Formatter.formatCurrency((Number) row.get("income")));
                    row.put("expense_formatted", Formatter.formatCurrency((Number) row.get("expense")));
                    row.put("profit_formatted", Formatter.formatCurrency((Number) row.get("profit")));
                }
                break;
               
            case "course_enrollment":
                report = getCourseEnrollmentReport();
                for (Map<String, Object> row : report) {
                    row.put("fee_formatted", Formatter.formatCurrency((Number) row.get("fee")));
                    row.put("total_fee_collected_formatted", Formatter.formatCurrency((Number) row.get("total_fee_collected")));
                }
                break;
               
//            case "support_performance":
//                Date startDate = (Date) params.get("startDate");
//                Date endDate = (Date) params.get("endDate");
//                report = getSupportPerformanceReport(startDate, endDate);
//                break;
               
            case "project_progress":
                report = getProjectDetailReport();
                for (Map<String, Object> row : report) {
                    row.put("budget_formatted", Formatter.formatCurrency((Number) row.get("Budget")));
                    row.put("revenue_received_formatted", Formatter.formatCurrency((Number) row.get("revenue_received")));
                }
                break;
        }
       
        return report;
    }
}
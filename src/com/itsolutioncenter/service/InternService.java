package com.itsolutioncenter.service;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.util.DateUtil;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;


public class InternService {
    private DatabaseManager db = DatabaseManager.getInstance();
   
    // ============ INTERN APPLICATIONS ============
   
    /**
     * Submit intern application
     */
    public int submitApplication(String applicant,String email,String phone,String university, String course,
           String qualification, String path,String appliedFor,Date date,String status, LocalDateTime interviewDate,
           String note,String skills) throws SQLException {
       
        Map<String, Object> application = new HashMap<>();
        application.put("applicant_name", applicant);
        application.put("email", email);
        application.put("phone", phone);
        application.put("university", university);
        application.put("course", course);
        application.put("qualification", qualification);
        application.put("resume_path", path);
        application.put("applied_for", appliedFor);
        application.put("applicatoin_date",date);
        application.put("status", status);
        application.put("interview_date",interviewDate);
        application.put("interview_notes",note);
        application.put("skills", skills);
       
        return db.insert("intern_applications", application);
    }
   public int updateIntern(int internID,String applicant,String email,String phone,String university, String course,
           String qualification, String path,String appliedFor,Date date,String status, LocalDateTime interviewDate,String note,String skills)
{
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("application_id", internID);
        updateData.put("applicant_name", applicant);
        updateData.put("email", email);
        updateData.put("phone",phone);
        updateData.put("university",university);
        updateData.put("course", course);
        updateData.put("qualification", qualification);
        updateData.put("resume_path",path);
        updateData.put("applied_for", appliedFor);
        updateData.put("application_date", date);
        updateData.put("status",status);
        updateData.put("interview_date", interviewDate);     
        updateData.put("interview_notes",note);
        updateData.put("skills", skills); 
       int rows=db.update("intern_applications", updateData, "application_id = ?", internID);

       return rows;
} 
    /**
     * Get all applications
     */
    public List<Map<String, Object>> getAllApplications() throws SQLException {
        return db.selectAll("intern_applications");
    }
   
    /**
     * Get applications by status
     */
    public List<Map<String, Object>> getApplicationsByStatus(String status) throws SQLException {
        return db.select("intern_applications", "status = ? ORDER BY application_date DESC", status);
    }
   
    /**
     * Get pending applications (received/reviewed)
     */
    public List<Map<String, Object>> getPendingApplications() throws SQLException {
        return db.select("intern_applications",
                        "status IN ('received', 'reviewed') ORDER BY application_date");
    }
   
    /**
     * Update application status
     */
    public boolean updateApplicationStatus(int applicationId, String status,
                                          String notes) throws SQLException {
       
        Map<String, Object> update = new HashMap<>();
        update.put("status", status);
       
        if (notes != null && !notes.trim().isEmpty()) {
            update.put("interview_notes", notes);
        }
       
        if (status.equals("interviewed")) {
            update.put("interview_date", DateUtil.getCurrentDate());
        }
       
        int rows = db.update("intern_applications", update,
                            "application_id = ?", applicationId);
        return rows > 0;
    }
   
    /**
     * Schedule interview
     */
    public boolean scheduleInterview(int applicationId, Date interviewDate,
                                    String notes) throws SQLException {
       
        Map<String, Object> update = new HashMap<>();
        update.put("status", "interviewed");
        update.put("interview_date", interviewDate);
       
        if (notes != null && !notes.trim().isEmpty()) {
            update.put("interview_notes", notes);
        }
       
        int rows = db.update("intern_applications", update,
                            "application_id = ?", applicationId);
        return rows > 0;
    }
   
    // ============ HIRED INTERNS ============
   
    /**
     * Hire an intern (convert application to intern)
     */
    public int hireIntern(int applicationId, int userId, String department,
                         int supervisorId, double stipend, Date startDate,
                         Date endDate) throws SQLException {
       
        // Get application details
        Map<String, Object> application = db.selectOne("intern_applications",
                                                      "application_id = ?", applicationId);
        if (application == null) {
            throw new SQLException("Application not found");
        }
       
        Map<String, Object> intern = new HashMap<>();
        intern.put("application_id", applicationId);
        intern.put("user_id", userId);
        intern.put("department", department);
        intern.put("supervisor_id", supervisorId);
        intern.put("stipend", stipend);
        intern.put("start_date", startDate);
        intern.put("end_date", endDate);
        intern.put("certificate_issued", false);
       
        // Insert into interns table
        int internId = db.insert("interns", intern);
       
        // Update application status
        if (internId > 0) {
            db.update("intern_applications",
                     Collections.singletonMap("status", "hired"),
                     "application_id = ?", applicationId);
        }
       
        return internId;
    }
   
    /**
     * Get all interns
     */
    public List<Map<String, Object>> getAllInterns() throws SQLException {
        String sql = "SELECT i.*, u.full_name, u.email, u.phone, " +
                     "       ia.applicant_name, ia.university " +
                     "FROM interns i " +
                     "JOIN users u ON i.user_id = u.user_id " +
                     "JOIN intern_applications ia ON i.application_id = ia.application_id " +
                     "ORDER BY i.start_date DESC";
        return db.query(sql);
    }
   
    /**
     * Get active interns
     */
    public List<Map<String, Object>> getActiveInterns() throws SQLException {
        String currentDate = DateUtil.getCurrentDateString();
        String sql = "SELECT i.*, u.full_name, u.email " +
                     "FROM interns i " +
                     "JOIN users u ON i.user_id = u.user_id " +
                     "WHERE i.end_date >= ? OR i.end_date IS NULL " +
                     "ORDER BY i.start_date";
        return db.query(sql, currentDate);
    }
   
    /**
     * Update intern performance
     */
    public boolean updateInternPerformance(int internId, double performanceRating,
                                          String notes) throws SQLException {
       
        Map<String, Object> update = new HashMap<>();
        update.put("performance_rating", performanceRating);
       
        if (notes != null && !notes.trim().isEmpty()) {
            update.put("notes", notes);
        }
       
        int rows = db.update("interns", update, "intern_id = ?", internId);
        return rows > 0;
    }
   
    /**
     * Issue certificate to intern
     */
    public boolean issueCertificate(int internId) throws SQLException {
        Map<String, Object> update = Collections.singletonMap("certificate_issued", true);
        int rows = db.update("interns", update, "intern_id = ?", internId);
        return rows > 0;
    }
   
    /**
     * Get intern statistics
     */
    public Map<String, Object> getInternStatistics() throws SQLException {
        String sql = "SELECT " +
                     "  COUNT(DISTINCT application_id) as total_applications, " +
                     "  SUM(CASE WHEN status = 'received' THEN 1 ELSE 0 END) as new_applications, " +
                     "  SUM(CASE WHEN status = 'interviewed' THEN 1 ELSE 0 END) as interviewed, " +
                     "  SUM(CASE WHEN status = 'accepted' THEN 1 ELSE 0 END) as accepted, " +
                     "  SUM(CASE WHEN status = 'hired' THEN 1 ELSE 0 END) as hired, " +
                     "  (SELECT COUNT(*) FROM interns) as total_interns, " +
                     "  (SELECT COUNT(*) FROM interns WHERE certificate_issued = true) as certified " +
                     "FROM intern_applications";
       
        List<Map<String, Object>> results = db.query(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
}
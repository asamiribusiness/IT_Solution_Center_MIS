package com.itsolutioncenter.service;

import com.itsolutioncenter.dao.DatabaseManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseService {
    private DatabaseManager dbManager = DatabaseManager.getInstance();
   
    public int addCourse(String code, String name, String description, int duration,double fee,
                        String category,int instructor,Date startDate,Date endDate,String schedule,String status) {
       try
       {
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("course_code", code);
        courseData.put("course_name", name);
        courseData.put("description",description);
        courseData.put("duration_hours", duration);
        courseData.put("fee", fee);
        courseData.put("category", category);
        courseData.put("instructor_id", instructor);
        courseData.put("start_date",startDate);
        courseData.put("end_date", endDate);
        courseData.put("schedule", schedule);
        courseData.put("status", "upcoming");
        return dbManager.insert("courses", courseData);
       } catch (SQLException e) {
            return -1;
        }
    }
   public int updateCourse(int courseID,String code, String name, String description, int duration,double fee,
                        String category,int instructor,Date startDate,Date endDate,String schedule,String status) {
       Map<String, Object> courseData = new HashMap<>();
       courseData.put("course_code", code);
       courseData.put("course_name", name);
       courseData.put("description",description);
       courseData.put("duration_hours", duration);
       courseData.put("fee", fee);
       courseData.put("category", category);
       courseData.put("instructor_id", instructor);
       courseData.put("start_date",startDate);
       courseData.put("end_date", endDate);
       courseData.put("schedule", schedule);
       courseData.put("status", status);
       return dbManager.update("courses", courseData, "course_id = ?", courseID);
    }
    public List<Map<String, Object>> getAllCourses() throws SQLException {
        return dbManager.getActiveCourses();
    } 
     public List<Map<String, Object>> getActiveCourses() throws SQLException {
        return dbManager.getActiveCourses();//select("courses", "status IN ('ongoing', 'upcoming') ORDER BY start_date");
    }
    public int enrollStudent(int courseId, String studentName, String studentEmail,
                            String phone,Date enrollmentDate, double fee, double totalFee, String paymentStatus, 
                            double attendance, boolean certificateIssued, String note) throws SQLException {
       
        Map<String, Object> enrollment = new HashMap<>();
        enrollment.put("course_id", courseId);
        enrollment.put("student_name", studentName);
        enrollment.put("student_email", studentEmail);
        enrollment.put("student_phone", phone);
        enrollment.put("enrollment_date", enrollmentDate);
        enrollment.put("fee_paid", fee);
        enrollment.put("total_fee", totalFee);
        enrollment.put("payment_status", paymentStatus);
        enrollment.put("attendance_percentage",attendance);
        enrollment.put("certificate_issued", certificateIssued);
        enrollment.put("notes",note);
        return dbManager.insert("course_enrollments", enrollment);
    }
    public int updateEnrollment(int enrollmentID,int courseId, String studentName, String studentEmail,
                            String phone,Date enrollmentDate, double fee, double totalFee, String paymentStatus, 
                            double attendance, boolean certificateIssued, String note)
    {
        Map<String, Object> updateEnrollment = new HashMap<>();
        updateEnrollment.put("course_id", courseId);
        updateEnrollment.put("student_name", studentName);
        updateEnrollment.put("student_email", studentEmail);
        updateEnrollment.put("student_phone", phone);
        updateEnrollment.put("enrollment_date", enrollmentDate);
        updateEnrollment.put("fee_paid", fee);
        updateEnrollment.put("total_fee", totalFee);
        updateEnrollment.put("payment_status", paymentStatus);
        updateEnrollment.put("attendance_percentage",attendance);
        updateEnrollment.put("certificate_issued", certificateIssued);
        updateEnrollment.put("notes",note);
        return dbManager.update("course_enrollments", updateEnrollment, "enrollment_id = ?", enrollmentID);
    }
       public Map<String, Object> getCourseStatistics() {
        try
        {
        String sql = "SELECT COUNT(student_name) as No_of_Students,SUM(fee_paid) as Fee_Paid, SUM(total_fee) as Total_Fee, "
        + "SUM(total_fee)-SUM(fee_paid) as Remained_Payments,SUM(IF(payment_status='Pending',1,0)) AS Pending_Payments, "
        + "SUM(IF(payment_status='Partial',1,0)) as Partial_Payments,SUM(IF(payment_status='Paid',1,0)) as Paid_Payments, "
        + "SUM(IF(payment_status='Refunded',1,0)) as Refunded_Payments, SUM(IF(certificate_issued=true,1,0)) as Certificate_Issued"
                + " FROM course_enrollments";
        List<Map<String, Object>> results = dbManager.select(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
        }catch(SQLException e)
        {
            e.getMessage();
            return null;
        }
    }
}
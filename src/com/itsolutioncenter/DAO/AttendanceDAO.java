
package com.itsolutioncenter.DAO;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.dao.DatabaseManager;
import java.sql.SQLException;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class AttendanceDAO {
    //private final DatabaseManager dbManager; // Your existing DatabaseManager
    DatabaseManager dbManager=DatabaseManager.getInstance();
    public AttendanceDAO() {
       
    }

    // Mark attendance for a single student
    public boolean markAttendance(int studentId, int courseId, Date date,
                                 String status, String checkInTime, String remarks,
                                 String recordedBy) {
        String sql = "INSERT INTO attendance (enrollment_id, course_id, attendance_date, " +
                    "status, check_in_time, remarks, recorded_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE status = ?, check_in_time = ?, remarks = ?";
       
        try {
            int result = dbManager.executeUpdate(sql,
                studentId, courseId, new java.sql.Date(date.getTime()),
                status, checkInTime, remarks, recordedBy,
                status, checkInTime, remarks
            );
            return result > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error saving attendance: " + e.getMessage());
            return false;
        }
    }

    // Get today's attendance for a course
    public List<Object[]> getTodaysAttendance(int courseId) {
        List<Object[]> attendanceList = new ArrayList<>();
//        String sql = "SELECT s.student_id, s.student_reg_no, s.full_name, " +
//                    "COALESCE(a.status, 'Absent') as status, " +
//                    "TIME_FORMAT(a.check_in_time, '%H:%i') as check_in, a.remarks " +
//                    "FROM course_enrollments s " +
//                    "LEFT JOIN attendance a ON s.student_id = a.student_id " +
//                    "AND a.course_id = ? AND a.attendance_date = CURDATE() " +
//                    "WHERE s.course_id = ? AND s.status = 'Active' " +
//                    "ORDER BY s.full_name";
         String sql="SELECT s.enrollment_id, s.student_name,COALESCE(a.status, 'Absent') as status," +
"                    TIME_FORMAT(a.check_in_time, '%H:%i') as check_in, a.remarks FROM course_enrollments s" +
"                    LEFT JOIN attendance a ON s.enrollment_id = a.enrollment_id" +
"                    AND a.course_id = ? AND a.attendance_date = CURDATE()" +
"                    WHERE s.course_id = ? AND s.status = 'Active' ORDER BY s.student_name";
        try {
            List<Map<String, Object>> results = dbManager.query(sql, courseId, courseId);
            for (Map<String, Object> row : results) {
                Object[] rowData = {
                    row.get("enrollment_id"),
                    row.get("student_name"),
                    row.get("status"),
                    row.get("check_in"),
                    row.get("remarks")
                };
                attendanceList.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }

    // Get all active courses
    public List<Object[]> getActiveCourses() {
        List<Object[]> courses = new ArrayList<>();
        String sql = "SELECT course_id, CONCAT(course_code, ' - ', course_name) as course_name " +
                    "FROM courses WHERE status = 'Ongoing' ORDER BY course_name";
       
        try {
            List<Map<String, Object>> results = dbManager.query(sql);
            for (Map<String, Object> row : results) {
                Object[] course = {
                    row.get("course_id"),
                    row.get("course_name")
                };
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
}
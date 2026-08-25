package com.itsolutioncenter.service;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.util.DateUtil;
import com.itsolutioncenter.util.Validator;
import java.sql.SQLException;
import java.util.*;

public class ProjectService {
    private DatabaseManager db = DatabaseManager.getInstance();
   
    // ============ PROJECT MANAGEMENT ============
   
    /**
     * Create new project
     */
    public int createProject(String code, String name, int clientId, String description,
                            String projectType, Date startDate, Date deadline,
                            double budget, int managerId) throws SQLException {
       
        Validator.validateRequired(code, "Project Code");
        Validator.validateRequired(name, "Project Name");
        Validator.validateRequired(projectType, "Project Type");
       
        Map<String, Object> project = new HashMap<>();
        project.put("project_code", code);
        project.put("project_name", name);
        project.put("client_id", clientId);
        project.put("description", description);
        project.put("project_type", projectType);
        project.put("start_date", startDate);
        project.put("deadline", deadline);
        project.put("budget", budget);
        project.put("manager_id", managerId);
        project.put("status", "proposal");
        project.put("completion_percentage", 0);
       
        return db.insert("development_projects", project);
    }
   
    /**
     * Get all projects
     */
    public List<Map<String, Object>> getAllProjects() throws SQLException {
        String sql = "SELECT p.*, c.client_name, u.full_name as manager_name " +
                     "FROM development_projects p " +
                     "LEFT JOIN clients c ON p.client_id = c.client_id " +
                     "LEFT JOIN users u ON p.manager_id = u.user_id " +
                     "ORDER BY p.deadline";
        return db.query(sql);
    }
   
    /**
     * Get active projects
     */
    public List<Map<String, Object>> getActiveProjects() throws SQLException {
        return db.getActiveProjects();
    }
   
    /**
     * Get project by ID
     */
    public Map<String, Object> getProjectById(int projectId) throws SQLException {
        String sql = "SELECT p.*, c.client_name, c.contact_person, c.email as client_email, " +
                     "       u.full_name as manager_name, u.email as manager_email " +
                     "FROM development_projects p " +
                     "LEFT JOIN clients c ON p.client_id = c.client_id " +
                     "LEFT JOIN users u ON p.manager_id = u.user_id " +
                     "WHERE p.project_id = ?";
       
        List<Map<String, Object>> results = db.query(sql, projectId);
        return results.isEmpty() ? null : results.get(0);
    }
   
    /**
     * Update project status
     */
    public boolean updateProjectStatus(int projectId, String status) throws SQLException {
        Map<String, Object> update = Collections.singletonMap("status", status);
        int rows = db.update("development_projects", update, "project_id = ?", projectId);
        return rows > 0;
    }
   
    /**
     * Update project completion percentage
     */
    public boolean updateProjectProgress(int projectId, double completionPercentage) throws SQLException {
        Map<String, Object> update = new HashMap<>();
        update.put("completion_percentage", completionPercentage);
       
        // Auto-update status based on completion
        if (completionPercentage >= 100) {
            update.put("status", "completed");
        } else if (completionPercentage > 0) {
            update.put("status", "in_progress");
        }
       
        int rows = db.update("development_projects", update, "project_id = ?", projectId);
        return rows > 0;
    }
   
    /**
     * Search projects by name or code
     */
    public List<Map<String, Object>> searchProjects(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProjects();
        }
       
        String sql = "SELECT p.*, c.client_name " +
                     "FROM development_projects p " +
                     "LEFT JOIN clients c ON p.client_id = c.client_id " +
                     "WHERE p.project_name LIKE ? OR p.project_code LIKE ? " +
                     "ORDER BY p.deadline";
       
        String searchTerm = "%" + keyword + "%";
        return db.query(sql, searchTerm, searchTerm);
    }
   
    // ============ MILESTONE MANAGEMENT ============
   
    /**
     * Add milestone to project
     */
    public int addMilestone(int projectId, String name, String description,
                           Date dueDate, double amount) throws SQLException {
       
        Map<String, Object> milestone = new HashMap<>();
        milestone.put("project_id", projectId);
        milestone.put("milestone_name", name);
        milestone.put("description", description);
        milestone.put("due_date", dueDate);
        milestone.put("amount", amount);
        milestone.put("status", "pending");
       
        return db.insert("project_milestones", milestone);
    }
   
    /**
     * Get project milestones
     */
    public List<Map<String, Object>> getProjectMilestones(int projectId) throws SQLException {
        return db.select("project_milestones", "project_id = ? ORDER BY due_date", projectId);
    }
   
    /**
     * Update milestone status
     */
    public boolean updateMilestoneStatus(int milestoneId, String status, Date completedDate)
            throws SQLException {
       
        Map<String, Object> update = new HashMap<>();
        update.put("status", status);
       
        if (completedDate != null && status.equals("completed")) {
            update.put("completed_date", completedDate);
        }
       
        int rows = db.update("project_milestones", update, "milestone_id = ?", milestoneId);
       
        // Update project completion percentage
        if (rows > 0) {
            updateProjectCompletionPercentage(getProjectIdFromMilestone(milestoneId));
        }
       
        return rows > 0;
    }
   
    private int getProjectIdFromMilestone(int milestoneId) throws SQLException {
        Map<String, Object> milestone = db.selectOne("project_milestones",
                                                    "milestone_id = ?", milestoneId);
        return milestone != null ? (int) milestone.get("project_id") : 0;
    }
   
    private void updateProjectCompletionPercentage(int projectId) throws SQLException {
        List<Map<String, Object>> milestones = getProjectMilestones(projectId);
       
        if (milestones.isEmpty()) return;
       
        long completed = milestones.stream()
            .filter(m -> "completed".equals(m.get("status")))
            .count();
       
        double percentage = (completed * 100.0) / milestones.size();
        updateProjectProgress(projectId, percentage);
    }
   
    // ============ TEAM MANAGEMENT ============
   
    /**
     * Add team member to project
     */
    public boolean addTeamMember(int projectId, int userId, String role,
                                double hourlyRate) throws SQLException {
       
        // Check if already assigned
        boolean exists = db.exists("project_team",
                                  "project_id = ? AND user_id = ?",
                                  projectId, userId);
        if (exists) return false;
       
        Map<String, Object> teamMember = new HashMap<>();
        teamMember.put("project_id", projectId);
        teamMember.put("user_id", userId);
        teamMember.put("role", role);
        teamMember.put("hourly_rate", hourlyRate);
        teamMember.put("hours_worked", 0);
       
        int rows = db.insert("project_team", teamMember);
        return rows > 0;
    }
   
    /**
     * Get project team members
     */
    public List<Map<String, Object>> getProjectTeam(int projectId) throws SQLException {
        String sql = "SELECT pt.*, u.full_name, u.email, u.phone " +
                     "FROM project_team pt " +
                     "JOIN users u ON pt.user_id = u.user_id " +
                     "WHERE pt.project_id = ? " +
                     "ORDER BY pt.role";
        return db.query(sql, projectId);
    }
   
    /**
     * Remove team member from project
     */
    public boolean removeTeamMember(int projectId, int userId) throws SQLException {
        int rows = db.delete("project_team",
                            "project_id = ? AND user_id = ?",
                            projectId, userId);
        return rows > 0;
    }
   
    /**
     * Update team member hours
     */
    public boolean updateTeamMemberHours(int assignmentId, double hours) throws SQLException {
        Map<String, Object> update = Collections.singletonMap("hours_worked", hours);
        int rows = db.update("project_team", update, "assignment_id = ?", assignmentId);
        return rows > 0;
    }
   
    // ============ PROJECT STATISTICS ============
   
    /**
     * Get project statistics
     */
    public Map<String, Object> getProjectStatistics() throws SQLException {
        String sql = "SELECT " +
                     "  COUNT(*) as total_projects, " +
                     "  SUM(CASE WHEN status = 'proposal' THEN 1 ELSE 0 END) as proposal_projects, " +
                     "  SUM(CASE WHEN status = 'planned' THEN 1 ELSE 0 END) as planned_projects, " +
                     "  SUM(CASE WHEN status = 'in_progress' THEN 1 ELSE 0 END) as in_progress_projects, " +
                     "  SUM(CASE WHEN status = 'testing' THEN 1 ELSE 0 END) as testing_projects, " +
                     "  SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_projects, " +
                     "  COALESCE(SUM(budget), 0) as total_budget " +
                     "FROM development_projects";
       
        List<Map<String, Object>> results = db.query(sql);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
   
    /**
     * Get overdue projects
     */
    public List<Map<String, Object>> getOverdueProjects() throws SQLException {
        String currentDate = DateUtil.getCurrentDateString();
        String sql = "SELECT p.*, c.client_name " +
                     "FROM development_projects p " +
                     "LEFT JOIN clients c ON p.client_id = c.client_id " +
                     "WHERE p.deadline < ? AND p.status NOT IN ('completed', 'cancelled') " +
                     "ORDER BY p.deadline";
        return db.query(sql, currentDate);
    }
   
    /**
     * Get project financial summary
     */
    public Map<String, Object> getProjectFinancialSummary(int projectId) throws SQLException {
        String sql = "SELECT " +
                     "  p.budget, " +
                     "  COALESCE(SUM(m.amount), 0) as milestone_amount, " +
                     "  COALESCE(SUM(CASE WHEN m.status = 'completed' THEN m.amount ELSE 0 END), 0) as completed_amount, " +
                     "  (SELECT COALESCE(SUM(amount), 0) FROM income_transactions " +
                     "   WHERE source_type = 'development_project' AND source_id = ?) as received_amount, " +
                     "  (SELECT COALESCE(SUM(pt.hours_worked * pt.hourly_rate), 0) FROM project_team pt " +
                     "   WHERE pt.project_id = ?) as labor_cost " +
                     "FROM development_projects p " +
                     "LEFT JOIN project_milestones m ON p.project_id = m.project_id " +
                     "WHERE p.project_id = ?";
       
        List<Map<String, Object>> results = db.query(sql, projectId, projectId, projectId);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }
}
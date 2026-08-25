
package com.itsolutioncenter.DAO;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.model.Project;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class ProjectDAO {
     DatabaseManager dbManager;//=DatabaseManager.getInstance();
   
    public ProjectDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
   
    // Get service types (same as MilestoneDAO)
    public String[] getServiceTypes() {
        return new String[] {
            "Software Development",
            "Troubleshooting",
            "PC Assembly",
            "Networking & Cabling",
            "Training",
            "Workshop",
            "Consulting",
            "Other"
        };
    }
   
    // Get project statuses
    public String[] getStatuses() {
        return new String[] {
            "Planning",
            "Active",
            "On Hold",
            "Completed",
            "Cancelled"
        };
    }
   
    // Get all active clients for dropdown
    public List<Map<String, Object>> getActiveClients() {
        String sql = "SELECT client_id, client_name, contact_person FROM clients " +
                    "WHERE status = 'Active' ORDER BY client_name";
       
        try {
            return dbManager.query(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading clients: " + e.getMessage());
            return new ArrayList<>();
        }
    }
   
    // Get team members (project managers)
    public List<Map<String, Object>> getTeamMembers() {
        String sql="select full_name from users";
         try {
            return dbManager.query(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading clients: " + e.getMessage());
            return new ArrayList<>();
        }
    }
   
    // Generate unique project code
    public String generateProjectCode(String serviceType) {
        String prefix = "PRJ";
       
        // Add service type prefix
        switch(serviceType) {
            case "Software Development": prefix = "SD"; break;
            case "Troubleshooting": prefix = "TS"; break;
            case "PC Assembly": prefix = "PA"; break;
            case "Networking & Cabling": prefix = "NC"; break;
            case "Training": prefix = "TR"; break;
            case "Workshop": prefix = "WS"; break;
            case "Consulting": prefix = "CN"; break;
            default: prefix = "OT";
        }
       
        // Get next sequence number
        String sql = "SELECT COUNT(*) + 1 as next_num FROM projects " +
                    "WHERE project_code LIKE '" + prefix + "%'";
       
        try {
            List<Map<String, Object>> result = dbManager.query(sql);
            if (!result.isEmpty()) {
                int nextNum = ((Long) result.get(0).get("next_num")).intValue();
                return String.format("%s%03d", prefix, nextNum);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
        return prefix + "001";
    }
   
    // Save/Update project
    public boolean saveProject(Project project) {
        String sql;
        boolean isUpdate = project.getProjectId() > 0;
       
        if (isUpdate) {
            sql = "UPDATE projects SET " +
                  "project_name = ?, client_id = ?, service_type = ?, " +
                  "start_date = ?, end_date = ?, status = ?, " +
                  "budget = ?, project_manager = ?, description = ? " +
                  "WHERE project_id = ?";
        } else {
            sql = "INSERT INTO projects (project_code, project_name, client_id, " +
                  "service_type, start_date, end_date, status, budget, " +
                  "project_manager, description) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
       
        try {
            if (isUpdate) {
                return dbManager.executeUpdate(sql,
                    project.getProjectName(),
                    project.getClientId(),
                    project.getServiceType(),
                    project.getStartDate() != null ? new java.sql.Date(project.getStartDate().getTime()) : null,
                    project.getEndDate() != null ? new java.sql.Date(project.getEndDate().getTime()) : null,
                    project.getStatus(),
                    project.getBudget(),
                    project.getProjectManager(),
                    project.getDescription(),
                    project.getProjectId()
                ) > 0;
            } else {
                return dbManager.executeUpdate(sql,
                    project.getProjectCode(),
                    project.getProjectName(),
                    project.getClientId(),
                    project.getServiceType(),
                    project.getStartDate() != null ? new java.sql.Date(project.getStartDate().getTime()) : null,
                    project.getEndDate() != null ? new java.sql.Date(project.getEndDate().getTime()) : null,
                    project.getStatus(),
                    project.getBudget(),
                    project.getProjectManager(),
                    project.getDescription()
                ) > 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error saving project: " + e.getMessage());
            return false;
        }
    }
   
    // Get all projects with client info
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT projects.*, c.client_name, c.contact_person " +
        "FROM projects LEFT JOIN clients c ON projects.client_id = c.client_id " +
        "ORDER BY projects.created_date DESC";
       
        try {
            List<Map<String, Object>> results = dbManager.query(sql);
            for (Map<String, Object> row : results) {
                projects.add(mapToProject(row));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return projects;
    }
   
    // Get active projects only
    public List<Project> getActiveProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, c.client_name FROM projects p " +
                    "LEFT JOIN clients c ON p.client_id = c.client_id " +
                    "WHERE p.status IN ('Planning', 'Active') " +
                    "ORDER BY p.project_name";
       
        try {
            List<Map<String, Object>> results = dbManager.query(sql);
            for (Map<String, Object> row : results) {
                projects.add(mapToProject(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
   
    // Get project by ID
    public Project getProjectById(int projectId) {
        String sql = "SELECT p.*, c.client_name, c.contact_person FROM projects p " +
                    "LEFT JOIN clients c ON p.client_id = c.client_id " +
                    "WHERE p.project_id = ?";
       
        try {
            List<Map<String, Object>> results = dbManager.query(sql, projectId);
            if (!results.isEmpty()) {
                return mapToProject(results.get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
   
    // Delete project
    public boolean deleteProject(int projectId) {
        // First check if project has milestones
        String checkSql = "SELECT COUNT(*) as milestone_count FROM milestones WHERE project_id = ?";
        try {
            List<Map<String, Object>> result = dbManager.query(checkSql, projectId);
            if (!result.isEmpty()) {
                long milestoneCount = (Long) result.get(0).get("milestone_count");
                if (milestoneCount > 0) {
                    JOptionPane.showMessageDialog(null,
                        "Cannot delete project. It has " + milestoneCount + " milestones.\n" +
                        "Delete milestones first or archive the project.");
                    return false;
                }
            }
           
            String deleteSql = "DELETE FROM projects WHERE project_id = ?";
            return dbManager.executeUpdate(deleteSql, projectId) > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting project: " + e.getMessage());
            return false;
        }
    }
   
    // Get project statistics
    public Map<String, Object> getProjectStats() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(*) as total_projects, " +
                    "COUNT(CASE WHEN status = 'Active' THEN 1 END) as active, " +
                    "COUNT(CASE WHEN status = 'Completed' THEN 1 END) as completed, " +
                    "COUNT(CASE WHEN status = 'On Hold' THEN 1 END) as on_hold, " +
                    "SUM(budget) as total_budget " +
                    "FROM projects";
       
        try {
            List<Map<String, Object>> results = dbManager.query(sql);
            if (!results.isEmpty()) {
                stats = results.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
   
    // Search projects
    public List<Project> searchProjects(String searchTerm) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.*, c.client_name FROM projects p " +
                    "LEFT JOIN clients c ON p.client_id = c.client_id " +
                    "WHERE p.project_code LIKE ? OR p.project_name LIKE ? OR c.client_name LIKE ? " +
                    "ORDER BY p.project_name";
       
        try {
            String searchPattern = "%" + searchTerm + "%";
            List<Map<String, Object>> results = dbManager.query(sql,
                searchPattern, searchPattern, searchPattern);
            for (Map<String, Object> row : results) {
                projects.add(mapToProject(row));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
   
    private Project mapToProject(Map<String, Object> row) {
        Project project = new Project();
        try
        {
        project.setProjectId((Integer) row.get("project_id"));
        project.setProjectCode((String) row.get("project_code"));
        project.setProjectName((String) row.get("project_name"));
       
        if (row.get("client_id") != null)
            project.setClientId((Integer) row.get("client_id"));
       
        project.setServiceType((String) row.get("service_type"));
        project.setStartDate((Date) row.get("start_date"));
        project.setEndDate((Date) row.get("end_date"));
        project.setStatus((String) row.get("status"));
       
        if (row.get("budget") != null)
            project.setBudget(((Number) row.get("budget")).doubleValue());
           
        project.setProjectManager((String) row.get("project_manager"));
        project.setDescription((String) row.get("description"));
        project.setCreatedDate((Date) row.get("created_date"));
       
        if (row.containsKey("client_name"))
            project.setClientName((String) row.get("client_name"));
        if (row.containsKey("contact_person"))
            project.setContactPerson((String) row.get("contact_person"));
        }catch(ClassCastException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
//        if (row.containsKey("milestone_count"))
//            project.setMilestoneCount(((Long) row.get("milestone_count")).intValue());
//        if (row.containsKey("completed_milestones"))
//            project.setCompletedMilestones(((Long) row.get("completed_milestones")).intValue());
           
        return project;
    }
}

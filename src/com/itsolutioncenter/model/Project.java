
package com.itsolutioncenter.model;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import java.util.Date;

public class Project {
    private int projectId;
    private String projectCode;
    private String projectName;
    private int clientId;
    private String serviceType;
    private Date startDate;
    private Date endDate;
    private String status;
    private double budget;
    private String projectManager;
    private String description;
    private Date createdDate;
   
    // Additional fields for display
    private String clientName;
    private String contactPerson;
    private int milestoneCount;
    private int completedMilestones;

    // Constructors
    public Project() {}
   
    public Project(String projectCode, String projectName, String serviceType,
                   Date startDate, String status) {
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.serviceType = serviceType;
        this.startDate = startDate;
        this.status = status;
    }

    // Getters and Setters
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
   
    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
   
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
   
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
   
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
   
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
   
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
   
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
   
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
   
    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }
   
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
   
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
   
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
   
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
   
    public int getMilestoneCount() { return milestoneCount; }
    public void setMilestoneCount(int milestoneCount) { this.milestoneCount = milestoneCount; }
   
    public int getCompletedMilestones() { return completedMilestones; }
    public void setCompletedMilestones(int completedMilestones) { this.completedMilestones = completedMilestones; }
   
    public double getProgressPercentage() {
        if (milestoneCount == 0) return 0;
        return (completedMilestones * 100.0) / milestoneCount;
    }
}

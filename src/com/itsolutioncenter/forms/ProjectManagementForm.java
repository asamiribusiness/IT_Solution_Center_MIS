
package com.itsolutioncenter.forms;

import com.itsolutioncenter.DAO.ProjectDAO;
import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.model.Permission;
import com.itsolutioncenter.model.Project;
import com.itsolutioncenter.model.User;
import com.itsolutioncenter.service.ProjectService;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class ProjectManagementForm extends javax.swing.JInternalFrame {
private DatabaseManager dbManager=DatabaseManager.getInstance();
private User currentUser;
private Permission permissionService;
private ProjectDAO projectDAO = new ProjectDAO();
private ProjectService projectService;
private int projectID=0;
private String query;
private DefaultTableModel model=new DefaultTableModel();
    /**
     * Creates new form ProjectManagementForm
     */
    public ProjectManagementForm() {
        initComponents();
        loadProjectList();
      //  updateStats();
        loadInitialData();
    }
    private void loadProjectList()
    {
        query="SELECT projects.project_code,projects.project_name," +
        "clients.client_name, projects.service_type, projects.start_date," +
        "projects.`status`, projects.budget, 1+2 as progress FROM clients " +
        "INNER JOIN projects ON projects.client_id = clients.client_id";
        model=dbManager.getTableModel(query, tblProjectList);
        tblProjectList.setModel(model);
    }
    private void filterByStatus(String status) {
        if ("-- All Status --".equals(status)) {
            loadProjectList();
            return;
        }
        model.setRowCount(0);
        List<Project> allProjects = projectDAO.getAllProjects();
       
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       
        for (Project project : allProjects) {
            if (project.getStatus().equals(status)) {
                String startDate = project.getStartDate() != null ?
                    dateFormat.format(project.getStartDate()) : "";
               
                String budget = project.getBudget() > 0 ?
                    String.format("AFN%.2f", project.getBudget()) : "-";
               
                String progress = project.getMilestoneCount() > 0 ?
                    String.format("%.0f%%", project.getProgressPercentage()) : "0%";
               
                model.addRow(new Object[]{
                    project.getProjectCode(),
                    project.getProjectName(),
                    project.getClientName() != null ? project.getClientName() : "-",
                    project.getServiceType(),
                    startDate,
                    project.getStatus(),
                    budget,
                    progress
                });
            }
        }
    }
     private void updateStats() {
        Map<String, Object> stats = projectDAO.getProjectStats();
       
        long total = stats.get("total_projects") != null ?
            ((Long) stats.get("total_projects")) : 0;
        long active = stats.get("active") != null ?
            ((Long) stats.get("active")) : 0;
        long completed = stats.get("completed") != null ?
            ((Long) stats.get("completed")) : 0;
        double budget = stats.get("total_budget") != null ?
            ((Double) stats.get("total_budget")) : 0;
       
        String statsText = String.format(
            "Total Projects: %d | Active: %d | Completed: %d | Total Budget: AFN %.2f",
            total, active, completed, budget
        );
        lblStats.setText(statsText);
     }
     private void loadInitialData() {
        // Load clients
        cmbClient.removeAllItems();
        cmbClient.addItem("-- Select Client --");
        List<Map<String, Object>> clients = projectDAO.getActiveClients();
        for (Map<String, Object> client : clients) {
            String displayText = client.get("client_name").toString();
            if (client.get("contact_person") != null) {
                displayText += " (" + client.get("contact_person") + ")";
            }
            cmbClient.addItem(displayText);
        }
       
        // Load project managers
        cmbProjectManager.removeAllItems();
        cmbProjectManager.addItem("-- Not Assigned --");
        List<Map<String, Object>> managers = projectDAO.getTeamMembers();
        for (Map<String,Object> manager : managers) {
            cmbProjectManager.addItem(manager.get("full_name").toString());
        }
       
        // Generate initial project code
       // generateProjectCode();
    }
     private void saveProject() {
//        if (!validateForm()) {
//            return;
//        }
       
        Project project = createProjectFromForm();
       
        if (projectDAO.saveProject(project)) {
            JOptionPane.showMessageDialog(this, "Project saved successfully!");
           // clearForm();
            loadProjectList();
//            updateStats();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving project!");
        }
    }
   
    private void updateProject() {
        if (projectID == 0) {
            JOptionPane.showMessageDialog(this, "Please select a project to update!");
            return;
        }
       
        Project project = createProjectFromForm();
        project.setProjectId(projectID);
        project.setProjectCode(txtProjectCode.getText()); // Keep existing code
       
        if (projectDAO.saveProject(project)) {
            JOptionPane.showMessageDialog(this, "Project updated successfully!");
            //clearForm();
            loadInitialData();
  //          updateStats();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating project!");
        }
    }
   
      private void deleteProject() {
        if (projectID == 0) {
            JOptionPane.showMessageDialog(this, "Please select a project to delete!");
            return;
        }
       
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this project?\n" +
            "This action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
           
        if (confirm == JOptionPane.YES_OPTION) {
            if (projectDAO.deleteProject(projectID)) {
                JOptionPane.showMessageDialog(this, "Project deleted successfully!");
                //clearForm();
                loadProjectList();
             //   updateStats();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting project!");
            }
        }
    }
      private void generateProjectCode() {
        String serviceType = cmbServiceType.getSelectedItem().toString();
        String projectCode = projectDAO.generateProjectCode(serviceType);
        txtProjectCode.setText(projectCode);
    }
       private Project createProjectFromForm() {
        Project project = new Project();
       
        project.setProjectCode(txtProjectCode.getText());
        project.setProjectName(txtProjectName.getText().trim());
        project.setServiceType(cmbServiceType.getSelectedItem().toString());
        project.setStartDate(txtStartDate.getDate());
        project.setEndDate(txtEndDate.getDate());
        project.setStatus(cmbStatus.getSelectedItem().toString());
       
        // Get client ID from selection
        int selectedClientIndex = cmbClient.getSelectedIndex();
        if (selectedClientIndex > 0) {
            List<Map<String, Object>> clients = projectDAO.getActiveClients();
            Map<String, Object> selectedClient = clients.get(selectedClientIndex - 1);
            project.setClientId((Integer) selectedClient.get("client_id"));
        }
       
        // Get project manager
        if (cmbProjectManager.getSelectedIndex() > 0) {
            project.setProjectManager(cmbProjectManager.getSelectedItem().toString());
        }
       
        // Parse budget
        try {
            if (!txtBudget.getText().isEmpty()) {
                project.setBudget(Double.parseDouble(txtBudget.getText()));
            }
        } catch (NumberFormatException e) {
            project.setBudget(0);
        }
       
        project.setDescription(txtProjectDescription.getText().trim());
       
        return project;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlProjectDetails = new javax.swing.JPanel();
        pnlBasicInformation = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtProjectCode = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JTextField();
        cmbClient = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbServiceType = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cmbProjectManager = new javax.swing.JComboBox<>();
        pnlProjectTimeline = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtStartDate = new com.toedter.calendar.JDateChooser();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtBudget = new javax.swing.JTextField();
        pnlProjectDescription = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtProjectDescription = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        pnlProjectStatistics = new javax.swing.JPanel();
        lblStats = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        pnlSearch = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnFilter = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        cmbStatusFilter = new javax.swing.JComboBox<>();
        btnRefresh = new javax.swing.JButton();
        pnlProjectList = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProjectList = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);

        pnlProjectDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Project Details"));

        pnlBasicInformation.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Basic Information", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("Project Code:");

        jLabel2.setText("Project Name:");

        jLabel3.setText("Client:");

        jLabel4.setText("Service Type:");

        cmbServiceType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Software Development", "Troubleshooting", "PC Assembly", "Networking & Cabling", "Training", "Workshop", "Consulting", "Other" }));

        jLabel5.setText("Project Manager:");

        javax.swing.GroupLayout pnlBasicInformationLayout = new javax.swing.GroupLayout(pnlBasicInformation);
        pnlBasicInformation.setLayout(pnlBasicInformationLayout);
        pnlBasicInformationLayout.setHorizontalGroup(
            pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBasicInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(28, 28, 28)
                .addGroup(pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbClient, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtProjectCode)
                    .addComponent(txtProjectName)
                    .addComponent(cmbServiceType, 0, 168, Short.MAX_VALUE)
                    .addComponent(cmbProjectManager, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        pnlBasicInformationLayout.setVerticalGroup(
            pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBasicInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtProjectCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbServiceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlBasicInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cmbProjectManager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlProjectTimeline.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Project Timeline & Budget", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel6.setText("Start Date");

        jLabel7.setText("End Date:");

        jLabel8.setText("Status:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Planning", "Active", "On Hold", "Completed", "Cancelled" }));

        jLabel9.setText("Budget:");

        javax.swing.GroupLayout pnlProjectTimelineLayout = new javax.swing.GroupLayout(pnlProjectTimeline);
        pnlProjectTimeline.setLayout(pnlProjectTimelineLayout);
        pnlProjectTimelineLayout.setHorizontalGroup(
            pnlProjectTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectTimelineLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProjectTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(pnlProjectTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBudget, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );
        pnlProjectTimelineLayout.setVerticalGroup(
            pnlProjectTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectTimelineLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProjectTimelineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlProjectTimelineLayout.createSequentialGroup()
                        .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBudget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlProjectTimelineLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel9)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlProjectDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Project Description"));

        txtProjectDescription.setColumns(20);
        txtProjectDescription.setRows(5);
        jScrollPane1.setViewportView(txtProjectDescription);

        javax.swing.GroupLayout pnlProjectDescriptionLayout = new javax.swing.GroupLayout(pnlProjectDescription);
        pnlProjectDescription.setLayout(pnlProjectDescriptionLayout);
        pnlProjectDescriptionLayout.setHorizontalGroup(
            pnlProjectDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectDescriptionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlProjectDescriptionLayout.setVerticalGroup(
            pnlProjectDescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectDescriptionLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Operations"));

        btnSave.setText("Save");

        btnUpdate.setText("Update");

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");

        btnReport.setText("Report");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnReport)))
        );

        javax.swing.GroupLayout pnlProjectDetailsLayout = new javax.swing.GroupLayout(pnlProjectDetails);
        pnlProjectDetails.setLayout(pnlProjectDetailsLayout);
        pnlProjectDetailsLayout.setHorizontalGroup(
            pnlProjectDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectDetailsLayout.createSequentialGroup()
                .addGroup(pnlProjectDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlBasicInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlProjectDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlProjectTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlProjectDetailsLayout.setVerticalGroup(
            pnlProjectDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectDetailsLayout.createSequentialGroup()
                .addComponent(pnlBasicInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlProjectTimeline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlProjectDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlProjectStatistics.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Project Statistics", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        lblStats.setText("Total Projects:");

        javax.swing.GroupLayout pnlProjectStatisticsLayout = new javax.swing.GroupLayout(pnlProjectStatistics);
        pnlProjectStatistics.setLayout(pnlProjectStatisticsLayout);
        pnlProjectStatisticsLayout.setHorizontalGroup(
            pnlProjectStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectStatisticsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStats)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlProjectStatisticsLayout.setVerticalGroup(
            pnlProjectStatisticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectStatisticsLayout.createSequentialGroup()
                .addComponent(lblStats)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Project List"));

        pnlSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search & Filter", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel10.setText("Search:");

        btnSearch.setText("Search");

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        jLabel11.setText("Status:");

        cmbStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- All Status --", "Planning", "Active", "On Hold", "Completed", "Cancelled" }));

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addGap(65, 65, 65)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                .addComponent(btnRefresh)
                .addGap(17, 17, 17))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnFilter)
                    .addComponent(jLabel11)
                    .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh))
                .addGap(0, 3, Short.MAX_VALUE))
        );

        pnlProjectList.setBorder(javax.swing.BorderFactory.createTitledBorder("Project List"));

        tblProjectList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Code", "Project Name", "Client", "Service", "Start Date", "Status", "Budget", "Progress"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProjectList.setColumnSelectionAllowed(true);
        tblProjectList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProjectList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProjectList.getTableHeader().setReorderingAllowed(false);
        tblProjectList.setUpdateSelectionOnSort(false);
        tblProjectList.setVerifyInputWhenFocusTarget(false);
        jScrollPane2.setViewportView(tblProjectList);
        tblProjectList.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tblProjectList.getColumnModel().getColumnCount() > 0) {
            tblProjectList.getColumnModel().getColumn(0).setResizable(false);
            tblProjectList.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblProjectList.getColumnModel().getColumn(1).setResizable(false);
            tblProjectList.getColumnModel().getColumn(1).setPreferredWidth(200);
            tblProjectList.getColumnModel().getColumn(2).setResizable(false);
            tblProjectList.getColumnModel().getColumn(2).setPreferredWidth(80);
            tblProjectList.getColumnModel().getColumn(3).setResizable(false);
            tblProjectList.getColumnModel().getColumn(3).setPreferredWidth(120);
            tblProjectList.getColumnModel().getColumn(4).setResizable(false);
            tblProjectList.getColumnModel().getColumn(4).setPreferredWidth(50);
            tblProjectList.getColumnModel().getColumn(5).setResizable(false);
            tblProjectList.getColumnModel().getColumn(5).setPreferredWidth(50);
            tblProjectList.getColumnModel().getColumn(6).setResizable(false);
            tblProjectList.getColumnModel().getColumn(6).setPreferredWidth(50);
            tblProjectList.getColumnModel().getColumn(7).setResizable(false);
            tblProjectList.getColumnModel().getColumn(7).setPreferredWidth(50);
        }

        javax.swing.GroupLayout pnlProjectListLayout = new javax.swing.GroupLayout(pnlProjectList);
        pnlProjectList.setLayout(pnlProjectListLayout);
        pnlProjectListLayout.setHorizontalGroup(
            pnlProjectListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        pnlProjectListLayout.setVerticalGroup(
            pnlProjectListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProjectListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlProjectList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlProjectList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlProjectDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlProjectStatistics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlProjectStatistics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlProjectDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        filterByStatus(cmbStatusFilter.getSelectedItem().toString());
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadProjectList();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteProject();
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbClient;
    private javax.swing.JComboBox<String> cmbProjectManager;
    private javax.swing.JComboBox<String> cmbServiceType;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> cmbStatusFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStats;
    private javax.swing.JPanel pnlBasicInformation;
    private javax.swing.JPanel pnlProjectDescription;
    private javax.swing.JPanel pnlProjectDetails;
    private javax.swing.JPanel pnlProjectList;
    private javax.swing.JPanel pnlProjectStatistics;
    private javax.swing.JPanel pnlProjectTimeline;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JTable tblProjectList;
    private javax.swing.JTextField txtBudget;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private javax.swing.JTextField txtProjectCode;
    private javax.swing.JTextArea txtProjectDescription;
    private javax.swing.JTextField txtProjectName;
    private javax.swing.JTextField txtSearch;
    private com.toedter.calendar.JDateChooser txtStartDate;
    // End of variables declaration//GEN-END:variables
}

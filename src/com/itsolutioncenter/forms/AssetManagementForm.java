
package com.itsolutioncenter.forms;

import javax.swing.JOptionPane;
import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.service.AssetService;
import com.itsolutioncenter.service.UserService;
import com.itsolutioncenter.util.Formatter;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class AssetManagementForm extends javax.swing.JInternalFrame {
    private DatabaseManager dbManager=DatabaseManager.getInstance();
    private UserService userService=new UserService();
    private Main_Form obj=new Main_Form();
    private AssetService assetService=new AssetService();
    private DefaultTableModel model=new DefaultTableModel();
    private String assettTag,query,assetName;
    private int assetID;
    public AssetManagementForm() {
        initComponents();
        loadData();
        loadStatistics();
    }
private void loadData()
{
    query="SELECT assets.asset_id,assets.asset_tag,assets.asset_name,assets.category," +
    "assets.serial_number,assets.purchase_date,assets.purchase_cost,assets.current_value," +
    "assets.`status`,users.full_name,assets.location FROM assets " +
    "INNER JOIN users ON users.user_id = assets.assigned_to";
    model=dbManager.getTableModel(query, tblAsset);
    tblAsset.setModel(model);
}
// Proceed with action
    private void searchData() {
    
    // Proceed with action
        String searchText=txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter search criteria: Asset ID, Asset Name or Tag Number");
            return;
        }
        boolean isNumeric = searchText.matches("\\d+");  
            if (isNumeric) {
                // Search by ID (numeric ID)
                assetID=Integer.parseInt(searchText);
                query = "select * from assets where asset_id = '"+assetID+"'";
            } else {
                // Search by username OR full name only (non-numeric)
                query= "select * from assets where asset_name LIKE '"+searchText+"' OR asset_tag LIKE '"+searchText+"'";
            }
            model=dbManager.getTableModel(query,tblAsset);
            tblAsset.setModel(model); 
            txtSearch.setText("");
    }
    private void filterAssets() {
        String category = (String) cmbCategory.getSelectedItem();
        String status = (String) cmbStatus.getSelectedItem();
       
        try {
            List<Map<String, Object>> filteredAssets = assetService.getAllAssets();
           
            // Apply filters
            if (!category.equals("--- All Categories ---")) {
                filteredAssets.removeIf(asset -> !category.equals(asset.get("category")));
            }
           
            if (!status.equals("--- All Statuses ---")) {
                filteredAssets.removeIf(asset -> !status.equalsIgnoreCase((String) asset.get("status")));
            }
           
           model = (DefaultTableModel) tblAsset.getModel();
        model.setRowCount(0);
       
        for (Map<String, Object> asset : filteredAssets) {
            model.addRow(new Object[]{
                asset.get("asset_id"),
                asset.get("asset_tag"),
                asset.get("asset_name"),
                asset.get("category"),
                asset.get("serial_number"),
                asset.get("purchase_date"),
                asset.get("purchase_cost"),
                asset.get("current_value"),
                asset.get("status"),
                asset.get("assigned_to_name"),
                asset.get("location")
            });
        }
        colorTableRows();
           
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering assets: " + e.getMessage());
        }
    }
        private void colorTableRows() {
        tblAsset.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
               
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getValueAt(row, 8);
               
                if (!isSelected) {
                    switch (status) {
                        case "Available":
                            c.setBackground(new Color(220, 255, 220)); // Light green
                            break;
                        case "In Use":
                            c.setBackground(new Color(255, 255, 200)); // Light yellow
                            break;
                        case "Maintenance":
                            c.setBackground(new Color(255, 220, 220)); // Light red
                            break;
                        case "Retired":
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
         private void assignAsset() {
        int selectedRow = tblAsset.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset");
            return;
        }
        int assetId = (int) tblAsset.getValueAt(selectedRow, 0);
        assetName = (String) tblAsset.getValueAt(selectedRow, 2);
        String currentStatus = (String) tblAsset.getValueAt(selectedRow, 8);
        try {
            if (currentStatus.equals("Available")) {
                // Assign to user
                Map<String, Object> asset = assetService.getAssetById(assetId);
                if (asset == null) return;
                // Get list of active users
              List<Map<String,Object>> users = userService.getAllUsers();
                JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
                panel.add(new JLabel("Asset:"));
                panel.add(new JLabel(assetName));
                panel.add(new JLabel("Assign to User:"));
                JComboBox<String> cmbUsers = new JComboBox<>();
                cmbUsers.addItem("-- Select User --");
                for (Map<String, Object> user : users) {
                    String display = user.get("full_name") + " (" + user.get("role") + ")";
                    cmbUsers.addItem(display);
                }
                panel.add(cmbUsers);
                panel.add(new JLabel("Location:"));
                JTextField txtLocation = new JTextField(20);
                txtLocation.setText((String) asset.get("location"));
                panel.add(txtLocation);
                int result = JOptionPane.showConfirmDialog(this, panel,
                    "Assign Asset",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    int selectedIndex = cmbUsers.getSelectedIndex();
                    if (selectedIndex > 0) {
                        // Extract user ID from selection
                        String selected = (String) cmbUsers.getSelectedItem();
                        String userName = selected.split("\\(")[0].trim();
                        // Find user ID
                        int userId = 0;
                        for (Map<String, Object> user : users) {
                            if (userName.equals(user.get("full_name"))) {
                                userId = (int) user.get("user_id");
                                break;
                            }
                        }                 
                        if (userId > 0) {
                            boolean success = assetService.assignAsset(assetId, userId, txtLocation.getText());
                            if (success) {                   JOptionPane.showMessageDialog(this, "Asset assigned successfully");
                                loadData();
                            }
                        }
                    }
                }
            } else if (currentStatus.equals("In_Use")) {
                // Unassign asset
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Unassign this asset from current user?",
                    "Unassign Asset",
                    JOptionPane.YES_NO_OPTION);           
                if (confirm == JOptionPane.YES_OPTION) {
                    Map<String, Object> asset = assetService.getAssetById(assetId);
                    String location = JOptionPane.showInputDialog(this,
                        "Enter new location:", asset.get("location"));              
                    if (location != null && !location.trim().isEmpty()) {
                        boolean success = assetService.unassignAsset(assetId, location);
                        if (success) {
    JOptionPane.showMessageDialog(this, "Asset unassigned successfully");
                   loadData();
   }       }   }
            } else {JOptionPane.showMessageDialog(this,
                    "Cannot assign/unassign asset with status: " + currentStatus);
            }
           
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
       private void loadStatistics() {
        try {
            Map<String, Object> stats = assetService.getAssetStatistics(); lblNoAssets.setText(lblNoAssets.getText()+" "+stats.get("total_assets").toString());    
            lblValueofAssets.setText(lblValueofAssets.getText()+" "+Formatter.formatCurrency((Number) stats.get("total_value")));
            lblAvailableAsset.setText(lblAvailableAsset.getText()+" "+stats.get("available_assets").toString());
            lblInUseAsset.setText(lblInUseAsset.getText()+" "+stats.get("in_use_assets").toString());
            lblMaintainAsset.setText(lblMaintainAsset.getText()+" "+stats.get("maintenance_assets").toString());
            lblRetiredAsset.setText(lblRetiredAsset.getText()+" "+stats.get("retired_assets").toString());
            lblAssetCategory.setText(lblAssetCategory.getText()+" "+stats.get("categories_count").toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading statistics: " + e.getMessage());
        }
    }
       private void addNewAsset()
       {
           NewAssetForm form = new NewAssetForm(obj, true);
        form.setVisible(true);
        loadData();
        loadStatistics();
       }
       private void editAsset()
       {
            int selectedRow = tblAsset.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset to edit");
            return;
        }
       
        int assetId = (int) tblAsset.getValueAt(selectedRow, 0);
        NewAssetForm form = new NewAssetForm(obj, true, assetId);
        form.setVisible(true);
        loadData();
        loadStatistics();
       }
        private void deleteAsset() {
        int selectedRow = tblAsset.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset to delete");
            return;
        }
       
        int assetId = (int) tblAsset.getValueAt(selectedRow, 0);
        assetName = (String) tblAsset.getValueAt(selectedRow, 2);
       
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete asset: " + assetName + "?\n" +
            "This action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Actually we'll just retire it instead of deleting
                boolean success = assetService.retireAsset(assetId, "Manually deleted from system");
                if (success) {
  JOptionPane.showMessageDialog(this, "Asset retired successfully");
                    loadData();
                    loadStatistics();
                } else { JOptionPane.showMessageDialog(this, "Failed to retire asset");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
         private void markMaintenance() {
        int selectedRow = tblAsset.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset");
            return;
        }
       
        int assetId = (int) tblAsset.getValueAt(selectedRow, 0);
        assetName = (String) tblAsset.getValueAt(selectedRow, 2);
       
        String notes = JOptionPane.showInputDialog(this,
            "Enter maintenance notes for " + assetName + ":");
       
        if (notes != null && !notes.trim().isEmpty()) {
            try {
                boolean success = assetService.markForMaintenance(assetId, notes);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Asset marked for maintenance");
                    loadData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
   
    private void retireAsset() {
        int selectedRow = tblAsset.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset");
            return;
        }
       
        int assetId = (int) tblAsset.getValueAt(selectedRow, 0);
        assetName = (String) tblAsset.getValueAt(selectedRow, 2);
       
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to retire asset: " + assetName + "?\n" +
            "This will set its value to 0 and mark it as retired.",
            "Retire Asset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
       
        if (confirm == JOptionPane.YES_OPTION) {
            String notes = JOptionPane.showInputDialog(this, "Retirement notes:");
           
            try {
                boolean success = assetService.retireAsset(assetId, notes);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Asset retired successfully");
                    loadData();
                    loadStatistics();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        btnFilter = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAsset = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnNewAsset = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnAssign = new javax.swing.JButton();
        btnMaintenance = new javax.swing.JButton();
        btnRetireAsset = new javax.swing.JButton();
        btnAssetCheckout = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblNoAssets = new javax.swing.JLabel();
        lblValueofAssets = new javax.swing.JLabel();
        lblAvailableAsset = new javax.swing.JLabel();
        lblInUseAsset = new javax.swing.JLabel();
        lblMaintainAsset = new javax.swing.JLabel();
        lblRetiredAsset = new javax.swing.JLabel();
        lblAssetCategory = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle("Asset Management Form");

        jLabel1.setText("Search:");

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jLabel2.setText("Categary:");

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- All Categories ---", "IT Equipment", "Office Equipment", "Office Supply", "Other" }));

        jLabel3.setText("Status:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- All Statuses ---", "Available", "In_Use", "Maintenance", "Retired", "Lost" }));

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        tblAsset.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Asset Tag", "Asset Name", "Category", "Serial No", "Purchase Date", "Purchase Cost", "Current Value", "Status", "Assign To", "Location"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAsset.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblAsset.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblAsset.getTableHeader().setReorderingAllowed(false);
        tblAsset.setUpdateSelectionOnSort(false);
        jScrollPane1.setViewportView(tblAsset);
        tblAsset.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblAsset.getColumnModel().getColumnCount() > 0) {
            tblAsset.getColumnModel().getColumn(0).setResizable(false);
            tblAsset.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblAsset.getColumnModel().getColumn(1).setResizable(false);
            tblAsset.getColumnModel().getColumn(1).setPreferredWidth(40);
            tblAsset.getColumnModel().getColumn(2).setResizable(false);
            tblAsset.getColumnModel().getColumn(2).setPreferredWidth(50);
            tblAsset.getColumnModel().getColumn(3).setResizable(false);
            tblAsset.getColumnModel().getColumn(4).setResizable(false);
            tblAsset.getColumnModel().getColumn(5).setResizable(false);
            tblAsset.getColumnModel().getColumn(6).setResizable(false);
            tblAsset.getColumnModel().getColumn(7).setResizable(false);
            tblAsset.getColumnModel().getColumn(8).setResizable(false);
            tblAsset.getColumnModel().getColumn(9).setResizable(false);
            tblAsset.getColumnModel().getColumn(10).setResizable(false);
        }

        btnNewAsset.setText("New Asset");
        btnNewAsset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewAssetActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAssign.setText("Assign/Unassign");
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });

        btnMaintenance.setText("Mark Maintenance");
        btnMaintenance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaintenanceActionPerformed(evt);
            }
        });

        btnRetireAsset.setText("Retire Asset");
        btnRetireAsset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetireAssetActionPerformed(evt);
            }
        });

        btnAssetCheckout.setText("Asset Checkout");
        btnAssetCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssetCheckoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnNewAsset)
                .addGap(18, 18, 18)
                .addComponent(btnEdit)
                .addGap(18, 18, 18)
                .addComponent(btnDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAssign)
                .addGap(18, 18, 18)
                .addComponent(btnMaintenance)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRetireAsset)
                .addGap(28, 28, 28)
                .addComponent(btnAssetCheckout)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewAsset)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete)
                    .addComponent(btnAssign)
                    .addComponent(btnMaintenance)
                    .addComponent(btnRetireAsset)
                    .addComponent(btnAssetCheckout))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefresh)
                .addGap(94, 94, 94)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClear)
                .addGap(17, 17, 17))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1104, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnRefresh)
                    .addComponent(jLabel2)
                    .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear)
                    .addComponent(btnFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Asset", jPanel1);

        lblNoAssets.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNoAssets.setText("Total Number of Assets:");

        lblValueofAssets.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblValueofAssets.setText("Total Value of Assets:");

        lblAvailableAsset.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblAvailableAsset.setText("Total Available Assets:");

        lblInUseAsset.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblInUseAsset.setText("Total In Use Assets:");

        lblMaintainAsset.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMaintainAsset.setText("Total Maintenance Assets:");

        lblRetiredAsset.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblRetiredAsset.setText("Total Retired Assets:");

        lblAssetCategory.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblAssetCategory.setText("Total Categories of Assets:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRetiredAsset, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInUseAsset, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblNoAssets, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                        .addComponent(lblAvailableAsset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblValueofAssets, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblAssetCategory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                        .addComponent(lblMaintainAsset, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(285, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lblNoAssets)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblValueofAssets)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAvailableAsset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInUseAsset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMaintainAsset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRetiredAsset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAssetCategory)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(474, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 118, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Statistics", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchData();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        cmbCategory.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        loadData();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        filterAssets();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnNewAssetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewAssetActionPerformed
    addNewAsset();
    }//GEN-LAST:event_btnNewAssetActionPerformed

    private void btnAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignActionPerformed
       assignAsset();
    }//GEN-LAST:event_btnAssignActionPerformed

    private void btnAssetCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssetCheckoutActionPerformed
        AssetCheckoutForm acof=new AssetCheckoutForm(obj,true);
        acof.setVisible(true);
    }//GEN-LAST:event_btnAssetCheckoutActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editAsset();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteAsset();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnMaintenanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaintenanceActionPerformed
        markMaintenance();
    }//GEN-LAST:event_btnMaintenanceActionPerformed

    private void btnRetireAssetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetireAssetActionPerformed
        retireAsset();
    }//GEN-LAST:event_btnRetireAssetActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssetCheckout;
    private javax.swing.JButton btnAssign;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnMaintenance;
    private javax.swing.JButton btnNewAsset;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRetireAsset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblAssetCategory;
    private javax.swing.JLabel lblAvailableAsset;
    private javax.swing.JLabel lblInUseAsset;
    private javax.swing.JLabel lblMaintainAsset;
    private javax.swing.JLabel lblNoAssets;
    private javax.swing.JLabel lblRetiredAsset;
    private javax.swing.JLabel lblValueofAssets;
    private javax.swing.JTable tblAsset;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

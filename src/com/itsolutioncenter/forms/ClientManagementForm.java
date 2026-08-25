
package com.itsolutioncenter.forms;

import com.itsolutioncenter.dao.DatabaseManager;
import com.itsolutioncenter.util.Validator;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ahmad Shafiq Amiri
 */

public class ClientManagementForm extends javax.swing.JInternalFrame {
    private DatabaseManager dbManager=DatabaseManager.getInstance();
    private DefaultTableModel model=new DefaultTableModel();
    private String query,clientName,address,phone,email,contactPerson,clientType,status,note;
    private int row,clientID;
    public ClientManagementForm() {
        initComponents();
        initCompnt();
        loadClientData();
        
    }
    private void initCompnt()
    {
        tblPopUpMenu.add("Edit").addActionListener(e ->loadSelectedRowToForm());
        tblPopUpMenu.add("Delete").addActionListener(e ->{ deleteClient();});
        tblPopUpMenu.add("Refresh").addActionListener(e ->loadClientData());
        //Double click to load data into controls
        tblClient.addMouseListener(new MouseAdapter(){
        @Override
    public void mouseClicked(MouseEvent e)
    {
        if(e.getClickCount()==2)
        {
            int rows=tblClient.rowAtPoint(e.getPoint());
            int col=tblClient.columnAtPoint(e.getPoint());
            if(rows>=0 && col>=0)
            {
                loadSelectedRowToForm();
            } 
        }}
 });}
    private void loadClientData()
    {
       query="select * from clients";
       model=dbManager.getTableModel(query, tblClient);
       tblClient.setModel(model);
    }
    private void loadSelectedRowToForm() { 
    row = tblClient.getSelectedRow();
        try
        { 
             if (row >= 0) {
            clientID=Integer.parseInt(tblClient.getValueAt(row, 0).toString());
            txtClientName.setText(tblClient.getValueAt(row, 1).toString());
            txtContactPerson.setText(tblClient.getValueAt(row, 2).toString());
            txtEmail.setText(tblClient.getValueAt(row, 3).toString());
            txtPhone.setText(tblClient.getValueAt(row, 4).toString());
            txtAddress.setText(tblClient.getValueAt(row, 5).toString());
            cmbClientType.setSelectedItem(tblClient.getValueAt(row, 6).toString());
            txtDate.setDate((Date)tblClient.getValueAt(row, 7));
            cmbStatus.setSelectedItem(tblClient.getValueAt(row, 8).toString());
            txtNote.setText(tblClient.getValueAt(row, 9).toString());
        }
        }catch(NullPointerException e) 
        {
            JOptionPane.showMessageDialog(null, "Some Fields Are Null");
        }
        
        }

    private void searchData() {
    
    // Proceed with action
        String searchText=txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter search criteria: Client ID, Client Name or Phone Number");
            return;
        }
        boolean isNumeric = searchText.matches("\\d+");  
            if (isNumeric) {
                // Search by ID (numeric ID)
                clientID=Integer.parseInt(searchText);
                query = "select * from clients where client_id = '"+clientID+"'";
            } else {
                // Search by username OR full name only (non-numeric)
                query= "select * from clients where client_name LIKE '"+searchText+"' OR phone LIKE '"+searchText+"'";
            }
            model=dbManager.getTableModel(query,tblClient);
            tblClient.setModel(model); 
            txtSearch.setText("");
    }
     private void deleteClient() {
        row = tblClient.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete");
            return;
        }
        clientID = (int) tblClient.getValueAt(row, 0);
        clientName = tblClient.getValueAt(row, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Client: " + clientName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
               dbManager.delete("clients", "client_id = ?", clientID);
                JOptionPane.showMessageDialog(this, "Client deleted successfully!");
                loadClientData(); }
        } 
     private void insertData()
     {
         if(!ValidateControls()) return;
         clientName=txtClientName.getText();
     }
     private boolean ValidateControls()
     {
         if(!Validator.validateRequired(txtClientName, "Client Name"))
         {
             return false;
         }
         if(!Validator.validateRequired(txtContactPerson, "Contact Person"))
         {
             return false;
         }
         if(!txtEmail.getText().isBlank())
         {
             if(!Validator.isEmailValid(txtEmail.getText(), "Email", txtEmail))
             {
                 return false;
             }
         }
         if(!Validator.validateRequired(txtPhone, "Phone Number"))
         {
             return false;
         }
         if(!Validator.isPhoneValid(txtPhone.getText(), txtPhone, "Phone Number"))
         {
             return false;
         }
         if(cmbClientType.getSelectedIndex()<1)
         {
             JOptionPane.showMessageDialog(this, "Please select client type!");
             return false;
         }
         if(!Validator.validateDateRequired(txtDate,"Date"))
         {
             return false;
         }
         if(!Validator.isDateValid(txtDate.getDate(),txtDate))
         {
             return false;
         }
         if(cmbStatus.getSelectedIndex()<1)
         {
             JOptionPane.showMessageDialog(this, "Please select client status!");
             return false;
         }
         return true;
     }
     private void clearControls()
     {
         txtSearch.setText("");
         cmbClientType.setSelectedIndex(0);
         cmbClientTypeFilter.setSelectedIndex(0);
         cmbStatus.setSelectedIndex(0);
         cmbStatusFilter.setSelectedIndex(0);
         txtClientName.setText("");
         txtContactPerson.setText("");
         txtEmail.setText("");
         txtPhone.setText("");
         txtAddress.setText("");
         txtDate.setDate(null);
         txtNote.setText("");
     }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tblPopUpMenu = new javax.swing.JPopupMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClient = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        cmbClientTypeFilter = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        cmbStatusFilter = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        btnInsert = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtClientName = new javax.swing.JTextField();
        cmbClientType = new javax.swing.JComboBox<>();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtContactPerson = new javax.swing.JTextField();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNote = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Client Management Form");

        tblClient.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Client ID", "Cient Name", "Contact Person", "Email", "Phone", "Address", "Client Type", "Registeration Date", "Status", "Notes", "Created At"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        tblClient.setCellSelectionEnabled(false);
        tblClient.setRowSelectionAllowed(true);
        tblClient.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblClient.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblClient.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblClient);
        tblClient.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (tblClient.getColumnModel().getColumnCount() > 0) {
            tblClient.getColumnModel().getColumn(0).setResizable(false);
            tblClient.getColumnModel().getColumn(1).setResizable(false);
            tblClient.getColumnModel().getColumn(2).setResizable(false);
            tblClient.getColumnModel().getColumn(3).setResizable(false);
            tblClient.getColumnModel().getColumn(4).setResizable(false);
            tblClient.getColumnModel().getColumn(5).setResizable(false);
            tblClient.getColumnModel().getColumn(6).setResizable(false);
            tblClient.getColumnModel().getColumn(7).setResizable(false);
            tblClient.getColumnModel().getColumn(8).setResizable(false);
            tblClient.getColumnModel().getColumn(9).setResizable(false);
            tblClient.getColumnModel().getColumn(10).setResizable(false);
        }

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        cmbClientTypeFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Client Type", "Individual", "Business", "Government" }));
        cmbClientTypeFilter.setToolTipText("");

        jButton1.setText("Filter");

        cmbStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Client Status", "Active", "Inactive", "Suspended" }));
        cmbStatusFilter.setToolTipText("");

        jPanel1.setBackground(new java.awt.Color(0, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Client Operation")));

        btnInsert.setText("Insert");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        jLabel2.setText("Client Status:");

        jLabel1.setText("Client Name:");

        cmbClientType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Client Type", "Individual", "Business", "Government", " " }));

        btnDelete.setText("Delete");

        btnUpdate.setText("Update");

        jLabel4.setText("Registeration Date:");

        jLabel3.setText("Contact Person:");

        txtDate.setDateFormatString("dd-MM-yyyy");

        jLabel5.setText("Email:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Client Status", "Active", "Inactive", "Suspended" }));

        jLabel6.setText("Client Status:");

        jLabel8.setText("Note:");

        jLabel9.setText("Address:");

        jLabel7.setText("Phone:");

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtClientName)
                            .addComponent(cmbClientType, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnInsert))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(7, 7, 7))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtContactPerson, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnDelete)
                        .addGap(165, 165, 165)
                        .addComponent(btnUpdate)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmail)
                            .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNote, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(28, 28, 28)
                                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(180, 180, 180)
                        .addComponent(btnClear)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtContactPerson, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbClientType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel8))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDelete)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnInsert)
                        .addComponent(btnUpdate))
                    .addComponent(btnClear))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85)
                .addComponent(btnRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 277, Short.MAX_VALUE)
                .addComponent(cmbClientTypeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jButton1)
                .addGap(83, 83, 83))
            .addComponent(jScrollPane1)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbClientTypeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh)
                    .addComponent(btnSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
       searchData();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadClientData();
        clearControls();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        insertData();
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearControls();
    }//GEN-LAST:event_btnClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbClientType;
    private javax.swing.JComboBox<String> cmbClientTypeFilter;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> cmbStatusFilter;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblClient;
    private javax.swing.JPopupMenu tblPopUpMenu;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtClientName;
    private javax.swing.JTextField txtContactPerson;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNote;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

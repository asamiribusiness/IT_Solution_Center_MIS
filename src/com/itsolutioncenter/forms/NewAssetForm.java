
package com.itsolutioncenter.forms;

import com.itsolutioncenter.service.AssetService;
import com.itsolutioncenter.util.DateUtil;
import com.itsolutioncenter.util.Validator;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class NewAssetForm extends JDialog {
    private AssetService assetService = new AssetService();
    private int assetId = 0;
    private boolean isEditMode = false;
    public NewAssetForm(JFrame parent, boolean modal) {
     super(parent,modal);
        initComponents();
        initAction();
    }
     public NewAssetForm(JFrame parent, boolean modal, int assetId) {
        super(parent, modal);
        this.assetId = assetId;
        this.isEditMode = true;
        initComponents();
        setTitle("Edit Asset");
        loadAssetData();
        initAction();
    }
     private void initAction()
     {
         setLocationRelativeTo(null);
       // Set default values
        if (!isEditMode) {
            txtPurchaseDate.setDate(DateUtil.getCurrentDate());
            txtLocation.setText("Main Office");
        }  
     }
private void loadAssetData() {
        try {
            Map<String, Object> asset = assetService.getAssetById(assetId);
            if (asset != null) {
                txtAssetTag.setText((String) asset.get("asset_tag"));
                txtAssetName.setText((String) asset.get("asset_name"));
                cmbCategory.setSelectedItem(asset.get("category"));
                txtSerialNo.setText((String) asset.get("serial_number"));
               
                Date purchaseDate = (Date) asset.get("purchase_date");
                if (purchaseDate != null) {
                    txtPurchaseDate.setDate(purchaseDate);
                }
               
                txtPurchaseCost.setText(String.valueOf(asset.get("purchase_cost")));
                txtCurrentValue.setText(String.valueOf(asset.get("current_value")));
               
                if (cmbStatus != null) {
                    cmbStatus.setSelectedItem(asset.get("status"));
                }
               
                txtLocation.setText((String) asset.get("location"));
                txtNote.setText((String) asset.get("notes"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading asset data: " + e.getMessage());
        }
    }
   
    private void saveAsset() {
        try {
            // Validate required fields
            Validator.validateRequired(txtAssetTag.getText(), "Asset Tag");
            Validator.validateRequired(txtAssetName.getText(), "Asset Name");
            Validator.validateRequired(txtPurchaseCost.getText(), "Purchase Cost");
            Validator.validateNumeric(txtPurchaseCost.getText(), "Purchase Cost");
           
            if (!txtCurrentValue.getText().trim().isEmpty()) {
                Validator.validateNumeric(txtCurrentValue.getText(), "Current Value");
            }
           
            // Prepare asset data
            Map<String, Object> assetData = new HashMap<>();
            assetData.put("asset_tag", txtAssetTag.getText().trim());
            assetData.put("asset_name", txtAssetName.getText().trim());
            assetData.put("category", cmbCategory.getSelectedItem());
            assetData.put("serial_number", txtSerialNo.getText().trim());
            assetData.put("location", txtLocation.getText().trim());
            assetData.put("notes", txtNote.getText().trim());
           
            // Parse dates
            if (!txtPurchaseDate.getDate().equals("")) {
                    assetData.put("purchase_date", txtPurchaseDate); 
            }
           
            // Parse numeric values
            double purchaseCost = Double.parseDouble(txtPurchaseCost.getText().trim());
            assetData.put("purchase_cost", purchaseCost);
           
            if (!txtCurrentValue.getText().trim().isEmpty()) {
                double currentValue = Double.parseDouble(txtCurrentValue.getText().trim());
                assetData.put("current_value", currentValue);
            } else {
                // Default current value to purchase cost
                assetData.put("current_value", purchaseCost);
            }
           
            // Status (for edit mode)
            if (isEditMode && cmbStatus != null) {
                assetData.put("status", cmbStatus.getSelectedItem());
            }
           
            // Save to database
            boolean success;
            if (isEditMode) {
                success = assetService.updateAsset(assetId, assetData);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Asset updated successfully!");
                    dispose();
                }
            } else {
                int newAssetId = assetService.addAsset(
                    txtAssetTag.getText().trim(),
                    txtAssetName.getText().trim(),
                    (String) cmbCategory.getSelectedItem(),
                    txtSerialNo.getText().trim(),
                    assetData.get("purchase_date") != null ? (Date) assetData.get("purchase_date") : null,
                    purchaseCost,
                    txtLocation.getText().trim()
                );
               
                if (newAssetId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Asset added successfully!\nAsset ID: " + newAssetId);
                    dispose();
                }
            }
           
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        txtAssetTag = new javax.swing.JTextField();
        txtAssetName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtSerialNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtPurchaseCost = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCurrentValue = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtNote = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        txtPurchaseDate = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add New Asset");
        setResizable(false);

        jLabel1.setText("Asset Tag:");

        jLabel2.setText("Asset Name:");

        jLabel3.setText("Serial Number:");

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Category ---", "IT Equipment", "Office Equipment", "Office Supply", "Other" }));

        jLabel4.setText("Category");

        jLabel5.setText("Purchase Date:");

        jLabel6.setText("Purchase Cost:");

        jLabel7.setText("Current Value:");

        txtNote.setColumns(20);
        txtNote.setRows(5);
        jScrollPane1.setViewportView(txtNote);

        jLabel8.setText("Note");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- All Statuses ---", "Available", "In_Use", "Maintenance", "Retired", "Lost" }));

        jLabel9.setText("Status:");

        jLabel10.setText("Location:");

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        txtPurchaseDate.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(198, 198, 198))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtSerialNo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel4)
                                            .addGap(46, 46, 46)
                                            .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(txtAssetName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(40, 40, 40)
                                                .addComponent(txtAssetTag, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addGap(22, 22, 22)
                                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton2))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel8)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtCurrentValue, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                    .addComponent(txtPurchaseCost, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)))
                            .addComponent(txtPurchaseDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(58, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtAssetTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtAssetName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSerialNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtPurchaseDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtPurchaseCost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtCurrentValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        saveAsset();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(NewAssetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(NewAssetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(NewAssetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(NewAssetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                NewAssetForm dialog = new NewAssetForm(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtAssetName;
    private javax.swing.JTextField txtAssetTag;
    private javax.swing.JTextField txtCurrentValue;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextArea txtNote;
    private javax.swing.JTextField txtPurchaseCost;
    private com.toedter.calendar.JDateChooser txtPurchaseDate;
    private javax.swing.JTextField txtSerialNo;
    // End of variables declaration//GEN-END:variables
}


package com.itsolutioncenter.forms;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import com.itsolutioncenter.model.StudentPayment;
import com.itsolutioncenter.util.PayslipGenerator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayslipPrintForm extends javax.swing.JFrame {
    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JTextField txtCourse;
    private JTextField txtAmount;
    private JTextField txtTransactionId;
    private JComboBox<String> cmbPaymentMethod;
    private JButton btnGenerate;
    private JButton btnPrint;
    private JButton btnPreview;
    private JTextArea txtReceiptPreview;
   
    public PayslipPrintForm() {
        initComponents();
    }
   
    private void initComponents() {
        // Initialize components
        setTitle("Student Payslip Generator");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
       
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
       
        // Add form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        txtStudentId = new JTextField(20);
        formPanel.add(txtStudentId, gbc);
       
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        txtStudentName = new JTextField(20);
        formPanel.add(txtStudentName, gbc);
       
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        txtCourse = new JTextField(20);
        formPanel.add(txtCourse, gbc);
       
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Amount Paid:"), gbc);
        gbc.gridx = 1;
        txtAmount = new JTextField(20);
        formPanel.add(txtAmount, gbc);
       
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        cmbPaymentMethod = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Online Payment"});
        formPanel.add(cmbPaymentMethod, gbc);
       
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Transaction ID:"), gbc);
        gbc.gridx = 1;
        txtTransactionId = new JTextField(20);
        txtTransactionId.setText("TXN" + System.currentTimeMillis());
        formPanel.add(txtTransactionId, gbc);
       
        // Button panel
        JPanel buttonPanel = new JPanel();
        btnGenerate = new JButton("Generate Receipt");
        btnPrint = new JButton("Print Receipt");
        btnPreview = new JButton("Preview");
       
        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnPreview);
        buttonPanel.add(btnPrint);
       
        // Preview area
        txtReceiptPreview = new JTextArea(20, 50);
        txtReceiptPreview.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtReceiptPreview);
       
        // Add action listeners
        btnGenerate.addActionListener(e -> generateReceipt());
        btnPreview.addActionListener(e -> previewReceipt());
        btnPrint.addActionListener(e -> printReceipt());
       
        // Add components to frame
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
       
        pack();
        setLocationRelativeTo(null);
    }
   
    private void generateReceipt() {
        try {
            // Create payment object
            StudentPayment payment = new StudentPayment();
            payment.setStudentId(txtStudentId.getText());
            payment.setStudentName(txtStudentName.getText());
            payment.setCourseName(txtCourse.getText());
            payment.setAmountPaid(Double.parseDouble(txtAmount.getText()));
            payment.setPaymentMethod(cmbPaymentMethod.getSelectedItem().toString());
            payment.setTransactionId(txtTransactionId.getText());
            payment.setPaymentDate(new Date());
           
            // Generate receipt text
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String receipt = "========================================\n" +
                           "        PAYMENT RECEIPT\n" +
                           "========================================\n" +
                           "Receipt No: " + payment.getTransactionId() + "\n" +
                           "Date: " + sdf.format(payment.getPaymentDate()) + "\n\n" +
                           "STUDENT INFORMATION:\n" +
                           "Student ID: " + payment.getStudentId() + "\n" +
                           "Student Name: " + payment.getStudentName() + "\n" +
                           "Course: " + payment.getCourseName() + "\n\n" +
                           "PAYMENT DETAILS:\n" +
                           "Amount Paid: AFN. " + String.format("%.2f", payment.getAmountPaid()) + "\n" +
                           "Payment Method: " + payment.getPaymentMethod() + "\n" +
                           "========================================\n" +
                           "        THANK YOU!\n" +
                           "========================================\n";
           
            txtReceiptPreview.setText(receipt);
           
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
        }
    }
   
    private void previewReceipt() {
        generateReceipt();
        JOptionPane.showMessageDialog(this, "Receipt preview updated!");
    }
   
    private void printReceipt() {
        try {
            StudentPayment payment = new StudentPayment(
                txtStudentId.getText(),
                txtStudentName.getText(),
                txtCourse.getText(),
                Double.parseDouble(txtAmount.getText()),
                new Date(),
                cmbPaymentMethod.getSelectedItem().toString()
            );
            payment.setTransactionId(txtTransactionId.getText());
           
            PayslipGenerator payslip = new PayslipGenerator(payment);
            payslip.printPayslip();
           
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid data!");
        }
    }
   
    // Main method to test the form
    public static void main(String args[]) {
        EventQueue.invokeLater(() -> {
            new PayslipPrintForm().setVisible(true);
        });
    }
}
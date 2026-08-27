
package com.itsolutioncenter.util;

import com.itsolutioncenter.model.StudentPayment;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class PayslipGenerator implements Printable {
    private StudentPayment payment;
   
    public PayslipGenerator(StudentPayment payment) {
        this.payment = payment;
    }
   
    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }
       
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
       
        // Set font and color
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.BLACK);
       
        // Draw header
        g2d.drawString("PAYMENT RECEIPT", 200, 50);
        g2d.drawLine(50, 60, 550, 60);
       
        // Institution details
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Farah IT Solution Center", 250, 80);
        g2d.drawString("Address: Sayed Mir Ajab Street", 230, 95);
        g2d.drawString("Contact: +93799063252 | Email: info@itsolution.com", 150, 110);
       
        g2d.drawLine(50, 120, 550, 120);
       
        // Payment details
        int y = 150;
        g2d.drawString("Receipt No: " + payment.getTransactionId(), 50, y);
        y += 20;
       
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        g2d.drawString("Date: " + sdf.format(payment.getPaymentDate()), 50, y);
        y += 30;
       
        // Student information
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("STUDENT INFORMATION", 50, y);
        y += 20;
       
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Student ID: " + payment.getStudentId(), 50, y);
        g2d.drawString("Student Name: " + payment.getStudentName(), 300, y);
        y += 20;
       
        g2d.drawString("Course: " + payment.getCourseName(), 50, y);
        y += 30;
       
        // Payment details box
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("PAYMENT DETAILS", 50, y);
        y += 20;
       
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(50, y - 10, 500, 100);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(50, y - 10, 500, 100);
       
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Amount Paid: AFN. " + String.format("%.2f", payment.getAmountPaid()), 70, y + 20);
        g2d.drawString("Payment Method: " + payment.getPaymentMethod(), 70, y + 40);
       
        if (payment.getBalanceDue() > 0) {
            g2d.drawString("Balance Due: AFN. " + String.format("%.2f", payment.getBalanceDue()), 70, y + 60);
        } else {
            g2d.drawString("Payment Status: FULLY PAID", 70, y + 60);
        }
       
        y += 120;
       
        // Footer and signature
        g2d.drawLine(50, y, 550, y);
        y += 20;
       
        g2d.drawString("Authorized Signature", 400, y);
        g2d.drawLine(400, y + 5, 520, y + 5);
       
        y += 30;
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.drawString("This is a computer generated receipt. No signature required.", 150, y);
       
        return PAGE_EXISTS;
    }
   
    public void printPayslip() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Student Payment Receipt");
        job.setPrintable(this);
       
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, "Print Error: " + e.getMessage());
            }
        }
    }
}
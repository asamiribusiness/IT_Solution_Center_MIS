
package com.itsolutioncenter.model;

import java.util.Date;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
public class StudentPayment {
    private String studentId;
    private String studentName;
    private String courseName;
    private double amountPaid;
    private Date paymentDate;
    private String paymentMethod;
    private String transactionId;
    private double balanceDue;
   
    // Constructors, getters, and setters
    public StudentPayment() {}
   
    public StudentPayment(String studentId, String studentName, String courseName,
                         double amountPaid, Date paymentDate, String paymentMethod) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }
   
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
   
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getTransactionId(){return transactionId;}
    public void setTransactionId(String transactionId){this.transactionId=transactionId;}
    public Date getPaymentDate(){return paymentDate;}
    public void setPaymentDate(Date paymentDate){this.paymentDate=paymentDate;}
    public String getCourseName(){return courseName;}
    public void setCourseName(String courseName){this.courseName=courseName;}
    public String getPaymentMethod(){return paymentMethod;}
    public void setPaymentMethod(String paymentMethod){this.paymentMethod=paymentMethod;}
    public double getAmountPaid(){return amountPaid;}
    public void setAmountPaid(double amountPaid){this.amountPaid=amountPaid;}
    public double getBalanceDue(){return balanceDue;}
    public void setBalanceDue(double balanceDue){this.balanceDue=balanceDue;}
    // ... other getters and setters
}

package com.itsolutioncenter.model;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import java.awt.Image;

public class Student {
    private String studentId;
    private String fullName;
    private String fathername;
    private String email;
    private String phone;
    private String imagePath;
    private Image image;
   
    // Constructors
    public Student() {}
   
    public Student(String studentId, String fullName, String fathername,String email,
                  String phone, String imagePath) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.fathername = fathername;
        this.email = email;
        this.phone = phone;
        this.imagePath = imagePath;
    }
   
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
   
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
   
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
   
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
   
    public String getFathername() { return fathername; }
    public void setCourse(String fathername) { this.fathername = fathername; }
   
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
   
    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }
}

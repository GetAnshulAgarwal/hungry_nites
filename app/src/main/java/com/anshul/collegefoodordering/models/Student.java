// Student.java
package com.anshul.collegefoodordering.models;

public class Student extends User {
    private String studentId;

    public Student() {
        // Required empty constructor for Firebase
    }

    public Student(String uid, String email, String name, String studentId) {
        super(uid, email, name, "student");
        this.studentId = studentId;
    }

    // Getter and setter
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}

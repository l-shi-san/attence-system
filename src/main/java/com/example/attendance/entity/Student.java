package com.example.attendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true, length = 20)
    private String studentId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "age")
    private Integer age;

    public Student() {}

    public Student(String studentId, String name, String className, Integer age) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.age = age;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
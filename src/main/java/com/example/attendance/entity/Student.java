package com.example.attendance.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Column(name = "student_no", nullable = false, unique = true, length = 20)
    private String studentNo;  // 学号

    @Column(name = "name", nullable = false, length = 50)
    private String name;  // 姓名

    @Column(name = "gender", length = 10)
    private String gender;  // 性别：男/女

    @Column(name = "birth_date")
    private LocalDate birthDate;  // 出生日期

    @Column(name = "phone", length = 20)
    private String phone;  // 联系方式

    @Column(name = "class_name", length = 50)
    private String className;  // 班级

    @Column(name = "status")
    private Integer status;  // 状态：1-在读，0-休学

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;


    public Student(Integer id, String studentNo, String name, LocalDate birthDate, String gender, String className, String phone, Integer status, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.studentNo = studentNo;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.className = className;
        this.phone = phone;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Student() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
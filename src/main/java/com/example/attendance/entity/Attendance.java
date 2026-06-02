package com.example.attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id")
    private Integer studentId;  // 学生ID

    @Column(name = "student_no", length = 20)
    private String studentNo;  // 学号

    @Column(name = "student_name", length = 50)
    private String studentName;  // 学生姓名

    @Column(name = "course_id")
    private Integer courseId;  // 课程ID

    @Column(name = "course_name", length = 100)
    private String courseName;  // 课程名称（冗余字段）

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;  // 打卡时间

    @Column(name = "seat_row")
    private Integer seatRow;  // 座位行

    @Column(name = "seat_col")
    private Integer seatCol;  // 座位列

    @Column(length = 20)
    private String status;  // NORMAL正常/LATE迟到/EARLY早退/ABSENT缺勤

    @Column(length = 15)
    private String ip;  // IP地址

    @Column(length = 255)
    private String remark;  // 备注

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public Attendance(Integer id, Integer studentId, String studentNo, String studentName, Integer courseId, String courseName, LocalDateTime checkInTime, Integer seatRow, Integer seatCol, String status, String ip, String remark, LocalDateTime createTime) {
        this.id = id;
        this.studentId = studentId;
        this.studentNo = studentNo;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.checkInTime = checkInTime;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.status = status;
        this.ip = ip;
        this.remark = remark;
        this.createTime = createTime;
    }

    public Attendance() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Integer getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Integer seatRow) {
        this.seatRow = seatRow;
    }

    public Integer getSeatCol() {
        return seatCol;
    }

    public void setSeatCol(Integer seatCol) {
        this.seatCol = seatCol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
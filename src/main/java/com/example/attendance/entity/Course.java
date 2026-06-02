package com.example.attendance.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;  // 课程名称

    @Column(name = "course_code", length = 50)
    private String courseCode;  // 课程编号

    @Column(name = "teacher_name", length = 50)
    private String teacherName;  // 授课教师

    @Column(name = "start_time")
    private LocalTime startTime;  // 上课时间

    @Column(name = "end_time")
    private LocalTime endTime;  // 下课时间

    @Column(name = "week_day")
    private Integer weekDay;  // 星期几（1-7）

    @Column(name = "classroom", length = 50)
    private String classroom;  // 教室

    @Column(name = "status")
    private Integer status = 1;  // 状态：1-启用，0-停用

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (status == null) {
            status = 1;
        }
    }

    public Course(Integer id, String courseName, String courseCode, String teacherName, LocalTime startTime, LocalTime endTime, Integer weekDay, String classroom, Integer status, LocalDateTime createTime) {
        this.id = id;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDay = weekDay;
        this.classroom = classroom;
        this.status = status;
        this.createTime = createTime;
    }

    public Course() {
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
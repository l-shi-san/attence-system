package com.example.attendance.dto;

public class AttendanceRequest {
    private String studentId;

    public AttendanceRequest() {
    }

    public AttendanceRequest(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "AttendanceRequest{" +
                "studentId='" + studentId + '\'' +
                '}';
    }
}

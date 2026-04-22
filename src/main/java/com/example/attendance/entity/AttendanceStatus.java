package com.example.attendance.entity;

public enum AttendanceStatus {
    NORMAL("正常"),
    LATE("迟到"),
    ABSENT("缺勤"),
    LEAVE("请假");

    private final String description;

    AttendanceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
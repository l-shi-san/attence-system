package com.example.attendance.dto;

import com.example.attendance.entity.Attendance;

public class CheckInResult {
    private final Attendance attendance;
    private final String errorMsg;

    private CheckInResult(Attendance attendance, String errorMsg) {
        this.attendance = attendance;
        this.errorMsg = errorMsg;
    }

    public static CheckInResult success(Attendance attendance) { return new CheckInResult(attendance, null); }
    public static CheckInResult fail(String errorMsg) { return new CheckInResult(null, errorMsg); }

    public boolean isSuccess() { return attendance != null; }
    public Attendance getAttendance() { return attendance; }
    public String getErrorMsg() { return errorMsg; }
}

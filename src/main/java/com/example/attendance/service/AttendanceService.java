package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    // 创建考勤记录
    Attendance createAttendance(Attendance attendance);

    // 根据ID查询考勤记录
    Attendance findAttendanceById(Long id);

    // 查询所有考勤记录
    List<Attendance> findAllAttendances();

    // 根据学生ID查询
    List<Attendance> findByStudentId(String studentId);

    // 根据课程ID查询
    List<Attendance> findByCourseId(String courseId);

    // 根据状态查询
    List<Attendance> findByStatus(AttendanceStatus status);

    // 更新考勤记录
    Attendance updateAttendance(Long id, Attendance attendance);

    // 删除考勤记录
    void deleteAttendance(Long id);

    // 统计课程考勤情况
    Map<AttendanceStatus, Long> getCourseAttendanceStatistics(String courseId);

    // 查询迟到学生
    List<Attendance> findLateStudentsByCourse(String courseId);

    // 根据时间范围查询
    List<Attendance> findByTimeRange(LocalDateTime start, LocalDateTime end);
}
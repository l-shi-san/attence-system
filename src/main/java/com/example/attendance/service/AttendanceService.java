package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    // 原有的方法...
    Attendance createAttendance(Attendance attendance);
    Attendance findAttendanceById(Long id);
    List<Attendance> findAllAttendances();
    List<Attendance> findByStudentId(String studentId);
    List<Attendance> findByCourseId(String courseId);
    List<Attendance> findByStatus(AttendanceStatus status);
    Attendance updateAttendance(Long id, Attendance attendance);
    void deleteAttendance(Long id);
    Map<AttendanceStatus, Long> getCourseAttendanceStatistics(String courseId);
    List<Attendance> findLateStudentsByCourse(String courseId);
    List<Attendance> findByTimeRange(LocalDateTime start, LocalDateTime end);

    // ========== 新增分页和多条件查询方法 ==========

    /**
     * 分页查询所有考勤记录（支持排序）
     */
    Page<Attendance> findAttendancesWithPagination(Pageable pageable);

    /**
     * 动态条件分页查询
     */
    Page<Attendance> findAttendancesByCondition(String studentId,
                                                LocalDateTime startTime,
                                                LocalDateTime endTime,
                                                String status,
                                                String courseId,
                                                Pageable pageable);
}
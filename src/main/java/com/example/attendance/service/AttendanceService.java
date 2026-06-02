package com.example.attendance.service;

import com.example.attendance.dto.AttendanceStatisticsDTO;
import com.example.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance save(Attendance attendance);

    Attendance findById(Integer id);

    List<Attendance> findAll();

    Page<Attendance> findAll(Pageable pageable);

    Page<Attendance> findByStudentId(Integer studentId, Pageable pageable);

    List<Attendance> findByStudentId(Integer studentId);

    void deleteById(Integer id);

    Attendance checkIn(Integer studentId, Integer courseId, String remark, String ip);

    Integer getLateCount(Integer studentId);

    Integer getNormalCount(Integer studentId);

    Page<Attendance> search(String keyword, Pageable pageable);

    // ===== 考勤统计方法 =====

    /**
     * 按日期范围统计某学生的考勤
     */
    AttendanceStatisticsDTO getStatisticsByStudentAndDateRange(
            Integer studentId, LocalDate startDate, LocalDate endDate);

    /**
     * 按日期范围统计全局考勤
     */
    AttendanceStatisticsDTO getStatisticsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 按周统计考勤（最近 N 周）
     */
    List<AttendanceStatisticsDTO> getWeeklyStatistics(int weeks);

    /**
     * 按月统计考勤（最近 N 个月）
     */
    List<AttendanceStatisticsDTO> getMonthlyStatistics(int months);
}
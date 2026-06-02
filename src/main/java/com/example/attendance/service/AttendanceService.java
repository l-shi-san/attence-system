package com.example.attendance.service;

import com.example.attendance.dto.AttendanceStatisticsDTO;
import com.example.attendance.dto.CheckInResult;
import com.example.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
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
    CheckInResult checkIn(Integer studentId, Integer courseId, String remark, String ip);
    Integer getLateCount(Integer studentId);
    Integer getNormalCount(Integer studentId);
    Page<Attendance> search(String keyword, Pageable pageable);

    AttendanceStatisticsDTO getStatisticsByStudentAndDateRange(Integer studentId, LocalDate startDate, LocalDate endDate);
    AttendanceStatisticsDTO getStatisticsByDateRange(LocalDate startDate, LocalDate endDate);
    byte[] exportAttendanceToExcel(LocalDate startDate, LocalDate endDate) throws IOException;
    List<AttendanceStatisticsDTO> getStatisticsByCourse(LocalDate startDate, LocalDate endDate);
    List<AttendanceStatisticsDTO> getWeeklyStatistics(int weeks);
    List<AttendanceStatisticsDTO> getMonthlyStatistics(int months);
}

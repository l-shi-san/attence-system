package com.example.attendance.service.impl;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.specification.AttendanceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // 原有的方法实现...

    @Override
    public Attendance createAttendance(Attendance attendance) {
        if (attendance.getCreateTime() == null) {
            attendance.setCreateTime(LocalDateTime.now());
        }
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance findAttendanceById(Long id) {
        return attendanceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Attendance> findAllAttendances() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> findByStudentId(String studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    @Override
    public List<Attendance> findByCourseId(String courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }

    @Override
    public List<Attendance> findByStatus(AttendanceStatus status) {
        return attendanceRepository.findByStatus(status);
    }

    @Override
    public Attendance updateAttendance(Long id, Attendance attendance) {
        Attendance existingAttendance = findAttendanceById(id);
        if (existingAttendance != null) {
            existingAttendance.setStudentId(attendance.getStudentId());
            existingAttendance.setStudentName(attendance.getStudentName());
            existingAttendance.setCourseId(attendance.getCourseId());
            existingAttendance.setCheckInTime(attendance.getCheckInTime());
            existingAttendance.setSeatRow(attendance.getSeatRow());
            existingAttendance.setSeatCol(attendance.getSeatCol());
            existingAttendance.setStatus(attendance.getStatus());
            existingAttendance.setIp(attendance.getIp());
            return attendanceRepository.save(existingAttendance);
        }
        return null;
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public Map<AttendanceStatus, Long> getCourseAttendanceStatistics(String courseId) {
        List<Object[]> results = attendanceRepository.countByCourseIdGroupByStatus(courseId);
        Map<AttendanceStatus, Long> statistics = new HashMap<>();

        for (Object[] result : results) {
            AttendanceStatus status = (AttendanceStatus) result[0];
            Long count = (Long) result[1];
            statistics.put(status, count);
        }

        for (AttendanceStatus status : AttendanceStatus.values()) {
            statistics.putIfAbsent(status, 0L);
        }

        return statistics;
    }

    @Override
    public List<Attendance> findLateStudentsByCourse(String courseId) {
        return attendanceRepository.findByCourseIdAndStatus(courseId, AttendanceStatus.LATE);
    }

    @Override
    public List<Attendance> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.findByCheckInTimeBetween(start, end);
    }

    // ========== 新增分页方法实现 ==========

    @Override
    public Page<Attendance> findAttendancesWithPagination(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    @Override
    public Page<Attendance> findAttendancesByCondition(String studentId,
                                                       LocalDateTime startTime,
                                                       LocalDateTime endTime,
                                                       String status,
                                                       String courseId,
                                                       Pageable pageable) {
        Specification<Attendance> spec = Specification
                .where(AttendanceSpecification.hasStudentId(studentId))
                .and(AttendanceSpecification.hasCourseId(courseId))
                .and(AttendanceSpecification.hasStatus(status))
                .and(AttendanceSpecification.hasCheckInTimeBetween(startTime, endTime));

        return attendanceRepository.findAll(spec, pageable);
    }
}
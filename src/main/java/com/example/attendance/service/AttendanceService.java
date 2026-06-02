package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}
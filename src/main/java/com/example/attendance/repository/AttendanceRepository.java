package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer>, JpaSpecificationExecutor<Attendance> {

    List<Attendance> findByStudentId(Integer studentId);

    Page<Attendance> findByStudentId(Integer studentId, Pageable pageable);

    List<Attendance> findByCourseId(Integer courseId);

    boolean existsByStudentIdAndCourseIdAndCheckInTimeBetween(
            Integer studentId, Integer courseId, LocalDateTime start, LocalDateTime end);

    List<Attendance> findByStudentIdAndCheckInTimeBetween(
            Integer studentId, LocalDateTime start, LocalDateTime end);

    Integer countByStudentIdAndStatus(Integer studentId, String status);

    Page<Attendance> findByStudentNoContainingOrStudentNameContainingOrCourseNameContaining(
            String studentNo, String studentName, String courseName, Pageable pageable);
}
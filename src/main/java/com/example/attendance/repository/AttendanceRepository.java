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

    // ===== 考勤统计查询 =====

    /** 统计某学生在指定日期范围内的总记录数 */
    long countByStudentIdAndCheckInTimeBetween(Integer studentId, LocalDateTime start, LocalDateTime end);

    /** 统计某学生在指定日期范围内按状态的记录数 */
    long countByStudentIdAndStatusAndCheckInTimeBetween(
            Integer studentId, String status, LocalDateTime start, LocalDateTime end);

    /** 统计指定日期范围内所有考勤记录 */
    long countByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

    /** 统计指定日期范围内按状态的记录数 */
    long countByStatusAndCheckInTimeBetween(String status, LocalDateTime start, LocalDateTime end);

    /** 查询指定日期范围内的所有记录（按时间升序） */
    List<Attendance> findByCheckInTimeBetweenOrderByCheckInTimeAsc(LocalDateTime start, LocalDateTime end);
}
package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
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
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    // 原有的方法...
    List<Attendance> findByStudentId(String studentId);
    List<Attendance> findByCourseId(String courseId);
    List<Attendance> findByStatus(AttendanceStatus status);
    List<Attendance> findByStudentIdAndCourseId(String studentId, String courseId);
    List<Attendance> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Attendance> findByCourseIdAndStatus(String courseId, AttendanceStatus status);

    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.courseId = :courseId GROUP BY a.status")
    List<Object[]> countByCourseIdGroupByStatus(@Param("courseId") String courseId);

    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.courseId = :courseId ORDER BY a.checkInTime DESC")
    List<Attendance> findLatestByStudentIdAndCourseId(@Param("studentId") String studentId, @Param("courseId") String courseId);

    // ========== 新增分页查询方法 ==========

    /**
     * 分页查询所有考勤记录
     */
    Page<Attendance> findAll(Pageable pageable);

    /**
     * 根据学生ID分页查询
     */
    Page<Attendance> findByStudentId(String studentId, Pageable pageable);

    /**
     * 根据课程ID分页查询
     */
    Page<Attendance> findByCourseId(String courseId, Pageable pageable);

    /**
     * 根据状态分页查询
     */
    Page<Attendance> findByStatus(AttendanceStatus status, Pageable pageable);

    /**
     * 根据时间范围分页查询
     */
    Page<Attendance> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
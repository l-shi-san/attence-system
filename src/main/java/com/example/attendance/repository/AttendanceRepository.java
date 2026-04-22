package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 根据学生ID查询考勤记录
    List<Attendance> findByStudentId(String studentId);

    // 根据课程ID查询考勤记录
    List<Attendance> findByCourseId(String courseId);

    // 根据状态查询考勤记录
    List<Attendance> findByStatus(AttendanceStatus status);

    // 根据学生ID和课程ID查询
    List<Attendance> findByStudentIdAndCourseId(String studentId, String courseId);

    // 查询某时间范围内的考勤记录
    List<Attendance> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

    // 查询某课程某状态的学生
    List<Attendance> findByCourseIdAndStatus(String courseId, AttendanceStatus status);

    // 统计某课程的各种状态数量
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.courseId = :courseId GROUP BY a.status")
    List<Object[]> countByCourseIdGroupByStatus(@Param("courseId") String courseId);

    // 查询某学生某课程的最新考勤记录
    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.courseId = :courseId ORDER BY a.checkInTime DESC")
    List<Attendance> findLatestByStudentIdAndCourseId(@Param("studentId") String studentId, @Param("courseId") String courseId);
}
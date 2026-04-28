package com.example.attendance.controller;

import com.example.attendance.common.Result;
import com.example.attendance.config.AttendanceSortConfig;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.specification.AttendanceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // ========== 原有接口 ==========

    @PostMapping("/create")
    public Result<Attendance> createAttendance(@RequestBody Attendance attendance) {
        Attendance created = attendanceService.createAttendance(attendance);
        return Result.success(created);
    }

    @GetMapping("/{id}")
    public Result<Attendance> getAttendanceById(@PathVariable Long id) {
        Attendance attendance = attendanceService.findAttendanceById(id);
        if (attendance != null) {
            return Result.success(attendance);
        }
        return Result.error("考勤记录不存在");
    }

    @GetMapping("/all")
    public Result<List<Attendance>> getAllAttendances() {
        List<Attendance> attendances = attendanceService.findAllAttendances();
        return Result.success(attendances);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Attendance>> getByStudentId(@PathVariable String studentId) {
        List<Attendance> attendances = attendanceService.findByStudentId(studentId);
        return Result.success(attendances);
    }

    @GetMapping("/course/{courseId}")
    public Result<List<Attendance>> getByCourseId(@PathVariable String courseId) {
        List<Attendance> attendances = attendanceService.findByCourseId(courseId);
        return Result.success(attendances);
    }

    @GetMapping("/status/{status}")
    public Result<List<Attendance>> getByStatus(@PathVariable String status) {
        try {
            AttendanceStatus attendanceStatus = AttendanceStatus.valueOf(status.toUpperCase());
            List<Attendance> attendances = attendanceService.findByStatus(attendanceStatus);
            return Result.success(attendances);
        } catch (IllegalArgumentException e) {
            return Result.error("无效的状态值");
        }
    }

    @PutMapping("/update/{id}")
    public Result<Attendance> updateAttendance(@PathVariable Long id, @RequestBody Attendance attendance) {
        Attendance updated = attendanceService.updateAttendance(id, attendance);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("更新失败，考勤记录不存在");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return Result.success("删除成功");
    }

    @GetMapping("/statistics/{courseId}")
    public Result<Map<AttendanceStatus, Long>> getCourseStatistics(@PathVariable String courseId) {
        Map<AttendanceStatus, Long> statistics = attendanceService.getCourseAttendanceStatistics(courseId);
        return Result.success(statistics);
    }

    @GetMapping("/late/{courseId}")
    public Result<List<Attendance>> getLateStudents(@PathVariable String courseId) {
        List<Attendance> lateStudents = attendanceService.findLateStudentsByCourse(courseId);
        return Result.success(lateStudents);
    }

    @GetMapping("/time-range")
    public Result<List<Attendance>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Attendance> attendances = attendanceService.findByTimeRange(start, end);
        return Result.success(attendances);
    }

    // ========== 新增：分页查询接口（支持排序） ==========

    /**
     * 分页查询所有考勤记录（支持排序）
     *
     * 请求参数示例：
     * - page=0&size=10&sortField=checkInTime&sortDirection=desc
     * - page=1&size=20&sortField=studentId&sortDirection=asc
     * - page=0&size=5&sortField=studentName&sortDirection=desc
     */
    @GetMapping("/page")
    public Result<Page<Attendance>> getAttendancePage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = AttendanceSortConfig.createSort(sortField, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Attendance> attendancePage = attendanceService.findAttendancesWithPagination(pageable);

        return Result.success(attendancePage);
    }

    // ========== 新增：多条件分页查询接口（动态查询+排序） ==========

    /**
     * 多条件分页查询考勤记录（支持动态条件和排序）
     *
     * 请求参数示例：
     * 1. 按学号查询：?studentId=20230001&page=0&size=10&sortField=checkInTime&sortDirection=desc
     * 2. 按状态查询：?status=LATE&page=0&size=10
     * 3. 按日期范围查询：?startTime=2023-10-30T00:00:00&endTime=2023-10-30T23:59:59&page=0&size=10
     * 4. 组合查询：?studentId=2023&status=LATE&courseId=CS101&page=0&size=10&sortField=checkInTime&sortDirection=desc
     * 5. 多条件+时间范围：?studentId=202300&startTime=2023-10-30T00:00:00&endTime=2023-10-31T00:00:00&page=0&size=20
     */
    @GetMapping("/search")
    public Result<Page<Attendance>> searchAttendances(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Integer seatRow,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        // 创建排序和分页对象
        Sort sort = AttendanceSortConfig.createSort(sortField, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 使用 Specification 动态查询
        Page<Attendance> attendancePage = attendanceService.findAttendancesByCondition(
                studentId, startTime, endTime, status, courseId, pageable
        );

        return Result.success(attendancePage);
    }
}
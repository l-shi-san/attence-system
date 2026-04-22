package com.example.attendance.controller;

import com.example.attendance.common.Result;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceStatus;
import com.example.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 创建考勤记录
    @PostMapping("/create")
    public Result<Attendance> createAttendance(@RequestBody Attendance attendance) {
        Attendance created = attendanceService.createAttendance(attendance);
        return Result.success(created);
    }

    // 根据ID查询考勤记录
    @GetMapping("/{id}")
    public Result<Attendance> getAttendanceById(@PathVariable Long id) {
        Attendance attendance = attendanceService.findAttendanceById(id);
        if (attendance != null) {
            return Result.success(attendance);
        }
        return Result.error("考勤记录不存在");
    }

    // 查询所有考勤记录
    @GetMapping("/all")
    public Result<List<Attendance>> getAllAttendances() {
        List<Attendance> attendances = attendanceService.findAllAttendances();
        return Result.success(attendances);
    }

    // 根据学生ID查询
    @GetMapping("/student/{studentId}")
    public Result<List<Attendance>> getByStudentId(@PathVariable String studentId) {
        List<Attendance> attendances = attendanceService.findByStudentId(studentId);
        return Result.success(attendances);
    }

    // 根据课程ID查询
    @GetMapping("/course/{courseId}")
    public Result<List<Attendance>> getByCourseId(@PathVariable String courseId) {
        List<Attendance> attendances = attendanceService.findByCourseId(courseId);
        return Result.success(attendances);
    }

    // 根据状态查询
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

    // 更新考勤记录
    @PutMapping("/update/{id}")
    public Result<Attendance> updateAttendance(@PathVariable Long id, @RequestBody Attendance attendance) {
        Attendance updated = attendanceService.updateAttendance(id, attendance);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("更新失败，考勤记录不存在");
    }

    // 删除考勤记录
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return Result.success("删除成功");
    }

    // 获取课程考勤统计
    @GetMapping("/statistics/{courseId}")
    public Result<Map<AttendanceStatus, Long>> getCourseStatistics(@PathVariable String courseId) {
        Map<AttendanceStatus, Long> statistics = attendanceService.getCourseAttendanceStatistics(courseId);
        return Result.success(statistics);
    }

    // 查询课程迟到学生
    @GetMapping("/late/{courseId}")
    public Result<List<Attendance>> getLateStudents(@PathVariable String courseId) {
        List<Attendance> lateStudents = attendanceService.findLateStudentsByCourse(courseId);
        return Result.success(lateStudents);
    }

    // 根据时间范围查询
    @GetMapping("/time-range")
    public Result<List<Attendance>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Attendance> attendances = attendanceService.findByTimeRange(start, end);
        return Result.success(attendances);
    }

    // 批量导入考勤记录（用于初始化数据）
    @PostMapping("/batch")
    public Result<String> batchCreate(@RequestBody List<Attendance> attendances) {
        for (Attendance attendance : attendances) {
            attendanceService.createAttendance(attendance);
        }
        return Result.success("批量导入成功，共导入 " + attendances.size() + " 条记录");
    }
}
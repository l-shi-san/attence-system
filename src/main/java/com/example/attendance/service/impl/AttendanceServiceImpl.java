package com.example.attendance.service.impl;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Attendance save(Attendance attendance) {
        if (attendance.getId() == null) {
            attendance.setCreateTime(LocalDateTime.now());
        }
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance findById(Integer id) {
        return attendanceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    public Page<Attendance> findAll(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    @Override
    public Page<Attendance> findByStudentId(Integer studentId, Pageable pageable) {
        return attendanceRepository.findByStudentId(studentId, pageable);
    }

    @Override
    public List<Attendance> findByStudentId(Integer studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    @Override
    public void deleteById(Integer id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public Attendance checkIn(Integer studentId, Integer courseId, String remark, String ip) {
        // 获取学生信息
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            return null;
        }

        // 获取课程信息
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return null;
        }

        // 检查今天是否已经打卡过这门课
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        boolean alreadyChecked = attendanceRepository.existsByStudentIdAndCourseIdAndCheckInTimeBetween(
                studentId, courseId, startOfDay, endOfDay);

        if (alreadyChecked) {
            return null;  // 已经打卡过了
        }

        // 创建考勤记录
        Attendance attendance = new Attendance();
        attendance.setStudentId(studentId);
        attendance.setStudentNo(student.getStudentNo());
        attendance.setStudentName(student.getName());
        attendance.setCourseId(courseId);
        attendance.setCourseName(course.getCourseName());
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setIp(ip);
        attendance.setRemark(remark);

        // 判断是否迟到
        LocalDateTime now = LocalDateTime.now();
        LocalTime nowTime = now.toLocalTime();
        LocalTime startTime = course.getStartTime();

        if (startTime != null && nowTime.isAfter(startTime)) {
            attendance.setStatus("LATE");  // 迟到
        } else {
            attendance.setStatus("NORMAL");  // 正常
        }

        attendance.setCreateTime(LocalDateTime.now());

        return attendanceRepository.save(attendance);
    }

    @Override
    public Integer getLateCount(Integer studentId) {
        return attendanceRepository.countByStudentIdAndStatus(studentId, "LATE");
    }

    @Override
    public Integer getNormalCount(Integer studentId) {
        return attendanceRepository.countByStudentIdAndStatus(studentId, "NORMAL");
    }

    @Override
    public Page<Attendance> search(String keyword, Pageable pageable) {
        return attendanceRepository.findByStudentNoContainingOrStudentNameContainingOrCourseNameContaining(
                keyword, keyword, keyword, pageable);
    }
}
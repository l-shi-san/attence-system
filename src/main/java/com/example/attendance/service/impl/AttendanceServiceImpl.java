package com.example.attendance.service.impl;

import com.example.attendance.dto.AttendanceStatisticsDTO;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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

    // ========================================================================
    // 考勤统计方法实现
    // ========================================================================

    @Override
    public AttendanceStatisticsDTO getStatisticsByStudentAndDateRange(
            Integer studentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        long total = attendanceRepository.countByStudentIdAndCheckInTimeBetween(studentId, start, end);
        long normal = attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(
                studentId, "NORMAL", start, end);
        long late = attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(
                studentId, "LATE", start, end);
        long early = attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(
                studentId, "EARLY", start, end);
        long absent = attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(
                studentId, "ABSENT", start, end);

        return new AttendanceStatisticsDTO(total, normal, late, absent, early);
    }

    @Override
    public AttendanceStatisticsDTO getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        long total = attendanceRepository.countByCheckInTimeBetween(start, end);
        long normal = attendanceRepository.countByStatusAndCheckInTimeBetween("NORMAL", start, end);
        long late = attendanceRepository.countByStatusAndCheckInTimeBetween("LATE", start, end);
        long early = attendanceRepository.countByStatusAndCheckInTimeBetween("EARLY", start, end);
        long absent = attendanceRepository.countByStatusAndCheckInTimeBetween("ABSENT", start, end);

        return new AttendanceStatisticsDTO(total, normal, late, absent, early);
    }

    @Override
    public List<AttendanceStatisticsDTO> getWeeklyStatistics(int weeks) {
        List<AttendanceStatisticsDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = weeks - 1; i >= 0; i--) {
            // 计算第 i 周的开始（周一）和结束（周日）
            LocalDate weekStart = today.minusWeeks(i)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate weekEnd = weekStart.plusDays(6);

            AttendanceStatisticsDTO dto = getStatisticsByDateRange(weekStart, weekEnd);

            // 设置分组信息
            int weekOfYear = weekStart.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            dto.setGroupKey(weekStart.getYear() + "-W" + String.format("%02d", weekOfYear));
            dto.setGroupLabel("第" + weekOfYear + "周（" + weekStart + " ~ " + weekEnd + "）");

            result.add(dto);
        }
        return result;
    }

    @Override
    public List<AttendanceStatisticsDTO> getMonthlyStatistics(int months) {
        List<AttendanceStatisticsDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth ym = YearMonth.from(today).minusMonths(i);
            LocalDate monthStart = ym.atDay(1);
            LocalDate monthEnd = ym.atEndOfMonth();

            AttendanceStatisticsDTO dto = getStatisticsByDateRange(monthStart, monthEnd);

            // 设置分组信息
            dto.setGroupKey(ym.toString());  // "2026-06"
            dto.setGroupLabel(ym.getYear() + "年" + ym.getMonthValue() + "月");

            result.add(dto);
        }
        return result;
    }
}
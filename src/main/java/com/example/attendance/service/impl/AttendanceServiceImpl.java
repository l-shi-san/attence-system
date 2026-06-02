package com.example.attendance.service.impl;

import com.example.attendance.config.SemesterConfig;
import com.example.attendance.dto.AttendanceStatisticsDTO;
import com.example.attendance.dto.CheckInResult;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.AttendanceService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private SemesterConfig semesterConfig;

    @Override
    public Attendance save(Attendance a) {
        if (a.getId() == null) a.setCreateTime(LocalDateTime.now());
        return attendanceRepository.save(a);
    }

    @Override public Attendance findById(Integer id) { return attendanceRepository.findById(id).orElse(null); }
    @Override public List<Attendance> findAll() { return attendanceRepository.findAll(); }
    @Override public Page<Attendance> findAll(Pageable p) { return attendanceRepository.findAll(p); }
    @Override public Page<Attendance> findByStudentId(Integer sid, Pageable p) { return attendanceRepository.findByStudentId(sid, p); }
    @Override public List<Attendance> findByStudentId(Integer sid) { return attendanceRepository.findByStudentId(sid); }
    @Override public void deleteById(Integer id) { attendanceRepository.deleteById(id); }

    @Override
    public CheckInResult checkIn(Integer studentId, Integer courseId, String remark, String ip) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return CheckInResult.fail("学生信息不存在，请联系管理员");

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return CheckInResult.fail("课程不存在，请重新选择");

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        if (attendanceRepository.existsByStudentIdAndCourseIdAndCheckInTimeBetween(studentId, courseId, startOfDay, endOfDay))
            return CheckInResult.fail("今天已打卡过「" + course.getCourseName() + "」，无需重复打卡");

        LocalDateTime now = LocalDateTime.now();
        LocalTime nowTime = now.toLocalTime();
        LocalTime startTime = course.getStartTime();

        if (startTime == null) {
            return CheckInResult.success(buildAttendance(student, course, now, "NORMAL", ip, remark));
        }

        LocalTime windowStart = startTime.minusMinutes(20);
        LocalTime windowEnd = startTime.plusMinutes(5);
        if (nowTime.isBefore(windowStart))
            return CheckInResult.fail("打卡时间未到，有效时段为 " + windowStart + " ~ " + windowEnd + "，当前时间 " + nowTime);
        if (nowTime.isAfter(windowEnd))
            return CheckInResult.fail("打卡时间已过，有效时段为 " + windowStart + " ~ " + windowEnd + "，当前时间 " + nowTime);

        String status = nowTime.isAfter(startTime) ? "LATE" : "NORMAL";
        return CheckInResult.success(buildAttendance(student, course, now, status, ip, remark));
    }

    private Attendance buildAttendance(Student student, Course course, LocalDateTime now, String status, String ip, String remark) {
        Attendance a = new Attendance();
        a.setStudentId(student.getId());
        a.setStudentNo(student.getStudentNo());
        a.setStudentName(student.getName());
        a.setCourseId(course.getId());
        a.setCourseName(course.getCourseName());
        a.setCheckInTime(now);
        a.setStatus(status);
        a.setIp(ip);
        a.setRemark(remark);
        a.setCreateTime(now);
        return attendanceRepository.save(a);
    }

    @Override public Integer getLateCount(Integer sid) { return attendanceRepository.countByStudentIdAndStatus(sid, "LATE"); }
    @Override public Integer getNormalCount(Integer sid) { return attendanceRepository.countByStudentIdAndStatus(sid, "NORMAL"); }
    @Override public Page<Attendance> search(String kw, Pageable p) { return attendanceRepository.findByStudentNoContainingOrStudentNameContainingOrCourseNameContaining(kw, kw, kw, p); }

    // ===== 统计 =====
    @Override
    public AttendanceStatisticsDTO getStatisticsByStudentAndDateRange(Integer sid, LocalDate start, LocalDate end) {
        LocalDateTime s = start.atStartOfDay(), e = end.atTime(LocalTime.MAX);
        return new AttendanceStatisticsDTO(
            attendanceRepository.countByStudentIdAndCheckInTimeBetween(sid, s, e),
            attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(sid, "NORMAL", s, e),
            attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(sid, "LATE", s, e),
            attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(sid, "ABSENT", s, e),
            attendanceRepository.countByStudentIdAndStatusAndCheckInTimeBetween(sid, "EARLY", s, e));
    }

    @Override
    public AttendanceStatisticsDTO getStatisticsByDateRange(LocalDate start, LocalDate end) {
        LocalDateTime s = start.atStartOfDay(), e = end.atTime(LocalTime.MAX);
        return new AttendanceStatisticsDTO(
            attendanceRepository.countByCheckInTimeBetween(s, e),
            attendanceRepository.countByStatusAndCheckInTimeBetween("NORMAL", s, e),
            attendanceRepository.countByStatusAndCheckInTimeBetween("LATE", s, e),
            attendanceRepository.countByStatusAndCheckInTimeBetween("ABSENT", s, e),
            attendanceRepository.countByStatusAndCheckInTimeBetween("EARLY", s, e));
    }

    @Override
    public List<AttendanceStatisticsDTO> getWeeklyStatistics(int weeks) {
        List<AttendanceStatisticsDTO> result = new ArrayList<>();
        int cur = semesterConfig.getCurrentTeachingWeek();
        int start = Math.max(1, cur - weeks + 1);
        for (int w = start; w <= cur; w++) {
            AttendanceStatisticsDTO dto = getStatisticsByDateRange(
                semesterConfig.getTeachingWeekStart(w), semesterConfig.getTeachingWeekEnd(w));
            dto.setGroupKey(semesterConfig.getTeachingWeekKey(w));
            dto.setGroupLabel(semesterConfig.getTeachingWeekLabel(w));
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
            AttendanceStatisticsDTO dto = getStatisticsByDateRange(ym.atDay(1), ym.atEndOfMonth());
            dto.setGroupKey(ym.toString());
            dto.setGroupLabel(ym.getYear() + "年" + ym.getMonthValue() + "月");
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<AttendanceStatisticsDTO> getStatisticsByCourse(LocalDate start, LocalDate end) {
        List<Attendance> records = attendanceRepository.findByCheckInTimeBetweenOrderByCheckInTimeAsc(
            start.atStartOfDay(), end.atTime(LocalTime.MAX));
        Map<String, List<Attendance>> grouped = records.stream()
            .collect(Collectors.groupingBy(at -> at.getCourseName() != null ? at.getCourseName() : "未知课程"));
        List<AttendanceStatisticsDTO> result = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            long t = entry.getValue().size();
            long n = entry.getValue().stream().filter(at -> "NORMAL".equals(at.getStatus())).count();
            long l = entry.getValue().stream().filter(at -> "LATE".equals(at.getStatus())).count();
            long early = entry.getValue().stream() .filter(at -> "EARLY".equals(at.getStatus())).count();
            long absent = entry.getValue().stream().filter(at -> "ABSENT".equals(at.getStatus())).count();
            AttendanceStatisticsDTO dto = new AttendanceStatisticsDTO(t, n, l, absent, early);
            dto.setGroupKey(entry.getKey()); dto.setGroupLabel(entry.getKey());
            result.add(dto);
        }
        result.sort((x, y) -> y.getTotalCount().compareTo(x.getTotalCount()));
        return result;
    }

    @Override
    public byte[] exportAttendanceToExcel(LocalDate start, LocalDate end) throws IOException {
        LocalDateTime s = start.atStartOfDay(), e = end.atTime(LocalTime.MAX);
        List<Attendance> list = attendanceRepository.findByCheckInTimeBetweenOrderByCheckInTimeAsc(s, e);

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("考勤数据");

        CellStyle hs = wb.createCellStyle();
        Font hf = wb.createFont(); hf.setBold(true); hf.setColor(IndexedColors.WHITE.getIndex());
        hs.setFont(hf); hs.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        hs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (BorderStyle bs : new BorderStyle[]{BorderStyle.THIN,BorderStyle.THIN,BorderStyle.THIN,BorderStyle.THIN}) {
            hs.setBorderBottom(bs); hs.setBorderTop(bs); hs.setBorderLeft(bs); hs.setBorderRight(bs);
        }
        CellStyle ds = wb.createCellStyle();
        ds.setBorderBottom(BorderStyle.THIN); ds.setBorderTop(BorderStyle.THIN);
        ds.setBorderLeft(BorderStyle.THIN); ds.setBorderRight(BorderStyle.THIN);

        String[] headers = {"学号","姓名","课程名称","打卡日期","打卡时间","状态","备注"};
        Row hr = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) { Cell c = hr.createCell(i); c.setCellValue(headers[i]); c.setCellStyle(hs); }

        int rn = 1;
        for (Attendance att : list) {
            Row row = sheet.createRow(rn++);
            row.createCell(0).setCellValue(ns(att.getStudentNo()));
            row.createCell(1).setCellValue(ns(att.getStudentName()));
            row.createCell(2).setCellValue(ns(att.getCourseName()));
            if (att.getCheckInTime() != null) {
                row.createCell(3).setCellValue(att.getCheckInTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                row.createCell(4).setCellValue(att.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            } else { row.createCell(3).setCellValue(""); row.createCell(4).setCellValue(""); }
            row.createCell(5).setCellValue(ns(att.getStatus()));
            row.createCell(6).setCellValue(ns(att.getRemark()));
            for (int i = 0; i < headers.length; i++) row.getCell(i).setCellStyle(ds);
        }
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out); wb.close();
        return out.toByteArray();
    }
    private String ns(String s) { return s != null ? s : ""; }
}

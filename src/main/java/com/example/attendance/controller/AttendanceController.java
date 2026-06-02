package com.example.attendance.controller;

import com.example.attendance.dto.CheckInResult;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.Student;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import com.example.attendance.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    static String normalizeIp(String ip) {
        if (ip == null) return "";
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) return "127.0.0.1";
        return ip;
    }

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    private Student getCurrentStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return studentService.findByStudentNo(auth.getName());
    }

    @GetMapping("/checkin")
    public String checkInPage(Model model) {
        Student student = getCurrentStudent();
        if (student == null) {
            return "redirect:/login";
        }

        List<Course> courses = courseService.findActiveCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("student", student);
        return "attendance-checkin";
    }

    @PostMapping("/checkin")
    public String checkIn(@RequestParam Integer courseId,
                          @RequestParam(required = false) String remark,
                          HttpServletRequest request,
                          Model model) {
        Student student = getCurrentStudent();
        if (student == null) {
            return "redirect:/login";
        }

        String ip = normalizeIp(request.getRemoteAddr());
        CheckInResult result = attendanceService.checkIn(student.getId(), courseId, remark, ip);

        if (!result.isSuccess()) {
            model.addAttribute("errorMsg", result.getErrorMsg());
            List<Course> courses = courseService.findActiveCourses();
            model.addAttribute("courses", courses);
            model.addAttribute("student", student);
            return "attendance-checkin";
        }

        return "redirect:/attendance/my-list";
    }

    @GetMapping("/my-list")
    public String myList(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Student student = getCurrentStudent();
        if (student == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("checkInTime").descending());
        Page<Attendance> attendancePage = attendanceService.findByStudentId(student.getId(), pageable);

        int prevPage = page > 1 ? page - 1 : 1;
        int nextPage = page < attendancePage.getTotalPages() ? page + 1 : attendancePage.getTotalPages();

        model.addAttribute("attendances", attendancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("totalPages", attendancePage.getTotalPages());
        model.addAttribute("totalElements", attendancePage.getTotalElements());
        model.addAttribute("normalCount", attendanceService.getNormalCount(student.getId()));
        model.addAttribute("lateCount", attendanceService.getLateCount(student.getId()));
        model.addAttribute("student", student);

        return "attendance-my-list";
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("checkInTime").descending());
        Page<Attendance> attendancePage;

        if (keyword != null && !keyword.isBlank()) {
            attendancePage = attendanceService.search(keyword, pageable);
        } else {
            attendancePage = attendanceService.findAll(pageable);
        }

        int prevPage = page > 1 ? page - 1 : 1;
        int nextPage = page < attendancePage.getTotalPages() ? page + 1 : attendancePage.getTotalPages();

        model.addAttribute("attendances", attendancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("totalPages", attendancePage.getTotalPages());
        model.addAttribute("totalElements", attendancePage.getTotalElements());
        model.addAttribute("keyword", keyword);

        return "attendance-list";
    }

    @GetMapping("/student/{id}")
    public String studentAttendance(@PathVariable Integer id,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        Student student = studentService.findById(id);
        if (student == null) {
            return "redirect:/student/list";
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("checkInTime").descending());
        Page<Attendance> attendancePage = attendanceService.findByStudentId(id, pageable);

        int prevPage = page > 1 ? page - 1 : 1;
        int nextPage = page < attendancePage.getTotalPages() ? page + 1 : attendancePage.getTotalPages();

        model.addAttribute("student", student);
        model.addAttribute("attendances", attendancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("totalPages", attendancePage.getTotalPages());
        model.addAttribute("totalElements", attendancePage.getTotalElements());
        model.addAttribute("normalCount", attendanceService.getNormalCount(id));
        model.addAttribute("lateCount", attendanceService.getLateCount(id));

        return "attendance-student-list";
    }
}

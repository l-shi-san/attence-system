package com.example.attendance.controller;

import com.example.attendance.entity.Student;
import com.example.attendance.entity.User;
import com.example.attendance.service.StudentService;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class PageController {

    private final StudentService studentService;
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PageController(StudentService studentService, UserService userService) {
        this.studentService = studentService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            Model model) {
        model.addAttribute("title", "用户登录");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "学生注册");
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String studentNo,
                           @RequestParam String name,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam(required = false) String className,
                           @RequestParam(required = false) String gender,
                           @RequestParam(required = false) String phone,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // 密码一致性校验
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMsg", "两次输入的密码不一致");
            model.addAttribute("title", "学生注册");
            return "register";
        }

        // 检查学号是否已被注册（作为用户名）
        if (userService.existsByUsername(studentNo)) {
            model.addAttribute("errorMsg", "学号已存在，请直接登录");
            model.addAttribute("title", "学生注册");
            return "register";
        }

        try {
            // 1. 创建User账号
            User user = new User();
            user.setUsername(studentNo);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("STUDENT");
            user.setCreateTime(LocalDateTime.now());
            user = userService.save(user);

            // 2. 创建Student信息并关联User
            Student student = new Student();
            student.setStudentNo(studentNo);
            student.setName(name);
            student.setClassName(className);
            student.setGender(gender);
            student.setPhone(phone);
            student.setStatus(1);
            student.setUser(user);  // 关键：关联User
            student.setCreateTime(LocalDateTime.now());
            studentService.save(student);

            // 3. 设置双向关联
            user.setStudent(student);
            userService.save(user);

            redirectAttributes.addFlashAttribute("successMsg", "注册成功，请登录");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("errorMsg", "注册失败：" + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        model.addAttribute("title", "班级考勤管理系统");
        return "dashboard";
    }
}
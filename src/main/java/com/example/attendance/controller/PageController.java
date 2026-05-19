package com.example.attendance.controller;


import com.example.attendance.entity.Student;
import com.example.attendance.entity.User;
import com.example.attendance.service.StudentService;
import com.example.attendance.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class PageController {

    private final StudentService studentService;
    private final UserService userService;

    public PageController(StudentService studentService, UserService userService) {
        this.studentService = studentService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            Model model) {
        model.addAttribute("title", "用户登录");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "用户注册");
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {
        if(!password.equals(confirmPassword)){
            model.addAttribute("errorMsg","两次输入密码不一样");
            model.addAttribute("title","用户注册");
            return "register";
        }

        boolean success = userService.register(username, password);
        if(success){
            return "redirect:/login";
        }
        else{
            model.addAttribute("errorMsg","用户名已存在");
            model.addAttribute("title","用户注册");
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        model.addAttribute("title", "班级考勤管理系统");
        return "dashboard";
    }
}

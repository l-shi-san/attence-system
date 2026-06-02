package com.example.attendance.controller;

import com.example.attendance.config.SemesterConfig;
import com.example.attendance.entity.User;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserManageController {

    @Autowired
    private UserService userService;

    @Autowired
    private SemesterConfig semesterConfig;

    @GetMapping("/users")
    public String userList(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    @PostMapping("/users/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        userService.updateRole(id, role);
        redirectAttributes.addFlashAttribute("successMsg", "角色已更新");
        return "redirect:/admin/users";
    }

    @GetMapping("/semester")
    public String semesterPage(Model model) {
        model.addAttribute("semesterStart", semesterConfig.getSemesterStart().toString());
        model.addAttribute("currentWeek", semesterConfig.getCurrentTeachingWeek());
        return "semester-config";
    }

    @PostMapping("/semester")
    public String updateSemester(@RequestParam String startDate,
                                 RedirectAttributes redirectAttributes) {
        try {
            LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            semesterConfig.setSemesterStart(date);
            redirectAttributes.addFlashAttribute("successMsg",
                    "学期开始日期已更新为 " + date);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "日期格式错误，请使用 yyyy-MM-dd 格式");
        }
        return "redirect:/admin/semester";
    }
}

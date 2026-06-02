package com.example.attendance.controller;

import com.example.attendance.entity.Course;
import com.example.attendance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Course> courses = courseService.findAll();
        model.addAttribute("courses", courses);
        return "course-list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("title", "新增课程");
        return "course-form";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model) {
        Course course = courseService.findById(id);
        if (course == null) {
            return "redirect:/course/list";
        }
        model.addAttribute("course", course);
        model.addAttribute("title", "编辑课程");
        return "course-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Course course) {
        courseService.save(course);
        return "redirect:/course/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        courseService.deleteById(id);
        return "redirect:/course/list";
    }
}

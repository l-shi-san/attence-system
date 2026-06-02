package com.example.attendance.controller;

import com.example.attendance.common.Result;
import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/create")
    public Result<Student> create(@RequestBody Student student){
        return Result.success(studentService.createStudent(student));
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Integer id){
        return Result.success(studentService.findStudentById(id));
    }

    @GetMapping("/students")
    public String studentList(Model model) {
        List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        return "student-list";
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(defaultValue = "id") String sortField,
                       @RequestParam(defaultValue = "desc") String sortDir,
                       Model model) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Student> studentPage;
        if (keyword != null && !keyword.isEmpty()) {
            if ("studentNo".equals(searchType)) {
                studentPage = studentService.searchByStudentNo(keyword, pageable);
            } else {
                studentPage = studentService.searchByName(keyword, pageable);
            }
        } else {
            studentPage = studentService.findAll(pageable);
        }

        int prevPage = page > 1 ? page - 1 : 1;
        int nextPage = page < studentPage.getTotalPages() ? page + 1 : studentPage.getTotalPages();

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalElements", studentPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "student-list";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("title", "新增学生");
        model.addAttribute("isNew", true);
        return "student-form";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model) {
        Student student = studentService.findById(id);
        if (student == null) {
            return "redirect:/student/list";
        }
        model.addAttribute("student", student);
        model.addAttribute("title", "编辑学生");
        model.addAttribute("isNew", false);
        return "student-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Student student,
                       @RequestParam(required = false) String initialPassword,
                       Model model) {
        boolean isNew = student.getId() == 0;

        if (isNew) {
            if (studentService.existsByStudentNo(student.getStudentNo())) {
                model.addAttribute("errorMsg", "学号已存在！");
                model.addAttribute("student", student);
                model.addAttribute("title", "新增学生");
                model.addAttribute("isNew", true);
                return "student-form";
            }
            try {
                studentService.saveWithUser(student, initialPassword);
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMsg", e.getMessage());
                model.addAttribute("student", student);
                model.addAttribute("title", "新增学生");
                model.addAttribute("isNew", true);
                return "student-form";
            }
        } else {
            studentService.save(student);
        }
        return "redirect:/student/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        studentService.deleteById(id);
        return "redirect:/student/list";
    }
}

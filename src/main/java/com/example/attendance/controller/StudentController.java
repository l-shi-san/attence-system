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
        return  Result.success(studentService.createStudent(student));
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id){
        return Result.success(studentService.findStudentById(id));
    }

    @GetMapping("/students")
    public String studentList(Model model) {
        List<Student> students = studentService.findAll();
        model.addAttribute("students", students);
        return "students";
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(defaultValue = "id") String sortField,
                       @RequestParam(defaultValue = "desc") String sortDir,
                       Model model) {

        // 创建排序对象
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);

        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // 查询数据
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

        // 计算上一页和下一页
        int prevPage = page > 1 ? page - 1 : 1;
        int nextPage = page < studentPage.getTotalPages() ? page + 1 : studentPage.getTotalPages();

        // 添加到模型
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

    // 新增学生页面
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("title", "新增学生");
        return "student-form";
    }

    // 编辑学生页面
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        model.addAttribute("title", "编辑学生");
        return "student-form";
    }

    // 保存学生
    @PostMapping("/save")
    public String save(@ModelAttribute Student student, Model model) {
        if (student.getId() == null) {
            if (studentService.existsByStudentNo(student.getStudentNo())) {
                model.addAttribute("errorMsg", "学号已存在！");
                model.addAttribute("student", student);
                model.addAttribute("title", "新增学生");
                return "student-form";
            }
        }
        studentService.save(student);
        return "redirect:/student/list";
    }

    // 删除学生
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        studentService.deleteById(id);
        return "redirect:/student/list";
    }
}

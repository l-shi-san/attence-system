package com.example.attendance.controller;

import com.example.attendance.common.Result;
import com.example.attendance.dto.AttendanceRequest;
import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
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
}

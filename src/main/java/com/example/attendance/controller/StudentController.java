package com.example.attendance.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    @GetMapping("/info")
    public  String getStudentInfo()
    {
        return "学号：42411143" +
                "姓名：刘佳奇" +
                "班级：周三上午12节";
    }
    @GetMapping("/courses")
    public List<String> getCourses()
    {
        List<String> list=new ArrayList<>();
        list.add("Java EE开发实践");
        return list;
    }
    @PostMapping("/attendance")
    public String takeAttendance(@RequestBody String studentId)
    {
        return "学号为" + studentId + "的学生打卡成功";
    }
}

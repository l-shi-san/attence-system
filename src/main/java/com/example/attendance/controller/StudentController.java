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
        return "学号" +
                "姓名" +
                "班级";
    }
    @GetMapping("/courses")
    public List<String> getCourses()
    {
        List<String> list=new ArrayList<>();
        list.add("语文");
        list.add("数学");
        return list;
    }
    @PostMapping("/attendance")
    public String takeAttendance(@RequestBody String studentId)
    {
        return "学号为" + studentId + "的学生打卡成功";
    }
}

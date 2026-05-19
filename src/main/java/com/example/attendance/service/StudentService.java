package com.example.attendance.service;

import com.example.attendance.entity.Student;

import java.util.List;

public interface StudentService {
    Student createStudent(Student student);

    Student findStudentById(Long studentId);

    Student findStudentByName(String name);

    List<Student> findAll();
}

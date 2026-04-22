package com.example.attendance.service;

import com.example.attendance.entity.Student;

public interface StudentService {
    Student createStudent(Student student);

    Student findStudentById(Long studentId);

    Student findStudentByName(String name);
}

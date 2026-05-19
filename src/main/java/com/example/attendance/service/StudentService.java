package com.example.attendance.service;

import com.example.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    Student createStudent(Student student);

    Student findStudentById(Long studentId);

    Student findStudentByName(String name);

    List<Student> findAll();

    // 基础 CRUD
    Student save(Student student);
    Student findById(Long id);
    void deleteById(Long id);
    boolean existsByStudentNo(String studentNo);

    // 分页查询
    Page<Student> findAll(Pageable pageable);

    // 按学号精确搜索
    Page<Student> searchByStudentNo(String studentNo, Pageable pageable);

    // 按姓名模糊搜索
    Page<Student> searchByName(String name, Pageable pageable);
}

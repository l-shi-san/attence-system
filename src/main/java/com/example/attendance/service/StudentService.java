package com.example.attendance.service;

import com.example.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    Student createStudent(Student student);

    Student findStudentById(Integer studentId);

    Student findStudentByName(String name);

    List<Student> findAll();

    Student save(Student student);

    Student findById(Integer id);

    void deleteById(Integer id);

    /**
     * 管理员新增学生时，同步创建登录账号
     */
    Student saveWithUser(Student student, String initialPassword);

    Page<Student> findAll(Pageable pageable);

    Page<Student> searchByStudentNo(String studentNo, Pageable pageable);

    Page<Student> searchByName(String name, Pageable pageable);

    Student findByStudentNo(String studentNo);

    boolean existsByStudentNo(String studentNo);
}

package com.example.attendance.repository;

import com.example.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByClassName(String className);

    List<Student> findAll();

    Student findByName(String name);

    Page<Student> findAll(Pageable pageable);

    Page<Student> findByNameContaining(String keyword, Pageable pageable);

    Page<Student> findByClassName(String className, Pageable pageable);

    Student findByStudentNo(String studentNo);

    // 按学号模糊搜索（分页）
    Page<Student> findByStudentNoContaining(String studentNo, Pageable pageable);

    // 按姓名排序查询
    List<Student> findAllByOrderByNameAsc();
    List<Student> findAllByOrderByNameDesc();

    // 按学号排序查询
    List<Student> findAllByOrderByStudentNoAsc();
    List<Student> findAllByOrderByStudentNoDesc();
}
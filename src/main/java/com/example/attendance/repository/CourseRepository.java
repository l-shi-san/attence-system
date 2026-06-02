package com.example.attendance.repository;

import com.example.attendance.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    // 查询所有启用的课程
    List<Course> findByStatus(Integer status);

    // 根据教师查询
    List<Course> findByTeacherNameContaining(String teacherName);
}

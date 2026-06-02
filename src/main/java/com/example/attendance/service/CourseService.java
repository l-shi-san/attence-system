package com.example.attendance.service;

import com.example.attendance.entity.Course;

import java.time.LocalTime;
import java.util.List;

public interface CourseService {
    List<Course> findAll();
    List<Course> findActiveCourses();
    Course findById(Integer id);
    LocalTime getCourseStartTime(Integer courseId);
    Course save(Course course);

    void deleteById(Integer id);
}

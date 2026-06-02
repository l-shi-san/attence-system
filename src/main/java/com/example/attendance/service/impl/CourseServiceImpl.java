package com.example.attendance.service.impl;

import com.example.attendance.entity.Course;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> findActiveCourses() {
        return courseRepository.findByStatus(1);
    }

    @Override
    public Course findById(Integer id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public LocalTime getCourseStartTime(Integer courseId) {
        Course course = findById(courseId);
        return course != null ? course.getStartTime() : null;
    }

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteById(Integer id) {
        courseRepository.deleteById(id);
    }
}

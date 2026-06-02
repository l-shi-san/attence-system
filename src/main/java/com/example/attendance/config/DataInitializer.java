package com.example.attendance.config;

import com.example.attendance.entity.Course;
import com.example.attendance.entity.User;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdminUser();
        initSampleCourses();
    }

    private void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("系统管理员");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("teacher")) {
            User teacher = new User();
            teacher.setUsername("teacher");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setRealName("张老师");
            teacher.setRole("TEACHER");
            userRepository.save(teacher);
        }
    }

    private void initSampleCourses() {
        if (courseRepository.count() > 0) {
            return;
        }

        Course java = new Course();
        java.setCourseName("Java EE 程序设计");
        java.setCourseCode("CS101");
        java.setTeacherName("张老师");
        java.setStartTime(LocalTime.of(8, 0));
        java.setEndTime(LocalTime.of(9, 40));
        java.setWeekDay(1);
        java.setClassroom("教学楼 A101");
        java.setStatus(1);
        courseRepository.save(java);

        Course database = new Course();
        database.setCourseName("数据库原理");
        database.setCourseCode("CS102");
        database.setTeacherName("李老师");
        database.setStartTime(LocalTime.of(10, 0));
        database.setEndTime(LocalTime.of(11, 40));
        database.setWeekDay(3);
        database.setClassroom("教学楼 B203");
        database.setStatus(1);
        courseRepository.save(database);

        Course web = new Course();
        web.setCourseName("Web 前端开发");
        web.setCourseCode("CS103");
        web.setTeacherName("王老师");
        web.setStartTime(LocalTime.of(14, 0));
        web.setEndTime(LocalTime.of(15, 40));
        web.setWeekDay(5);
        web.setClassroom("实验楼 C305");
        web.setStatus(1);
        courseRepository.save(web);
    }
}

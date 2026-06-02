package com.example.attendance.service.impl;

import com.example.attendance.entity.Student;
import com.example.attendance.entity.User;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Student createStudent(Student student) {
        student.setCreateTime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Override
    public Student findStudentById(Integer id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student findStudentByName(String name){
        return studentRepository.findByName(name);
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student save(Student student) {
        if (student.getId() == null || student.getId() == 0) {
            student.setCreateTime(LocalDateTime.now());
        } else {
            student.setUpdateTime(LocalDateTime.now());
        }
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student saveWithUser(Student student, String initialPassword) {
        if (student.getId() == null || student.getId() == 0) {
            if (userRepository.existsByUsername(student.getStudentNo())) {
                throw new IllegalArgumentException("学号已存在，无法创建账号");
            }

            User user = new User();
            user.setUsername(student.getStudentNo());
            user.setRealName(student.getName());
            user.setRole("STUDENT");
            String password = (initialPassword == null || initialPassword.isBlank()) ? "123456" : initialPassword;
            user.setPassword(passwordEncoder.encode(password));
            user.setCreateTime(LocalDateTime.now());
            user = userRepository.save(user);

            student.setUser(user);
            student.setCreateTime(LocalDateTime.now());
            student = studentRepository.save(student);

            user.setStudent(student);
            userRepository.save(user);
            return student;
        }

        student.setUpdateTime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Override
    public Student findById(Integer id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public Page<Student> searchByStudentNo(String studentNo, Pageable pageable) {
        return studentRepository.findByStudentNoContaining(studentNo, pageable);
    }

    @Override
    public Page<Student> searchByName(String name, Pageable pageable) {
        return studentRepository.findByNameContaining(name, pageable);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            User user = student.getUser();
            studentRepository.delete(student);
            if (user != null) {
                userRepository.delete(user);
            }
        }
    }

    @Override
    public boolean existsByStudentNo(String studentNo) {
        return studentRepository.findByStudentNo(studentNo) != null;
    }

    @Override
    public Student findByStudentNo(String studentNo) {
        return studentRepository.findByStudentNo(studentNo);
    }
}

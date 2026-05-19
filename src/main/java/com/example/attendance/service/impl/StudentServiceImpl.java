package com.example.attendance.service.impl;

import com.example.attendance.entity.Student;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student createStudent(Student student) {
        student.setCreateTime(LocalDateTime.now());
        return studentRepository.save(student);
    }

    @Override
    public Student findStudentById(Long id) {
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
        if (student.getId() == null) {
            student.setCreateTime(LocalDateTime.now());
        } else {
            student.setUpdateTime(LocalDateTime.now());
        }
        return studentRepository.save(student);
    }

    @Override
    public Student findById(Long id) {
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
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public boolean existsByStudentNo(String studentNo) {
        return studentRepository.findByStudentNo(studentNo) != null;
    }
}
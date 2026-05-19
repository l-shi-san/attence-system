package com.example.attendance.service.impl;

import com.example.attendance.entity.Student;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student createStudent(Student student){
        return studentRepository.save(student);
    }

    @Override
    public Student findStudentById(Long id){
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student findStudentByName(String name){
        return studentRepository.findByName(name);
    }

    @Override
    public List<Student> findAll(){
        return studentRepository.findAll();
    }
}
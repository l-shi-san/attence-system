package com.example.attendance.service.impl;

import com.example.attendance.dao.UserDao;
import com.example.attendance.entity.Student;
import com.example.attendance.entity.User;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional
    public User save(User user) {
        // 设置创建时间（新增时）
        if (user.getId() == null) {
            user.setCreateTime(LocalDateTime.now());
        }

        // 保存并返回
        return userRepository.save(user);
    }

    // 用户注册
    @Override
    public boolean register(String username, String password) {

        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("STUDENT");
        user.setCreateTime(LocalDateTime.now());

        userRepository.save(user);
        return true;
    }

    @Override
    public Optional<User> login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(passwordEncoder.matches(password,user.getPassword())){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // 根据用户名查找用户
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // 验证用户是否存在
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void updateRole(Long userId, String newRole) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        String oldRole = user.getRole();
        user.setRole(newRole);
        userRepository.save(user);

        if ("STUDENT".equals(oldRole) && !"STUDENT".equals(newRole)) {
            Student student = studentRepository.findByStudentNo(user.getUsername());
            if (student != null) {
                user.setStudent(null);
                userRepository.save(user);
                student.setUser(null);
                studentRepository.delete(student);
            }
        }
    }
}
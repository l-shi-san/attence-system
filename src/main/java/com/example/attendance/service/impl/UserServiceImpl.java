package com.example.attendance.service.impl;

import com.example.attendance.dao.UserDao;
import com.example.attendance.entity.User;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用户注册
    public boolean register(String username, String password, String realName, String role) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // BCrypt加密
        user.setRealName(realName);
        user.setRole(role != null ? role : "STUDENT");
        user.setCreateTime(LocalDateTime.now());

        userRepository.save(user);
        return true;
    }

    // 根据用户名查找用户
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // 验证用户是否存在
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
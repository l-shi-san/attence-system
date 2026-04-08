package com.example.attendance.service.impl;

import com.example.attendance.dao.UserDao;
import com.example.attendance.entity.User;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean addUser(User user) {
        // 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            return false; // 用户名已存在
        }
        // 设置默认角色为 TEACHER
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("TEACHER");
        }
        int result = userDao.insert(user);
        return result > 0;
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public List<User> getAllTeachers() {
        return userDao.findAllTeachers();
    }

    @Override
    public boolean updateUser(User user) {
        // 检查用户是否存在
        User existingUser = userDao.findById(user.getId());
        if (existingUser == null) {
            return false; // 用户不存在
        }
        int result = userDao.update(user);
        return result > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        // 检查用户是否存在
        User existingUser = userDao.findById(id);
        if (existingUser == null) {
            return false; // 用户不存在
        }
        int result = userDao.deleteById(id);
        return result > 0;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user; // 登录成功
        }
        return null; // 登录失败
    }
}
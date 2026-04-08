package com.example.attendance.service;

import com.example.attendance.entity.User;
import java.util.List;

public interface UserService {

    /**
     * 新增用户
     */
    boolean addUser(User user);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名查询用户（登录验证）
     */
    User getUserByUsername(String username);

    /**
     * 查询所有教师
     */
    List<User> getAllTeachers();

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 用户登录验证
     */
    User login(String username, String password);
}
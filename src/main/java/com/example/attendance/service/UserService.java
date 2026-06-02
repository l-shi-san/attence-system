package com.example.attendance.service;

import com.example.attendance.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean register(String username, String password);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> login(String username, String password);

    User save(User user);

    List<User> findAll();

    void updateRole(Long userId, String newRole);
}
package com.example.attendance.service;

import com.example.attendance.entity.User;
import com.example.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public interface UserService {
    boolean register(String username, String password, String realName, String role);

    public User findByUsername(String username);

    public boolean existsByUsername(String username);
}
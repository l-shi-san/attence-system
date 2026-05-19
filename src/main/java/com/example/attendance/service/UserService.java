package com.example.attendance.service;

import com.example.attendance.entity.User;
import com.example.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserService {
    boolean register(String username, String password);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> login(String username, String password);
}
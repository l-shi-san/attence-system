package com.example.attendance.repository;

import com.example.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名查询用户（用于登录验证）
    Optional<User> findByUsername(String username);

    // 检查用户名是否存在（用于注册）
    boolean existsByUsername(String username);

    // 根据角色查询用户列表
    List<User> findByRole(String role);
}
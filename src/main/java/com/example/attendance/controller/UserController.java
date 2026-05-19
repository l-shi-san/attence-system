package com.example.attendance.controller;

import com.example.attendance.dto.LoginRequest;
import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // 登录接口
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Spring Security会自动处理登录，这里只需要返回成功响应
        // 实际登录由Security的过滤器处理
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "登录成功");
        response.put("username", request.getUsername());
        return ResponseEntity.ok(response);
    }

    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(error);
        }

        // 执行注册
        boolean success = userService.register(
                request.getUsername(),
                request.getPassword()
        );

        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "注册成功");
        } else {
            response.put("code", 500);
            response.put("message", "注册失败");
        }
        return ResponseEntity.ok(response);
    }

    // 获取当前用户信息
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities());
        return ResponseEntity.ok(response);
    }
}
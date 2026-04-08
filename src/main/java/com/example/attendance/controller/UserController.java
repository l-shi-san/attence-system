package com.example.attendance.controller;

import com.example.attendance.entity.User;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录验证接口
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginInfo) {
        Map<String, Object> response = new HashMap<>();
        String username = loginInfo.get("username");
        String password = loginInfo.get("password");

        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            response.put("code", 400);
            response.put("message", "用户名不能为空");
            return ResponseEntity.badRequest().body(response);
        }
        if (password == null || password.trim().isEmpty()) {
            response.put("code", 400);
            response.put("message", "密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功，不返回密码
            user.setPassword(null);
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * 新增用户
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        boolean success = userService.addUser(user);
        if (success) {
            response.put("code", 200);
            response.put("message", "用户添加成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 400);
            response.put("message", "用户名已存在或添加失败");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID查询用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getUserById(id);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 根据用户名查询用户
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            user.setPassword(null);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 查询所有教师
     * GET /api/users/teachers
     */
    @GetMapping("/teachers")
    public ResponseEntity<Map<String, Object>> getAllTeachers() {
        Map<String, Object> response = new HashMap<>();
        List<User> teachers = userService.getAllTeachers();
        // 不返回密码
        teachers.forEach(user -> user.setPassword(null));
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", teachers);
        response.put("count", teachers.size());
        return ResponseEntity.ok(response);
    }

    /**
     * 查询所有用户
     * GET /api/users/all
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userService.getAllUsers();
        users.forEach(user -> user.setPassword(null));
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", users);
        response.put("count", users.size());
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        user.setId(id);
        boolean success = userService.updateUser(user);
        if (success) {
            User updatedUser = userService.getUserById(id);
            updatedUser.setPassword(null);
            response.put("code", 200);
            response.put("message", "用户更新成功");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在或更新失败");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = userService.deleteUser(id);
        if (success) {
            response.put("code", 200);
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在或删除失败");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
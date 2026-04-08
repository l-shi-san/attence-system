package com.example.attendance.dao;

import com.example.attendance.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 1. 新增用户（教师用户）
     */
    public int insert(User user) {
        String sql = "INSERT INTO \"user\" (username, password, real_name, role, create_time) " +
                "VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getRole(),
                new Timestamp(System.currentTimeMillis())
        );
    }

    /**
     * 2. 根据ID查询用户
     */
    public User findById(Long id) {
        String sql = "SELECT id, username, password, real_name, role, create_time " +
                "FROM \"user\" WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * 3. 根据用户名查询用户（用于登录验证）
     */
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, real_name, role, create_time " +
                "FROM \"user\" WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), username);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * 4. 查询所有教师（role = 'TEACHER'）
     */
    public List<User> findAllTeachers() {
        String sql = "SELECT id, username, password, real_name, role, create_time " +
                "FROM \"user\" WHERE role = 'TEACHER' ORDER BY id";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    /**
     * 5. 更新用户信息
     */
    public int update(User user) {
        String sql = "UPDATE \"user\" SET username = ?, password = ?, real_name = ?, role = ? " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getRole(),
                user.getId()
        );
    }

    /**
     * 6. 根据ID删除用户
     */
    public int deleteById(Long id) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 7. 查询所有用户（额外方法）
     */
    public List<User> findAll() {
        String sql = "SELECT id, username, password, real_name, role, create_time " +
                "FROM \"user\" ORDER BY id";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    /**
     * 8. 查询管理员（role = 'ADMIN'）
     */
    public List<User> findAllAdmins() {
        String sql = "SELECT id, username, password, real_name, role, create_time " +
                "FROM \"user\" WHERE role = 'ADMIN' ORDER BY id";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    /**
     * 9. 统计教师数量
     */
    public int countTeachers() {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE role = 'TEACHER'";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * 10. 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    /**
     * User 实体类的 RowMapper
     */
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRealName(rs.getString("real_name"));
            user.setRole(rs.getString("role"));
            user.setCreateTime(rs.getTimestamp("create_time"));
            return user;
        }
    }
}
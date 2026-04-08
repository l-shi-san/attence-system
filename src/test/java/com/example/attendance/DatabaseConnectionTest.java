package com.example.attendance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("✅ 数据库连接成功！");
            System.out.println("数据库URL: " + conn.getMetaData().getURL());
            System.out.println("数据库版本: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("用户名: " + conn.getMetaData().getUserName());
        }
    }
}
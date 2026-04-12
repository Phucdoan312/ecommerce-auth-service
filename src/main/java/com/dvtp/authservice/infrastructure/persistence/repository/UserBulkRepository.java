package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertUsersAndRoles(List<User> users, UUID clientId, UUID roleId) {
        if (users == null || users.isEmpty()) {
            return;
        }

        // 🚀 FIX 1: Dùng hàm nghiệp vụ chuẩn Clean Architecture thay vì setId()
        users.forEach(User::initializeIdIfNeeded);

        log.info("🚀 Đang bắn {} Users vào DB...", users.size());

        // 1. Bắn dữ liệu vào bảng users
        String sqlUsers = """
            INSERT INTO users (id, username, email, password, phone, enabled, created_at, updated_at, dob_updated) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sqlUsers, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setObject(1, user.getId());
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPassword());
                ps.setString(5, user.getPhone());
                ps.setBoolean(6, user.isEnabled());
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBoolean(9, false);
            }

            @Override
            public int getBatchSize() { return users.size(); }
        });

        log.info("🚀 Đang phân quyền cho {} Users...", users.size());

        // 2. Bắn tiếp dữ liệu vào bảng trung gian (user_app_roles)
        String sqlRoles = """
            INSERT INTO user_app_roles (id, user_id, client_id, role_id, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sqlRoles, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setObject(1, UUID.randomUUID());
                ps.setObject(2, user.getId());
                ps.setObject(3, clientId);
                // 🚀 FIX 2: Bổ sung tham số số 4 (roleId) bị thiếu
                ps.setObject(4, roleId);
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() { return users.size(); }
        });
    }
}
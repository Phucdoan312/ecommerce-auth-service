// File: src/main/java/com/dvtp/authservice/domain/repository/UserRepository.java
package com.dvtp.authservice.domain.repository;

import com.dvtp.authservice.domain.entity.User;
import java.util.Optional;
import java.util.UUID; // Nhớ import UUID

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
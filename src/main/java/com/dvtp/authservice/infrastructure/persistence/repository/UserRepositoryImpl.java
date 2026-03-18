package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.repository.UserRepository;
import com.dvtp.authservice.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataJpaUserRepository jpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        // 1. Dịch từ Domain Entity sang JPA Entity để lưu vào DB
        var jpaEntity = userMapper.toJpaEntity(user);
        var savedEntity = jpaRepository.save(jpaEntity);
        // 2. Dịch ngược từ JPA Entity sang Domain Entity để trả về cho Service
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return jpaRepository.findByUsernameOrEmail(username, email)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
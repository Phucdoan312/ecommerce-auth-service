package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.repository.UserRepository;
import com.dvtp.authservice.infrastructure.persistence.entity.AppClientJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.UserAppRoleJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataJpaUserRepository jpaRepository;
    private final SpringDataAppClientRepository appClientRepository;
    private final SpringDataJpaRoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity jpaEntity = userMapper.toJpaEntity(user);
        if (jpaEntity.getAppRoles() == null) {
            jpaEntity.setAppRoles(new HashSet<>());
        } else {
            jpaEntity.getAppRoles().clear();
        }

        if (user.getAppRoles() != null && !user.getAppRoles().isEmpty()) {
            user.getAppRoles().forEach((clientId, roleNames) -> {

                AppClientJpaEntity appClient = appClientRepository.findByClientId(clientId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy App Client: " + clientId));

                roleNames.forEach(roleName -> {
                    RoleJpaEntity role = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy Role: " + roleName));

                    UserAppRoleJpaEntity userAppRole = UserAppRoleJpaEntity.builder()
                            .user(jpaEntity)
                            .appClient(appClient)
                            .role(role)
                            .build();

                    jpaEntity.getAppRoles().add(userAppRole);
                });
            });
        }

        UserJpaEntity savedEntity = jpaRepository.save(jpaEntity);
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
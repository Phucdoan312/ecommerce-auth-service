package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"appRoles", "appRoles.appClient", "appRoles.role"})
    Optional<UserJpaEntity> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
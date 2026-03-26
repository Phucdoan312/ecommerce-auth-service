package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.infrastructure.persistence.entity.AppClientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAppClientRepository extends JpaRepository<AppClientJpaEntity, UUID> {
    Optional<AppClientJpaEntity> findByClientId(String clientId);
}
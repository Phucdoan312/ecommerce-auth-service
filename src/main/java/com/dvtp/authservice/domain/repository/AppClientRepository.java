package com.dvtp.authservice.domain.repository;

import com.dvtp.authservice.infrastructure.persistence.entity.AppClientJpaEntity;
import java.util.Optional;

public interface AppClientRepository {
    Optional<AppClientJpaEntity> findByClientId(String clientId);
}
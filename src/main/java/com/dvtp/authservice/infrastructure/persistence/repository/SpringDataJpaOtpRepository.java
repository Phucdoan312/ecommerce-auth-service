package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.infrastructure.persistence.entity.OtpTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaOtpRepository extends JpaRepository<OtpTokenJpaEntity, UUID> {

    Optional<OtpTokenJpaEntity> findTopByEmailOrderByExpirationTimeDesc(String email);
}
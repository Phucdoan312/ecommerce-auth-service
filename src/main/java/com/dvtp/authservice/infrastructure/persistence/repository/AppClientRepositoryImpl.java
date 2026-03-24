package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.domain.repository.AppClientRepository;
import com.dvtp.authservice.infrastructure.persistence.entity.AppClientJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AppClientRepositoryImpl implements AppClientRepository {

    private final SpringDataAppClientRepository springDataRepository;

    @Override
    public Optional<AppClientJpaEntity> findByClientId(String clientId) {
        return springDataRepository.findByClientId(clientId);
    }
}
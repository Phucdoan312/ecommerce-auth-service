package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.domain.entity.Role;
import com.dvtp.authservice.domain.repository.RoleRepository;
import com.dvtp.authservice.infrastructure.persistence.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final SpringDataJpaRoleRepository jpaRepository;
    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(roleMapper::toDomain);
    }

    @Override
    public Role save(Role role) {
        var jpaEntity = roleMapper.toJpaEntity(role);
        var savedEntity = jpaRepository.save(jpaEntity);
        return roleMapper.toDomain(savedEntity);
    }
}
package com.dvtp.authservice.infrastructure.persistence.mapper;

import com.dvtp.authservice.domain.entity.Role;
import com.dvtp.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toDomain(RoleJpaEntity entity);
    RoleJpaEntity toJpaEntity(Role domain);
}
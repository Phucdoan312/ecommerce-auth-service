package com.dvtp.authservice.infrastructure.persistence.mapper;

import com.dvtp.authservice.domain.entity.Role;
import com.dvtp.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping; // Bổ sung import này

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toDomain(RoleJpaEntity entity);

    @Mapping(target = "appRoles", ignore = true) // Thêm dòng này
    RoleJpaEntity toJpaEntity(Role domain);
}
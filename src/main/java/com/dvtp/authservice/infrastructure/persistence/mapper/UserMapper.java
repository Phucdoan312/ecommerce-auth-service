package com.dvtp.authservice.infrastructure.persistence.mapper;

import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.infrastructure.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserJpaEntity entity);

    UserJpaEntity toJpaEntity(User domain);
}
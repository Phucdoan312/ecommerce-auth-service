package com.dvtp.authservice.infrastructure.persistence.mapper;

import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.UserAppRoleJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "appRoles", source = "appRoles", qualifiedByName = "mapAppRoles")
    User toDomain(UserJpaEntity entity);

    @Mapping(target = "appRoles", ignore = true)
    UserJpaEntity toJpaEntity(User domain);

    @Named("mapAppRoles")
    default Map<String, Set<String>> mapAppRoles(Set<UserAppRoleJpaEntity> jpaRoles) {
        if (jpaRoles == null || jpaRoles.isEmpty()) {
            return new HashMap<>();
        }
        return jpaRoles.stream()
                .collect(Collectors.groupingBy(
                        role -> role.getAppClient().getClientId(),
                        Collectors.mapping(
                                role -> role.getRole().getName(),
                                Collectors.toSet()
                        )
                ));
    }
}
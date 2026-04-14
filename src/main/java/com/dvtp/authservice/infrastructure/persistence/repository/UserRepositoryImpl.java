package com.dvtp.authservice.infrastructure.persistence.repository;

import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.repository.UserRepository;
import com.dvtp.authservice.infrastructure.persistence.entity.AppClientJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.UserAppRoleJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final SpringDataJpaUserRepository jpaRepository;
    private final SpringDataAppClientRepository appClientRepository;
    private final SpringDataJpaRoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity jpaEntity;
        boolean isNew = user.getId() == null;

        if (!isNew) {
            jpaEntity = jpaRepository.findById(user.getId())
                    .orElseGet(() -> userMapper.toJpaEntity(user));
            // Cập nhật thông tin cơ bản từ domain sang JpaEntity (không chạm tới collection roles)
            userMapper.updateJpaEntity(user, jpaEntity);
        } else {
            jpaEntity = userMapper.toJpaEntity(user);
        }

        // Chỉ xử lý roles nếu JpaEntity chưa có hoặc là user mới
        // Tuy nhiên để an toàn và giữ logic cũ (cho phép cập nhật roles), ta quản lý collection cẩn thận hơn
        manageAppRoles(user, jpaEntity);

        UserJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return userMapper.toDomain(savedEntity);
    }

    private void manageAppRoles(User domainUser, UserJpaEntity jpaEntity) {
        if (domainUser.getAppRoles() == null || domainUser.getAppRoles().isEmpty()) {
            if (jpaEntity.getAppRoles() != null) {
                jpaEntity.getAppRoles().clear();
            }
            return;
        }

        // Logic: Giữ lại những role đã có, thêm mới những role chưa có
        // Xóa những role không còn trong domainUser
        
        // 1. Tạo tập hợp các định danh role hiện có trong JPA: "clientId:roleName"
        Set<String> existingRoleKeys = new HashSet<>();
        if (jpaEntity.getAppRoles() != null) {
            jpaEntity.getAppRoles().forEach(ar -> 
                existingRoleKeys.add(ar.getAppClient().getClientId() + ":" + ar.getRole().getName())
            );
        } else {
            jpaEntity.setAppRoles(new HashSet<>());
        }

        // 2. Tạo tập hợp các định danh role mới từ Domain
        Set<String> newRoleKeys = new HashSet<>();
        domainUser.getAppRoles().forEach((clientId, roles) -> 
            roles.forEach(roleName -> newRoleKeys.add(clientId + ":" + roleName))
        );

        // 3. Xóa các role không còn thuộc tập mới
        jpaEntity.getAppRoles().removeIf(ar -> 
            !newRoleKeys.contains(ar.getAppClient().getClientId() + ":" + ar.getRole().getName())
        );

        // 4. Thêm các role mới chưa có trong tập cũ
        domainUser.getAppRoles().forEach((clientId, roleNames) -> {
            AppClientJpaEntity appClient = appClientRepository.findByClientId(clientId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy App Client: " + clientId));

            roleNames.forEach(roleName -> {
                String key = clientId + ":" + roleName;
                if (!existingRoleKeys.contains(key)) {
                    RoleJpaEntity role = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy Role: " + roleName));

                    UserAppRoleJpaEntity userAppRole = UserAppRoleJpaEntity.builder()
                            .user(jpaEntity)
                            .appClient(appClient)
                            .role(role)
                            .build();

                    jpaEntity.getAppRoles().add(userAppRole);
                }
            });
        });
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return jpaRepository.findByUsernameOrEmail(username, email)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
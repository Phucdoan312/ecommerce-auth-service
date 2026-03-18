package com.dvtp.authservice.domain.repository;

import com.dvtp.authservice.domain.entity.Role;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    Role save(Role role); // Dùng cho DataInitializer lúc tạo quyền ban đầu
}
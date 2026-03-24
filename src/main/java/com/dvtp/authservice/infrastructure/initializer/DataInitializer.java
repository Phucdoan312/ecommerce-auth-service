package com.dvtp.authservice.infrastructure.initializer;

import com.dvtp.authservice.domain.constant.RoleConstant;
import com.dvtp.authservice.domain.entity.Role;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.repository.RoleRepository;
import com.dvtp.authservice.domain.repository.UserRepository;
// 💎 IMPORT THÊM 2 CÁI NÀY:
import com.dvtp.authservice.infrastructure.persistence.entity.AppClientJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.repository.SpringDataAppClientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 💎 THÊM DEPENDENCY NÀY:
    private final SpringDataAppClientRepository appClientRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // ----------------------------------------------------------------
        // 0. KHỞI TẠO ỨNG DỤNG MẶC ĐỊNH (CLIENT) CHO HỆ THỐNG SSO
        // ----------------------------------------------------------------
        String defaultClientId = "ecommerce-web";
        if (appClientRepository.findByClientId(defaultClientId).isEmpty()) {
            AppClientJpaEntity appClient = AppClientJpaEntity.builder()
                    .clientId(defaultClientId)
                    .clientSecret("secret-key-123456") // Mật khẩu để app E-commerce gọi sang
                    .redirectUri("http://localhost:5173") // Link Frontend của E-commerce
                    .build();
            appClientRepository.save(appClient);
            log.info("🚀 Đã khởi tạo thành công Ứng dụng Client: {}", defaultClientId);
        }

        // 1. Khởi tạo ROLE_USER
        if (roleRepository.findByName(RoleConstant.USER).isEmpty()) {
            Role userRole = Role.builder().name(RoleConstant.USER).build();
            roleRepository.save(userRole);
            log.info("🚀 Đã khởi tạo thành công quyền: {}", RoleConstant.USER);
        }

        // 2. Khởi tạo ROLE_ADMIN
        if (roleRepository.findByName(RoleConstant.ADMIN).isEmpty()) {
            Role adminRole = Role.builder().name(RoleConstant.ADMIN).build();
            roleRepository.save(adminRole);
            log.info("🚀 Đã khởi tạo thành công quyền: {}", RoleConstant.ADMIN);
        }

        // ----------------------------------------------------------------
        // 3. KHỞI TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH CHO E-COMMERCE
        // ----------------------------------------------------------------
        String adminUsername = "admin";
        String adminEmail = "admin@system.com";
        String adminPassword = "AdminPassword@123";

        if (!userRepository.existsByUsername(adminUsername)) {

            User adminUser = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .enabled(true)
                    .build();

            adminUser.addAppRole(defaultClientId, RoleConstant.ADMIN);

            userRepository.save(adminUser);

            log.info("🚀 Đã tạo thành công tài khoản Quản trị viên SSO!");
            log.info("  Username: {}", adminUsername);
            log.info("  Password: {}", adminPassword);
            log.info("  Quyền hạn: ADMIN tại ứng dụng {}", defaultClientId);
        }
    }
}
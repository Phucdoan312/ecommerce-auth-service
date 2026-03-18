package com.dvtp.authservice.infrastructure.initializer;

import com.dvtp.authservice.domain.constant.RoleConstant;
import com.dvtp.authservice.domain.entity.Role;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.repository.RoleRepository;
import com.dvtp.authservice.domain.repository.UserRepository;
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

    @Override
    @Transactional
    public void run(String... args) throws Exception {

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
        // 3. KHỞI TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH
        // ----------------------------------------------------------------
        String adminUsername = "admin";
        String adminEmail = "admin@system.com";
        String adminPassword = "AdminPassword@123";

        if (!userRepository.existsByUsername(adminUsername)) {

            Role adminRole = roleRepository.findByName(RoleConstant.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ROLE_ADMIN"));

            User adminUser = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .enabled(true)
                    .build();

            adminUser.addRole(adminRole);
            userRepository.save(adminUser);

            log.info("Đã tạo thành công tài khoản Quản trị viên!");
            log.info("  Username: {}", adminUsername);
            log.info("  Password: {}", adminPassword);
        }
    }
}
package com.dvtp.authservice.application.job;

import com.dvtp.authservice.application.service.UserImportService;
import com.dvtp.authservice.domain.constant.RoleConstant;
import com.dvtp.authservice.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegacyDataSyncJob {

    private final UserImportService userImportService;
    private final PasswordEncoder passwordEncoder;

    // Cron expression: Chạy vào đúng 02:00:00 "0 0 2 * * ?" sáng mỗi ngày
    // (Để test liền cho nóng, bạn đổi thành "0/10 * * * * ?" -> Cứ 10 giây chạy 1 lần)
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncDataFromLegacySystem() {
        log.info("⏰ BẮT ĐẦU CRON JOB: Đang kéo dữ liệu từ hệ thống cũ về...");

        // Giả lập việc gọi API sang hệ thống cũ (PHP/NodeJS) lấy về 50.000 user
        List<User> legacyUsers = fetchUsersFromOldSystem();

        log.info("Kéo xong {} users. Đang tiến hành đồng bộ (Bulk Insert)...", legacyUsers.size());

        // Gọi động cơ bắn thẳng xuống DB
        userImportService.importUsersInBulk(legacyUsers, "ecommerce-web", RoleConstant.USER);

        log.info("✅ HOÀN TẤT CRON JOB ĐỒNG BỘ DỮ LIỆU!");
    }

    // Hàm giả lập (Thực tế bạn sẽ dùng RestTemplate hoặc FeignClient gọi API ngoài)
    private List<User> fetchUsersFromOldSystem() {
        List<User> list = new ArrayList<>();
        String pass = passwordEncoder.encode("123456");
        for (int i = 1; i <= 5000; i++) {
            list.add(User.builder()
                    .id(UUID.randomUUID())
                    .username("legacy_user_" + i)
                    .email("old_" + i + "@yahoo.com")
                    .password(pass)
                    .enabled(true)
                    .build());
        }
        return list;
    }
}
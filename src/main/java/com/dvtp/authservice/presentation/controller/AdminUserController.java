package com.dvtp.authservice.presentation.controller;

import com.dvtp.authservice.application.service.ExcelParserService;
import com.dvtp.authservice.application.service.UserImportService;
import com.dvtp.authservice.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dvtp.authservice.domain.constant.RoleConstant;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
// 💎 Đổi đường dẫn thành /api/admin/... để phân biệt hoàn toàn với End-User
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "3. Admin Management API", description = "Các API quyền lực cao dành cho System Admin")
@Slf4j
public class AdminUserController {

    // 💎 Kéo cái Service đang bị "no usage" vào đây
    private final UserImportService userImportService;
    private final PasswordEncoder passwordEncoder;
    private final ExcelParserService excelParserService;

    @Operation(summary = "Tiêm 10.000 User ảo vào DB", description = "Dùng để test tốc độ Bulk Insert bằng JdbcTemplate")
    @PostMapping("/test-bulk-insert")
    public ResponseEntity<String> testBulkInsert() {
        log.info("Đang tạo 10.000 user ảo trên RAM...");
        List<User> dummyUsers = new ArrayList<>();

        // Mã hóa sẵn 1 cái pass để dùng chung cho 10.000 user cho nhanh
        String defaultHashedPassword = passwordEncoder.encode("123456");

        // Giả lập sinh 10.000 user
        for (int i = 1; i <= 10000; i++) {
            User user = User.builder()
                    .id(UUID.randomUUID()) // Khởi tạo luôn UUID chuẩn Clean Architecture
                    .username("test_user_" + i + "_" + System.currentTimeMillis())
                    .email("test_user_" + i + "@system.com")
                    .password(defaultHashedPassword)
                    .phone("09" + String.format("%08d", i))
                    .enabled(true)
                    .build();
            dummyUsers.add(user);
        }

        log.info("Bắt đầu gọi Service import...");

        // Gọi Service import. Ở DataInitializer bạn đã tạo "ecommerce-web" và "ROLE_USER"
        userImportService.importUsersInBulk(dummyUsers, "ecommerce-web", RoleConstant.USER);

        return ResponseEntity.ok("Đã Insert thành công 10.000 user và cấp quyền ROLE_USER trong chớp mắt!");
    }

    @Operation(summary = "Import User từ file Excel", description = "Admin upload file .xlsx để import hàng loạt")
    @PostMapping(value = "/import-excel", consumes = "multipart/form-data")
    public ResponseEntity<String> importFromExcel(@RequestParam("file") MultipartFile file) {

        // 1. Nhờ ExcelParser "mổ" file ra thành List
        List<User> usersFromExcel = excelParserService.parseExcelFile(file);

        // 2. Ném List đó vào "động cơ" Bulk Insert để bắn xuống DB
        userImportService.importUsersInBulk(usersFromExcel, "ecommerce-web", RoleConstant.USER);

        return ResponseEntity.ok("Đã import thành công " + usersFromExcel.size() + " tài khoản từ Excel!");
    }
}
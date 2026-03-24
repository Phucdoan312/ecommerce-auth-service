package com.dvtp.authservice.domain.entity;

import com.dvtp.authservice.domain.exception.AppException; // Ném luôn Exception của dự án
import com.dvtp.authservice.domain.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class User {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private LocalDate dob;
    private String phone;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private Map<String, Set<String>> appRoles = new java.util.HashMap<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean dobUpdated = false; // Rõ ràng hóa giá trị mặc định

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String phone, LocalDate dob) {
        if (phone != null && !phone.isBlank()) {
            this.phone = phone;
        }

        // 2. Logic cập nhật ngày sinh (Chỉ cho phép đổi 1 lần duy nhất)
        if (dob != null) {
            if (this.dobUpdated && !dob.equals(this.dob)) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "Ngày sinh chỉ được cập nhật duy nhất 1 lần.");
            }
            if (!this.dobUpdated) {
                this.dob = dob;
                this.dobUpdated = true;
            }
        }

        // 3. Luôn cập nhật thời gian chỉnh sửa cuối cùng
        this.updatedAt = LocalDateTime.now();
    }

    public void disableAccount() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void enableAccount() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void addAppRole(String clientId, String roleName) {
        if (this.appRoles == null) {
            this.appRoles = new java.util.HashMap<>();
        }
        this.appRoles.computeIfAbsent(clientId, k -> new HashSet<>()).add(roleName);

        this.updatedAt = LocalDateTime.now();
    }
}
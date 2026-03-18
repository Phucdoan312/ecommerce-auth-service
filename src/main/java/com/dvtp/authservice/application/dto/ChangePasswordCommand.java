package com.dvtp.authservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordCommand(
        @Schema(example = "MatKhauManh@123")
        @NotBlank(message = "Vui lòng nhập mật khẩu cũ")
        String oldPassword,

        @Schema(example = "NewPassword123")
        @NotBlank(message = "Vui lòng nhập mật khẩu mới")
        @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
        String newPassword,

        @Schema(example = "NewPassword123")
        @NotBlank(message = "Vui lòng xác nhận mật khẩu mới")
        String confirmPassword
) {}
package com.dvtp.authservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordCommand(
        @Schema(example = "phucdoan849@gmail.com")
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mã OTP không được để trống")
        String otpCode,

        @Schema(example = "NewPassword123")
        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        String newPassword,

        @Schema(example = "NewPassword123")
        @NotBlank(message = "Vui lòng xác nhận mật khẩu")
        String confirmPassword
) {}
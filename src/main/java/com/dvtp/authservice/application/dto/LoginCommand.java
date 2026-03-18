package com.dvtp.authservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginCommand(

        @Schema(description = "Tên đăng nhập hoặc Email", example = "phucdoan849@gmail.com")
        @NotBlank(message = "Tên đăng nhập hoặc Email không được để trống")
        String usernameOrEmail,

        @Schema(description = "Mật khẩu", example = "MatKhauManh@123")
        @NotBlank(message = "Mật khẩu không được để trống")
        String password
) {
}
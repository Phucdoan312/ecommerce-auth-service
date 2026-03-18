package com.dvtp.authservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterCommand(

        @Schema(description = "Tên đăng nhập (duy nhất)", example = "phucdoan")
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(min = 4, max = 20, message = "Tên đăng nhập phải từ 4 đến 20 ký tự")
        String username,

        @Schema(description = "Địa chỉ Email", example = "phucdoan849@gmail.com")
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @Schema(description = "Mật khẩu bảo mật", example = "MatKhauManh@123")
        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        String password,

        @Schema(description = "Ngày tháng năm sinh (YYYY-MM-DD)", example = "1999-12-31")
        LocalDate dob,

        @Schema(description = "Số điện thoại liên hệ", example = "0987654321")
        String phone,

        @NotBlank(message = "Mã OTP không được để trống")
        String otpCode
) {}
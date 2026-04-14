package com.dvtp.authservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyOtpCommand(
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email,

    @NotBlank(message = "Mã OTP không được để trống")
    String otpCode
) {}

package com.dvtp.authservice.presentation.controller;

import com.dvtp.authservice.application.dto.*;
import com.dvtp.authservice.application.service.ForgotPasswordService;
import com.dvtp.authservice.application.usecase.LoginUserUseCase;
import com.dvtp.authservice.application.usecase.RefreshTokenUseCase;
import com.dvtp.authservice.application.usecase.RegisterUserUseCase;
import com.dvtp.authservice.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication API", description = "Các API dùng để đăng ký và đăng nhập hệ thống")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ForgotPasswordService forgotPasswordService;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Operation(
            summary = "Đăng ký - Bước 1: Yêu cầu gửi OTP",
            description = "Gửi mã OTP về email để xác thực trước khi tạo tài khoản chính thức."
    )
    @PostMapping("/register/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestRegisterOtp(
            @Valid @RequestBody RegisterOtpRequestCommand command) { // SỬA Ở ĐÂY

        registerUserUseCase.requestOtpForRegister(command);
        return ResponseEntity.ok(ApiResponse.success(null, "Mã OTP đã được gửi đến email của bạn."));
    }

    @Operation(
            summary = "Đăng ký - Bước 2: Tạo tài khoản mới",
            description = "Tạo một người dùng mới. Yêu cầu username, email và mã OTP."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterCommand command) {

        UserResponse data = registerUserUseCase.register(command);
        return ResponseEntity.ok(ApiResponse.success(data, "Đăng ký tài khoản thành công!"));
    }

    @Operation(
            summary = "Đăng nhập hệ thống",
            description = "Xác thực người dùng. Trả về JWT Token."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginCommand command) {
        AuthResponse data = loginUserUseCase.login(command);
        return ResponseEntity.ok(ApiResponse.success(data, "Đăng nhập thành công!"));
    }

    @Operation(summary = "Quên mật khẩu - Bước 1: Yêu cầu gửi OTP")
    @PostMapping("/forgot-password/request")
    public ResponseEntity<ApiResponse<Void>> requestForgotPassword(
            @Valid @RequestBody ForgotPasswordCommand command) {

        forgotPasswordService.requestOtp(command);
        return ResponseEntity.ok(ApiResponse.success(null, "Mã OTP đã được gửi đến email của bạn."));
    }

    @Operation(summary = "Quên mật khẩu - Bước 2: Xác nhận OTP và đổi mật khẩu")
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordCommand command) {

        forgotPasswordService.resetPassword(command);
        return ResponseEntity.ok(ApiResponse.success(null, "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập."));
    }

    @Operation(
            summary = "Làm mới Token (Refresh Token)",
            description = "Gửi Refresh Token còn hạn lên đây để đổi lấy một cặp Access Token và Refresh Token mới toanh mà không cần đăng nhập lại."
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenCommand command) {

        AuthResponse data = refreshTokenUseCase.refreshToken(command);
        return ResponseEntity.ok(ApiResponse.success(data, "Làm mới Token thành công!"));
    }
}
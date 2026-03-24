package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.AuthResponse;
import com.dvtp.authservice.application.dto.LoginCommand;
import com.dvtp.authservice.application.port.JwtTokenProvider;
import com.dvtp.authservice.application.usecase.LoginUserUseCase;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.UserRepository;
// Nhớ import thêm cái Repository của AppClient nhé (tùy theo package ông đặt)
import com.dvtp.authservice.domain.repository.AppClientRepository;
import com.dvtp.authservice.infrastructure.security.SecurityConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserService implements LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppClientRepository appClientRepository;

    @Override
    public AuthResponse login(LoginCommand command) {

        appClientRepository.findByClientId(command.clientId())
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_ERROR,
                        "SSO Failed: Ứng dụng [" + command.clientId() + "] không tồn tại hoặc chưa đăng ký với hệ thống Auth."));

        User user = userRepository.findByUsernameOrEmail(command.usernameOrEmail(), command.usernameOrEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS,
                        "Login Failed: Không tìm thấy User với thông tin đăng nhập: " + command.usernameOrEmail()));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS,
                    "Login Failed: Mật khẩu không khớp cho User: " + user.getUsername());
        }

        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED,
                    "Login Failed: Tài khoản [" + user.getUsername() + "] đang bị khóa (enabled=false).");
        }


        if (user.getAppRoles() == null || !user.getAppRoles().containsKey(command.clientId())) {
            log.warn("🚨 [SECURITY ALERT] User {} cố gắng đăng nhập trái phép vào ứng dụng {}", user.getUsername(), command.clientId());
            throw new AppException(ErrorCode.VALIDATION_ERROR,
                    "Access Denied: Tài khoản của bạn không được cấp quyền truy cập vào hệ thống [" + command.clientId() + "].");
        }


        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        long expirationMs = jwtTokenProvider.getExpirationTime();

        log.info("🔑 [AUDIT] Người dùng [{}] đăng nhập thành công vào hệ thống [{}]", user.getUsername(), command.clientId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                SecurityConstant.TOKEN_PREFIX.trim(),
                Instant.now(),
                Instant.now().plusMillis(expirationMs)
        );
    }
}
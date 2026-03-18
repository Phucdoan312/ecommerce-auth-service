package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.AuthResponse;
import com.dvtp.authservice.application.dto.LoginCommand;
import com.dvtp.authservice.application.port.JwtTokenProvider;
import com.dvtp.authservice.application.usecase.LoginUserUseCase;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.UserRepository;
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

    @Override
    public AuthResponse login(LoginCommand command) {
        // 1. Tìm User bằng Username hoặc Email
        User user = userRepository.findByUsernameOrEmail(command.usernameOrEmail(), command.usernameOrEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS,
                        "Login Failed: Không tìm thấy User với thông tin đăng nhập: " + command.usernameOrEmail()));

        // 2. Xác thực mật khẩu
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS,
                    "Login Failed: Mật khẩu không khớp cho User: " + user.getUsername());
        }

        // 3. Kiểm tra trạng thái tài khoản
        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED,
                    "Login Failed: Tài khoản [" + user.getUsername() + "] đang bị khóa (enabled=false).");
        }

        // 4. Sinh bộ đôi Token thông qua Port JwtTokenProvider
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user); // 👈 Gọi hàm đẻ Refresh Token
        long expirationMs = jwtTokenProvider.getExpirationTime();

        // 5. Ghi log Audit đăng nhập
        log.info("🔑 [AUDIT] Người dùng đăng nhập thành công: {}", user.getUsername());

        // 6. Trả về cả cặp vé
        return new AuthResponse(
                accessToken,
                refreshToken,
                                SecurityConstant.TOKEN_PREFIX.trim(),
                Instant.now(),
                Instant.now().plusMillis(expirationMs)
        );
    }
}
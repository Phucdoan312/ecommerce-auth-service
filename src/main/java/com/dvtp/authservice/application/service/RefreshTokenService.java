package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.AuthResponse;
import com.dvtp.authservice.application.dto.RefreshTokenCommand;
import com.dvtp.authservice.application.port.JwtTokenProvider;
import com.dvtp.authservice.application.usecase.RefreshTokenUseCase;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.UserRepository;
import com.dvtp.authservice.infrastructure.security.SecurityConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public AuthResponse refreshToken(RefreshTokenCommand command) {
        String requestRefreshToken = command.refreshToken();

        if(!jwtTokenProvider.validateToken(command.refreshToken())){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION,
                    "Expired or invalid refresh token: " + requestRefreshToken);
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(requestRefreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        "User not found with id from refresh token: " + userId));

        if(!user.isEnabled()){
            throw new AppException(ErrorCode.VALIDATION_ERROR,
                    "User with id [" + userId + "] is disabled.");
        }

        String newAccessToken = jwtTokenProvider.generateToken(user);
        String newRefreshToke =jwtTokenProvider.generateRefreshToken(user);
        long expirationMs = jwtTokenProvider.getExpirationTime();

        log.info("[AUDIT] Refresh token used for user:" + user.getUsername() + " at " + Instant.now());

        return new AuthResponse(
                newAccessToken,
                newRefreshToke,
                SecurityConstant.TOKEN_PREFIX.trim(),
                Instant.now(),
                Instant.now().plusSeconds(expirationMs)
        );
    }
}
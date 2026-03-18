package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.UserResponse;
import com.dvtp.authservice.application.usecase.GetUserProfileUseCase;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements GetUserProfileUseCase {

    private final UserRepository userRepository;


    @Override
    public UserResponse getMyProfile() {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + userId));
        log.info("👤 [AUDIT] Truy cập thông tin profile của user: {}", user.getUsername());
        return UserResponse.fromDomain(user);
    }
}
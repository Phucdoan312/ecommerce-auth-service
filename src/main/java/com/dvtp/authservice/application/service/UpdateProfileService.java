package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.UpdateProfileCommand;
import com.dvtp.authservice.application.dto.UserResponse;
import com.dvtp.authservice.application.usecase.UpdateProfileUseCase;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileCommand command) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + userId));
        user.updateProfile(command.phone(), command.dob());
        User savedUser = userRepository.save(user);
        log.info("User with id {} updated successfully", savedUser.getId());
        return UserResponse.fromDomain(savedUser);
    }
}
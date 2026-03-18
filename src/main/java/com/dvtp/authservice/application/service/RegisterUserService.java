package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.dto.RegisterCommand;// Nhớ đổi tên DTO này theo đúng tên ông đã tạo nhé
import com.dvtp.authservice.application.dto.RegisterOtpRequestCommand;
import com.dvtp.authservice.application.dto.UserResponse;
import com.dvtp.authservice.application.usecase.RegisterUserUseCase;
import com.dvtp.authservice.domain.constant.RoleConstant;
import com.dvtp.authservice.domain.entity.Role;
import com.dvtp.authservice.domain.entity.User;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.domain.repository.RoleRepository;
import com.dvtp.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override
    @Transactional
    public void requestOtpForRegister(RegisterOtpRequestCommand command) {
        if(userRepository.existsByEmail(command.email())){
            throw new AppException(ErrorCode.EMAIL_ALREADY_IN_USE,
                    "OTP request failed: Email [" + command.email() + "] is already in use.");
        }
        otpService.generateAndSendOtp(command.email());
        log.info("[AUDIT] OTP requested for registration with email: {}", command.email());
    }


    @Override
    @Transactional
    public UserResponse register(RegisterCommand command) {
        if(userRepository.existsByUsername(command.username())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS,
                    "Register failed: User with username [" + command.username() + "already exists.");
        }

        if(userRepository.existsByEmail(command.email())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_IN_USE,
                    "Register failed: Email [" + command.email() + "] is already in use.");
        }

        otpService.validateOtp(command.email(), command.otpCode());

        Role userRole = roleRepository.findByName(RoleConstant.USER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND,
                        "Role not found: " + RoleConstant.USER));
        User newUser = User.builder()
                .username(command.username())
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .dob(command.dob())
                .phone(command.phone())
                .build();

        newUser.addRole(userRole);
        User savedUser = userRepository.save(newUser);

        log.info("[AUDIT] User {} registerd successfully with email: {}", savedUser.getUsername(), savedUser.getEmail());
        return UserResponse.fromDomain(savedUser);
    }
}
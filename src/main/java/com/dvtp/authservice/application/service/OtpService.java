package com.dvtp.authservice.application.service;

import com.dvtp.authservice.application.port.EmailSender;
import com.dvtp.authservice.domain.exception.AppException;
import com.dvtp.authservice.domain.exception.ErrorCode;
import com.dvtp.authservice.infrastructure.persistence.entity.OtpTokenJpaEntity;
import com.dvtp.authservice.infrastructure.persistence.repository.SpringDataJpaOtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final SpringDataJpaOtpRepository otpRepository;
    private final EmailSender emailSender;

    @Value("${application.security.otp.expiration-minutes}")
    private int otpExpirationMinutes;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void generateAndSendOtp(String email) {
        int otpValue = 100000 + secureRandom.nextInt(900000);
        String otpCode = String.valueOf(otpValue);

        OtpTokenJpaEntity otpEntity  = OtpTokenJpaEntity.builder()
                .email(email)
                .otpCode(otpCode)
                .expirationTime(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .used(false)
                .build();

        otpRepository.save(otpEntity);
        emailSender.sendOtpEmail(email, otpCode);
        log.info("OTP has been sent to " + email);
    }



    @Transactional
    public void validateOtp(String email, String otpCode) {
        OtpTokenJpaEntity otpEntity = otpRepository.findTopByEmailOrderByExpirationTimeDesc(email)
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_ERROR, "OTP validation failed: No OTP found for email [" + email + "]."));

        if(otpEntity.isUsed()){
            throw new AppException(ErrorCode.VALIDATION_ERROR, "OTP validation failed: OTP is already used.");
        }

        if(otpEntity.isExpired()){
            throw new AppException(ErrorCode.VALIDATION_ERROR, "OTP validation failed: OTP is expired.");
        }

        if(!otpEntity.getOtpCode().equals(otpCode)){
            throw new AppException(ErrorCode.VALIDATION_ERROR, "OTP validation failed: Invalid OTP code.");
        }

        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
        log.info("OTP validation successful for email: " + email);
    }
}
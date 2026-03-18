package com.dvtp.authservice.application.usecase;

import com.dvtp.authservice.application.dto.ChangePasswordCommand;

import java.util.UUID;

public interface ChangePasswordUseCase {
    void changePassword(UUID userId, ChangePasswordCommand command);
}
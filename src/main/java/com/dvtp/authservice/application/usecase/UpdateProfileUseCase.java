package com.dvtp.authservice.application.usecase;

import com.dvtp.authservice.application.dto.UpdateProfileCommand;
import com.dvtp.authservice.application.dto.UserResponse;

import java.util.UUID;

public interface UpdateProfileUseCase {
    UserResponse updateProfile(UUID userId, UpdateProfileCommand command);

}
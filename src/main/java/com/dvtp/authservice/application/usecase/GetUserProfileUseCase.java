package com.dvtp.authservice.application.usecase;

import com.dvtp.authservice.application.dto.UserResponse;

public interface GetUserProfileUseCase {
    UserResponse getMyProfile();
}
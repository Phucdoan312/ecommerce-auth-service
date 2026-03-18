package com.dvtp.authservice.application.usecase;

import com.dvtp.authservice.application.dto.AuthResponse;
import com.dvtp.authservice.application.dto.RefreshTokenCommand;

public interface RefreshTokenUseCase {
    AuthResponse refreshToken(RefreshTokenCommand command);
}
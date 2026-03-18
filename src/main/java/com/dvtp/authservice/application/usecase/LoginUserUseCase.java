package com.dvtp.authservice.application.usecase;

import com.dvtp.authservice.application.dto.LoginCommand;
import com.dvtp.authservice.application.dto.AuthResponse;

public interface LoginUserUseCase {
    AuthResponse login(LoginCommand command);
}
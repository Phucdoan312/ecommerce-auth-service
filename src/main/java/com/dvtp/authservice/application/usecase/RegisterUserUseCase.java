package com.dvtp.authservice.application.usecase;

import com.dvtp.authservice.application.dto.RegisterCommand;
import com.dvtp.authservice.application.dto.RegisterOtpRequestCommand;
import com.dvtp.authservice.application.dto.UserResponse;

public interface RegisterUserUseCase {
    void requestOtpForRegister(RegisterOtpRequestCommand command);
    UserResponse register(RegisterCommand command);
}
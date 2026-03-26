package com.dvtp.authservice.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenCommand(
        @NotBlank(message = "ClientId không được để trống")
        String clientId,

        @NotBlank(message = "Refresh Token không được để trống")
        String refreshToken
) {}
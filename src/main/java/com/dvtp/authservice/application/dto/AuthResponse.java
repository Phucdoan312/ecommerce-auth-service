package com.dvtp.authservice.application.dto;

import java.time.Instant;

public record AuthResponse(
        String token,
        String refreshToken,
        String type,
        Instant issuedAt,
        Instant expiresAt
) {
}